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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class MainController implements Initializable {

	private static Stage secondwindowstage;
	private static String searchKey;
	private static String searchMethod;
	private static String priceFilter;
	
	@FXML
	private JFXComboBox<String> searchCB, filterCB;
	
	@FXML
	private JFXTextField searchTF;
	
	@FXML
	private JFXButton searchBT;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		this.setupCombo();		
		
	}
	

	private void setupCombo() {
		this.searchCB.getItems().addAll("Single", "Multiple");
		this.filterCB.getItems().addAll("All Prices", "Paid", "Free");
	}
	
	public void onSearch() throws IOException {
		if(validate()) {
			searchKey = searchTF.getText();
			searchMethod  = searchCB.getSelectionModel().getSelectedItem();
			priceFilter = filterCB.getSelectionModel().getSelectedItem();
			Stage stage = new Stage();
			secondwindowstage = stage;
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SecondWindow.fxml"));
			if(searchMethod.equals("Single")) {
				//single search
				fxmlLoader.setController(new SecondWindowController(null, true));
			}else {
				//multiple search
				fxmlLoader.setController(new SecondWindowController(null, false));
			}
			Parent root = (Parent)fxmlLoader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		}
		
	}
	
	private boolean validate() {
		if(searchTF.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please provide a keyword to search.");
			alert.showAndWait();
			return false;
		}else if(searchCB.getSelectionModel().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please select a search method.");
			alert.showAndWait();
			return false;
		}else if(filterCB.getSelectionModel().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please select a price filter.");
			alert.showAndWait();
			return false;
		}
		return true;
	}

	public static Stage getMyStage() {
		// TODO Auto-generated method stub
		return secondwindowstage;
	}
	
	public static String getSearchKey() {
		// TODO Auto-generated method stub
		return searchKey;
	}
	
	public static String getSearchMethod() {
		// TODO Auto-generated method stub
		return searchMethod;
	}
	
	public static String getPriceFilter() {
		return priceFilter;
	}

}
