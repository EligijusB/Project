package com.yourcompany;

import java.net.URL;
import java.util.ResourceBundle;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class MainController implements Initializable {
	
	@FXML
	private Label label;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);

		/*
		 * //mac caps.setCapability(
		 * PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
		 * "src/mac_phantomjs");
		 */

		// windows
		//caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "windows_phantomjs.exe");
		PhantomJsDriverManager.getInstance().setup();
		WebDriver driver = new PhantomJSDriver();
		driver.get("www.wikipedia.org");
		label.setText("Got Driver");
	}

}
