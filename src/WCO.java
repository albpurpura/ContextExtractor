import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;


/**
 * Created by Alberto Purpura on 20/04/16.
 */
public class WCO {

    public static void main(String[] args)throws Exception{
        String inputFilePath = "";
        String outputFilePath = "";
        int windowSizeUpperLimit = 0;
        try{
            inputFilePath = args[0]; //"./TestData/BUNIN.txt";
            outputFilePath = args[1];
            windowSizeUpperLimit = Integer.parseInt(args[2]);
        }catch(Exception e){
            System.out.println("Error in input parameters!");
            System.out.println("Usage example: " + "\"input/file/path.txt\" " + "\"output/file/path.txt\" windowSize" );
            System.exit(1);
        }
        /*Input params*/

        MyTokenizer t = new MyTokenizer(inputFilePath);
        ArrayList<String>  tokens = t.getTokens();
        LinkedHashMap<String,Object>  vocabulary = t.getVocabulary();
        System.out.println("Vocabulary size: " + vocabulary.size());
        System.out.println("Token number: " + tokens.size());
        ArrayList<Object> wcoMatrix = ComputeCoOccurrences(windowSizeUpperLimit,tokens,vocabulary, outputFilePath);
        System.exit(0);
    }

    public static ArrayList<String[]> computeWordsRelativeWeight(int windowBound, ArrayList<String> tokens, LinkedHashMap<String,Object> vocabulary){
        ArrayList<String[]> retVal = new ArrayList<String[]>();

        List<String> words = new ArrayList<>(vocabulary.keySet());
        Collections.sort(words);
        System.out.println("Computing WCO...");

        for(String tVocWord : words){
            ArrayList<Integer> tPos = (ArrayList<Integer>) vocabulary.get(tVocWord);
            Iterator it = tPos.iterator(); //occurrences in token list of a vocabulary word
            LinkedHashMap<String, Integer[]> foundWords = new LinkedHashMap<String, Integer[]>(); //found word, array of occurrences

            while (it.hasNext()) {//scan position where it was found the current vocabulary word
                int center = (Integer) it.next();
                //look before the central word
                for(int j = center - windowBound; j < center; j++){
                    int relativePosition = j - (center - windowBound);
                    if(j>=0 && j < tokens.size()){
                        if (foundWords.get(tokens.get(j)) != null) { //if it has found that word already
                            Integer[] tFreq = foundWords.get(tokens.get(j));
                            tFreq[relativePosition]++;
                            foundWords.put(tokens.get(j), tFreq);
                        } else { //create entry in foundwords list
                            Integer[] frequencies = new Integer[2 * windowBound];
                            for (int k = 0; k < frequencies.length; k++) { frequencies[k] = 0; }//initialize array
                            frequencies[relativePosition]++;
                            foundWords.put(tokens.get(j), frequencies);
                        }
                    }
                }//for
                //loog after the central word
                for(int j = center + 1; j <= center + windowBound; j++){
                    int relativePosition = (j-(center + 1)) + windowBound;
                    if(j < tokens.size()){
                        if (foundWords.get(tokens.get(j)) != null) { //if it has found that word already
                            Integer[] tFreq = foundWords.get(tokens.get(j));
                            tFreq[relativePosition]++;
                            foundWords.put(tokens.get(j), tFreq);
                        } else { //create entry in foundwords list
                            Integer[] frequencies = new Integer[2 * windowBound];
                            for (int k = 0; k < frequencies.length; k++) { frequencies[k] = 0; }//initialize array
                            frequencies[relativePosition]++;
                            foundWords.put(tokens.get(j), frequencies);
                        }
                    }
                }//for

            }//wend

            for (Map.Entry<String, Integer[]> entry : foundWords.entrySet()) {//select words found for current tVocWord
                //build row of matrix to return
                Integer[] freqs = entry.getValue();
                String[] w1w2w = new String[3];//word1-word2-weight
                //System.out.print(tVocWord + "\t" + entry.getKey());
                int w = 0;
                for(int k = 0; k < freqs.length; k++){
                    w += freqs[k];
                    //System.out.print("\t" + freqs[k]);
                }
                w1w2w[0] = tVocWord;
                w1w2w[1] = entry.getKey();
                w1w2w[2] = w + "";
                retVal.add(w1w2w);
            }//for
        }//for on tVocWord


        return retVal;
    }


    private static ArrayList<Object> ComputeCoOccurrences(int windowBound, ArrayList<String> tokens, LinkedHashMap<String,Object> vocabulary, String outputFilePath) throws FileNotFoundException {
        boolean print = false;
        PrintWriter writer = null;
        if (outputFilePath.length()>0){
            writer = new PrintWriter(outputFilePath + "CoOccurrences.txt");
            print = true;
        }
        ArrayList<Object> retVal = new ArrayList<Object>();
        String colDelimiter  = "\t";
        List<String> words = new ArrayList<>(vocabulary.keySet());
        Collections.sort(words);
        System.out.println("Computing WCO...");

        for(String tVocWord : words){
            ArrayList<Integer> tPos = (ArrayList<Integer>) vocabulary.get(tVocWord);
            Iterator it = tPos.iterator(); //occurrences in token list of a vocabulary word
            LinkedHashMap<String, Integer[]> foundWords = new LinkedHashMap<String, Integer[]>(); //found word, array of occurrences

            while (it.hasNext()) {//scan position where it was found the current vocabulary word
                int center = (Integer) it.next();
                //look before the central word
                for(int j = center - windowBound; j < center; j++){
                    int relativePosition = j - (center - windowBound);
                    if(j>=0 && j < tokens.size()){
                        if (foundWords.get(tokens.get(j)) != null) { //if it has found that word already
                            Integer[] tFreq = foundWords.get(tokens.get(j));
                            tFreq[relativePosition]++;
                            foundWords.put(tokens.get(j), tFreq);
                        } else { //create entry in foundwords list
                            Integer[] frequencies = new Integer[2 * windowBound];
                            for (int k = 0; k < frequencies.length; k++) { frequencies[k] = 0; }//initialize array
                            frequencies[relativePosition]++;
                            foundWords.put(tokens.get(j), frequencies);
                        }
                    }
                }//for
                //loog after the central word
                for(int j = center + 1; j <= center + windowBound; j++){
                    int relativePosition = (j-(center + 1)) + windowBound;
                    if(j < tokens.size()){
                        if (foundWords.get(tokens.get(j)) != null) { //if it has found that word already
                            Integer[] tFreq = foundWords.get(tokens.get(j));
                            tFreq[relativePosition]++;
                            foundWords.put(tokens.get(j), tFreq);
                        } else { //create entry in foundwords list
                            Integer[] frequencies = new Integer[2 * windowBound];
                            for (int k = 0; k < frequencies.length; k++) { frequencies[k] = 0; }//initialize array
                            frequencies[relativePosition]++;
                            foundWords.put(tokens.get(j), frequencies);
                        }
                    }
                }//for

            }//wend
            //print results
            for (Map.Entry<String, Integer[]> entry : foundWords.entrySet()) {//select words found for current tVocWord
                //build row of matrix to return
                Integer[] freqs = entry.getValue();
                String[] row = new String[freqs.length + 2];
                for(int k = 2; k < row.length; k++){
                    row[k] = "" + freqs[k-2];
                }
                row[0] = tVocWord;
                row[1] = entry.getKey();

                for (String s: row ) {
                    //System.out.print(s + colDelimiter);
                    if(print) writer.print(s + colDelimiter);
                }
                if(print) writer.println();
                retVal.add(row);
            }//for (print results)
        }//for on tVocWord

        return retVal;
    }//{m} ComputeWordCoOccurrences

}//{c} App
