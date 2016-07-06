import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created by Alberto Purpura on 04/05/16.
 */
public class ContextExtractor {
    //private ArrayList<Object> wcoMatrix;
    private HashMap<String, ArrayList<String[]>> wordContexts ;
    private static HashMap<String,Integer> wordFrequencies;
    private LinkedHashMap<String,Object> vocabulary;
    ArrayList<String> tokens;
    private MyTokenizer t;
    private int weightThreshold = 1;

    public ContextExtractor(String inputFilePath,int windowBound ) throws Exception {
        double weightAvg = 0;

        t = new MyTokenizer(inputFilePath);
        vocabulary = t.getVocabulary();
        wordFrequencies = t.getWordFrequencies();
        tokens = t.getTokens();
        ArrayList<String[]> w1w2w = WCO.computeWordsRelativeWeight(windowBound, tokens, vocabulary);
        wordContexts = new HashMap<>();
        for(String[] wCoupleWithWeight : w1w2w){

            String key = wCoupleWithWeight[0];
            String[] weightedWord = new String[2];
            weightedWord[0] = wCoupleWithWeight[1];
            weightedWord[1] = wCoupleWithWeight[2];
            weightAvg +=  Double.parseDouble(wCoupleWithWeight[2]);
            if(wordContexts.containsKey(key)){
                ArrayList<String[]>  coOccurrences = wordContexts.get(key);
                coOccurrences.add(weightedWord);
                wordContexts.replace(key,coOccurrences);
            } else{
                ArrayList<String[]> coOccurrences = new ArrayList<String[]>();
                coOccurrences.add(weightedWord);
                wordContexts.put(key,coOccurrences);
            }
        }//for
        if (w1w2w.size() != 0) {
            weightThreshold = (int) weightAvg/w1w2w.size();
        } else{
            weightThreshold = 1;
        }
        System.out.println("Hypothetical weight threshold could be: " + weightThreshold);
    }//[m] ContextExtractor

    //for italian language
    private boolean isNotSignificant(String value){
        String token = value.toLowerCase();
        if (token.compareTo("with") == 0
                || token.compareTo("a") == 0
                || token.compareTo("an") == 0
                || token.compareTo("than") == 0
                || token.compareTo("then") == 0
                || token.compareTo("now") == 0
                || token.compareTo("a") == 0
                || token.compareTo("where") == 0
                || token.compareTo("who") == 0
                || token.compareTo("what") == 0
                || token.compareTo("whom") == 0
                || token.compareTo("which") == 0
                || token.compareTo("i") == 0
                || token.compareTo("you") == 0
                || token.compareTo("me") == 0
                || token.compareTo("he") == 0
                || token.compareTo("she") == 0
                || token.compareTo("it") == 0
                || token.compareTo("we") == 0
                || token.compareTo("they") == 0
                || token.compareTo("is") == 0
                || token.compareTo("were") == 0
                || token.compareTo("are") == 0
                || token.compareTo("was") == 0
                || token.compareTo("have") == 0
                || token.compareTo("had") == 0
                || token.compareTo("be") == 0
                || token.compareTo("my") == 0
                || token.compareTo("mine") == 0
                || token.compareTo("your") == 0
                || token.compareTo("yours") == 0
                || token.compareTo("his") == 0
                || token.compareTo("her") == 0
                || token.compareTo("hers") == 0
                || token.compareTo("its") == 0
                || token.compareTo("their") == 0
                || token.compareTo("so") == 0
                || token.compareTo("or") == 0
                || token.compareTo("and") == 0) {
            return true;
        }
        return false;
    }
    private boolean isToIgnore(String value, int weight){
        int thresholdValue = (tokens.size()/vocabulary.size()) * 10 ; //need to enhance accuracy here it is the average * 10
        //ignore if:
        // weight is too low OR freq too high OR is not significant
        if (!weightIsEnough(weight) || (wordFrequencies.get(value)>thresholdValue || isNotSignificant(value)) ) return true;
        return false;
    }

    private boolean weightIsEnough(int weight){
        if(weight >= weightThreshold ){
            return true;
        }
        return false;
    }


    private String getContexts(String outputFilePath) throws FileNotFoundException, UnsupportedEncodingException {
        String oFileName = outputFilePath + "CONTEXTS.txt";
        PrintWriter writer = new PrintWriter(oFileName, "UTF-8");
        System.out.println ("Extracting contexts...");
        int p = 0;
        int thresholdValue = 1;
        if (vocabulary.size()!= 0 ) thresholdValue = tokens.size()/vocabulary.size() * 5 ; //need to enhance accuracy here it is the average * 10
        for (String tVocWord : vocabulary.keySet()){
            if(!(wordFrequencies.get(tVocWord)>thresholdValue || isNotSignificant(tVocWord))) { //ignore useless vocabulary words
                HashMap<String, ArrayList<String[]>> contextsSet = new HashMap<String, ArrayList<String[]>>();
                if (p % 10000 == 0) {
                    System.out.println("Processing word " + p + " of " + vocabulary.size());
                }
                p++;
                for (String[] wWord : wordContexts.get(tVocWord)) {
                    String tWord = wWord[0];
                    int weight = Integer.parseInt(wWord[1]);
                    if (!isToIgnore(tWord, weight)) {
                        if (!contextsSet.containsKey(tVocWord)) {
                            ArrayList<String[]> tList = new ArrayList<>();
                            tList.add(new String[]{tWord, weight + ""});
                            contextsSet.put(tVocWord, tList);
                        } else {
                            ArrayList<String[]> tList = contextsSet.get(tVocWord);
                            tList.add(new String[]{tWord, weight + ""});
                            contextsSet.replace(tVocWord, tList);
                        }
                    }
                }//for (tWord)

                if (null != contextsSet.get(tVocWord)) {
                    writer.println("****Context of word \"" + tVocWord + "\":****");
                    for (String[] wWord : contextsSet.get(tVocWord)) {
                        String tWord = wWord[0];
                        writer.println("\"" + tWord + "\"" + " with frequency: " + wordFrequencies.get(tWord) + " and weight: " + wWord[1]);
                    }
                    writer.println();
                }
            }
        }//for (tVocWord)
        return oFileName;
    }


    public static void main(String[] args) throws Exception{
        String inputFilePath = args[0];
        String outputFilePath = args[1];
        int windowBound = Integer.parseInt(args[2]);
        String sentenceDelim = "$$$";
        boolean printfile = true;
        String coOccMatrixFilePath = "/Users/albertopurpura/GitHub/wikiDumpOUTCoOcc.txt";
        coOccMatrixFilePath = ""; //Used FOR TESTING
        ContextExtractor c = new ContextExtractor(inputFilePath, windowBound);

        String s = c.getContexts(outputFilePath);
        System.out.println("File created at: \"" + s + "\"");
    }

}//[c] ContextExtractor
