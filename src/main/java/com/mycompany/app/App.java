package com.mycompany.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    final static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws IOException {

        String homePage = args[0];
        String emailPassword = args[1];
        File file = new File("Job_Postings.json");
        final WebClient client = configureClient();
        Collection<JobPosition> alreadySavedJobPostings = returnSavedJobPostings(file);

        HtmlPage page = client.getPage(homePage);
        List<HtmlElement> jobPostings = (List<HtmlElement>) page.getByXPath("//div[@class='posting']");
        EmailSender emailSender = new EmailSender(emailPassword);

        List<JobPosition> newJobPostings = jobPostings.stream()
                .map(htmlElement -> makeJobPosting(htmlElement))
                .filter(jobPosting -> !(alreadySavedJobPostings.contains(jobPosting)))
                .collect(Collectors.toList());

        newJobPostings.forEach(jobPosting -> emailSender.sendEmail(jobPosting));

        newJobPostings.forEach(jobPosting -> alreadySavedJobPostings.add(jobPosting));

        writeToJSON(file, alreadySavedJobPostings);

    }

    private static JobPosition makeJobPosting(HtmlElement htmlElement) {
        logger.info("Making job posting from htmlElement -  "  + htmlElement);

        HtmlAnchor JobHyperLink = htmlElement.getFirstByXPath(".//div[@class='posting-apply']/a");
        HtmlElement jobTitle = htmlElement.getFirstByXPath(".//a[@class='posting-title']/h5");

        JobPosition jobPosition = new JobPosition();

        jobPosition.setHyperLink(JobHyperLink.getHrefAttribute());
        jobPosition.setJobTitle(jobTitle.asText());

        logger.error("Returning job posting  -  "  + jobPosition.toString());
        return jobPosition;
    }


    private static void writeToJSON(File file, Collection<JobPosition> currentPositions) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(file, currentPositions);
    }

    private static WebClient configureClient() {
        WebClient client = new WebClient(BrowserVersion.FIREFOX_38);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        return client;
    }

    private static Collection<JobPosition> returnSavedJobPostings(File json) throws IOException {
        Collection<JobPosition> jobPositions = new HashSet<>();
        if (json.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            List<JobPosition> JobPostingsSaved = mapper.readValue(json, new TypeReference<List<JobPosition>>() {
            });
            JobPostingsSaved.forEach(jobPosition -> jobPositions.add(jobPosition));
        }
        return jobPositions;
    }
}
