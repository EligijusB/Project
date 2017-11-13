/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yourcompany;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Eligijus
 */
class JavaScriptExecutions {

    static void scrollPage(WebDriver driver){
        //scrolling page
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //int old_elements = Jsoup.parse(driver.getPageSource()).getAllElements().size();
        Elements old_elements = Jsoup.parse(driver.getPageSource()).select("div.card-content.id-track-click.id-track-impression");
        System.out.println("1     " + old_elements.size());
        while (true) {
            js.executeScript("scrollBy(0,2500)");
            try{
            	Thread.sleep(2000);
            }catch(InterruptedException e) {
            		System.out.println("Interrupted in scrolling");
            }
            Elements new_elements = Jsoup.parse(driver.getPageSource()).select("div.card-content.id-track-click.id-track-impression");
            if (old_elements.size() != new_elements.size()) {
                System.out.println("URL's DIFF ELEMENTS = " + new_elements.size());
                js.executeScript("scrollBy(0,2500)");
                try{
                	Thread.sleep(2000);
                }catch(InterruptedException e) {
                		System.out.println("Interrupted in scrolling");
                }
                old_elements = new_elements;
            } else {
                System.out.println("URL's SAME");
                break;
            }

        }
        try {
            //click show more button to load even more apps.
            driver.findElement(By.id("show-more-button")).click();

        } catch (ElementNotVisibleException e) {
            //just pass through
            System.out.println("show more button not found");
        } finally {
        	try{
            	Thread.sleep(2000);
            }catch(InterruptedException e) {
            		System.out.println("Interrupted in scrolling");
            }
        }

    }

}
