package Crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.crawler.exceptions.PageBiggerThanMaxSizeException;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.*;
import java.util.regex.Pattern;

public class USCCrawler extends WebCrawler {
    private static Pattern FILTERS = Pattern.compile(".*(\\.(" +
            "css|js" +
            "|mid|mp2|mp3|mp4|wav|wma|flv|mpe?g" +
            "|avi|mov|mpeg|ram|m4v|wmv|rm|smil" +
            "|pub|xls|xlsx|vsd|ppt|pptx" +
            "|swf" +
            "|zip|rar|gz|bz2|7z|bin" +
            "|xml|txt|java|c|cpp|exe|git|xml; charset=UTF-8|rss+xml; charset=UTF-8|x-javascript" +
            "))$");

    private CrawlStat myCrawlStat;
    private final CrawlConfig config = new CrawlConfig();
    private PageFetcher pageFetcher = new PageFetcher(config);



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
        myCrawlStat.incUniqueURL(href);
        try {
            File file = new File("output/root/urls_mercurynews.csv");
            FileWriter pw;
            if (file.exists()) {
                pw = new FileWriter(file, true);
            } else {
                pw = new FileWriter(file);
            }
            StringBuilder sb = new StringBuilder();
            if (href.startsWith("https://www.mercurynews.com/") ||
                    href.startsWith("http://www.mercurynews.com/")) {
                myCrawlStat.incUniqueURLinsite(href);
                sb.append(href).append(",").append("OK").append('\n');
            } else {
                sb.append(href).append(",").append("N_OK").append('\n');
            }
            pw.append(sb.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return !FILTERS.matcher(href).matches() &&
                (href.startsWith("https://www.mercurynews.com/") ||
                        href.startsWith("http://www.mercurynews.com/"));

    }


    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        if (statusCode >= 300) {
            myCrawlStat.incStatusCodes(statusCode + " " + statusDescription);
            myCrawlStat.incFailPages();
            try {
                File file = new File ("output/root/fetch_mercurynews.csv");
                FileWriter pw;
                if (file.exists()) {
                    pw = new FileWriter(file, true);
                } else {
                    pw = new FileWriter(file);
                }
                StringBuilder sb = new StringBuilder();
                String url = webUrl.getURL();
                sb.append(url).append(",").append(statusCode).append('\n');
                pw.append(sb.toString());
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            myCrawlStat.incProcessedPages();
        }


    }
    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        if (!(page.getContentType().contains("image")||page.getContentType().contains("html")
        ||page.getContentType().contains("pdf")||page.getContentType().contains("doc"))) {
            return;
        }
        myCrawlStat.incProcessedPages();
        myCrawlStat.incSuccessPages();
        myCrawlStat.incStatusCodes(page.getStatusCode() + " " + "OK");
        try {
            File file = new File ("output/root/fetch_mercurynews.csv");
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
            myCrawlStat.incTotalLinks(page.getParseData().getOutgoingUrls().size());
            sb.append(url).append(",").append(page.getContentData().length)
                    .append(",").append(page.getParseData().getOutgoingUrls().size())
                    .append(",").append(page.getContentType()).append('\n');
            pw.append(sb.toString());
            pw.close();
            myCrawlStat.incFileSize(page.getContentData().length);
            myCrawlStat.incType(page.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
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

}
