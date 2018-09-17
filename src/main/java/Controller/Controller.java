package Controller;

import Crawler.CrawlStat;
import Crawler.USCCrawler;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static edu.uci.ics.crawler4j.robotstxt.UserAgentDirectives.logger;

public class Controller {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "output/root";
        int numberOfCrawlers = 50;
        int maxDepthOfCrawling = 16;
        int maxPages = 100;
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(maxDepthOfCrawling);
        config.setMaxPagesToFetch(maxPages);
        config.setPolitenessDelay(1000);
        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("https://www.mercurynews.com/");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(USCCrawler.class, numberOfCrawlers);

        List<Object> crawlersLocalData = controller.getCrawlersLocalData();

        int totalProcessedPages = 0;
        long totalLinks = 0;
        int totalFailPages = 0;
        Map<Integer,Integer> statusMap = new HashMap<>();
        Map<Integer,Integer> fileSizeMap = new HashMap<>();
        Map<String,Integer> typeMap = new HashMap<>();
        Set<String> uniqueURLSet = new HashSet<>();
        Set<String> uniqueURLinSite = new HashSet<>();
        for (Object localData : crawlersLocalData) {
            CrawlStat stat = (CrawlStat) localData;
            totalLinks += stat.getTotalLinks();
            totalProcessedPages += stat.getTotalProcessedPages();
            statusMap.putAll(stat.getStatusMap());
            fileSizeMap.putAll(stat.getFileSizeMap());
            typeMap.putAll(stat.getTypeMap());
            uniqueURLinSite.addAll(stat.getUniqueURLInSite());
            uniqueURLSet.addAll(stat.getUniqueURL());
        }
        try {
            File file= new File ("output/root/statistics.txt");
            FileWriter pw;
            if (file.exists()) {
                pw = new FileWriter(file, true);
            } else {
                pw = new FileWriter(file);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Fetch Statistics").append('\n').append("================");
            sb.append()
            pw.append(sb.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}