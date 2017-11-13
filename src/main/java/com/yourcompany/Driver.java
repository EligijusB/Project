package com.yourcompany;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;


public class Driver {
	private static WebDriver driver;
	private static boolean driverSet = false;

    private static final String mainPage = "https://play.google.com/store/apps";

    public static void setupDriver() {
        if(driver != null) {
        		driver = null;
        }
        DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		//PhantomJsDriverManager.getInstance().setup();
		System.out.println("Setting chrome path");
		System.setProperty("webdriver.chrome.driver", "/Users/eligijusblankus/eclipse-workspace/Project/chromedriver.exe");
		driver = new ChromeDriver();
		System.out.println("opening chrome driver");
		//driver = new ChromeDriver(caps);
		driver.navigate().to(mainPage);     
		driverSet = true;

        //4: Create driver instance, open the main page
        //chrome driver for testing
        //driver = new ChromeDriver();
    }
    
    public static WebDriver getDriver() {
    		return driver;
    }
    
    public static boolean isSet() {
    		return driverSet;
    }

    public static void TearDown() {
    		System.out.println("Tearing down...");
        if (driver != null) {
            driver.quit();
            driverSet = false;
        }
    }
}
