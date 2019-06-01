package com.mycompany.app.scripts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.mycompany.app.data.JobPosition;
import com.mycompany.app.data.ScraperProperties;
import com.mycompany.app.scrapers.VintedWebScraper;
import com.mycompany.app.scrapers.WebScraperForJobs;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class JobScrapper {

    private final static Logger logger = Logger.getLogger(JobScrapper.class);

    private static ScraperProperties scraperProperties;
    private static EmailSender emailSender;
    private static WebScraperForJobs jobScrapper;

    public JobScrapper(ScraperProperties scraperProperties,
                       EmailSender emailSender,
                       VintedWebScraper jobScrapper) {
        JobScrapper.scraperProperties = scraperProperties;
        JobScrapper.emailSender = emailSender;
        JobScrapper.jobScrapper = jobScrapper;
    }

    /**
     * Method checks if there are any new open Job positions on Vinted compared to previous run.
     * If there is some new data it send an email to email address specified in application.yaml
     * after that it updates JSON file to include new data, so that during next scheduled run
     * no emails are sent for the data which user already got email for.
     * configuration for file name, password, emails, scrapping target page is held on application.yaml
     *
     * @throws IOException
     */

    void checkForChanges() throws IOException {
        logger.info("checkForChanges is being run");
        File file = new File(scraperProperties.getFilename());

        Collection<JobPosition> alreadySavedJobPostings = returnSavedJobPostings(file);

        List<HtmlElement> jobPostingElements = jobScrapper.getHtmlElementsDescribingJobs();

        List<JobPosition> newJobPostings = jobPostingElements.stream()
                .map(htmlElement -> jobScrapper.makeJobPosting(htmlElement))
                .filter(jobPosting -> !(alreadySavedJobPostings.contains(jobPosting)))
                .collect(Collectors.toList());

        newJobPostings.forEach(emailSender::sendEmail);

        alreadySavedJobPostings.addAll(newJobPostings);

        writeToJSON(file, alreadySavedJobPostings);
    }

    private void writeToJSON(File file, Collection<JobPosition> currentPositions) throws IOException {
        logger.info("writeToJSON is being run");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(file, currentPositions);
    }

    private Collection<JobPosition> returnSavedJobPostings(File json) throws IOException {
        logger.info("returnSavedJobPostings is being run");
        Collection<JobPosition> jobPositions = new HashSet<>();
        if (json.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            List<JobPosition> JobPostingsSaved = mapper.readValue(json, new TypeReference<List<JobPosition>>() {
            });
            jobPositions.addAll(JobPostingsSaved);
        }
        return jobPositions;
    }
}
