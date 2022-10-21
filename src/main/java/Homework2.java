import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class Homework2 {
    public static void main(String[] args) {
        Indexer i = new Indexer();
        Searcher s = new Searcher();

        //create the Index using Indexer i
        try {
            long startTime = System.currentTimeMillis();
            i.createIndex();
            long endTime = System.currentTimeMillis();
            System.out.println("\nIndexing time: " + (endTime-startTime) + "ms");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //search through the index
        int selection = -1;
        Scanner keyboard = new Scanner(System.in);
        while(selection != 0) {
            System.out.println("\nChoose an option:\n0 -> Exit\n1 -> Get all documents\n2 -> Run a query");

            String nextIntString = keyboard.nextLine(); //get the number as a single line
            selection = Integer.parseInt(nextIntString); //convert the string to an int

            switch(selection) {
                case 0:
                    return;
                case 1:
                    try {
                        s.searchAllDocuments();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 2:
                    try {
                        s.searchByQuery();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    // code block
            }
        }
    }
}
