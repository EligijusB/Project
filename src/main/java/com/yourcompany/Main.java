package com.yourcompany;
	
import org.openqa.selenium.WebDriver;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	private static WebDriver driver;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Thread thread = new Thread(new MyGetDriverTask());
			thread.setDaemon(true);
			thread.start();
			Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					Driver.TearDown();
				}
			});
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static void setDriver(WebDriver d) {
		if(d != null) {
			System.out.println("Driver Set");
			driver = d;
		}
	}
	
	public static WebDriver getDriver() {
		return driver;
	}
}
