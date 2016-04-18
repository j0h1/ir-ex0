package com.group2;

import com.group2.stemmer.EnglishStemmer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by j0h1 on 17.04.2016.
 */
public class Tokenizer {

    public static String tonkenizeBOW(String text) throws IOException {
        // case folding
        text = text.toLowerCase();

        // remove special characters
        StringBuilder stringBuilder = new StringBuilder();
        String[] tokens = text.split("\\s");
        for (String s : tokens) {
            String result = s.replaceAll("[-+.^:,*?!]", " ");
            if (!result.isEmpty()) {
                stringBuilder.append(result + " ");
            }
        }
        String textWithoutSpecialChars = stringBuilder.toString();

        // remove stop words
        String textWithoutStopWords = removeStopWords(textWithoutSpecialChars);

        EnglishStemmer stemmer = new EnglishStemmer();
        String[] cleanTokens = textWithoutStopWords.split("\\s");

        StringBuilder stemmingResult = new StringBuilder();
        for (String s : cleanTokens) {
            stemmer.setCurrent(s);
            if (stemmer.stem()) {
                stemmingResult.append(stemmer.getCurrent() + " ");
            }
        }

        return stemmingResult.toString();
    }

    public static String[] tonkenizeBi(String text) {
        //TODO perform removing of special chars
        //TODO perform case folding
        //TODO perform
        return null;
    }

    private static String removeStopWords(String text) throws IOException {
        // use default Lucene stop word set
        CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        StandardTokenizer standardTokenizer = new StandardTokenizer(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
        standardTokenizer.setReader(new StringReader(text));
        TokenStream tokenStream = standardTokenizer;

        tokenStream = new StopFilter(tokenStream, stopWords);

        StringBuilder stringBuilder = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            stringBuilder.append(term + " ");
        }
        return stringBuilder.toString();
    }

}
