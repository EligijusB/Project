package com.yourcompany;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController implements Initializable {

	@FXML
	private JFXComboBox<String> searchCB;
	
	@FXML
	private JFXTextField searchTF;
	
	@FXML
	private JFXButton searchBT;
	
	@FXML
	private JFXTreeTableView tableView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
//		DesiredCapabilities caps = new DesiredCapabilities();
//		caps.setJavascriptEnabled(true);
//		PhantomJsDriverManager.getInstance().setup();
//		WebDriver driver = new PhantomJSDriver();
//		driver.get("www.wikipedia.org");
		
		this.setupCombo();		
		
	}

	private void setupCombo() {
		this.searchCB.getItems().addAll("Single", "Multiple");
	}
	
	public void onSearch() throws IOException {
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("SecondWindow.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
		
	}

}
