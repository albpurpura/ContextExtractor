/**
 * Created by Alberto Purpura on 20/04/16.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class MyTokenizer {
    FileReader reader;
    LinkedHashMap<String,Object> posVocabulary;
    ArrayList<String> tokens;
    HashMap<String,Integer> wordFreqs = new HashMap<String,Integer>();
    Iterator iter;

    public MyTokenizer(String inputFilePath) throws FileNotFoundException {
        File file = new File(inputFilePath);
        Scanner inputFileReader = new Scanner(file);

        tokens = new ArrayList<String>();
        posVocabulary = new LinkedHashMap<String,Object>();
        int positionInText = 0;

        while(inputFileReader.hasNext()){
            String tWord = inputFileReader.next().toString().toLowerCase();
            if (tWord.length() > 0) {
                if (!posVocabulary.containsKey(tWord)) {
                    ArrayList<Integer> occurrences = new ArrayList<Integer>();
                    occurrences.add(positionInText);
                    posVocabulary.put(tWord,occurrences);
                } else {
                    ArrayList<Integer> occurrences = (ArrayList<Integer>) posVocabulary.get(tWord);
                    occurrences.add(positionInText);
                    posVocabulary.put(tWord,occurrences);
                }
                tokens.add(tWord);
                positionInText++;

                //compute frequency
                if(!wordFreqs.containsKey(tWord)){
                    wordFreqs.put(tWord,1);
                } else {
                    int freq = wordFreqs.get(tWord) + 1;
                    wordFreqs.replace(tWord,freq);
                }
            }//if
        }

    }//{m} Tokenizer

    
    public LinkedHashMap<String,Object> getVocabulary(){
        return posVocabulary;
    }//{m} getVocabulary

    public HashMap<String,Integer> getWordFrequencies(){ return wordFreqs; }
    /*
    * Returns an ArrayList of Strings containing the tokens of the text on which the tokenizer is created
    * one token per element of the list.
    * */
    public ArrayList<String> getTokens(){
        return tokens;
    }//{m} getTokens

    /*
    * Returns true if the tokenizer can return another token, else returns false
    * */
    public boolean hasNext(){
        return iter.hasNext();
    }//{m} hasNext

    /*
    * Returns the next token in the text
    * */
    public String getNext(){
        return (String) iter.next();
    }//{m} getNext
}//{c}MyTokenizer
