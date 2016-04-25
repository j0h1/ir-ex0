package com.group2;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by j0h1 on 18.04.2016.
 */
public class Indexer {

    private static Indexer indexer;
    private File indexDirectory;

    private static final String BOW_INDEX_NAME = "BOW_INDEX";
    private static final String BI_INDEX_NAME = "BI_INDEX";

    private static HashSet<String> dictionary = new HashSet<>();
    private static HashMap<String, Integer> frequencyMapping = new HashMap<>();

    private Indexer(boolean bowIndexing) {
        String indexFileName = (bowIndexing) ? BOW_INDEX_NAME : BI_INDEX_NAME;
        indexDirectory = new File(indexFileName);
        if (indexDirectory.exists()) {
            cleanDirectory(indexDirectory);
        }
        indexDirectory.mkdir();
    }

    public static Indexer getInstance(boolean bowIndexing) {
        if (indexer == null) {
            indexer = new Indexer(bowIndexing);
            return indexer;
        }
        return indexer;
    }

    private void cleanDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                cleanDirectory(file);
            } else {
                file.delete();
            }
        }
    }

    public void index(String docNo, String[] terms, boolean bowIndexing) {
        for (String term : terms) {
            if (!dictionary.contains(term)) {
                frequencyMapping.put(term, 1);
                dictionary.add(term);

                File termFile = new File(indexDirectory.getPath() + "/" + term);
                try {
                    termFile.createNewFile();
                } catch (IOException e) {
                }
            } else {
                if (frequencyMapping.get(term) == null) {
                    frequencyMapping.put(term, 1);
                } else {
                    // term has already been found in the current document, increase term frequency
                    frequencyMapping.put(term, frequencyMapping.get(term) + 1);
                }
            }
        }
        persistFrequencyMapping(docNo);
        frequencyMapping.clear();
    }

    private void persistFrequencyMapping(String docNo) {
        for (String term : frequencyMapping.keySet()) {
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(indexDirectory.getPath() + "/" + term, true));
                bufferedWriter.newLine();
                bufferedWriter.write(docNo + "," + frequencyMapping.get(term));
            } catch (IOException ex) {
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    public void saveIndex(boolean bowIndexing) {
        BufferedWriter bufferedWriter = null;
        BufferedWriter bufferedOffsetWriter = null;
//        try {
//            bufferedWriter = new BufferedWriter(new FileWriter(indexFile));

//            for (String term : dictionary.keySet()) {

//                bufferedWriter.write("<ENTRY>");
//                bufferedWriter.newLine();
//                bufferedWriter.write("<TERM> " + term + " </TERM>");
//                bufferedWriter.newLine();
//                bufferedWriter.write("<DOCS>");
//                bufferedWriter.newLine();
//                for (String docNo : dictionary.get(term).keySet()) {
//                    bufferedWriter.write(docNo + "," + dictionary.get(term).get(docNo));
//                    bufferedWriter.newLine();
//                }
//                bufferedWriter.write("</DOCS>");
//                bufferedWriter.newLine();
//                bufferedWriter.write("</ENTRY>");
//                bufferedWriter.newLine();
//            }
//        } catch (IOException ex) {
//            System.out.println("Error writing index to file.");
//        } finally {
//            if (bufferedWriter != null) {
//                try {
//                    bufferedWriter.close();
//                } catch (IOException e) {
//                    System.out.println("Error closing BufferedWriter.");
//                }
//            }
//        }
    }

//    public static void saveDictionary(boolean bowIndexing) {
//        String indexFileName = (bowIndexing) ? BOW_INDEX_NAME : BI_INDEX_NAME;
//        File indexFile = new File(indexFileName);
//        try {
//            indexFile.createNewFile();
//        } catch (IOException e) {
//            System.out.println("Unable to create index file.");
//            return;
//        }
//
//        BufferedWriter bufferedWriter = null;
//        try {
//            bufferedWriter = new BufferedWriter(new FileWriter(indexFile));
//            for (String term : dictionary.keySet()) {
//                bufferedWriter.write("<ENTRY>");
//                bufferedWriter.newLine();
//                bufferedWriter.write("<TERM> " + term + " </TERM>");
//                bufferedWriter.newLine();
//                bufferedWriter.write("<DOCS>");
//                bufferedWriter.newLine();
//                for (String docNo : dictionary.get(term).keySet()) {
//                    bufferedWriter.write(docNo + "," + dictionary.get(term).get(docNo));
//                    bufferedWriter.newLine();
//                }
//                bufferedWriter.write("</DOCS>");
//                bufferedWriter.newLine();
//                bufferedWriter.write("</ENTRY>");
//                bufferedWriter.newLine();
//            }
//        } catch (IOException ex) {
//            System.out.println("Error writing index to file.");
//        } finally {
//            if (bufferedWriter != null) {
//                try {
//                    bufferedWriter.close();
//                } catch (IOException e) {
//                    System.out.println("Error closing BufferedWriter.");
//                }
//            }
//        }
//    }

//    public void loadDictionary(boolean bowIndexing) {
//        String indexFileName = (bowIndexing) ? BOW_INDEX_NAME : BI_INDEX_NAME;
//        File indexFile = new File(indexFileName);
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new FileReader(indexFile));
//            String line = "";
//
//            String term = "";
//            HashMap<String, Integer> temp = new HashMap<>();
//            boolean parsingDocs = false;
//            while ((line = bufferedReader.readLine()) != null) {
//                if (line.equals("<ENTRY>")) {
//                    temp = new HashMap<>();
//                } else if (line.startsWith("<TERM>")) {
//                    String[] termLineParts = line.split("\\s");
//                    term = termLineParts[1].trim();
//                } else if (line.equals("<DOCS>")) {
//                    parsingDocs = true;
//                } else if (line.equals("</DOCS>")) {
//                    parsingDocs = false;
//                } else if (line.equals("</ENTRY>")) {
//                    dictionary.put(term, temp);
//                }
//                if (parsingDocs && !line.equals("<DOCS>")) {
//                    String[] docInfo = line.split(",");
//                    temp.put(docInfo[0], Integer.parseInt(docInfo[1]));
//                }
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("Error loading dictionary file.");
//        } catch (IOException e) {
//            System.out.println("Error reading from dictionary file.");
//        }
//    }

}
