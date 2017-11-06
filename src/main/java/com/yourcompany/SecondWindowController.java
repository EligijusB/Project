package com.yourcompany;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;

public class SecondWindowController implements Initializable {
	
	@FXML
	private JFXTreeTableView<App> tableView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setupTableView();
		
	}

	private void setupTableView() {
		JFXTreeTableColumn<App, String> titleColumn = new JFXTreeTableColumn<App, String>("Title");
		titleColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<App,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<App, String> param) {
				// TODO Auto-generated method stub
				return param.getValue().getValue().getTitle();
			}
		});
		
		JFXTreeTableColumn<App, String> subtitleColumn = new JFXTreeTableColumn<App, String>("Subtitle");
		subtitleColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<App,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<App, String> param) {
				// TODO Auto-generated method stub
				return param.getValue().getValue().getSubtitle();
			}
		});
		
		tableView.getColumns().addAll(titleColumn, subtitleColumn);
		
	}


}
