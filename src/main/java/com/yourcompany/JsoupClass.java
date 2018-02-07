package com.yourcompany;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Eligijus
 */
class JsoupClass {

    static Elements allApps = null;

    static ArrayList<App> findSingle(String pageSource, String appName) {
        if (allApps != null) {
            //return list of apps
            return findApps(allApps, appName);
        } else {
            final Document document = Jsoup.parse(pageSource);
            //parse and get all app divs
            Elements elementList = document.select("div.card-content.id-track-click.id-track-impression");
            //return list of apps
            return findApps(elementList, appName);
        }
    }

    static void setList(Elements list) {
        allApps = list;
    }
    
    static ArrayList<App> findMultiple(String pageSource) {
        if (allApps != null) {
            return makeApps(allApps);
        } else {
            final Document document = Jsoup.parse(pageSource);
            //parse and get all app divs
            Elements elementList = document.select("div.card-content.id-track-click.id-track-impression");
            return makeApps(elementList);
        }
    }

    private static ArrayList<App> findApps(Elements elementList, String appName) {
        ArrayList<App> list = new ArrayList();
        for (Element posibleElement : elementList) {
            if (posibleElement.select(".title").text().toLowerCase().trim().contains(appName.toLowerCase().trim())) {
                String title = posibleElement.select(".title").text().trim();
                String subtitle = posibleElement.select(".subtitle").text().trim();
                String link = posibleElement.select(".card-click-target").attr("href").trim();
                String price = posibleElement.select("button.price.buy.id-track-click.id-track-impression").text().split(" ")[0].trim();
                App app = buildApp(posibleElement);
                list.add(app);
            }
        }
        elementList = null;
        return list;
    }

    private static App buildApp(Element posibleElement) {
    		try {
    			return null;
    		}catch(Exception e) {
    			return null;
    		}
    		
	}

	private static ArrayList<App> makeApps(Elements elementList) {
        ArrayList<App> list = new ArrayList();
        for (Element posibleElement : elementList) {
            String title = posibleElement.select(".title").text().trim();
            String subtitle = posibleElement.select(".subtitle").text().trim();
            String link = posibleElement.select(".card-click-target").attr("href").trim();
            String price = posibleElement.select("button.price.buy.id-track-click.id-track-impression").text().split(" ")[0].trim();
           //App app = new App(title, subtitle, price, link);
            //list.add(app);
        }
        elementList = null;
        return list;
    }
    
    public static App getSingleAppInfo(String link) throws IOException {
        //ArrayList<Pair> resultList = new ArrayList();
    		link = "https://play.google.com" + link;
        Document document = Jsoup.connect(link).get();
        App app = new App();
        Elements elements = document.select("div.details-wrapper.apps.square-cover.id-track-partial-impression.id-deep-link-item");
        if(!elements.isEmpty()){
            Element element = elements.first();
            String title = elements.select(".id-app-title").text();
            if(title == null || title.isEmpty()) {
            		app.setTitle("Unknown");
            }else {
            	 	app.setTitle(title);
            }

            String ratingCount = element.select(".rating-count").text().trim();
            if(ratingCount == null || ratingCount.isEmpty()) {
            		app.setReviews("Unknown");
            }else {
            		app.setReviews(ratingCount);
            }

            String subtitle = element.select(".document-subtitle.primary").text();
            if(subtitle == null || subtitle.isEmpty()) {
        		app.setSubtitle("Unknown");
	        }else {
	        		app.setSubtitle(subtitle);
	        }

            //generate genres
            ArrayList<String> genre = new ArrayList<String>();
            Elements genres = element.select(".document-subtitle.category");
            if(genres == null || genres.isEmpty()){
            		genre.add("Unknown");
            		app.setGenre(genre);
            }else {
	            for(int i=0; i<genres.size(); i++) {
	            		genre.add(genres.get(i).text());
	            }
	            app.setGenre(genre);
            }
                        
            String price = element.select(".price.buy.id-track-click.id-track-impression").text();
            
            if(price == null || price.isEmpty() || price.equals("Install")) {
            		app.setFree(true);
            }else {
            		app.setPrice(price.split(" ")[0]);
            }

            String description = element.select(".show-more-content.text-body").text();
            if(description == null || description.isEmpty()) {
            		app.setDescription("None");
            }else {
            		app.setDescription(description);
            }
            
            //change div to reviews div
            element = document.select("div.details-section.reviews").first();
            if(element == null || element.select(".score") == null) {
            		app.setScore("None");
            		app.setScore1("None");
            		app.setScore2("None");
            		app.setScore3("None");
            		app.setScore4("None");
            		app.setScore5("None");
            }else {
	            String score = element.select(".score").text();
	            
	            app.setScore(score);
	            //resultList.add(new Pair("score", score));
	            app.setScore5(element.select(".rating-bar-container.five").text().replaceFirst("5 ", ""));
	            app.setScore4(element.select(".rating-bar-container.four").text().replaceFirst("4 ", ""));
	            app.setScore3(element.select(".rating-bar-container.three").text().replaceFirst("3 ", ""));
	            app.setScore2(element.select(".rating-bar-container.two").text().replaceFirst("2 ", ""));
	            app.setScore1(element.select(".rating-bar-container.one").text().replaceFirst("1 ", ""));
            }
            
            //get recent changes
            //change div to reviews div
            element = document.select("div.details-section.whatsnew").first();
            String recent_changes = "";
            if(element != null){
                for(Element recent_change: element.select(".recent-change")){
                    if(recent_changes.isEmpty()){
                        recent_changes += recent_change.text();
                    }else{
                        recent_changes += "\n"+recent_change.text();
                    }
                }
                app.setWhatsnew(recent_changes);
                //resultList.add(new Pair("recent_changes", recent_changes));
            }else{
                recent_changes = "None";
                app.setWhatsnew(recent_changes);
                //resultList.add(new Pair("recent_changes", recent_changes));
            }
            //fetch additional info
            //change div to reviews div
            element = document.select("div.details-section.metadata").first();
            String updated = null;
            String size = null;
            String installs = null;
            String current_version = null;
            String requires_android = null;
            String in_app_products = null;
            String interactive_elements = null;
            for(Element meta_info: element.select(".meta-info")){
                String key = meta_info.select(".title").text().trim();
                if(key.equals("Updated")){
                    updated = meta_info.select(".content").text().trim();
                }else if(key.equals("Size")){
                    size = meta_info.select(".content").text().trim(); 
                }else if(key.equals("Installs")){
                    installs = meta_info.select(".content").text().trim();
                }else if(key.equals("Current Version")){
                    current_version = meta_info.select(".content").text().trim();
                }else if(key.equals("Requires Android")){
                    requires_android = meta_info.select(".content").text().trim();
                }else if(key.equals("In-app Products")){
                    in_app_products = meta_info.select(".content").text().trim();
                }else if(key.equals("Interactive Elements")){
                    interactive_elements = meta_info.select(".content").text().trim();
                }
            }
            app.setLastUpdate(updated);
            app.setSize(size);
            app.setInstalls(installs);
            app.setCurrentVersion(current_version);
            app.setAndroidReq(requires_android);
            app.setInAppPurchases(in_app_products);
            app.setInteractiveElements(interactive_elements);
            
            //get content rating info
            ArrayList<String> content_rating = new ArrayList<>();
            for(Element meta_info_other: element.select(".meta-info.contains-text-link")){
                String key = meta_info_other.select(".title").text().trim();
                 if(key.equals("Content Rating")){
                    Elements content_rating_elements = meta_info_other.select(".content");
                    for(Element rating: content_rating_elements) {
                    		if(!rating.text().trim().equals("Learn more")) {
                    			content_rating.add(rating.text().trim());
                    		}
                    }
                  
                 }
            }
            app.setContentRating(content_rating);
            //resultList.add(new Pair("content_rating", content_rating));
            
            //get developer info
            String developer = "";
            String developerWeb = "";
            String developerEmail = "";
            String developerAddress = "";
            for(Element meta_info_developer: element.select(".meta-info.meta-info-wide")){
                String key = meta_info_developer.select(".title").text().trim();
                 if(key.equals("Developer")){
                	 	
                	 	for(Element devLink: meta_info_developer.select(".dev-link")) {
                	 		if(devLink.text().contains("website")) {
                	 			developerWeb = devLink.attr("href");
                	 			
                	 		}else if(devLink.text().contains("Email")) {
                	 			developerEmail = devLink.text().replaceAll("Email", "").replaceAll(" ", "").trim();
                	 		}
                	 	}
                	 	
                	 	Element addressElement = meta_info_developer.select(".content.physical-address").first();
                	 	if(addressElement != null && !addressElement.text().isEmpty()) {
                	 		developerAddress = addressElement.text().trim();
                	 	}
                	 	
                 }
            }
            app.setDeveloperWebsite(developerWeb);
            app.setDeveloperEmail(developerEmail);
            app.setDeveloperAddress(developerAddress);
            //resultList.add(new Pair("developer", developer.trim()));
        }
        //System.out.println(document.outerHtml());
        return app;
    }
}
