package com.mycompany.app.scripts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mycompany.app.data.JobPosition;
import com.mycompany.app.data.EmailProperties;
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

    final static Logger logger = Logger.getLogger(JobScrapper.class);

    private static EmailProperties emailProperties;
    private static EmailSender emailSender;


    public JobScrapper(EmailProperties emailProperties,
                       EmailSender emailSender) {
        this.emailProperties = emailProperties;
        this.emailSender = emailSender;
    }

    void scanForChanges() throws IOException {
        logger.info("scanForChanges is bein run");

        File file = new File(emailProperties.getFilename());
        final WebClient client = configureClient();
        Collection<JobPosition> alreadySavedJobPostings = returnSavedJobPostings(file);

        HtmlPage page = client.getPage(emailProperties.getAddress());
        List<HtmlElement> jobPostings = (List<HtmlElement>) page.getByXPath("//div[@class='posting']");

        List<JobPosition> newJobPostings = jobPostings.stream()
                .map(htmlElement -> makeJobPosting(htmlElement))
                .filter(jobPosting -> !(alreadySavedJobPostings.contains(jobPosting)))
                .collect(Collectors.toList());

        newJobPostings.forEach(emailSender::sendEmail);

        alreadySavedJobPostings.addAll(newJobPostings);

        writeToJSON(file, alreadySavedJobPostings);

    }

    private JobPosition makeJobPosting(HtmlElement htmlElement) {
        logger.info("Making job posting from htmlElement -  "  + htmlElement);

        HtmlAnchor JobHyperLink = htmlElement.getFirstByXPath(".//div[@class='posting-apply']/a");
        HtmlElement jobTitle = htmlElement.getFirstByXPath(".//a[@class='posting-title']/h5");

        JobPosition jobPosition = new JobPosition();

        jobPosition.setHyperLink(JobHyperLink.getHrefAttribute());
        jobPosition.setJobTitle(jobTitle.asText());

        logger.info("Returning job posting  -  "  + jobPosition.toString());

        return jobPosition;
    }


    private void writeToJSON(File file, Collection<JobPosition> currentPositions) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(file, currentPositions);
    }

    private WebClient configureClient() {
        WebClient client = new WebClient(BrowserVersion.FIREFOX_38);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        return client;
    }

    private Collection<JobPosition> returnSavedJobPostings(File json) throws IOException {
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
