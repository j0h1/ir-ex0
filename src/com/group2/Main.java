package com.group2;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) {
        try {
            final String fileName = "Adhoc/fbis/fb396003";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

            StringBuilder contentBuilder = new StringBuilder();
            boolean readingText = false;

            // read line for line in document
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("<DOC>")) {
                    // new document started
                    //TODO create new Document object????
                } else if (line.equals("</DOC>")) {
                    //TODO close document?? merge posting list??????
                } else {
                    String[] splitByWhiteSpace = line.split("\\s");
                    for (String s : splitByWhiteSpace) {
                        if (s.startsWith("<DOCNO>")) {
                            // store document number
                            String docNo;
                            if (splitByWhiteSpace.length == 1) {
                                docNo = s.substring(s.indexOf(">"), s.lastIndexOf("<"));
                            } else {
                                docNo = splitByWhiteSpace[1];
                            }
                            System.out.println(docNo);
                            break;
                        } else if (s.equals("<TEXT>")) {
                            // start of text to be tokenized
                            readingText = true;
                            contentBuilder = new StringBuilder();
                        }
                    }
                    if (readingText) {
                        if (line.equals("</TEXT>")) {
                            readingText = false;
                            System.out.println(contentBuilder.toString());
                            tokenizeText(contentBuilder.toString());
                        } else if (line.equals("<TEXT>")) {
                            // don't add <TEXT>
                        } else {
                            if (line.equals("<!--")) {
                                // comment, skip line
                            } else {
                                // remove HTML tags by Jsoup parsing each line within text bracket
                                contentBuilder.append(Jsoup.parse(line).text() + " ");
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void tokenizeText(String text) throws IOException {
//        yourString.replaceAll("[-+.^:,]","");
    }
}
