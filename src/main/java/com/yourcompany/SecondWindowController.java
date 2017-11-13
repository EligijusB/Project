package com.yourcompany;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;

public class SecondWindowController implements Initializable {
	
	private static ObservableList<App> listOfApps;
	private static TreeItem<App> apps;
	private static Stage mystage;
	private static MyTask mytask = new MyTask();
	private static MyGetAppLinksTask getLinksTask = new MyGetAppLinksTask();
	private StringProperty driverSetListener = new SimpleStringProperty();
	ExecutorService executor;
	
	@FXML
	private JFXTreeTableView<App> tableView;
	
	@FXML
	private Label notifierLabel;
	

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//run a thread which check if the driver was set
		Thread thread = new Thread(new Task() {
			
			@Override
			protected Object call() throws Exception {
				while(true) {
					System.out.println("In driver listener task");
					if(isCancelled() || Thread.currentThread().isInterrupted()) {
						break;
					}else {
						if(Driver.isSet()){
							Platform.runLater(new Runnable() {
					            @Override public void run() {
					                driverSetListener.set("Set");
					            }
					        });
							break;
						}
					}
					
				}
				return true;
			}
			
		});
		thread.setDaemon(true);
		thread.start();
		
		//set driver listener property change listener
		driverSetListener.addListener(e ->{
			System.out.println("Driver Set from property");
			getLinksTask.setOnSucceeded(el ->{
				notifierLabel.setText("Links gathering task completed...");
				System.out.println("Mylinkstask completed successfully... ");
				try {
					AppLinks.setLinksList((ArrayList<String>)getLinksTask.get());
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			getLinksTask.setDriver(Driver.getDriver());
			getLinksTask.setSearchKey(MainController.getSearchKey());
			Thread tr = new Thread(getLinksTask);
			tr.setDaemon(true);
			tr.start();
			notifierLabel.setText("Gathering links...");
		});
		
		if(!Driver.isSet()) {
			notifierLabel.setText("Waiting for Driver response...");
		}else {
			notifierLabel.setText("Driver has been set...");
		}
		mystage = MainController.getMyStage();
		mystage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		
			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Cancelling Tasks...");
				getLinksTask.cancel();
				if(executor != null) {
				executor.shutdownNow();}
			}
			
			
		});
		this.setupLabel();
		this.setupListOfApps();
		this.setupTableView();
		
		
		
		//mytask.setOnSucceeded(e ->{System.out.println("MyTask has finished");});
		
		/*
		mytask = new MyTask();
		notifierLabel.textProperty().bind(mytask.messageProperty());
		Thread thread = new Thread(mytask);
        thread.setDaemon(true);
        thread.start();
        */
        
        

		
	}
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	private void setupLabel() {
		// TODO Auto-generated method stub
		FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.1), notifierLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.play();
        System.out.println("Label setup");
	}




















	private void setupListOfApps() {
		listOfApps = FXCollections.observableArrayList();
		listOfApps.addListener(new ListChangeListener<App>() {

			@Override
			public void onChanged(Change<? extends App> c) {
				apps = new RecursiveTreeItem<App>(listOfApps, RecursiveTreeObject::getChildren); 
				tableView.setRoot(apps);
				tableView.setShowRoot(false);
			}
		});
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
		
		JFXTreeTableColumn<App, String> priceColumn = new JFXTreeTableColumn<App, String>("Price");
		priceColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<App,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<App, String> param) {
				// TODO Auto-generated method stub
				if(param.getValue().getValue().isFree()) {
					return new SimpleStringProperty("FREE");
				}else {
					return param.getValue().getValue().getPrice();
				}
			}
		});
		
		JFXTreeTableColumn<App, String> scoreColumn = new JFXTreeTableColumn<App, String>("Score");
		scoreColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<App,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<App, String> param) {
				// TODO Auto-generated method stub
				return new SimpleStringProperty(String.valueOf(param.getValue().getValue().getScore()));
			}
		});
		
		JFXTreeTableColumn<App, String> downloadsColumn = new JFXTreeTableColumn<App, String>("Downloads");
		downloadsColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<App,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<App, String> param) {
				// TODO Auto-generated method stub
				return param.getValue().getValue().getInstalls();
			}
		});
		
		tableView.getColumns().addAll(titleColumn, subtitleColumn, priceColumn, scoreColumn, downloadsColumn);
		
	}


}
