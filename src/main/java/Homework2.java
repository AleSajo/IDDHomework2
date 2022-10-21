import java.io.IOException;

public class Homework2 {
    public static void main(String[] args) {
        Indexer i = new Indexer();

        try {
            i.createIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
