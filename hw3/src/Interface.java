package domain_adapt;

import java.util.Collection;
import java.util.List;
import java.io.*;
import java.util.*;

import edu.stanford.nlp.util.Timing;
import edu.stanford.nlp.parser.common.ArgUtils;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.parser.lexparser.EvaluateTreebank;

class Interface {

  /**
   * Provides an Interface to LexicalizedParser class for performing
   * unsupervised domain adapation
   */


  /**
   * Performs unsupervised domain adaption using seedTreebank as labelled
   * seed set, using a parser trained on seedTreebank to label unlabelled
   * selfTreebank, and using selfTreebank as self-training set combined with
   * the seed set to train a new parser, and evaluate on testTreebank
   * @param seedTreebank Seed Treebank set for Training
   * @param selfTreebank Treebank set to be used as self-training set
   * @param testTreebank Treebank set to be used for testing
   * @param op           Options
   */
  private static void runExperiment(MemoryTreebank seedTreebank,
                                    MemoryTreebank selfTreebank,
                                    MemoryTreebank testTreebank,
                                    Options op) {
    LexicalizedParser parser =
      LexicalizedParser.trainFromTreebank(seedTreebank, op);

    printGrammarStats(parser, op);

    if (selfTreebank != null) {
      Options temp = new Options();
      // will finally have seed treebanks + self trained treebanks
      Treebank mixedTrainTreebank = temp.tlpParams.memoryTreebank();
      for (int i = 0; i < selfTreebank.size(); i++ ) {
        // extract HasWords from the tree and parse it using trained parser
        // add the parsed Tree to training set as a self-trained example
        mixedTrainTreebank.add(parser.parse(
                                 selfTreebank.get(i).yieldHasWord()));
      }
      mixedTrainTreebank.addAll(seedTreebank);
      parser = LexicalizedParser.trainFromTreebank(mixedTrainTreebank, op);
      printGrammarStats(parser, op);
    }

    // test the parser on testTreebank
    if (testTreebank != null) {
      // test parser on treebank
      EvaluateTreebank evaluator = new EvaluateTreebank(parser);
      evaluator.testOnTreebank(testTreebank);
    }
  }

  /**
   * Prints stats for the Grammar learned by the parser
   * @param lp Trained Lexicalized parser
   * @param op Options
   */
  private static void printGrammarStats(LexicalizedParser lp, Options op) {
    String lexNumRules = lp.lex != null ? Integer.toString(lp.lex.numRules()): "";
    System.err.println("Grammar\tStates\tTags\tWords\tUnaryR\tBinaryR\tTaggings");
    System.err.println("Grammar\t" +
        lp.stateIndex.size() + '\t' +
        lp.tagIndex.size() + '\t' +
        lp.wordIndex.size() + '\t' +
        (lp.ug != null ? lp.ug.numRules(): "") + '\t' +
        (lp.bg != null ? lp.bg.numRules(): "") + '\t' +
        lexNumRules);
    System.err.println("ParserPack is " + op.tlpParams.getClass().getName());
    System.err.println("Lexicon is " + lp.lex.getClass().getName());
    if (op.testOptions.verbose) {
      System.err.println("Tags are: " + lp.tagIndex);
      // System.err.println("States are: " + lp.pd.stateIndex); // This is too verbose. It was already printed out by the below printOptions command if the flag -printStates is given (at training time)!
    }
  }

  /**
   * Creates train and test split for the fractional set.
   * @param  path directory from where Treebanks need to be extracted
   * @param  frac fraction of Treebanks to be used for training
   * @param maxsize Maximum number of trees in self training set
   * @return      train and test Treebanks as a list of size 2.
   */
  private static List<MemoryTreebank> handleFractional(String path,
      Double frac,
      int maxsize) {
    int n, train_end, test_start;
    File folder = new File(path);
    File[] subdirs = folder.listFiles();
    Options op = new Options();

    MemoryTreebank trainTreebank = op.tlpParams.memoryTreebank();
    MemoryTreebank testTreebank = op.tlpParams.memoryTreebank();
    MemoryTreebank tb = op.tlpParams.memoryTreebank();

    int count = 0;
    for (File subdir : subdirs) {
      if (!subdir.isFile())
        count = count + 1;
    }

    for (File subdir : subdirs) {
      if (subdir.isFile())
        continue;
      tb = makeTreebank(subdir.getPath(), op, null);
      n = tb.size();
      test_start = (int)(n * frac);
      train_end = Math.min(test_start - 1, (maxsize / count - 1));
      for (int i = 0; i < n; i++) {
        if (i <= train_end ) {
          trainTreebank.add(tb.get(i));
        }
        if (i >= test_start) {
          testTreebank.add(tb.get(i));
        }
      }
    }

    // read extra trees from some genres to compensate for less populous genres
    int shortage = maxsize - trainTreebank.size();
    if (shortage != 0) {
      for (File subdir : subdirs) {
        if (subdir.isFile())
          continue;
        tb = makeTreebank(subdir.getPath(), op, null);
        n = tb.size();
        test_start = Math.round((int)(n * frac));
        train_end = Math.min(test_start - 1, (maxsize / count - 1));

        for (int i = train_end + 1; shortage > 0 && i < test_start; i++) {
          trainTreebank.add(tb.get(i));
          shortage = shortage - 1;
        }
      }
    }

    List<MemoryTreebank> a = new ArrayList();
    a.add(trainTreebank);
    a.add(testTreebank);
    return a;
  }

  /**
   * Creates MemoryTreebank from wsj sections 02 to 22.
   * @param  path directory from where Treebanks need to be extracted
   * @param  num Number of Trees in MemoryTreebank
   * @return      MemoryTreebank from section 02 to 22
   */
  private static MemoryTreebank handle02_22(String path, int num) {
    File folder = new File(path);
    File[] subdirs = folder.listFiles();
    Options op = new Options();

    MemoryTreebank treebank = op.tlpParams.memoryTreebank();
    MemoryTreebank tb = op.tlpParams.memoryTreebank();
    for (File subdir : subdirs) {
      if (subdir.isFile())
        continue;
      if (subdir.getName().equals("00") || subdir.getName().equals("01")
          || subdir.getName().equals("23") || subdir.getName().equals("24")) {
        continue;
      }

      tb = makeTreebank(subdir.getPath(), op, null);
      treebank.addAll(tb);
    }

    if (num == Integer.MAX_VALUE) {
      return treebank;
    } else {
      MemoryTreebank ret = op.tlpParams.memoryTreebank();
      for (int i = 0; i < num; i++) {
        ret.add(treebank.get(i));
      }
      return ret;
    }
  }

  /**
   * Creates MemoryTreebank from wsj section 23.
   * @param  path directory from where Treebanks need to be extracted
   * @return      MemoryTreebank from section 23
   */
  private static MemoryTreebank handle23(String path) {
    File folder = new File(path);
    File[] subdirs = folder.listFiles();
    Options op = new Options();

    MemoryTreebank treebank = op.tlpParams.memoryTreebank();
    MemoryTreebank tb = op.tlpParams.memoryTreebank();
    for (File subdir : subdirs) {
      if (subdir.isFile())
        continue;
      if (subdir.getName().equals("23")) {
        tb = makeTreebank(subdir.getPath(), op, null);
        treebank.addAll(tb);
      }
    }
    return treebank;
  }

  /**
   * Constructs a MemoryTreebank from all Treebank files in the given path
   * @param  treebankPath File/Directory path
   * @param  op           Options
   * @param  filt         FileFilter, always set to null in these experiments
   * @return              MemoryTreebank
   */
  private static MemoryTreebank makeTreebank(String treebankPath, Options op, FileFilter filt) {
    System.err.println("Training a parser from treebank dir: " + treebankPath);
    MemoryTreebank tb = op.tlpParams.memoryTreebank();
    System.err.print("Reading trees...");
    if (filt == null) {
      tb.loadPath(treebankPath);
    } else {
      tb.loadPath(treebankPath, filt);
    }

    Timing.tick("done [read " + tb.size() + " trees].");
    return tb;
  }

  public static void main(String[] args) {
    String seedPath = "";
    String selfPath = "";
    String testPath = "";
    Double frac = 1.0;
    int seedsize = 10000;
    int selfsize = Integer.MAX_VALUE;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-seed")) {
        if (!args[i + 1].equals("X"))
          seedPath = args[i + 1];
      }
      if (args[i].equals("-self")) {
        if (!args[i + 1].equals("X"))
          selfPath = args[i + 1];
      }
      if (args[i].equals("-test")) {
        if (!args[i + 1].equals("X"))
          testPath = args[i + 1];
      }
      if (args[i].equals("-frac")) {
        if (!args[i + 1].equals("X"))
          frac = Double.parseDouble(args[i + 1]);
      }
      if (args[i].equals("-seedsize")) {
        if (!args[i + 1].equals("X"))
          seedsize = Integer.parseInt(args[i + 1]);
      }
      if (args[i].equals("-selfsize")) {
        if (!args[i + 1].equals("X"))
          selfsize = Integer.parseInt(args[i + 1]);
      }
    }

    Options op = new Options();
    MemoryTreebank seedTreebank = op.tlpParams.memoryTreebank();
    MemoryTreebank selfTreebank = op.tlpParams.memoryTreebank();
    MemoryTreebank testTreebank = op.tlpParams.memoryTreebank();
    if (seedPath.toLowerCase().contains("brown")) {
      List<MemoryTreebank> temp = handleFractional(seedPath, frac, seedsize);
      seedTreebank = temp.get(0);
    }
    if (seedPath.toLowerCase().contains("wsj")) {
      seedTreebank = handle02_22(seedPath, seedsize);
    }

    if (testPath.toLowerCase().contains("brown")) {
      List<MemoryTreebank> temp = handleFractional(testPath, frac, selfsize);
      if (!selfPath.equals(""))
        selfTreebank = temp.get(0);
      testTreebank = temp.get(1);
    }
    if (testPath.toLowerCase().contains("wsj")) {
      if (!selfPath.equals(""))
        selfTreebank = handle02_22(selfPath, selfsize);
      testTreebank = handle23(testPath);
    }

    Options op_lex = new Options();
    op.doDep = false;
    op.doPCFG = true;
    op.setOptions("-goodPCFG", "-evals", "tsv");

    runExperiment(seedTreebank, selfTreebank, testTreebank, op);
  }

  private Interface() {} // static methods only

}
