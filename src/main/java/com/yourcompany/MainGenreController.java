package com.yourcompany;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXComboBox;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainGenreController implements Initializable{
	
	private static Stage mystage;
	
	@FXML
	private JFXComboBox<String> comboGames, comboOther;
	@FXML
	private VBox vbox;
	@FXML
	private Label notifierLabel;
	@FXML
	JFXButton homeBT, topBT, newBT;

	private static Stage secondWindowStage;
	private static boolean pleaseWait = false;
	private static boolean driverSet = false;
	public void comboListener(String whereFrom) throws InterruptedException {
		if(!pleaseWait) {
			pleaseWait = true;
			vbox.getChildren().clear();
			while(true) {
				if(driverSet) {
					notifierLabel.setText("Please wait...");
					System.out.println("Combo Listener");
					//get webdriver
					WebDriver driver = Driver.getDriver();
					System.out.println("got driver");
					
					if(whereFrom.equals("games") || whereFrom.equals("other")) {
						//find "categories" button and click it
						driver.findElement(By.cssSelector("#action-dropdown-parent-Categories")).click();
						System.out.println("clicked button");
						WebElement dropDownDiv;
						if(whereFrom.equals("games")) {
							dropDownDiv = driver.findElement(By.cssSelector("#action-dropdown-children-Categories > div > ul > li:nth-child(2)"));
						}else {
							dropDownDiv = driver.findElement(By.cssSelector("#action-dropdown-children-Categories > div > ul > li:nth-child(1)"));
						}
						List<WebElement> allOptions = dropDownDiv.findElements(By.tagName("a"));
						String selection = "";
						if(whereFrom.equals("games")) {
							selection = comboGames.getSelectionModel().getSelectedItem();
						}else {
							selection = comboOther.getSelectionModel().getSelectedItem();
						}
						for(WebElement element: allOptions) {
							if(element.getAttribute("title").toLowerCase().equals(selection.toLowerCase().replace("and", "&"))) {
								element.click();
							}
						}
					}else {
						if(whereFrom.equals("home")) {
							//click home button
							//just pass as home button is already clicked
						}else if(whereFrom.equals("top")) {
							//click top charts button
							try {
								driver.findElement(By.xpath("//*[@id=\"wrapper\"]/div[4]/div/div[2]/div[4]/div[2]/a")).click();
							}catch(Exception e) {
								System.out.println("Something went wrong when clicking top charts button");
								e.printStackTrace();
							}
						}else {
							//click new releases button
							try {
								driver.findElement(By.xpath("//*[@id=\"wrapper\"]/div[4]/div/div[2]/div[5]/div[2]/a")).click();
							}catch(Exception e) {
								System.out.println("Something went wrong when clicking new releases button");
								e.printStackTrace();
							}
						}
					}
					//get all cluster headings
					Thread.sleep(500);
					JavascriptExecutor js = (JavascriptExecutor)driver;
	                	while(true) {
		            		if(js.executeScript("return document.readyState").equals("complete")) {
		            			System.out.println("Doc ready");
		            			break;
		            		}
		            	}
					List<WebElement> clusterHeadings = driver.findElements(By.className("cluster-heading"));
					System.out.println("got headings "+ clusterHeadings.size());
					for(WebElement heading: clusterHeadings) {
						//create HBox
						try {
							HBox hbox = new HBox();
							HBox holderHbox = new HBox();
							holderHbox.setPrefWidth(200);
							holderHbox.setMaxWidth(200);
							holderHbox.setAlignment(Pos.CENTER_LEFT);
							hbox.setSpacing(50);
							Label label = new Label();
							label.setText(heading.getText().replaceAll("See more", ""));
							label.setWrapText(true);
							JFXButton button = new JFXButton("Select");
							button.setButtonType(ButtonType.RAISED);
							button.setStyle("-fx-background-color:#5add90");
							WebElement atag = heading.findElement(By.tagName("a"));
							System.out.println(atag.getText());
							String href = atag.getAttribute("href");
							button.setOnAction(e ->{
								try {
									FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SecondWindow.fxml"));
									fxmlLoader.setController(new SecondWindowController(href, false));
									Parent root = (Parent)fxmlLoader.load();
									Scene scene = new Scene(root);
									scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
									Stage stage = new Stage();
									stage.setScene(scene);
									secondWindowStage = stage;
									stage.show();
								}catch(Exception ex) {
									//print error
									ex.printStackTrace();
								}
							});
							holderHbox.getChildren().add(label);
							hbox.getChildren().addAll(holderHbox, button);
							vbox.getChildren().add(hbox);
						}catch(NoSuchElementException e) {
							//just pass because cluster heading does not have corresponding see more button
						}
						
					}
					//System.out.println("Cluster headings " + clusterHeadings.size());
					pleaseWait = false;
					notifierLabel.setText("Done...");
					driver.navigate().back();
					driver.navigate().refresh();
					break;
				}else {
					System.out.println("thread sleeping");
					Thread.sleep(500);
				}
			}
		}else {
			if(whereFrom.equals("games")) {
				//reset selection to old one
				comboGames.getSelectionModel().selectPrevious();
				System.out.println("In reseting games");
			}else {
				comboOther.getSelectionModel().selectPrevious();
				System.out.println("In reseting other");
			}
		}
		
		
		
	}
	
	public void gamesListener() {
		System.out.println("Selected game");
		try {
			this.comboListener("games");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void otherListener() {
		System.out.println("Selected other");
		try {
			this.comboListener("other");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void homeListener() {
		System.out.println("Selected home");
		try {
			this.comboListener("home");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void topListener() {
		System.out.println("Selected top");
		try {
			this.comboListener("top");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void newListener() {
		System.out.println("Selected new");
		try {
			this.comboListener("new");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		mystage = ChooseMainController.getSearchByGenreStage();
		
		comboGames.setOnAction(e ->{
			this.gamesListener();
		});
		
		comboOther.setOnAction(e ->{
			this.otherListener();
		});
		
		homeBT.setOnAction(e ->{
			this.homeListener();
		});
		
		topBT.setOnAction(e ->{
			this.topListener();
		});
		
		newBT.setOnAction(e ->{
			this.newListener();
		});
		
		Thread thread = new Thread(new Task() {

			@Override
			protected Object call() throws Exception {
				while (true) {
					Thread.sleep(1000);
					if (isCancelled() || Thread.currentThread().isInterrupted()) {
						break;
					} else {
						if (Driver.isSet()) {
							break;
						}
					}

				}
				return true;
			}
			
			@Override
			protected void succeeded() {
				driverSet = true;
				notifierLabel.setText("Driver has been set...");
			}

		});
		thread.setDaemon(true);
		thread.start();	
		
		
		if (!Driver.isSet()) {
			notifierLabel.setText("Waiting for Driver response...");
		} else {
			notifierLabel.setText("Driver has been set...");
		}
		
		//setup combo boxes
		ObservableList<String> gamesList = FXCollections.observableArrayList("action", "adventure", "arcade", "board", "card", "casino",
				"casual", "educational", "music", "puzzle", "racing", "role playing", "simulation", "sports", "strategy", "trivia", "word");
		ObservableList<String> othersList = FXCollections.observableArrayList("art and design", "auto and vehicles", "beauty", "books and reference", "business", "comics", "communication", 
				"dating", "education", "entertainment", "events", "finance", "food and drink", "health and fitness", "house and home", "libraries and demo", 
				"lifestyle", "maps and navigation", "medical", "music and audio", "news and magazines", "paranting", "personalisation", "photography", 
				"productivity", "shopping", "social", "sports", "tools", "travel and local", "video players and editors", "weather");
		
		comboGames.setItems(gamesList);
		comboOther.setItems(othersList);
	}
	
	public static void setDriverTrue() {
		driverSet = true;
	}
	
	public static Stage getMyStage() {
		return secondWindowStage;
	}
}
