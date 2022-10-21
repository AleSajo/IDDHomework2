import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.tests.analysis.TokenStreamToDot;

public class Indexer {
    Path path = Paths.get("target/lucene-index");

    public void createIndex() throws IOException {
        Directory directory = FSDirectory.open(path);

        //Get all the names of the files present in the given file directory
        File filesDirectory = new File("src/main/resources");
        File[] files = filesDirectory.listFiles();

        //Display the names of the files
        System.out.println("Files are:");
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getName());
        }

        //Create the analyzer for the fields
        Analyzer titleAnalyzer = CustomAnalyzer.builder()
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .build();

        Map<String, Analyzer> perFieldAnalyzer = new HashMap<>();
        CharArraySet stopWords = new CharArraySet(Arrays.asList("in", "dei", "di", "il", "lo", "la", "i", "o", "e"), true);

        perFieldAnalyzer.put("title", titleAnalyzer);
        perFieldAnalyzer.put("content", new StandardAnalyzer(stopWords));

        Analyzer analyzer = new PerFieldAnalyzerWrapper(new ItalianAnalyzer(), perFieldAnalyzer);

        //Index writer
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setCodec(new SimpleTextCodec());
        IndexWriter writer = new IndexWriter(directory, config);
        writer.deleteAll();

        //Read all files content using BufferedReader
        for (File file : files) {
            String title;
            String content;
            Document document = new Document();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                while ((title = file.getName()) != null && (content = br.readLine()) != null) {
                    title = title.replace(".txt", "");
                    document.add(new TextField("title", title, Field.Store.YES));
                    document.add(new TextField("content", content, Field.Store.YES));

                    writer.addDocument(document);
                    writer.commit();
                }
                br.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        writer.close();
    }
}
