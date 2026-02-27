package crawlers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.RobotsTxt;

import java.io.IOException;
import java.util.List;

public class RespectableCrawler implements Runnable {

    private final String url;
    public RespectableCrawler(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void run() {
        Connection connection = Jsoup.connect(url);
        connection.header("User-Agent", "OmegaBot/0.1 (+https://github.com/OmegaSleepy/Search");
        connection.header("Accept", "text/html");

        try {
            Document doc = connection.get();

            List<String> bannedUrl = RobotsTxt.getInfoByUrl(url).bannedUrl();

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                if (!link.attr("href").startsWith(url)) continue;
                if (link.attr("href").startsWith("#")) continue;
                if (link.attr("href").startsWith("/?")) continue;
                if (bannedUrl.contains(link.attr("href"))) continue;
                System.out.println(link.attr("href"));
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
