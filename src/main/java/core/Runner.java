package core;

import crawlers.ConcurrentCrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.ChapterParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Runner {

    public static final String START_URL = "https://witchculttranslation.com/table-of-content/";

    public static void main(String[] args) throws InterruptedException, IOException {

        Files.createDirectories(Path.of("ReZero"));

        ConcurrentCrawler crawler = new ConcurrentCrawler(
                16,     // worker threads
                1000,    // max pages
                5,      // max concurrent HTTP requests
                Runner::fetch
        );

        crawler.start(START_URL);
    }

    private static Set<String> fetch(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("OmegaBot/0.0.3 (+https://github.com/OmegaSleepy/Search)")
                    .timeout(10_000)
                    .get();

            System.out.println(url);

            if(!url.equals(START_URL)){
                String title = doc.title();
                var result = ChapterParser.parse(title);
                System.out.println(title);

                try {
                    Files.createDirectories(Paths.get("ReZero", result.volume()));

                    Path path = Paths.get("ReZero",
                            result.volume(),
                            result.chapter() + ".txt");

                    if(!Files.exists(path)){
                        Files.createDirectories(path.getParent());
                        Files.createFile(path);

                        List<Element> body;

                        if(url.startsWith("https://eminenttranslations.com")){
                            body = doc.body().getElementsByClass("my-0");
                            body.addAll(doc.body().getElementsByTag("h1"));
                        } else {
                            body = doc.body().getElementsByClass("entry-content");
                        }
                        for (Element element : body) {
                            List<Element> paragraphs = element.getElementsByTag("p");

                            for (Element p : paragraphs) {
                                String text = p.text().trim();
                                if (!text.isEmpty()) {
                                    try {
                                        Files.write(path,
                                                (text + System.lineSeparator() + System.lineSeparator()).getBytes(),
                                                StandardOpenOption.CREATE,
                                                StandardOpenOption.APPEND);
                                    } catch (IOException e) {
                                        System.err.println("Failed to write paragraph: " + e.getMessage());
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            if(url.equals(START_URL)){
                doc.getElementById("post-35")
                        .select("a[href]")
                        .stream()
                        .map(e -> e.attr("abs:href"))
                        .filter(link -> link.startsWith("https://witchculttranslation.com/2")
                                || link.startsWith("https://kagurojp.wordpress.com/2")
                                || link.startsWith("https://eminenttranslations.com/reader/rezero-starting-life-in-another-world-wn/"))
                        .forEach(System.out::println);

                return doc.getElementById("post-35")
                        .select("a[href]")
                        .stream()
                        .map(e -> e.attr("abs:href"))
                        .filter(link -> link.startsWith("https://witchculttranslation.com/2") || link.startsWith("https://kagurojp.wordpress.com"))
                        .collect(Collectors.toSet());
            }

            return Set.of();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            return Set.of();
        }
    }
}
