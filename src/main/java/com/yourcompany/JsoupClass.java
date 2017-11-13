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
    
    public static ArrayList<Pair> getSingleAppInfo(String link) throws IOException {
        ArrayList<Pair> resultList = new ArrayList();
        Document document = Jsoup.connect(link).get();
        
        Elements elements = document.select("div.details-wrapper.apps.square-cover.id-track-partial-impression.id-deep-link-item");
        if(!elements.isEmpty()){
            System.out.println(elements.size());
            Element element = elements.first();
            String img_link = element.select(".cover-image").attr("src");
            System.out.println(img_link);
            resultList.add(new Pair("img_link", img_link));
            String ratingCount = element.select(".rating-count").text();
            System.out.println(ratingCount);
            resultList.add(new Pair("ratingCount", ratingCount));
            String author = element.select(".document-subtitle.primary").text();
            System.out.println(author);
            resultList.add(new Pair("author", author));
            String genre = element.select(".document-subtitle.category").text();
            System.out.println(genre);
            resultList.add(new Pair("genre", genre));
            String compatibility = element.select(".app-compatibility-final").text();
            System.out.println(compatibility);
            resultList.add(new Pair("compatibility", compatibility));
            String description = element.select(".show-more-content.text-body").text();
            System.out.println(description);
            resultList.add(new Pair("description", description));
            
            //change div to reviews div
            element = document.select("div.details-section.reviews").first();
            String score = element.select(".score").text();
            System.out.println(score);
            resultList.add(new Pair("score", score));
            String ratingHistogram = 
                    element.select(".rating-bar-container.five").text() + "\n" +
                    element.select(".rating-bar-container.four").text() + "\n" +
                    element.select(".rating-bar-container.three").text() + "\n" +
                    element.select(".rating-bar-container.two").text() + "\n" +
                    element.select(".rating-bar-container.one").text();
            System.out.println(ratingHistogram);
            resultList.add(new Pair("ratingHistogram", ratingHistogram));
            
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
                System.out.println(recent_changes);
                resultList.add(new Pair("recent_changes", recent_changes));
            }else{
                recent_changes = "None";
                resultList.add(new Pair("recent_changes", recent_changes));
            }
            //fetch additional info
            //change div to reviews div
            element = document.select("div.details-section.metadata").first();
            String updated = "";
            String size = "";
            String installs = "";
            String current_version = "";
            String requires_android = "";
            String in_app_products = "";
            String interactive_elements = "";
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
            System.out.println(updated);
            resultList.add(new Pair("updated", updated));
            System.out.println(size);
            resultList.add(new Pair("size", size));
            System.out.println(installs);
            resultList.add(new Pair("installs", installs));
            System.out.println(current_version);
            resultList.add(new Pair("current_version", current_version));
            System.out.println(requires_android);
            resultList.add(new Pair("requires_android", requires_android));
            System.out.println(in_app_products);
            resultList.add(new Pair("in_app_products", in_app_products));
            System.out.println(interactive_elements);
            resultList.add(new Pair("interactive_elements", interactive_elements));
            
            //get content rating info
            String content_rating = "";
            for(Element meta_info_other: element.select(".meta-info.contains-text-link")){
                String key = meta_info_other.select(".title").text().trim();
                 if(key.equals("Content Rating")){
                    content_rating = meta_info_other.select(".content").text().trim();
                    content_rating = content_rating.replaceAll("Learn more", "");
                 }
            }
            System.out.println(content_rating);
            resultList.add(new Pair("content_rating", content_rating));
            
            //get developer info
            String developer = "";
            for(Element meta_info_developer: element.select(".meta-info.meta-info-wide")){
                String key = meta_info_developer.select(".title").text().trim();
                 if(key.equals("Developer")){
                    developer = meta_info_developer.select(".content").text().trim();
                    developer = developer.replaceAll("Visit website", "");
                    developer = developer.replaceAll("Email ", "");
                    developer = developer.replaceAll("Privacy Policy", "");
                 }
            }
            System.out.println(developer);
            resultList.add(new Pair("developer", developer.trim()));
        }
        //System.out.println(document.outerHtml());
        return resultList;
    }
}
