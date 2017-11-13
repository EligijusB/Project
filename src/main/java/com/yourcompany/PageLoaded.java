package com.yourcompany;

import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class PageLoaded implements ExpectedCondition<Boolean> {		
  int currentApps;
	
  public PageLoaded(int currentApps) {
    this.currentApps = currentApps;	
  }
	
  @Override
  public Boolean apply(WebDriver driver) {
	  JavascriptExecutor js = (JavascriptExecutor) driver;
	  do {
		  js.executeScript("scrollBy(0,2500)");
	  }while(driver.findElement(By.id("show-more-button")) != null);
	  System.out.println("Found show more button");
	  
      
  return true;
  }

}