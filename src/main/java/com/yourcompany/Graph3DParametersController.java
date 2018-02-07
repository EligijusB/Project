package com.yourcompany;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Graph3DParametersController implements Initializable{

	@FXML
	JFXComboBox<String> paramX, paramY, paramZ;
	
	
	private Stage mystage;
	
	
	public static ObservableList<App> mylist;
	public static String staticParamX,staticParamY,staticParamZ;
	
	public Graph3DParametersController(Stage stage, ObservableList<App> list) {
		this.mystage = stage;
		mylist = list;
	}

	
	public void onGenerateButtonPress() throws IOException {
		if(validate()) {
			//generate the window and pass the graph node to the controller
			staticParamX = paramX.getSelectionModel().getSelectedItem();
			staticParamY = paramY.getSelectionModel().getSelectedItem();
			staticParamZ = paramZ.getSelectionModel().getSelectedItem();
			Stage stage = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("Graph3DWindow.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			mystage.close();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		ObservableList<String> list = FXCollections.observableArrayList("score", "reviews", "price", "avg. downloads", "avg. in-app purchase",
				"number of score 5", "number of score 4", "number of score 3", "number of score 2", "number of score 1", "last updated date");
		
		paramX.getItems().addAll(list);
		paramY.getItems().addAll(list);
		paramZ.getItems().addAll(list);
	}
	
	private boolean validate() {
		
		String x = paramX.getSelectionModel().getSelectedItem();
		String y = paramY.getSelectionModel().getSelectedItem();
		String z = paramZ.getSelectionModel().getSelectedItem();
		
		if(x != null && y != null && z != null) {
			if(x.equals(y) || x.equals(z) || y.equals(z)) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmatation Alert");
				alert.setHeaderText(null);
				alert.setContentText("Two of your axis parameters are the same. "
						+ "Do you still want to generate the graph with selected parameters?");
				
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
				    return true;
				} else {
				    return false;
				}
			}else if(x.equals(y) && x.equals(z)) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmatation Alert");
				alert.setHeaderText(null);
				alert.setContentText("All three of axis parameters are the same. "
						+ "Do you still want to generate the graph with selected parameters?");
				
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
				    return true;
				} else {
				    return false;
				}
			}else {
				return true;
			}
		}else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Error. Please select the parameters for X, Y and Z axis.");
			alert.showAndWait();
			return false;
		}
		
		
		
	}
	
}
