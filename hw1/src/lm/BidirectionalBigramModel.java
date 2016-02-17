package hw1.lm;

import java.io.*;
import java.util.*;

/**
 * @author Shobhit Chaurasia
 * A bidirectional bigram language model that linearly combines the token
 * generation probabilities of the forward bigram and the backward bigram
 * model. Both forward and backward bigram model use simple fixed-weight
 * interpolation with a unigram model for smoothing.
*/

public class BidirectionalBigramModel {

    // An instance of the BigramModel
    public BigramModel forward = null;
    // An instance of the BackwardBigramModel
    public BackwardBigramModel backward = null;

    /** Interpolation weight for forward model */
    public double forward_weight = 0.5;

    /** Interpolation weight for backward model */
    public double backward_weight = 0.5;

    public BidirectionalBigramModel() {
        this.forward = new BigramModel();
        this.backward = new BackwardBigramModel();
    }

    public BidirectionalBigramModel(double forward_weight,
                                    double backward_weight) {
        this.forward = new BigramModel();
        this.backward = new BackwardBigramModel();
        this.forward_weight = forward_weight;
        this.backward_weight = backward_weight;
    }

    /**
     * Trains the Bigram and BackwardBigram models
     * @param sentences List of sentences
     */
    public void train (List<List<String>> sentences) {
        forward.train(sentences);
        backward.train(sentences);
    }

    /**
     * Calculates word perplexity for the Bidirectional model. Word perplexity
     * does not include prediction probabilities for start or end markers.
     * It is calculated as a weighted sum of the probabilities for each token
     * in both the models (forward as well as backward).
     * @param sentences [description]
     * @return  perplexity
     */
    public double test(List<List<String>> sentences) {
        double totalLogProb = 0;
        double totalNumTokens = 0;
        for (List<String> sentence : sentences) {
            totalNumTokens = totalNumTokens + sentence.size();

            double[] forward_probability = forward.sentenceTokenProbs(sentence);
            double[] backward_probability = backward.sentenceTokenProbs(sentence);

            assert(forward_probability.length == sentence.size() + 1);
            assert(backward_probability.length == sentence.size() + 1);

            int n = forward_probability.length;
            for (int i = 0; i < n - 1; i++) {   // do not include prediction for end or start
                totalLogProb = totalLogProb +
                               Math.log(forward_probability[i] * forward_weight
                                        + backward_probability[n - i - 2] * backward_weight);
            }
        }
        double perplexity = Math.exp(-totalLogProb / totalNumTokens);
        System.out.println("Word Perplexity = " + perplexity );
        return perplexity;
    }

    public void test_multiple_weights(List<List<String>> testSentences) {
        List<DoubleValue> forward_vector = new ArrayList<DoubleValue>();
        List<DoubleValue> backward_vector = new ArrayList<DoubleValue>();
        List<DoubleValue> perplexity = new ArrayList<DoubleValue>();
        for (double i = 0; i <= 1.0 ; i = i + 0.01 ) {
            this.forward_weight = i;
            this.backward_weight = 1.0 - i;

	    DoubleValue v = new DoubleValue();
            v.setValue(i);
            forward_vector.add(v);

	    v = new DoubleValue();
            v.setValue(1.0 - i);
            backward_vector.add(v);

	    v = new DoubleValue();    
            v.setValue(this.test(testSentences));
            perplexity.add(v);
        }
        for(int i = 0; i < perplexity.size(); i++) {
            System.out.print(forward_vector.get(i).getValue() + 
                            "," + backward_vector.get(i).getValue() + ","
                             + perplexity.get(i).getValue() + "\n");
        }
    }

    public static int wordCount (List<List<String>> sentences) {
        int wordCount = 0;
        for (List<String> sentence : sentences) {
            wordCount += sentence.size();
        }
        return wordCount;
    }

    /** Train and test a BidirectionalBigram model.
     *  Command format: "nlp.lm.BidirectionalBigramModel [DIR]* [TestFrac]" where DIR
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
        BidirectionalBigramModel model = new BidirectionalBigramModel(0.5, 0.5);
        System.out.println("Training...");
        model.train(trainSentences);
        // Test on training data using test.
        model.test(trainSentences);
        System.out.println("Testing...");
        // Test on test data using test.
        model.test(testSentences);
       // model.test_multiple_weights(testSentences);
    }
}
