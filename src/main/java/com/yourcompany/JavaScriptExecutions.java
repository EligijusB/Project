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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Eligijus
 */
class JavaScriptExecutions {

    static void scrollPage(WebDriver driver) throws InterruptedException{
        //scrolling page
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //int old_elements = Jsoup.parse(driver.getPageSource()).getAllElements().size();
        //Elements old_elements = Jsoup.parse(driver.getPageSource()).select("div.card-content.id-track-click.id-track-impression");
        //System.out.println("1     " + old_elements.size());

  	  	while(!driver.findElement(By.id("show-more-button")).isDisplayed()) {
  	  		js.executeScript("scrollBy(0, 70)");
  	  	}
  	  js.executeScript("scrollBy(0, 2500)");
  	  System.out.println("show more button displayed");
  	  	driver.findElement(By.id("show-more-button")).click();
  	  	Thread.sleep(1000);
  	  	js.executeScript("scrollBy(0, 50)");
  	    Thread.sleep(1000);
//        WebDriverWait wait = new WebDriverWait(driver,10);
//        
//        int counter = 0;
//        while (counter != 3) {
//        		
//        		System.out.println("executing scroll");
//            js.executeScript("scrollBy(0,2500)"); 
//            int currentApps = Jsoup.parse(driver.getPageSource()).select("div.card-content.id-track-click.id-track-impression").size();
//            if(wait.until(new PageLoaded(currentApps))) {
//            		counter++;
//            }
//            
//            try{
//            	Thread.sleep(2000);
//            }catch(InterruptedException e) {
//            		System.out.println("Interrupted in scrolling");
//            }
//            
//            Elements new_elements = Jsoup.parse(driver.getPageSource()).select("div.card-content.id-track-click.id-track-impression");
//            if (old_elements.size() != new_elements.size()) {
//                System.out.println("URL's DIFF ELEMENTS = " + new_elements.size());
//                js.executeScript("scrollBy(0,2500)");
//                innerloop:
//                while(true) {
//	            		if(js.executeScript("return document.readyState").equals("complete")) {
//	            			break innerloop;
//	            		}
//                }
//                
//                try{
//                	Thread.sleep(2000);
//                }catch(InterruptedException e) {
//                		System.out.println("Interrupted in scrolling");
//                }
//                
//                old_elements = new_elements;
//            } else {
//                System.out.println("URL's SAME");
//                break;
//            }
 

        try {
            //click show more button to load even more apps.
        		//System.out.println("Clicking show more button....");
           // driver.findElement(By.id("show-more-button")).click();
            //js.executeScript("scrollBy(0, 2500)");
            //Thread.sleep(5000);

        } catch (ElementNotVisibleException e) {
            //just pass through
            System.out.println("show more button not found");
        } finally {
        	innerloop:
        	while(true) {
        		if(js.executeScript("return document.readyState").equals("complete")) {
        			break innerloop;
        		}
        }
        	/*
        	try{
            	Thread.sleep(2000);
            }catch(InterruptedException e) {
            		System.out.println("Interrupted in scrolling");
            }
        }
        */
        }

    }
    
    private static class PressButton{
    		
    		public static boolean pressButton(WebElement button) {
    			if(button == null) {
    				System.out.println("Button null");
    				return false;
    			}
    			try {
    				button.click();
    	            Thread.sleep(2000);
    	          
    	        
    				System.out.println("In press button");
    				return true;
    			}catch(ElementNotVisibleException e){
    				System.out.println("In press button exception");
    				return false;
    			}catch(InterruptedException e) {
            		System.out.println("Interrupted in scrolling");
            		return false;
            }
    		}
    	
    }

}
