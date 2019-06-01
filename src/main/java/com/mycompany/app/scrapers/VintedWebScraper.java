package com.mycompany.app.scrapers;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mycompany.app.data.JobPosition;
import com.mycompany.app.data.ScraperProperties;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class VintedWebScraper implements WebScraperConfigurations, WebScraperForJobs {

    private final static Logger logger = Logger.getLogger(VintedWebScraper.class);
    private static ScraperProperties scraperProperties;

    public VintedWebScraper(ScraperProperties scraperProperties) {
        VintedWebScraper.scraperProperties = scraperProperties;
    }

    public List<HtmlElement> getHtmlElementsDescribingJobs() throws IOException {
        final WebClient client = configureClient();
        HtmlPage page = client.getPage(scraperProperties.getAddress());
        return (List<HtmlElement>) page.getByXPath("//div[@class='posting']");
    }

    public JobPosition makeJobPosting(HtmlElement htmlElement) {
        logger.info("Making job posting from htmlElement -  " + htmlElement);

        HtmlAnchor JobHyperLink = htmlElement.getFirstByXPath(".//div[@class='posting-apply']/a");
        HtmlElement jobTitle = htmlElement.getFirstByXPath(".//a[@class='posting-title']/h5");

        JobPosition jobPosition = new JobPosition();

        jobPosition.setHyperLink(JobHyperLink.getHrefAttribute());
        jobPosition.setJobTitle(jobTitle.asText());

        logger.info("Returning job posting  -  " + jobPosition.toString());

        return jobPosition;
    }
}
