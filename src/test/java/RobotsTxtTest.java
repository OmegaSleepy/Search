import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import util.RobotsTxt;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RobotsTxtTest {

    @Test
    public void testRobotsTxtInformation() {
        try {
            var result = RobotsTxt.getInfoByUrl("https://en.wikipedia.org");

            assertEquals(600, result.millisecondTimeout());
            assertNotEquals(List.of(),result.bannedUrl());
            assertEquals(result.bannedUrl().size(), RobotsTxt.getInfoByUrl("https://en.wikipedia.org").bannedUrl().size());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRobotsTxtMon(){
        try {
            var result = RobotsTxt.getInfoByUrl("https://mon.bg");

            assertEquals(600, result.millisecondTimeout());
            assertEquals(List.of(),result.bannedUrl());
            assertEquals(result.bannedUrl().size(), RobotsTxt.getInfoByUrl("https://mon.bg").bannedUrl().size());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
