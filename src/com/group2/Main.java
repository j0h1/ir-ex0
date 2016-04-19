package com.group2;

import org.jsoup.Jsoup;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static Boolean bowIndexing = null;
    private static ArrayList<File> fileList = new ArrayList<>();
    private static int N = 2293;    // total number of files

    public static void main(String[] args) {
        System.out.println("Index with bag-of-words(1) or bi-word(2)?");

        while (bowIndexing == null) {
            Scanner scanner = new Scanner(System.in);
            String selectedIndexing = scanner.next();
            if (selectedIndexing.equals("1")) {
                bowIndexing = true;
                System.out.println("Indexing with bag-of-words...");
            } else if (selectedIndexing.equals("2")) {
                bowIndexing = false;
                System.out.println("Indexing using bi-word-index...");
            } else {
                System.out.println("Please insert either 1 or 2.");
            }
        }

        retrieveFileList(new File("Adhoc/").listFiles());

        if (!Indexer.getDictionary(bowIndexing).isEmpty()) {
            // index file persisted locally
        } else {
            // create/save index
            int docCounter = 0;
            for (File f : fileList) {
                try {
                    System.out.println("Handling Document #" + docCounter++ + " of " + N);
                    handleFile(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Indexer.saveDictionary(bowIndexing);
        }
//
//        System.out.println("Search for a topic with: #topicNr <bow or bi>");
    }

    private static void handleFile(File f) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(f.getPath()));

        StringBuilder contentBuilder = new StringBuilder();
        boolean readingText = false;

        // read line for line in document
        String line;
        String docNo = "";
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals("<DOC>")) {
                // new document started
            } else if (line.equals("</DOC>")) {
                //TODO close document?? merge posting list??????
            } else {
                String[] splitByWhiteSpace = line.split("\\s");
                for (String s : splitByWhiteSpace) {
                    if (s.startsWith("<DOCNO>")) {
                        // store document number
                        if (splitByWhiteSpace.length == 1) {
                            docNo = s.substring(s.indexOf(">"), s.lastIndexOf("<"));
                        } else {
                            docNo = splitByWhiteSpace[1];
                        }
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
                        String[] terms;
                            terms = (bowIndexing) ? Tokenizer.tonkenizeBOW(contentBuilder.toString()) :
                                    Tokenizer.tonkenizeBi(contentBuilder.toString());
                        Indexer.index(docNo, terms, bowIndexing);
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
    }

    public static void retrieveFileList(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                retrieveFileList(file.listFiles());
            } else {
                fileList.add(file);
            }
        }
    }

}
