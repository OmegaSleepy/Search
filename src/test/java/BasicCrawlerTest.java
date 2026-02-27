import crawlers.BasicCrawler;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicCrawlerTest {
    PrintStream out = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            baos.write(b);          // capture
            out.write(b);   // still print to console
        }
    });

    @BeforeAll
    public void setup() {
        System.setOut(ps);
    }

    @AfterEach
    public void tearDown() {
        String captured = baos.toString();
        System.out.println("Captured: " + captured);
        assertNotEquals("", captured);
        baos.reset();
    }

    @AfterAll
    public void restoreSystemOut() {
        System.setOut(out);
    }

    @Test
    public void testBasicCrawlerMon(){
        BasicCrawler crawler = new BasicCrawler("https://www.mon.bg/");
        crawler.run();
    }

    @Test
    public void testBasicCrawlerBan(){
        BasicCrawler crawler = new BasicCrawler("https://www.bas.bg/");
        crawler.run();
    }
}
