package com.yourcompany;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.axis.LabelOrientation;
import com.orsoncharts.axis.LogAxis3D;
import com.orsoncharts.data.xyz.XYZSeries;
import com.orsoncharts.data.xyz.XYZSeriesCollection;
import com.orsoncharts.graphics3d.Dimension3D;
import com.orsoncharts.graphics3d.ViewPoint3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.xyz.ScatterXYZRenderer;
import com.orsoncharts.style.ChartStyler;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;

public class SecondWindowController implements Initializable {

	private ObservableList<App> listOfApps;
	private TreeItem<App> apps;
	private Stage mystage;
	private MyTask mytask = new MyTask();
	//private MyGetAppLinksTask getLinksTask = new MyGetAppLinksTask();
	public static ObservableList<App> selectedAppsList = FXCollections.observableArrayList();
	private StringProperty driverSetListener = new SimpleStringProperty();
	ExecutorService executor;

	@FXML
	private JFXComboBox<String> sortCB, filterCB, paramOneCombo, paramTwoCombo;

	@FXML
	private JFXButton selectAllButton, filterButton, sortButton;

	@FXML
	private JFXCheckBox descendingCheck, ascendingCheck;

	@FXML
	private TableView<App> tableView;
	
	@FXML
	private TableColumn<App, CheckBox> checkBoxColumn;
	
	@FXML
	private TableColumn<App, String> priceColumn, titleColumn, subtitleColumn, scoreColumn, reviewsColumn;

	@FXML
	private Label notifierLabel;
	
	@FXML
	private MenuBar menuBar;
	
	@FXML
	private Menu shortcutsMenuItem, filterMenuItem, sortMenuItem;
	
	private boolean isfilterredList;
	private String href;
	private boolean singleSearch;
	private String priceFilter;
	public Stage graphWindowStage;

	public SecondWindowController(String href, boolean singleSearch) {
		this.href = href;
		this.singleSearch = singleSearch;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MyGetAppLinksTask getLinksTask = new MyGetAppLinksTask();
		
		// run a thread which check if the driver was set
		Thread thread = new Thread(new Task() {

			@Override
			protected Object call() throws Exception {
				while (true) {
					Thread.sleep(1000);
					if (isCancelled() || Thread.currentThread().isInterrupted()) {
						break;
					} else {
						if (Driver.isSet()) {
							/*
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									driverSetListener.set("Set");
								}
							});
							*/
							break;
						}
					}

				}
				return true;
			}
			
			@Override
			protected void succeeded() {
				System.out.println("DriverSetting from succeeded");
				driverSetListener.set("Set");
			}

		});
		thread.setDaemon(true);
		thread.start();

		getLinksTask.setOnSucceeded(el -> {
			notifierLabel.setText("Links gathering task completed...");
			System.out.println("Mylinkstask completed successfully... ");
			try {
				ArrayList<String> links = getLinksTask.get();
				AppLinks.setLinksList((ArrayList<String>) getLinksTask.get());
				executor = Executors.newFixedThreadPool(5, r -> {
					Thread t = new Thread(r);
					t.setDaemon(true);
					return t;
				});
				ArrayList<MyTask> mytaskList = new ArrayList<MyTask>();
				int linksSize = links.size();
				for (int i = 1; i <= linksSize; i++) {
					MyTask mt = new MyTask();
					mt.setOnSucceeded(mytaskhandler -> {
						System.out.println("MYTASK HAS SUCCEEDED");
						App app = mt.getApp();
						if(app != null) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									listOfApps.add(app);
									tableView.getItems().add(app);
									notifierLabel.setText("Generating Apps --> "+listOfApps.size() + ":"+ linksSize);
								}
							});
						}else {
							System.out.println("APP IS NULL");
						}
					});
					mytaskList.add(mt);
				}
				notifierLabel.setText("Generating Apps --> 0:"+ linksSize);
				mytaskList.forEach(executor::execute);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		// set driver listener property change listener
		driverSetListener.addListener(e -> {
			System.out.println("Driver Set from property");

			getLinksTask.setDriver(Driver.getDriver());
			if(href!=null) {
				//search by href from genre
				getLinksTask.setSearchHref(href);
				System.out.println("HREF =" + href);
			}else {
				getLinksTask.setSearchKey(MainController.getSearchKey());
				getLinksTask.setPriceFilter(MainController.getPriceFilter());
			}
			if(singleSearch) {
				getLinksTask.setSingleSerach();
			}
			Thread tr = new Thread(getLinksTask);
			tr.start();
			notifierLabel.setText("Gathering links...");
		});

		if (!Driver.isSet()) {
			notifierLabel.setText("Waiting for Driver response...");
		} else {
			notifierLabel.setText("Driver has been set...");
		}
//		if(href!=null) {
//			mystage = MainGenreController.getMyStage();
//		}else {
//			mystage = MainController.getMyStage();
//		}
//		mystage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//
//			@Override
//			public void handle(WindowEvent event) {
//				// TODO Auto-generated method stub
//				System.out.println("Cancelling Tasks...");
//				getLinksTask.cancel();
//				if (executor != null) {
//					executor.shutdownNow();
//				}
//			}
//
//		});
		this.setupLabel();
		this.setupListOfApps();
		this.setupTableView();
		this.setupShortcuts();
		this.setupMenuBarSort();
		this.setupMenuBarFilter();
		this.setupGraphComboBoxes();

	}
	
	private void setupGraphComboBoxes() {
		//create an array of string selection
		ObservableList<String> list = FXCollections.observableArrayList("score", "reviews", "price", "avg. downloads", "avg. in-app purchase",
				"number of score 5", "number of score 4", "number of score 3", "number of score 2", "number of score 1", "last updated date");
		paramOneCombo.getItems().addAll(list);
		paramTwoCombo.getItems().addAll(list);
	}
	
	private void setupShortcuts() {
		//create a menu item to select all apps
		RadioMenuItem selectAll = new RadioMenuItem("Select All Apps");
		selectAll.setOnAction(e ->{
			
			if(selectAll.getText().equals("Select All Apps")) {
				for(App app : tableView.getItems()) {
					if(!app.getIsSelected().get()) {
						app.setIsSelected(true);
					}
				}
				tableView.getColumns().get(0).setVisible(false);
				tableView.getColumns().get(0).setVisible(true);
				selectAll.setText("Deselect All Apps");
				System.out.println("Selecting");
			}else {
				for(App app : listOfApps) {
					if(app.getIsSelected().get()) {
						app.setIsSelected(false);
					}
				}
				tableView.getColumns().get(0).setVisible(false);
				tableView.getColumns().get(0).setVisible(true);
				selectAll.setText("Select All Apps");
				System.out.println("Deselecting");
			}
		});
		
		shortcutsMenuItem.getItems().clear();
		shortcutsMenuItem.getItems().add(selectAll);
	}


	private void setupCheckBoxes() {
		ascendingCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				if(newValue) {
					//check if descending order is selected 
					if(descendingCheck.isSelected()) {
						descendingCheck.setSelected(false);
					}					
				}
			}
			
		});
		
		descendingCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				if(newValue) {
					//check if ascending order is selected 
					if(ascendingCheck.isSelected()) {
						ascendingCheck.setSelected(false);
					}					
				}
			}
			
		});
	}
	
	public void onGenerateGraph() throws IOException {
		if(validate() && validateAppSelection()) {
			Stage stage = new Stage();
			graphWindowStage = stage;
			FXMLLoader root = new FXMLLoader(getClass().getResource("GraphWindow.fxml"));
			root.setController(new GraphWindowController(selectedAppsList, paramOneCombo.getSelectionModel().getSelectedItem(), paramTwoCombo.getSelectionModel().getSelectedItem(), this));
			Parent parent = (Parent)root.load();
			Scene scene = new Scene(parent);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		}
	}
	
	private boolean validateAppSelection() {
		if(selectedAppsList == null || selectedAppsList.isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please select the apps you want to analyse.");
			alert.showAndWait();
			return false;
		}
		return true;
	}
	
	private boolean validate() {
		
		String x = paramOneCombo.getSelectionModel().getSelectedItem();
		String y = paramTwoCombo.getSelectionModel().getSelectedItem();
		
		if(x != null && y != null) {
			if(x.equals(y)) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmatation Alert");
				alert.setHeaderText(null);
				alert.setContentText("Both of your axis parameters are the same. "
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
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please select X and Y axis parameters.");
			alert.showAndWait();
			return false;
		}
		
	}
	
	
	//testing 3d
	public void onGenerateGraph3D() throws IOException {
		Stage stage = new Stage();
		FXMLLoader root = new FXMLLoader(getClass().getResource("Graph3DParameters.fxml"));
		root.setController(new Graph3DParametersController(stage, selectedAppsList));
		Parent parent = (Parent)root.load();
		Scene scene = new Scene(parent);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
		
		/*
		StackPane sp = new StackPane();
        Stage stage = new Stage();
		sp.getChildren().add(ScatterChart3D.createDemoNode());
        Scene scene = new Scene(sp, 768, 512);
        stage.setScene(scene);
        stage.setTitle("Orson Charts: ScatterPlotFXDemo2.java");
        stage.show();
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

		selectedAppsList.addListener(new ListChangeListener<App>() {

			@Override
			public void onChanged(Change<? extends App> c) {
				while(c.next()) {
					if(c.wasAdded()) {
						System.out.println("Added: " + c.getAddedSubList().get(0).getTitle());
					}
					if(c.wasRemoved()) {
						System.out.println("Removed: " + c.getRemoved().get(0).getTitle());
					}
				}
			}
			
		});
	}

	private void setupTableView() {
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
		    @Override 
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		            App app = tableView.getSelectionModel().getSelectedItem();      
		            try {
			            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("appInfoWindow.fxml"));
						fxmlLoader.setController(new appInfoWindowController(app));
						Parent root = (Parent)fxmlLoader.load();
						Scene scene = new Scene(root);
						scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
						Stage stage = new Stage();
						stage.setScene(scene);
						stage.show();
		            }catch(Exception e) {
		            		//pass
		            		e.printStackTrace();
		            }
		        }
		    }
		});
		
		checkBoxColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<App, CheckBox>, ObservableValue<CheckBox>>(){

					@Override
					public ObservableValue<CheckBox> call(
							javafx.scene.control.TableColumn.CellDataFeatures<App, CheckBox> param) {
						App app = param.getValue();

						CheckBox checkBox = new CheckBox();

						checkBox.selectedProperty().setValue(app.getIsSelected().getValue());
						
						/*
						checkBox.setOnAction(e ->{
							if(checkBox.isSelected()) {
								if(!selectedAppsList.contains(app)) {
								
									selectedAppsList.add(app);
									app.setIsSelected(true);
								}
								
							}else {
								selectedAppsList.remove(app);
							}
							System.out.println("Inside action event...");
						});
						*/

						checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
							public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
									Boolean new_val) {

								if(new_val) {
									app.setIsSelected(true);
								}else {
									app.setIsSelected(false);
								}
							}
						});
						
						/*
						app.getIsSelected().addListener(new ChangeListener<Boolean>() {
							public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
									Boolean new_val) {

								if(new_val) {
									if(!selectedAppsList.contains(app)) {
										//app.setIsSelected(true);
										selectedAppsList.add(app);
									}
								}else {
									//app.setIsSelected(false);
									System.out.println("In new value is false");
									//selectedAppsList.remove(app);
								}
							}
						});
							*/

						return new SimpleObjectProperty<CheckBox>(checkBox);
					}

				});

		titleColumn = new TableColumn<App, String>("Title");
		titleColumn.setSortable(false);
		titleColumn.setCellValueFactory(new PropertyValueFactory<App, String>("title"));

		subtitleColumn = new TableColumn<App, String>("Subtitle");
		subtitleColumn.setSortable(false);
		subtitleColumn.setCellValueFactory(new PropertyValueFactory<App, String>("subtitle"));

		priceColumn = new TableColumn<App, String>("Price");
		priceColumn.setSortable(false);
		priceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<App,String>,ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(javafx.scene.control.TableColumn.CellDataFeatures<App, String> param) {
				// TODO Auto-generated method stub
				App app = param.getValue();
				if(app.isFree()) {
					return new SimpleStringProperty("FREE");
				}else {
					return new SimpleStringProperty(app.getPrice());
				}
			}
			
		});
		//priceColumn.setCellValueFactory(new PropertyValueFactory<App, String>("price"));

		scoreColumn = new TableColumn<App, String>("Score");
		scoreColumn.setSortable(false);
		scoreColumn.setCellValueFactory(new PropertyValueFactory<App, String>("score"));

		TableColumn<App, String> downloadsColumn = new TableColumn<App, String>("Downloads");
		downloadsColumn.setSortable(false);
		downloadsColumn.setCellValueFactory(new PropertyValueFactory<App, String>("installs"));
		
		reviewsColumn = new TableColumn<App, String>("Reviews");
		reviewsColumn.setSortable(false);
		reviewsColumn.setCellValueFactory(new PropertyValueFactory<App, String>("reviews"));
		
		tableView.getColumns().addAll(titleColumn, subtitleColumn, priceColumn, scoreColumn,downloadsColumn, reviewsColumn);
		//tableView.getColumns().addAll(checkBoxColumn);
	}
	
	
	
	
	
	
	
	
	
	
	
	private void setupMenuBarSort() {
		//setup sorting menu
		
		//create new menu for each sorting type
		
		Menu sortByReviews = new Menu("ByReviews");
		
		//create radio items group for each sorting type
		ToggleGroup byReviewsToggle = new ToggleGroup();
		RadioMenuItem ascendingReviews = new RadioMenuItem("ascending");
		RadioMenuItem descendingReviews = new RadioMenuItem("descending");
		//add radio menu items to toggle group
		ascendingReviews.setToggleGroup(byReviewsToggle);
		descendingReviews.setToggleGroup(byReviewsToggle);
		
		ascendingReviews.setOnAction(handler ->{
			//Sorting by reviews ascending
			
			System.out.println("Sorting by reviews ascending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByReviewsAsc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByReviewsAsc());
				tableView.setItems(listOfApps);
			}
			
		});
		
		descendingReviews.setOnAction(handler ->{
			//Sorting by reviews ascending
			
			System.out.println("Sorting by reviews descending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByReviewsDesc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByReviewsDesc());
				tableView.setItems(listOfApps);
			}
			
		});
		
		sortByReviews.getItems().addAll(ascendingReviews, descendingReviews);
		
		
		Menu sortByScore = new Menu("ByScore");
		
		
		RadioMenuItem ascendingScore = new RadioMenuItem("ascending");
		RadioMenuItem descendingScore = new RadioMenuItem("descending");
		//add radio menu items to toggle group
		ascendingScore.setToggleGroup(byReviewsToggle);
		descendingScore.setToggleGroup(byReviewsToggle);
		
		ascendingScore.setOnAction(handler ->{
			//Sorting by reviews ascending
			/*
			System.out.println("Sorting by score ascending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByScoreAsc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByScoreAsc());
				tableView.setItems(listOfApps);
			}
			*/
			scoreColumn.setSortType(SortType.ASCENDING);
			tableView.getSortOrder().add(scoreColumn);
			scoreColumn.setSortable(true);
			scoreColumn.setSortable(false);
		});
		
		descendingScore.setOnAction(handler ->{
			//Sorting by reviews ascending
			/*
			System.out.println("Sorting by score descending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByScoreDesc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByScoreDesc());
				tableView.setItems(listOfApps);
			}
			*/
			scoreColumn.setSortType(SortType.DESCENDING);
			tableView.getSortOrder().add(scoreColumn);
			scoreColumn.setSortable(true);
			scoreColumn.setSortable(false);
		});
		sortByScore.getItems().addAll(ascendingScore, descendingScore);
		
		
		
		Menu sortByPrice = new Menu("ByPrice");
		
		
		RadioMenuItem ascendingPrice = new RadioMenuItem("ascending");
		RadioMenuItem descendingPrice = new RadioMenuItem("descending");
		//add radio menu items to toggle group
		ascendingPrice.setToggleGroup(byReviewsToggle);
		descendingPrice.setToggleGroup(byReviewsToggle);
		
		ascendingPrice.setOnAction(handler ->{
			//Sorting by reviews ascending
			System.out.println("Sorting by price ascending");
			/*
			priceColumn.setSortType(SortType.ASCENDING);
			tableView.getSortOrder().add(priceColumn);
			priceColumn.setSortable(true);
			priceColumn.setSortable(false);
			*/
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByPriceAsc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByPriceAsc());
				tableView.setItems(listOfApps);
			}
		});
		
		descendingPrice.setOnAction(handler ->{
			//Sorting by reviews ascending
			System.out.println("Sorting by price descending");
			/*
			priceColumn.setSortType(SortType.DESCENDING);
			tableView.getSortOrder().add(priceColumn);
			priceColumn.setSortable(true);
			priceColumn.setSortable(false);
			*/
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByPriceDesc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByPriceDesc());
				tableView.setItems(listOfApps);
			}
		});
		
		
		sortByPrice.getItems().addAll(ascendingPrice, descendingPrice);
		
		Menu sortByDownloads = new Menu("ByDownloads");
		
		
		RadioMenuItem lowerascendingDownloads = new RadioMenuItem("lower ascending");
		RadioMenuItem lowerdescendingDownloads = new RadioMenuItem("lower descending");
		RadioMenuItem upperascendingDownloads = new RadioMenuItem("upper ascending");
		RadioMenuItem upperdescendingDownloads = new RadioMenuItem("upper descending");
		//add radio menu items to toggle group
		lowerascendingDownloads.setToggleGroup(byReviewsToggle);
		lowerdescendingDownloads.setToggleGroup(byReviewsToggle);
		upperascendingDownloads.setToggleGroup(byReviewsToggle);
		upperdescendingDownloads.setToggleGroup(byReviewsToggle);
		
		lowerascendingDownloads.setOnAction(handler ->{
			//Sorting by lower downloads ascending
			System.out.println("Sorting by lower downloads ascending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByLowerDownloadsAsc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByLowerDownloadsAsc());
				tableView.setItems(listOfApps);
			}
		});
		
		upperascendingDownloads.setOnAction(handler ->{
			//Sorting by lower downloads ascending
			System.out.println("Sorting by upper downloads ascending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByUpperDownloadsAsc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByUpperDownloadsAsc());
				tableView.setItems(listOfApps);
			}
		});
		
		lowerdescendingDownloads.setOnAction(handler ->{
			//Sorting by lower downloads ascending
			System.out.println("Sorting by lower downloads descending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByLowerDownloadsDesc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByLowerDownloadsDesc());
				tableView.setItems(listOfApps);
			}
		});
		
		upperdescendingDownloads.setOnAction(handler ->{
			//Sorting by lower downloads ascending
			System.out.println("Sorting by upper downloads descending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByUpperDownloadsDesc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByUpperDownloadsDesc());
				tableView.setItems(listOfApps);
			}
		});
		
		sortByDownloads.getItems().addAll(lowerascendingDownloads, lowerdescendingDownloads, upperascendingDownloads, upperdescendingDownloads);

		
		Menu sortByTitle = new Menu("ByTitle");
		
		
		RadioMenuItem ascendingTitle = new RadioMenuItem("ascending");
		RadioMenuItem descendingTitle = new RadioMenuItem("descending");
		//add radio menu items to toggle group
		ascendingTitle.setToggleGroup(byReviewsToggle);
		descendingTitle.setToggleGroup(byReviewsToggle);
		
		ascendingTitle.setOnAction(handler ->{
			//Sorting by reviews ascending
			System.out.println("Sorting by title ascending");
			titleColumn.setSortType(SortType.ASCENDING);
			tableView.getSortOrder().add(titleColumn);
			titleColumn.setSortable(true);
			titleColumn.setSortable(false);
		});
		
		descendingTitle.setOnAction(handler ->{
			//Sorting by reviews ascending
			System.out.println("Sorting by title descending");
			titleColumn.setSortType(SortType.DESCENDING);
			tableView.getSortOrder().add(titleColumn);
			titleColumn.setSortable(true);
			titleColumn.setSortable(false);
		});
		
		
		sortByTitle.getItems().addAll(ascendingTitle, descendingTitle);
		
		
		Menu sortBySubTitle = new Menu("BySubTitle");
		
		
		RadioMenuItem ascendingSubTitle = new RadioMenuItem("ascending");
		RadioMenuItem descendingSubTitle = new RadioMenuItem("descending");
		//add radio menu items to toggle group
		ascendingTitle.setToggleGroup(byReviewsToggle);
		descendingTitle.setToggleGroup(byReviewsToggle);
		
		ascendingSubTitle.setOnAction(handler ->{
			//Sorting by reviews ascending
			System.out.println("Sorting by SubTitle ascending");
			subtitleColumn.setSortType(SortType.ASCENDING);
			tableView.getSortOrder().add(subtitleColumn);
			subtitleColumn.setSortable(true);
			subtitleColumn.setSortable(false);
		});
		
		descendingSubTitle.setOnAction(handler ->{
			//Sorting by reviews ascending
			System.out.println("Sorting by SubTitle descending");
			subtitleColumn.setSortType(SortType.DESCENDING);
			tableView.getSortOrder().add(subtitleColumn);
			subtitleColumn.setSortable(true);
			subtitleColumn.setSortable(false);
		});
		
		
		sortBySubTitle.getItems().addAll(ascendingSubTitle, descendingSubTitle);
		
		
		Menu sortByLastUpdatedDate = new Menu("ByLastUpdated");
		
		
		RadioMenuItem ascendingUpdate = new RadioMenuItem("ascending");
		RadioMenuItem descendingUpdate = new RadioMenuItem("descending");
		//add radio menu items to toggle group
		ascendingUpdate.setToggleGroup(byReviewsToggle);
		descendingUpdate.setToggleGroup(byReviewsToggle);
		
		ascendingUpdate.setOnAction(handler ->{
			//Sorting by last updated ascending
			System.out.println("Sorting by last updated ascending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByLastUpdatedAsc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByLastUpdatedAsc());
				tableView.setItems(listOfApps);
			}
		});
		
		descendingUpdate.setOnAction(handler ->{
			//Sorting by last updated ascending
			System.out.println("Sorting by last updated descending");
			if(isfilterredList) {
				Collections.sort(tableView.getItems(), new CustomSort.SortByLastUpdatedDesc());
			}else {
				Collections.sort(listOfApps, new CustomSort.SortByLastUpdatedDesc());
				tableView.setItems(listOfApps);
			}
		});
		sortByLastUpdatedDate.getItems().addAll(ascendingUpdate, descendingUpdate);
		
		sortMenuItem.getItems().clear();
		sortMenuItem.getItems().addAll(sortByReviews, sortByScore, sortByPrice, sortByDownloads, sortByTitle, sortBySubTitle, sortByLastUpdatedDate);
	}
	
	private void setupMenuBarFilter() {
		// TODO Auto-generated method stub
		
		Menu filterPrice = new Menu("Price");
		
		ToggleGroup priceToggle = new ToggleGroup();
		RadioMenuItem freeFilter = new RadioMenuItem("Free");
		RadioMenuItem paidFilter = new RadioMenuItem("Paid");
		//add radio menu items to toggle group
		freeFilter.setToggleGroup(priceToggle);
		paidFilter.setToggleGroup(priceToggle);
		
		freeFilter.setOnAction(handler ->{
			//Sorting by reviews ascending
			System.out.println("filtering free apps");
			//Filter Free
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.isFree()) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.isFree()) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			filterPrice.setDisable(true);
		});
		
		paidFilter.setOnAction(handler ->{
			//Sorting by reviews ascending
			System.out.println("filtering paid apps");
			//filter paid
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList && !freeFilter.isSelected()) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(!app.isFree()) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(!app.isFree()) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			filterPrice.setDisable(true);
		});
		
		
		filterPrice.getItems().addAll(freeFilter, paidFilter);
		
		//create genres menu
		Menu genres = new Menu("Genre");
		
		
		//create other class filters
		Menu otherFilters = new Menu("Other");
		//otherFilters.setDisable();
		RadioMenuItem artanddesign = new RadioMenuItem("Art & Design");
		artanddesign.setOnAction(e ->{
			//Filter Art & Design apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Art & Design")) {
						filterredList.add(app);
						System.out.println(app.getGenre());
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Art & Design")) {
						filterredList.add(app);
						System.out.println(app.getGenre());
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem autoandvehicles = new RadioMenuItem("Auto & Vehicles");
		autoandvehicles.setOnAction(e ->{
			//Filter Auto & Vehicles apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Auto & Vehicles")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Auto & Vehicles")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem beauty = new RadioMenuItem("Beauty");
		beauty.setOnAction(e ->{
			//Filter Beauty apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Beauty")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Beauty")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem booksandreferences = new RadioMenuItem("Books & References");
		booksandreferences.setOnAction(e ->{
			//Filter Books & References apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Books & References")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Books & References")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem business = new RadioMenuItem("Business");
		business.setOnAction(e ->{
			//Filter business apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Business")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Business")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem comics = new RadioMenuItem("Comics");
		comics.setOnAction(e ->{
			//Filter comics apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Comics")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Comics")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem communication = new RadioMenuItem("Communication");
		communication.setOnAction(e ->{
			//Filter communication apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Communication")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Communication")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem dating = new RadioMenuItem("Dating");
		dating.setOnAction(e ->{
			//Filter dating apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Dating")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Dating")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem education = new RadioMenuItem("Education");
		education.setOnAction(e ->{
			//Filter education apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Education")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Education")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem entertainment = new RadioMenuItem("Entertainement");
		entertainment.setOnAction(e ->{
			//Filter entertainment apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Entertainment")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Entertainment")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem events = new RadioMenuItem("Events");
		events.setOnAction(e ->{
			//Filter events apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Events")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Events")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem finance = new RadioMenuItem("Finance");
		finance.setOnAction(e ->{
			//Filter fincance apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Finance")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Finance")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem foodanddrink = new RadioMenuItem("Food & Drinks");
		foodanddrink.setOnAction(e ->{
			//Filter food & drinks apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Food & Drinks")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Food & Drinks")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem healthandfitness = new RadioMenuItem("Health & Fitness");
		healthandfitness.setOnAction(e ->{
			//Filter health & fitness apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Health & Fitness")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Health & Fitness")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem houseandhome = new RadioMenuItem("House & Home");
		houseandhome.setOnAction(e ->{
			//Filter house & home apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("House & Home")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("House & Home")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem librariesanddemo = new RadioMenuItem("Libraries & Demo");
		librariesanddemo.setOnAction(e ->{
			//Filter libraries & demo apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Libraries & Demo")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Libraries & Demo")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem lifestyle = new RadioMenuItem("Lifestyle");
		lifestyle.setOnAction(e ->{
			//Filter lifestyle apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Lifestyle")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Lifestyle")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem mapsandnavigation = new RadioMenuItem("Maps & Navigation");
		mapsandnavigation.setOnAction(e ->{
			//Filter maps & navigation apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Maps & Navigation")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Maps & Navigation")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem medical = new RadioMenuItem("Medical");
		medical.setOnAction(e ->{
			//Filter medical apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Medical")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Medical")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem musicandaudio = new RadioMenuItem("Music & Audio");
		musicandaudio.setOnAction(e ->{
			//Filter music & audio apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Music & Audio")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Music & Audio")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem newsandmagazines = new RadioMenuItem("News & Magazines");
		newsandmagazines.setOnAction(e ->{
			//Filter news & magazines apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("News & Magazines")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("News & Magazines")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem paranting = new RadioMenuItem("Paranting");
		paranting.setOnAction(e ->{
			//Filter paranting apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Paranting")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Paranting")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem personalisation = new RadioMenuItem("Personalisation");
		personalisation.setOnAction(e ->{
			//Filter personalisation apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Personalisation")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Personalisation")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem photography = new RadioMenuItem("Photography");
		photography.setOnAction(e ->{
			//Filter photography apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Photography")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Photography")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem productivity = new RadioMenuItem("Productivity");
		productivity.setOnAction(e ->{
			//Filter productivity apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Productivity")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Productivity")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem shopping = new RadioMenuItem("Shopping");
		shopping.setOnAction(e ->{
			//Filter shopping apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Shopping")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Shopping")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem social = new RadioMenuItem("Social");
		social.setOnAction(e ->{
			//Filter social apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Social")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Social")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem sports = new RadioMenuItem("Sports");
		sports.setOnAction(e ->{
			//Filter sports apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Sports")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Sports")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem tools = new RadioMenuItem("Tools");
		tools.setOnAction(e ->{
			//Filter tools apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Tools")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Tools")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem travelandlocal = new RadioMenuItem("Travel & Local");
		travelandlocal.setOnAction(e ->{
			//Filter travel & local apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Travel & Local")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Travel & Local")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem videoplayersandeditors = new RadioMenuItem("Video Players & Editors");
		videoplayersandeditors.setOnAction(e ->{
			//Filter video players & editors apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Video Players & Editors")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Video Players & Editors")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem weather = new RadioMenuItem("Weather");
		weather.setOnAction(e ->{
			//Filter weather apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Weather")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Weather")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
			
		});
		
		otherFilters.getItems().addAll(artanddesign, autoandvehicles, beauty, booksandreferences, business, comics, communication, 
				dating, education, entertainment, events, finance, foodanddrink, healthandfitness, houseandhome, librariesanddemo, 
				lifestyle, mapsandnavigation, medical, musicandaudio, newsandmagazines, paranting, personalisation, photography, 
				productivity, shopping, social, sports, tools, travelandlocal, videoplayersandeditors, weather);

		
		
		//create game genre menu items
		Menu gameGenres = new Menu("Games"); 
		
		RadioMenuItem action = new RadioMenuItem("Action");
		action.setOnAction(e ->{
			//Filter action games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Action")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Action")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem adventure = new RadioMenuItem("Adventure");
		adventure.setOnAction(e ->{
			//Filter adventure games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Adventure")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Adventure")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem arcade = new RadioMenuItem("Arcade");
		arcade.setOnAction(e ->{
			//Filter arcade games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Arcade")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Arcade")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem board = new RadioMenuItem("Board");
		board.setOnAction(e ->{
			//Filter board games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Board")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Board")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem card = new RadioMenuItem("Card");
		card.setOnAction(e ->{
			//Filter card games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Card")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Card")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem casino = new RadioMenuItem("Casino");
		casino.setOnAction(e ->{
			//Filter casino games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Casino")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Casino")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem casual = new RadioMenuItem("Casual");
		casual.setOnAction(e ->{
			//Filter casual games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Casual")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Casual")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem educational = new RadioMenuItem("Educational");
		educational.setOnAction(e ->{
			//Filter educational games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Educational")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Educational")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem music = new RadioMenuItem("Music");
		music.setOnAction(e ->{
			//Filter music games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Music")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Music")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem puzzle = new RadioMenuItem("Puzzle");
		puzzle.setOnAction(e ->{
			//Filter puzzle games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Puzzle")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Puzzle")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem racing = new RadioMenuItem("Racing");
		racing.setOnAction(e ->{
			//Filter racing games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Racing")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Racing")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem roleplaying = new RadioMenuItem("Role Playing");
		roleplaying.setOnAction(e ->{
			//Filter role playing games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Role Playing")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Role Playing")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem simulation = new RadioMenuItem("Simulation");
		simulation.setOnAction(e ->{
			//Filter simulation games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Simulation")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Simulation")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem sportsgame = new RadioMenuItem("Sports");
		sportsgame.setOnAction(e ->{
			//Filter sports games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Sports")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Sports")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem strategy = new RadioMenuItem("Strategy");
		strategy.setOnAction(e ->{
			//Filter strategy games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Strategy")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Strategy")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem trivia = new RadioMenuItem("Trivia");
		trivia.setOnAction(e ->{
			//Filter triva games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Trivia")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Trivia")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem word = new RadioMenuItem("Word");
		word.setOnAction(e ->{
			//Filter word games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Word")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Word")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		
		gameGenres.getItems().addAll(action, adventure, arcade, board, card, casino, casual, educational, 
				music, puzzle, racing, roleplaying,simulation, sportsgame, strategy, trivia, word);
		
		//create family genre section
		Menu family = new Menu("Family");
		
		RadioMenuItem actionandadventure = new RadioMenuItem("Action & Adventure");
		actionandadventure.setOnAction(e ->{
			//Filter action & adventure family games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Action & Adventure")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Action & Adventure")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem braingames = new RadioMenuItem("Brain Games");
		braingames.setOnAction(e ->{
			//Filter brain family games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Brain Games")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Brain Games")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem creativity = new RadioMenuItem("Creativity");
		creativity.setOnAction(e ->{
			//Filter creativity family games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Creativity")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Creativity")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem educationfamily = new RadioMenuItem("Education");
		education.setOnAction(e ->{
			//Filter education family games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Education")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Education")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem musicandvideo = new RadioMenuItem("Music & Video");
		musicandvideo.setOnAction(e ->{
			//Filter music and video family games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Music & Video")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Music & Video")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		RadioMenuItem pretendplay = new RadioMenuItem("Pretend Play");
		pretendplay.setOnAction(e ->{
			//Filter pretend play family games
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Pretend Play")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getGenre() != null && app.getGenre().contains("Pretend Play")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			genres.setDisable(true);
		});
		
		family.getItems().addAll(actionandadventure, braingames, creativity, educationfamily, musicandvideo, pretendplay);
		
		
		//create content rating filter
		
		Menu content_rating = new Menu("Content Rating");
		
		RadioMenuItem pegi3 = new RadioMenuItem("PEGI 3");
		pegi3.setOnAction(e ->{
			//Filter pegi 3 content rating apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 3")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 3")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			content_rating.setDisable(true);
		});
		RadioMenuItem pegi7 = new RadioMenuItem("PEGI 7");
		pegi7.setOnAction(e ->{
			//Filter pegi 7 content rating apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 7")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 7")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			content_rating.setDisable(true);
		});
		RadioMenuItem pegi12 = new RadioMenuItem("PEGI 12");
		pegi12.setOnAction(e ->{
			//Filter pegi 12 content rating apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 12")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 12")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			content_rating.setDisable(true);
		});
		RadioMenuItem pegi16 = new RadioMenuItem("PEGI 16");
		pegi16.setOnAction(e ->{
			//Filter pegi 16 content rating apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 16")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 16")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			content_rating.setDisable(true);
		});
		RadioMenuItem pegi18 = new RadioMenuItem("PEGI 18");
		pegi18.setOnAction(e ->{
			//Filter pegi 18 content rating apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 18")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("PEGI 18")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			content_rating.setDisable(true);
		});
		RadioMenuItem parentalguidance = new RadioMenuItem("Parental Guidance");
		parentalguidance.setOnAction(e ->{
			//Filter parental guidance content rating apps
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("Parental guidance")) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getContentRating() != null && app.getContentRating().contains("Parental guidance")) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			content_rating.setDisable(true);
		});
		
		content_rating.getItems().addAll(pegi3, pegi7, pegi12, pegi16, pegi18, parentalguidance);
		
		//filter for downloads average, upper and lower with up to and over ranges
		
		Menu downloads = new Menu("Downloads");
		
		Menu averageDownloads = new Menu("Avg. Downloads");
		Menu lowerDownloads = new Menu("Lower Downloads");
		Menu upperDownloads = new Menu("Upper Downloads");
		Menu avgupto = new Menu("Up To");
		Menu avgover = new Menu("Over");
		Menu lowerupto = new Menu("Up To");
		Menu lowerover = new Menu("Over");
		Menu upperupto = new Menu("Up To");
		Menu upperover = new Menu("Over");
		
		
		RadioMenuItem avgupto1000 = new RadioMenuItem("1 000");
		avgupto1000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average <= 1000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average <= 1000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem avgupto10000 = new RadioMenuItem("10 000");
		avgupto10000.setOnAction(e ->{
			//Filter average downloads up to 10000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average <= 10000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average <= 10000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem avgupto50000 = new RadioMenuItem("50 000");
		avgupto50000.setOnAction(e ->{
			//Filter average downloads up to 50000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average <= 50000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average <= 50000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem avgupto100000 = new RadioMenuItem("100 000");
		avgupto100000.setOnAction(e ->{
			//Filter average downloads up to 100000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average <= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average <= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem avgover100000 = new RadioMenuItem("100 000");
		avgover100000.setOnAction(e ->{
			//Filter average downloads over 100000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem avgover500000 = new RadioMenuItem("500 000");
		avgover500000.setOnAction(e ->{
			//Filter average downloads over 100000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 500000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 500000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem avgover1000000 = new RadioMenuItem("1 000 000");
		avgover1000000.setOnAction(e ->{
			//Filter average downloads over 100000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 1000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 1000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem avgover2000000 = new RadioMenuItem("2 000 000");
		avgover2000000.setOnAction(e ->{
			//Filter average downloads over 100000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 2000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 2000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem avgover5000000 = new RadioMenuItem("5 000 000");
		avgover5000000.setOnAction(e ->{
			//Filter average downloads over 100000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 5000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
						if(average >= 5000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		
		
		avgupto.getItems().addAll(avgupto1000, avgupto10000, avgupto50000, avgupto100000);
		avgover.getItems().addAll(avgover100000, avgover500000, avgover1000000, avgover2000000, avgover5000000);
		averageDownloads.getItems().addAll(avgupto, avgover);
		
		
		
		
		RadioMenuItem lowerupto1000 = new RadioMenuItem("1 000");
		lowerupto1000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower <= 1000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower <= 1000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem lowerupto10000 = new RadioMenuItem("10 000");
		lowerupto10000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower <= 10000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower <= 10000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		
		RadioMenuItem lowerupto50000 = new RadioMenuItem("50 000");
		lowerupto50000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower <= 50000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower <= 50000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem lowerupto100000 = new RadioMenuItem("100 000");
		lowerupto100000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower <= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower <= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem lowerover100000 = new RadioMenuItem("100 000");
		lowerover100000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem lowerover500000 = new RadioMenuItem("500 000");
		lowerover500000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 500000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 500000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem lowerover1000000 = new RadioMenuItem("1 000 000");
		lowerover1000000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 1000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 1000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem lowerover2000000 = new RadioMenuItem("2 000 000");
		lowerover2000000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 2000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 2000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem lowerover5000000 = new RadioMenuItem("5 000 000");
		lowerover5000000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 5000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int lower = Integer.parseInt(down[0].trim());
						if(lower >= 5000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		lowerupto.getItems().addAll(lowerupto1000, lowerupto10000, lowerupto50000, lowerupto100000);
		lowerover.getItems().addAll(lowerover100000, lowerover500000, lowerover1000000, lowerover2000000, lowerover5000000);
		lowerDownloads.getItems().addAll(lowerupto, lowerover);
		
		
		
		RadioMenuItem upperupto1000 = new RadioMenuItem("1 000");
		upperupto1000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper <= 1000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper <= 1000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem upperupto10000 = new RadioMenuItem("10 000");
		upperupto10000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper <= 10000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper <= 10000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		
		RadioMenuItem upperupto50000 = new RadioMenuItem("50 000");
		upperupto50000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper <= 50000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper <= 50000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem upperupto100000 = new RadioMenuItem("100 000");
		upperupto100000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper <= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper <= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem upperover100000 = new RadioMenuItem("100 000");
		upperover100000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 100000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem upperover500000 = new RadioMenuItem("500 000");
		upperover500000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 500000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 500000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem upperover1000000 = new RadioMenuItem("1 000 000");
		upperover1000000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 1000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 1000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem upperover2000000 = new RadioMenuItem("2 000 000");
		upperover2000000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 2000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 2000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		RadioMenuItem upperover5000000 = new RadioMenuItem("5 000 000");
		upperover5000000.setOnAction(e ->{
			//Filter average downloads up to 1000
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 5000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					try {
						String[] down = app.getInstalls().replaceAll(",", "").split("-");
						int upper = Integer.parseInt(down[1].trim());
						if(upper >= 5000000) {
							filterredList.add(app);
						}
					}catch(Exception ex) {
						//pass
						System.out.println("Something went wrong in downloads filter");
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			downloads.setDisable(true);
		});
		
		upperupto.getItems().addAll(upperupto1000, upperupto10000, upperupto50000, upperupto100000);
		upperover.getItems().addAll(upperover100000, upperover500000, upperover1000000, upperover2000000, upperover5000000);
		upperDownloads.getItems().addAll(upperupto, upperover);
		
		downloads.getItems().addAll(averageDownloads, lowerDownloads, upperDownloads);
		
		
		//setup reviews filter
		
		Menu reviews = new Menu("Reviews");
		Menu reviewsupto = new Menu("Up To");
		Menu reviewsover = new Menu("Over");
		
		RadioMenuItem reviewsupto500 = new RadioMenuItem("500");
		reviewsupto500.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 500) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 500) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
		RadioMenuItem reviewsupto1000 = new RadioMenuItem("1 000");
		reviewsupto1000.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 1000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 1000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
		RadioMenuItem reviewsupto5000 = new RadioMenuItem("5 000");
		reviewsupto5000.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 5000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 5000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
		
		RadioMenuItem reviewsupto10000 = new RadioMenuItem("10 000");
		reviewsupto10000.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 10000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 10000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
		RadioMenuItem reviewsupto50000 = new RadioMenuItem("50 000");
		reviewsupto50000.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 50000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 50000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
		RadioMenuItem reviewsupto100000 = new RadioMenuItem("100 000");
		reviewsupto100000.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 100000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum <= 100000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
		RadioMenuItem reviewsover100000 = new RadioMenuItem("100 000");
		reviewsover100000.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum >= 100000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum >= 100000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
		RadioMenuItem reviewsover500000 = new RadioMenuItem("500 000");
		reviewsover500000.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum >= 500000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum >= 500000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
		RadioMenuItem reviewsover1000000 = new RadioMenuItem("1 000 000");
		reviewsover1000000.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum >= 1000000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum >= 1000000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
		RadioMenuItem reviewsover5000000 = new RadioMenuItem("5 000 000");
		reviewsover5000000.setOnAction(e ->{
			//Filter reviews up to 500
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum >= 5000000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getReviews() != null) {
						try {
							String rev = app.getReviews().replaceAll(",", "").trim();
							int revNum = Integer.parseInt(rev);
							if(revNum >= 5000000) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in reviews filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			reviews.setDisable(true);
		});
		
	
		
		reviewsupto.getItems().addAll(reviewsupto500, reviewsupto1000, reviewsupto5000, reviewsupto10000, reviewsupto50000, reviewsupto100000);
		reviewsover.getItems().addAll(reviewsover100000, reviewsover500000, reviewsover1000000, reviewsover5000000);
		reviews.getItems().addAll(reviewsupto, reviewsover);
		
		
		//setup score filters
		
		Menu score = new Menu("Score");
		
		RadioMenuItem scoremostis5 = new RadioMenuItem("5-4");
		score.getItems().add(scoremostis5);
		scoremostis5.setOnAction(e ->{
			//Filter score most popular is 5*
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum >= 4 && scoreNum <=5) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum >= 4 && scoreNum <=5) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			score.setDisable(true);
		});
		
		RadioMenuItem scoremostis4 = new RadioMenuItem("4-3");
		score.getItems().add(scoremostis4);
		scoremostis4.setOnAction(e ->{
			//Filter score most popular is 5*
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum >= 3 && scoreNum <=4) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum >= 3 && scoreNum <=4) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			score.setDisable(true);
		});
		
		RadioMenuItem scoremostis3 = new RadioMenuItem("3-2");
		score.getItems().add(scoremostis3);
		scoremostis3.setOnAction(e ->{
			//Filter score most popular is 5*
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum >= 2 && scoreNum <=3) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum >= 2 && scoreNum <=3) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			score.setDisable(true);
		});
		
		RadioMenuItem scoremostis2 = new RadioMenuItem("2-1");
		score.getItems().add(scoremostis2);
		scoremostis2.setOnAction(e ->{
			//Filter score most popular is 5*
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum >= 1 && scoreNum <=2) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum >= 1 && scoreNum <=2) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			score.setDisable(true);
		});
		
		
//		int score5 = Integer.parseInt(app.getScore5().replaceAll(",", "").replaceAll(" ", "").trim());
//		int score4 = Integer.parseInt(app.getScore4().replaceAll(",", "").replaceAll(" ", "").trim());
//		int score3 = Integer.parseInt(app.getScore3().replaceAll(",", "").replaceAll(" ", "").trim());
//		int score2 = Integer.parseInt(app.getScore2().replaceAll(",", "").replaceAll(" ", "").trim());
//		int score1 = Integer.parseInt(app.getScore1().replaceAll(",", "").replaceAll(" ", "").trim());
//		if(score1 > score5 && score1 > score4 && score1 > score3 && score1 > score2) {
//			filterredList.add(app);
//		}
		
		RadioMenuItem scoremostis1 = new RadioMenuItem("Less than 1");
		score.getItems().add(scoremostis1);
		scoremostis1.setOnAction(e ->{
			//Filter score most popular is 5*
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum < 1) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getScore() != null) {
						try {
							double scoreNum = Double.parseDouble(app.getScore().replaceAll(" ", "").trim());
							if(scoreNum < 1) {
								filterredList.add(app);
							}
						}catch(Exception ex) {
							//pass
							System.out.println("Something went wrong in score filter");
						}
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			score.setDisable(true);
		});
		
		
		//setup interactive elements filters
		
		Menu interactive = new Menu("Interactive Elements");
		RadioMenuItem interactiveyes = new RadioMenuItem("Yes");
		RadioMenuItem interactiveno = new RadioMenuItem("No");
		interactive.getItems().addAll(interactiveyes, interactiveno);
		interactiveyes.setOnAction(e ->{
			//Filter interactive elements yes
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getInteractiveElements() != null) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getInteractiveElements() != null) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			interactive.setDisable(true);
		});
		
		interactiveno.setOnAction(e ->{
			//Filter interactive elements yes
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getInteractiveElements() == null) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getInteractiveElements() == null) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			interactive.setDisable(true);
		});
				
		//setup in-app-purchases filters
		Menu inappPurchase = new Menu("In-App Purchases");
		RadioMenuItem inappPurchaseyes = new RadioMenuItem("Yes");
		RadioMenuItem inappPurchaseno = new RadioMenuItem("No");
		inappPurchase.getItems().addAll(inappPurchaseyes, inappPurchaseno);
		
		inappPurchaseyes.setOnAction(e ->{
			//Filter interactive elements yes
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getInAppPurchases() != null) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getInAppPurchases() != null) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			inappPurchase.setDisable(true);
		});
		
		inappPurchaseno.setOnAction(e ->{
			//Filter interactive elements yes
			ObservableList<App> filterredList = FXCollections.observableArrayList();
			if(isfilterredList) {
				ObservableList<App> list = tableView.getItems();
				for(int i=0; i<list.size(); i++) {
					App app = list.get(i);
					if(app.getInAppPurchases() == null) {
						filterredList.add(app);
					}
				}
			}else {
				for(int i=0; i<listOfApps.size(); i++) {
					App app = listOfApps.get(i);
					if(app.getInAppPurchases() == null) {
						filterredList.add(app);
					}
				}
			}
			tableView.setItems(filterredList);
			isfilterredList = true;
			inappPurchase.setDisable(true);
		});
		
		//reset all filters
		
		MenuItem resetAllFilters = new MenuItem("Reset All");
		
		resetAllFilters.setOnAction(handler ->{
			if(isfilterredList) {
				tableView.setItems(listOfApps);
				isfilterredList = false;
				//reset price filters
				if(filterPrice.isDisable()) {
					filterPrice.setDisable(false);
					filterPrice.getItems().forEach((item) ->{
						RadioMenuItem radioItem  = (RadioMenuItem)item;
						radioItem.setSelected(false);
					});
				}
				//reset other filters
				if(genres.isDisable()) {
					genres.setDisable(false);
					otherFilters.getItems().forEach((item) ->{
						RadioMenuItem radioItem  = (RadioMenuItem)item;
						radioItem.setSelected(false);
					});
				}
				
				gameGenres.getItems().forEach((item) ->{
					RadioMenuItem radioItem  = (RadioMenuItem)item;
					radioItem.setSelected(false);
				});
				
				family.getItems().forEach((item) ->{
					RadioMenuItem radioItem  = (RadioMenuItem)item;
					radioItem.setSelected(false);
				});
				
				if(content_rating.isDisable()) {
					content_rating.setDisable(false);
					content_rating.getItems().forEach((item) ->{
						RadioMenuItem radioItem  = (RadioMenuItem)item;
						radioItem.setSelected(false);
					});
				}
				
				if(downloads.isDisable()) {
					downloads.setDisable(false);
					downloads.getItems().forEach((item) ->{
						Menu menu  = (Menu)item;
						menu.getItems().forEach((item2) ->{
							Menu menu2  = (Menu)item2;
							menu2.getItems().forEach((item3) ->{
								RadioMenuItem radioItem  = (RadioMenuItem)item3;
								radioItem.setSelected(false);
							});
						});
					});
				}
				
				if(reviews.isDisable()) {
					reviews.setDisable(false);
					reviews.getItems().forEach((item) ->{
						Menu menu  = (Menu)item;
						menu.getItems().forEach((item2) ->{
							RadioMenuItem radioItem  = (RadioMenuItem)item2;
							radioItem.setSelected(false);
						});
					});
				}
				
				if(score.isDisable()) {
					score.setDisable(false);
					score.getItems().forEach((item) ->{
						RadioMenuItem radioItem  = (RadioMenuItem)item;
						radioItem.setSelected(false);
					});
				}
				
				if(interactive.isDisable()) {
					interactive.setDisable(false);
					interactive.getItems().forEach((item) ->{
						RadioMenuItem radioItem  = (RadioMenuItem)item;
						radioItem.setSelected(false);
					});
				}
				
				if(inappPurchase.isDisable()) {
					inappPurchase.setDisable(false);
					inappPurchase.getItems().forEach((item) ->{
						RadioMenuItem radioItem  = (RadioMenuItem)item;
						radioItem.setSelected(false);
					});
				}
			}
		});
		
		genres.getItems().addAll(gameGenres, family ,otherFilters);
		
		filterMenuItem.getItems().clear();
		filterMenuItem.getItems().addAll(filterPrice, genres, content_rating , downloads, reviews, score, interactive, inappPurchase, new SeparatorMenuItem(), resetAllFilters);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
