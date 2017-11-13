package com.yourcompany;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

public class MyGetAppLinksTask extends Task {

	ArrayList<String> links;
	WebDriver driver;
	String searchKey;
	
	public ArrayList<String> getLinks() {
		return links;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	
	public MyGetAppLinksTask() {
		links = new ArrayList<String>();
	}

	@Override
	protected Object call() throws Exception {
		if (isCancelled()) {
			//pass
			return null;
		} else {
			// gather links
			String pageSource = Statics.search(driver, searchKey);
			try {
				final Document document = Jsoup.parse(pageSource);
				Elements allAppDivs = document.select("div.card-content.id-track-click.id-track-impression");
				System.out.println("Found in make links: " + allAppDivs.size() + " apps");
				for(Element appDiv : allAppDivs) {
					if(isCancelled() || Thread.currentThread().isInterrupted()) {
						break;
					}
					String appLink = appDiv.select(".card-click-target").attr("href").trim();
					if(!appLink.isEmpty()) {
						System.out.println(appLink);
						this.links.add(appLink);
					}else {
						System.out.println("Found empty link");
					}
				}
				return this.links;
			}catch(Exception e) {
				return null;
			}finally {
				this.cancel();
			}
		}
	}

}
