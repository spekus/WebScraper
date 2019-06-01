package com.mycompany.app.scrapers;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.mycompany.app.data.JobPosition;

import java.io.IOException;
import java.util.List;

public interface WebScraperForJobs {
    JobPosition makeJobPosting(HtmlElement htmlElement);

    List<HtmlElement> getHtmlElementsDescribingJobs() throws IOException;
}
