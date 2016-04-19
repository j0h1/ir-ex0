package com.group2;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by j0h1 on 18.04.2016.
 */
public class Indexer {

    private static final String BOW_INDEX_NAME = "BOW_INDEX";
    private static final String BI_INDEX_NAME = "BI_INDEX";

    private static HashMap<String, Map<String, Integer>> dictionary = new HashMap<>();

    public static void index(String docNo, String[] terms, boolean bowIndexing) {
        for (String term : terms) {
            if (dictionary.get(term) == null) {
                // term not in dictionary yet
                HashMap<String, Integer> termFrequency = new HashMap<>();
                termFrequency.put(docNo, 1);
                dictionary.put(term, termFrequency);
            } else if (dictionary.get(term).get(docNo) == null) {
                // term was found in another document but wasn't found in current document
                dictionary.get(term).put(docNo, 1);
            } else {
                // term has already been found in the current document, increase term frequency
                dictionary.get(term).put(docNo, dictionary.get(term).get(docNo) + 1);
            }
        }
    }

    private static boolean indexExisting(boolean bowIndexing) {
        String indexFileName = (bowIndexing) ? BOW_INDEX_NAME : BI_INDEX_NAME;
        File indexFile = new File(indexFileName);
        return indexFile.exists();
    }

    public static void saveDictionary(boolean bowIndexing) {
        String indexFileName = (bowIndexing) ? BOW_INDEX_NAME : BI_INDEX_NAME;
        File indexFile = new File(indexFileName);
        try {
            indexFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Unable to create index file.");
            return;
        }

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(indexFile));
            for (String term : dictionary.keySet()) {
                bufferedWriter.write("<ENTRY>");
                bufferedWriter.newLine();
                bufferedWriter.write("<TERM> " + term + " </TERM>");
                bufferedWriter.newLine();
                bufferedWriter.write("<DOCS>");
                bufferedWriter.newLine();
                for (String docNo : dictionary.get(term).keySet()) {
                    bufferedWriter.write(docNo + "," + dictionary.get(term).get(docNo));
                    bufferedWriter.newLine();
                }
                bufferedWriter.write("</DOCS>");
                bufferedWriter.newLine();
                bufferedWriter.write("</ENTRY>");
                bufferedWriter.newLine();
            }
        } catch (IOException ex) {
            System.out.println("Error writing index to file.");
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.out.println("Error closing BufferedWriter.");
                }
            }
        }
    }

    public static void loadDictionary(boolean bowIndexing) {
        String indexFileName = (bowIndexing) ? BOW_INDEX_NAME : BI_INDEX_NAME;
        File indexFile = new File(indexFileName);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(indexFile));
            String line = "";

            String term = "";
            HashMap<String, Integer> temp = new HashMap<>();
            boolean parsingDocs = false;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("<ENTRY>")) {
                    temp = new HashMap<>();
                } else if (line.startsWith("<TERM>")) {
                    String[] termLineParts = line.split("\\s");
                    term = termLineParts[1].trim();
                } else if (line.equals("<DOCS>")) {
                    parsingDocs = true;
                } else if (line.equals("</DOCS>")) {
                    parsingDocs = false;
                } else if (line.equals("</ENTRY>")) {
                    dictionary.put(term, temp);
                }
                if (parsingDocs && !line.equals("<DOCS>")) {
                    String[] docInfo = line.split(",");
                    temp.put(docInfo[0], Integer.parseInt(docInfo[1]));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading dictionary file.");
        } catch (IOException e) {
            System.out.println("Error reading from dictionary file.");
        }
    }

    public static HashMap<String, Map<String, Integer>> getDictionary(boolean bowIndexing) {
        if (dictionary == null && indexExisting(bowIndexing)) {
            loadDictionary(bowIndexing);
        }
        return dictionary;
    }

}
