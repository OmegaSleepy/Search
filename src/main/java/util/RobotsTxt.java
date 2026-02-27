package util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RobotsTxt {

    private RobotsTxt(){};

    private static final Map<String, RobotInformation> cache = new ConcurrentHashMap<>();

    public static RobotInformation getInfoByUrl(String url) throws IOException, InterruptedException {
        if(cache.containsKey(url)){
            return cache.get(url);
        }
        return robotsTxtInformation(url);
    }

    private static RobotInformation robotsTxtInformation(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/robots.txt"))
                .header("User-Agent", "OmegaBot/0.0.2 (+https://github.com/OmegaSleepy/Search)")
                .GET()
                .build();

        HttpResponse response = null;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            cache.put(url, new RobotInformation(List.of(), 600));
            return new RobotInformation(List.of(), 600);
        }

        boolean isUs = false;

        List<String> bannedUrl = new ArrayList<>();

        int miliseconds = 600;

        for (String line: response.body().toString().split("\\r?\\n")) {
            line = line.trim().toLowerCase();

            if (line.startsWith("user-agent:")) {

                isUs = line.contains("*");

            } else if (isUs && line.startsWith("disallow:")) {

                String path = line.replace("disallow:", "").trim();
                if (!path.isEmpty()) bannedUrl.add(path);
            } else if (isUs && line.startsWith("crawl-delay:")) {
                miliseconds = Integer.parseInt(line.replace("crawl-delay:", "").trim())*1000;
            }
        }

        cache.put(url, new RobotInformation(bannedUrl, miliseconds));
        return new RobotInformation(bannedUrl, miliseconds);
    }
}
