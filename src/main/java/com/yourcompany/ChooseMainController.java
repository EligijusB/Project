package com.yourcompany;

import java.net.URL;
import java.util.ResourceBundle;

import org.openqa.selenium.WebDriver;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChooseMainController{
	
	private static Stage searchByGenreStage;
	
	public void onClickByLink() {
		//open a search by keyword window
		try {
			Thread thread = new Thread(new MyGetDriverTask());
			thread.setDaemon(true);
			thread.start();
			Parent root = FXMLLoader.load(getClass().getResource("SearchByLinkWindow.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage mystage = new Stage();
			mystage.setScene(scene);
			mystage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					Driver.TearDown();
				}
			});
			Main.getChooseMainStage().hide();
			mystage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	public void onClickByKeyword() {
		//open a search by keyword window
		try {
			Thread thread = new Thread(new MyGetDriverTask());
			thread.setDaemon(true);
			thread.start();
			Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage mystage = new Stage();
			mystage.setScene(scene);
			mystage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					Driver.TearDown();
				}
			});
			Main.getChooseMainStage().hide();
			mystage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onClickByGenre() {
		//open a search by keyword window
		try {
			System.out.println("opening genre window");
			Thread thread = new Thread(new MyGetDriverTask());
			thread.setDaemon(true);
			thread.start();
			Parent root = FXMLLoader.load(getClass().getResource("MainByGenre.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage mystage = new Stage();
			mystage.setScene(scene);
			mystage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					Driver.TearDown();
				}
			});
			Main.getChooseMainStage().hide();
			searchByGenreStage = mystage;
			mystage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Stage getSearchByGenreStage() {
		return searchByGenreStage;
	}
	
	
}
