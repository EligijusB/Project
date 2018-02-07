/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yourcompany;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Eligijus
 */
public class Statics {

    public static String searchByKeyword(WebDriver driver, String appName, String priceFilter) throws InterruptedException{
        //search the application provided with the driver
        //1:input the name of the app provided into the search input field
        //search input css selector #gbqfq
        WebElement searchBox = driver.findElement(By.cssSelector("#gbqfq"));
        searchBox.clear();
        searchBox.sendKeys(appName);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        //2:click search button
        //search button css selector #gbqfb
        driver.findElement(By.cssSelector("#gbqfb")).click();
	    		if(priceFilter.equals("Paid") || priceFilter.equals("Free")) {
	        		Thread.sleep(500);
		        	while(true) {
		        		if(js.executeScript("return document.readyState").equals("complete")) {
		        			System.out.println("Doc ready");
		        			break;
		        		}
		        	}
		        	//click appropriate button for the price filter
		        	if(priceFilter.equals("Paid")) {
		        		//click paid filter
		        		driver.findElement(By.cssSelector("#action-dropdown-parent-All\\20 prices")).click();
		        		WebElement dropDownDiv = driver.findElement(
		        				By.cssSelector("#action-dropdown-children-All\\20 prices > div"));
		        		dropDownDiv.findElement(
		        				By.cssSelector("#action-dropdown-children-All\\20 prices > div > ul > li:nth-child(3) > div > a")).click();
		        	}else {
		        		//click free filter
		        		driver.findElement(By.cssSelector("#action-dropdown-parent-All\\20 prices")).click();
		        		WebElement dropDownDiv = driver.findElement(
		        				By.cssSelector("#action-dropdown-children-All\\20 prices > div"));
		        		dropDownDiv.findElement(
		        				By.cssSelector("#action-dropdown-children-All\\20 prices > div > ul > li:nth-child(2) > div > a")).click();
		        	}
		        	
	    		}
        System.out.println("waiting for page to load...");
        try{
        	Thread.sleep(500);
            	while(true) {
            		if(js.executeScript("return document.readyState").equals("complete")) {
            			System.out.println("Doc ready");
            			break;
            		}
            	}
        }catch(InterruptedException e) {
        		System.out.println("Interrupted in scrolling");
        }
        //scroll the page to the bottom to load all apps
        JavaScriptExecutions.scrollPage(driver);
        String page_source = driver.getPageSource();
        return page_source;
    }
    
    public static String searchByGenre(WebDriver driver, String href) throws InterruptedException{
        //search the application provided with the driver
        //1:navigate to href given in parameters
        driver.navigate().to(href);

        //2:load the page
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
