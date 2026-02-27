package core;

import crawlers.BasicCrawler;

public class Runner {
    public static void main(String[] args) {
        BasicCrawler crawler = new BasicCrawler("https://mon.bg/");
        crawler.run();
    }
}
