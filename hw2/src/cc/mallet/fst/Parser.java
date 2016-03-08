package cc.mallet.fst;
import java.io.*;
import java.util.*;

/**
 *
 * @author Ray Mooney
 * Methods for processing Linguistic Data Consortium (LDC,www.ldc.upenn.edu)
 * data files that are tagged for Part Of Speech (POS). Converts tagged files
 * into simple untagged Lists of sentences which are Lists of String tokens.
*/

public class Parser {

    /** The name of the LDC POS file */
    public File file = null;
    /** The I/O reader for accessing the file */
    protected BufferedReader reader = null;

    public static Boolean orthographic = false;

    private static List<String> features = Arrays.asList("hyphen", "num",
                                           "caps", "allcaps", "ance", "er",
                                           "ism", "ist", "ment", "ness",
                                           "ship", "sion", "ize", "ise",
                                           "able", "ible", "al", "esque",
                                           "ful", "ic", "ical", "ous",
                                           "ish", "ive", "less", "s");

    /** Create an object for a given LDC POS tagged file */
    public Parser(File file) {
        this.file = file;
        try {
            this.reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            System.out.println("\nCould not open Parser: " + file);
            System.exit(1);
        }
    }

    /** Return the next line of POS tagged tokens from this file.
        Returns "\n" if end of sentence and start of a new one.
        Returns null if end of file */
    protected String getNextPOSLine() {
        String line = null;
        try {
            do {
                // Read a line from the file
                line = reader.readLine();
                if (line == null) {
                    // End of file, no more tokens, return null
                    reader.close();
                    return null;
                }
                // Sentence boundary indicator
                if (line.startsWith("======="))
                    line = "\n";
                // If sentence number indicator for ATIS or comment for Brown, ignore it
                if (line.startsWith("[ @") || line.startsWith("*x*"))
                    line = "";
            } while (line.equals(""));
        } catch (IOException e) {
            System.out.println("\nCould not read from TextFileDocument: " + file);
            System.exit(1);
        }
        return line;
    }

    /** Take a line from the file and return a list of Tuples (token,tag)
     * in the line */
    protected List<Tuple> getTokens (String line) {
        List<Tuple> tokenList = new ArrayList<Tuple>();
        line = line.trim();
        // Use a tokenizer to extract token/POS pairs in line,
        // ignore brackets indicating chunk boundaries
        StringTokenizer tokenizer = new StringTokenizer(line, " []");
        while (tokenizer.hasMoreTokens()) {
            String tokenPos = tokenizer.nextToken();
            tokenList.add(segmentToken(tokenPos));
            // If last token in line has end of sentence tag ".",
            // add a sentence end token </S>
            if (tokenPos.endsWith("/.") && !tokenizer.hasMoreTokens()) {
                tokenList.add(new Tuple("</S>", "</S>"));
            }
        }
        return tokenList;
    }

    /** Segment a token/POS string and return a Tuple of (token, POS tag)*/
    protected Tuple segmentToken (String tokenPos) {
        // POS tag follows the last slash
        int slash = tokenPos.lastIndexOf("/");
        // If this assert fails, it means the token in not tagged.
        assert(slash >= 0);

        //if (slash < 0){
        //    // This case should not happen
        //
        //    return tokenPos;
        //}
        //else {
        String tok = tokenPos.substring(0, slash);
        String tag = tokenPos.substring(slash + 1, tokenPos.length());
        // System.out.println(tok + ", " + tag);

        return new Tuple(tok, tag);
        //return tokenPos.substring(0, slash);
        //}
    }

    /** Return a List of sentences each represented as a List of
     * Tuple (token,tag) for the sentences in this file */
    protected List<List<Tuple>> tokenLists() {
        List<List<Tuple>> sentences = new ArrayList<List<Tuple>>();
        List<Tuple> sentence = new ArrayList<Tuple>();
        String line;
        while ((line = getNextPOSLine()) != null) {
            // Newline line indicates new sentence
            if (line.equals("\n")) {
                if (!sentence.isEmpty()) {
                    // Save completed sentence
                    sentences.add(sentence);
                    // and start a new sentence
                    sentence = new ArrayList<Tuple>();
                }
            } else {
                // Get the tokens in the line
                List<Tuple> tokens = getTokens(line);
                if (!tokens.isEmpty()) {
                    // If last token is an end-sentence token "</S>"
                    if (tokens.get(tokens.size() - 1).tok.equals("</S>")) {
                        // Then remove it
                        tokens.remove(tokens.size() - 1);
                        // and add final sentence tokens
                        sentence.addAll(tokens);
                        // Save completed sentence
                        sentences.add(sentence);
                        // and start a new sentence
                        sentence = new ArrayList<Tuple>();
                    } else {
                        // Add the tokens in the line to the current sentence
                        sentence.addAll(tokens);
                    }
                }
            }
        }
        // File should always end at end of a sentence
        assert(sentence.isEmpty());
        return sentences;
    }


    /** Take a list of LDC tagged input files or directories and convert them to a List of sentences
       each represented as a List of Tuple (token, tag) */
    public static List<List<Tuple>> convertToTokenLists(File[] files)
    throws IOException {
        List<List<Tuple>> sentences = new ArrayList<List<Tuple>>();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.isDirectory()) {
                if (!file.getName().contains("CHANGES.LOG")) {
                    sentences = new Parser(file).tokenLists();
                    // write these parsed sentences into a new file
                    String filename = file.getName();

                    String filepath = "";
                    if (file.getName().contains("atis")) {
                        if (orthographic)
                            filepath = "../data/atis_ortho/";
                        else
                            filepath = "../data/atis/";
                    } else {
                        if (orthographic)
                            filepath = "../data/wsj_ortho/";
                        else
                            filepath = "../data/wsj/";
                        filepath = filepath + filename.charAt(4) + filename.charAt(5) + "/";
                    }
                    filepath = filepath + filename;

                    printFormattedData(sentences, filepath);
                }
            } else {
                File[] dirFiles = file.listFiles();
                sentences.addAll(convertToTokenLists(dirFiles));
            }

        }
        return sentences;
    }

    private static Boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private static Boolean isCaps(char c) {
        return (c >= 'A' && c <= 'Z');
    }

    /**
     * Checks if the token has the feature indexed f
     * The feature index corresponds to the feature array
     * @param  f     Feature index under consideration
     * @param  token Token under consideration.
     * @return       True if token has feature f
     */
    private static Boolean hasFeature(int f, String token) {
        if (f == 0)     // contains a hyphen
            return (token.indexOf("-") != -1);
        if (f == 1)     // starts with a digit
            return isDigit(token.charAt(0));
        if (f == 2)     // starts with a caps
            return isCaps(token.charAt(0));
        if (f == 3) {   // all caps
            for (int i = 0; i < token.length(); i++) {
                if (!isCaps(token.charAt(i)))
                    return false;
            }
            return true;
        } else {    // has the suffixes indexed f
            return token.endsWith(features.get(f));
        }
    }

    /*
    Writes the parsed form of a file to a new file.
     */
    public static void printFormattedData(List<List<Tuple>> sentences, String filename)
    throws FileNotFoundException, UnsupportedEncodingException, IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "utf-8"))) {

            // for (int i = 0; i < sentences.size(); i++) {
            //     for (int j = 0; j < sentences.get(i).size(); j++) {
            //         writer.write(sentences.get(i).get(j).tok + " "
            //                      + sentences.get(i).get(j).tag + "\n");
            //     }
            //     writer.write("\n");
            // }

            for (int i = 0; i < sentences.size(); i++) {
                for (int j = 0; j < sentences.get(i).size(); j++) {
                    String token = sentences.get(i).get(j).tok.toString();
                    writer.write(token);

                    if (orthographic) {
                        for (int s = 0; s < features.size(); s++) {
                            if (hasFeature(s, token)) {
                                writer.write(" " + features.get(s));
                            }
                        }
                    }

                    writer.write(" " + sentences.get(i).get(j).tag + "\n");
                }
                writer.write("\n");
            }
        }
    }

    /** Convert LDC POS tagged files to just lists of tokens for each setences
     *  and print them out. */
    public static void main(String[] args) throws IOException {
        if (args[0].equals("ortho")) {
            orthographic = true;
        }
        else if(args[0].equals("no-ortho"))
            orthographic = false;
        else
            throw new IllegalArgumentException(
                            "First argument must be [ortho] or [no-ortho] (without braces)");
        File[] files = new File[args.length - 1];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(args[i+1]);
        }
        List<List<Tuple>> sentences =  convertToTokenLists(files);
    }
}
