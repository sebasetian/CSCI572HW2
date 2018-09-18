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

public class Controller {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "output/root";
        int numberOfCrawlers = 30;
        int maxDepthOfCrawling = 16;
        int maxPages = 22000;
        int delay = 1;
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(maxDepthOfCrawling);
        config.setMaxPagesToFetch(maxPages);
        config.setPolitenessDelay(delay);
        config.setIncludeHttpsPages(true);
        config.setIncludeBinaryContentInCrawling(true);
        config.setResumableCrawling(true);
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
        int totalSuccessPages = 0;
        Map<String,Integer> statusMap = new HashMap<>();
        Map<Integer,Integer> fileSizeMap = new HashMap<>();
        Map<String,Integer> typeMap = new HashMap<>();
        Set<String> uniqueURLSet = new HashSet<>();
        Set<String> uniqueURLinSite = new HashSet<>();
        for (Object localData : crawlersLocalData) {
            CrawlStat stat = (CrawlStat) localData;
            totalLinks += stat.getTotalLinks();
            totalProcessedPages += stat.getTotalProcessedPages();
            totalSuccessPages += stat.getTotalSuccessPages();
            totalFailPages += stat.getTotalFailPages();
            incMap(statusMap,stat.getStatusMap());
            incMap(fileSizeMap,stat.getFileSizeMap());
            incMap(typeMap,stat.getTypeMap());
            uniqueURLinSite.addAll(stat.getUniqueURLInSite());
            uniqueURLSet.addAll(stat.getUniqueURL());
        }
        try {
            File file= new File ("output/root/CrawlReport_mercurynews.txt");
            FileWriter pw;
            if (file.exists()) {
                pw = new FileWriter(file, true);
            } else {
                pw = new FileWriter(file);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Fetch Statistics").append('\n').append("================").append('\n');
            sb.append("# fetches attempted: ").append(totalProcessedPages).append('\n');
            sb.append("# fetches succeeded: ").append(totalSuccessPages).append('\n');
            sb.append("# fetches aborted: ").append(totalProcessedPages - totalSuccessPages - totalFailPages).append('\n');
            sb.append("# fetches failed: ").append(totalFailPages).append('\n').append('\n');
            sb.append("Outgoing URLs: ").append('\n').append("================").append('\n');
            sb.append("Total URLs extracted: ").append(totalLinks + 1).append('\n');
            sb.append("# unique URLs extracted: ").append(uniqueURLSet.size()).append('\n');
            sb.append("# unique URLs within News Site: ").append(uniqueURLinSite.size()).append('\n');
            sb.append("# unique URLs outside News Site: ").append(uniqueURLSet.size() - uniqueURLinSite.size()).append('\n').append('\n');
            sb.append("Status Codes:").append('\n').append("================").append('\n');
            for (Map.Entry<String,Integer> en: statusMap.entrySet()) {
                sb.append(en.getKey()).append(" :").append(en.getValue()).append('\n');
            }
            sb.append('\n');
            sb.append("File Sizes:").append('\n').append("================").append('\n');
            for (Map.Entry<Integer,Integer> en: fileSizeMap.entrySet()) {
                switch (en.getKey()) {
                    case 0 :
                        sb.append("< 1KB").append(" :").append(en.getValue()).append('\n');
                        break;
                    case 1 :
                        sb.append("1KB ~ <10KB").append(" :").append(en.getValue()).append('\n');
                        break;
                    case 10:
                        sb.append("10KB ~ <100KB").append(" :").append(en.getValue()).append('\n');
                        break;
                    case 100:
                        sb.append("100KB ~ <1MB").append(" :").append(en.getValue()).append('\n');
                        break;
                    default:
                        sb.append(">=1MB").append(" :").append(en.getValue()).append('\n');
                }

            }
            sb.append('\n');
            sb.append("Content Types:").append('\n').append("================").append('\n');
            for (Map.Entry<String,Integer> en: typeMap.entrySet()) {
                sb.append(en.getKey()).append(" :").append(en.getValue()).append('\n');
            }
            pw.append(sb.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static <T> void incMap(Map<T,Integer> reducer,Map<T,Integer> map) {
        for (Map.Entry<T,Integer> en:map.entrySet()) {
            reducer.put(en.getKey(),reducer.getOrDefault(en.getKey(),0) + en.getValue());
        }
    }
}