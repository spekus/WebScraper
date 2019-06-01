package com.mycompany.app.scrapers;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

public interface WebScraperConfigurations {
    default WebClient configureClient() {
        WebClient client = new WebClient(BrowserVersion.FIREFOX_38);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        return client;
    }
}
