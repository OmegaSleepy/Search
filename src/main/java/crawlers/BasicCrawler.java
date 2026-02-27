package crawlers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class BasicCrawler implements Runnable {

    private final String url;
    public BasicCrawler(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void run() {
        Connection connection = Jsoup.connect(url);
        connection.header("User-Agent", "OmegaBot/0.0.1 (+https://github.com/OmegaSleepy/Search");
        connection.header("Accept", "text/html");

        try {
            Document doc = connection.get();

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                System.out.println(link.attr("href"));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
