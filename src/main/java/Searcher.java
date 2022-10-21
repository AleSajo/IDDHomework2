
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class Searcher {
    Path path = Paths.get("target/lucene-index");

    //Just a test method for retrieving some docs
    public void searchAllDocuments() throws IOException {
        Directory directory = FSDirectory.open(path);

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query allDocsQuery = new MatchAllDocsQuery();
        TopDocs hits = searcher.search(allDocsQuery, 10);

        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("doc-" + scoreDoc.doc + ": Title = '"+ doc.get("title") + "' ; Score = '" + scoreDoc.score +"'");
        }
    }

    public void searchByQuery() throws IOException, ParseException {
        Directory directory = FSDirectory.open(path);

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);


        QueryParser parser = new MultiFieldQueryParser(new String[] {"title", "content"}, new WhitespaceAnalyzer());;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Type here what you're looking for: ");
        Query query = parser.parse(keyboard.nextLine());
        //keyboard.close();
        TopDocs hits = searcher.search(query, 3);
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("doc-" + scoreDoc.doc + ": Title = '"+ doc.get("title") + "' ; Score = '" + scoreDoc.score +"'");
        }
    }
}
