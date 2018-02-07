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

public class MyGetAppLinksTask extends Task<ArrayList<String>> {

	ArrayList<String> links;
	WebDriver driver;
	String searchKey;
	String searchHref;
	boolean singleSearch = false;
	String priceFilter;
	
	public ArrayList<String> getLinks() {
		return links;
	}
	
	public void setPriceFilter(String priceFilter) {
		this.priceFilter = priceFilter;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	
	public void setSearchHref(String searchHref) {
		this.searchHref = searchHref;
	}
	
	public void setSingleSerach() {
		singleSearch = true;
	}
	
	public MyGetAppLinksTask() {
		links = new ArrayList<String>();
	}

	@Override
	protected ArrayList<String> call() throws Exception {
		if (isCancelled()) {
			//pass
			System.out.println("Something went wrong cancelled");
			return null;
		} else {
			// gather links
			String pageSource;
			if(searchHref != null) {
				//search by genre using href
				System.out.println("Called search by genre");
				pageSource = Statics.searchByGenre(driver, searchHref);
			}else {
				//search by keyword
				System.out.println("Called search by keyword");
				pageSource = Statics.searchByKeyword(driver, searchKey, priceFilter);
			}
			System.out.println("Called statics method...");
			try {
				final Document document = Jsoup.parse(pageSource);
				Elements allAppDivs = document.select("div.card-content.id-track-click.id-track-impression");
				System.out.println("Found in make links: " + allAppDivs.size() + " apps");
				for(Element appDiv : allAppDivs) {
					if(isCancelled() || Thread.currentThread().isInterrupted()) {
						break;
					}
					if(singleSearch) {
						//test the title of the app
						System.out.println("SINGLE SEARCH");
						String title = appDiv.select("a.title").text().trim();
						if(title.toUpperCase().contains(searchKey.toUpperCase())) {
							System.out.println("title");
							String appLink = appDiv.select(".card-click-target").attr("href").trim();
							if(!appLink.isEmpty()) {
								System.out.println(appLink);
								this.links.add(appLink);
							}else {
								System.out.println("Found empty link");
							}
						}else {
							//just pass without checking the app link and storing it into the arraylist
						}
					}else {
						String appLink = appDiv.select(".card-click-target").attr("href").trim();
						if(!appLink.isEmpty()) {
							System.out.println(appLink);
							this.links.add(appLink);
						}else {
							System.out.println("Found empty link");
						}
					}
				}
				driver.navigate().back();
				driver.navigate().refresh();
				return this.links;
			}catch(Exception e) {
				System.out.println("Something went wrong exception");
				return null;
			}
		}
	}

}
