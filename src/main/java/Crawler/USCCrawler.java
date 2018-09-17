package Crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.*;
import java.util.Set;
import java.util.regex.Pattern;

public class USCCrawler extends WebCrawler {
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js"
            + "|mp3|mp4|zip|gz))$");

    CrawlStat myCrawlStat;

    public USCCrawler() {
        myCrawlStat = new CrawlStat();
    }
    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && (href.startsWith("https://www.mercurynews.com/") || href.startsWith("https://mercurynews.com/"));
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        myCrawlStat.incProcessedPages();
        try {
            File file= new File ("output/root/fetch_mercurynews.csv");
            FileWriter pw;
            if (file.exists()) {
                pw = new FileWriter(file, true);
            } else {
                pw = new FileWriter(file);
            }
            StringBuilder sb = new StringBuilder();
            String url = page.getWebURL().getURL();
            sb.append(url).append(",").append(page.getStatusCode()).append('\n');
            pw.append(sb.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            myCrawlStat.incTotalLinks(links.size());
            try {
                File file= new File ("output/root/visit_mercurynews.csv");
                FileWriter pw;
                if (file.exists()) {
                    pw = new FileWriter(file, true);
                } else {
                    pw = new FileWriter(file);
                }
                StringBuilder sb = new StringBuilder();
                String url = page.getWebURL().getURL();
                sb.append(url).append(",").append(page.getContentData().length)
                        .append(",").append(htmlParseData.getOutgoingUrls().size())
                        .append(",").append(page.getContentType()).append('\n');
                pw.append(sb.toString());
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * This function is called by controller to get the local data of this crawler when job is
     * finished
     */
    @Override
    public Object getMyLocalData() {
        return myCrawlStat;
    }

    /**
     * This function is called by controller before finishing the job.
     * You can put whatever stuff you need here.
     */
    @Override
    public void onBeforeExit() {
        dumpMyData();
    }

    public void dumpMyData() {
        int id = getMyId();
        // You can configure the log to output to file
        logger.info("Crawler {} > Processed Pages: {}", id, myCrawlStat.getTotalProcessedPages());
        logger.info("Crawler {} > Total Links Found: {}", id, myCrawlStat.getTotalLinks());
        logger.info("Crawler {} > Total Text Size: {}", id, myCrawlStat.getTotalTextSize());
    }
}
