/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yourcompany;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Eligijus
 */
public class Statics {

    public static String search(WebDriver driver, String appName){
        //search the application provided with the driver
        //1:input the name of the app provided into the search input field
        //search input css selector #gbqfq
        WebElement searchBox = driver.findElement(By.cssSelector("#gbqfq"));
        searchBox.clear();
        searchBox.sendKeys(appName);

        //2:click search button
        //search button css selector #gbqfb
        driver.findElement(By.cssSelector("#gbqfb")).click();
        System.out.println("waiting for page to load...");
        try{
        	Thread.sleep(2000);
        }catch(InterruptedException e) {
        		System.out.println("Interrupted in scrolling");
        }
        //scroll the page to the bottom to load all apps
        JavaScriptExecutions.scrollPage(driver);
        String page_source = driver.getPageSource();
        return page_source;
    }
}
