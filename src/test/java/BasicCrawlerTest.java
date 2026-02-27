import crawlers.BasicCrawler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicCrawlerTest {

    static boolean isLocal() {
        return System.getenv("CI") == null;
    }

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

    @Test @EnabledIf("isLocal")
    public void testBasicCrawlerMon() throws InterruptedException {
        BasicCrawler crawler = new BasicCrawler("https://www.mon.bg/");
        crawler.run();
        Thread.sleep(200);
    }

    @Test @EnabledIf("isLocal")
    public void testBasicCrawlerBan() throws InterruptedException {
        BasicCrawler crawler = new BasicCrawler("https://www.bas.bg/");
        crawler.run();
    }
}
