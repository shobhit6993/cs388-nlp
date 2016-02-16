package hw1.lm;

import java.io.*;
import java.util.*;

/**
 * @author Shobhit Chaurasia
 * A backward bigram language model that uses simple fixed-weight interpolation
 * with a unigram model for smoothing. This class only overrides few methods in
 * BigramModel class that require sentences in reverse order.
*/

public class BackwardBigramModel extends BigramModel {
    /**
     * Trains backward bigram model. It calls the train method of the BigramModel
     * class with reversed sentences. This is equivalent to training a model from
     * right to left, despite <S> and </S> markers remaining unreversed because
     * these markers are have only representative purposes, and <S> can very
     * well be treated as a end-of-sentence marker (as it is indeed in this
     * backward bigram implementation.)
     * @param sentences [description]
     */
    @Override
    public void train(List<List<String>> sentences) {
        List<List<String>> r_sentences = new ArrayList<List<String>>();

        for (List<String> sentence : sentences) {
            List<String> r_sentence = new ArrayList<String>(sentence);
            Collections.reverse(r_sentence);
            r_sentences.add(r_sentence);
        }

        super.train(r_sentences);
    }
    
    @Override
    public double sentenceLogProb(List<String> sentence) {
        List<String> r_sentence = new ArrayList<String>(sentence);
        Collections.reverse(r_sentence);

        return super.sentenceLogProb(r_sentence);
    }

    @Override
    public double sentenceLogProb2(List<String> sentence) {
        List<String> r_sentence = new ArrayList<String>(sentence);
        Collections.reverse(r_sentence);

        return super.sentenceLogProb2(r_sentence);
    }

    @Override
    public double[] sentenceTokenProbs(List<String> sentence) {
        List<String> r_sentence = new ArrayList<String>(sentence);
        Collections.reverse(r_sentence);

        return super.sentenceTokenProbs(r_sentence);
    }

    /** Train and test a backward bigram model.
     *  Command format: "nlp.lm.BackwardBigramModel [DIR]* [TestFrac]" where DIR
     *  is the name of a file or directory whose LDC POS Tagged files should be
     *  used for input data; and TestFrac is the fraction of the sentences
     *  in this data that should be used for testing, the rest for training.
     *  0 < TestFrac < 1
     *  Uses the last fraction of the data for testing and the first part
     *  for training.
     */
    public static void main(String[] args) throws IOException {
        // All but last arg is a file/directory of LDC tagged input data
        File[] files = new File[args.length - 1];
        for (int i = 0; i < files.length; i++)
            files[i] = new File(args[i]);
        // Last arg is the TestFrac
        double testFraction = Double.valueOf(args[args.length - 1]);
        // Get list of sentences from the LDC POS tagged input files
        List<List<String>> sentences =  POSTaggedFile.convertToTokenLists(files);
        int numSentences = sentences.size();
        // Compute number of test sentences based on TestFrac
        int numTest = (int)Math.round(numSentences * testFraction);
        // Take test sentences from end of data
        List<List<String>> testSentences = sentences.subList(numSentences - numTest, numSentences);
        // Take training sentences from start of data
        List<List<String>> trainSentences = sentences.subList(0, numSentences - numTest);
        System.out.println("# Train Sentences = " + trainSentences.size() +
                           " (# words = " + wordCount(trainSentences) +
                           ") \n# Test Sentences = " + testSentences.size() +
                           " (# words = " + wordCount(testSentences) + ")");
        // Create a bigram model and train it.
        BackwardBigramModel model = new BackwardBigramModel();
        System.out.println("Training...");

        model.train(trainSentences);
        // model.print();
        // Test on training data using test and test2
        model.test(trainSentences);
        model.test2(trainSentences);
        System.out.println("Testing...");
        // Test on test data using test and test2
        model.test(testSentences);
        model.test2(testSentences);
    }
}
