package com.yourcompany;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;

public class Graph3DWindowController implements Initializable{
	
	@FXML
	StackPane stackpane;
	
	@FXML
	Label bigRxy, bigRxz, bigRyz;
	
	private String paramX, paramY, paramZ;
	private ObservableList<App> list;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		paramX = Graph3DParametersController.staticParamX;
		paramY = Graph3DParametersController.staticParamY;
		paramZ = Graph3DParametersController.staticParamZ;
		list = Graph3DParametersController.mylist;
	
		
		Node graphnode = ScatterChart3D.create3DGraphNode(paramX, paramY, paramZ, list);
		
		//get the big R value for each pair of axis
		ArrayList<ArrayList<Number>> sharedAxis = ScatterChart3D.getSharedAxisList();
		
		
		Number xy = 0, xz = 0, yz = 0;
		if(sharedAxis != null) {
			xy = GraphWindowController.calculateBigR(sharedAxis.get(0), sharedAxis.get(1));
			xz = GraphWindowController.calculateBigR(sharedAxis.get(0), sharedAxis.get(2));
			yz = GraphWindowController.calculateBigR(sharedAxis.get(1), sharedAxis.get(2));
			
			bigRxy.setText(xy.toString());
			bigRxz.setText(xz.toString());
			bigRyz.setText(yz.toString());
		}else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Soory, error occured while calculating big R values.");
			alert.showAndWait();
		}
		
		stackpane.getChildren().add(graphnode);
	}
	
	
	
}
