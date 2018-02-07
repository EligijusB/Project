package com.yourcompany;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GraphWindowController implements Initializable {
	private ObservableList<App> list;
	private String paramOne;
	private String paramTwo;
	
	@FXML
	private Label bigRlabel;
	
	@FXML
	private StackPane stackpane;
	
	@FXML
	private ScatterChart<Number, Number> scatterChart;
	
	private SecondWindowController swc;
	
	private XYChart.Series<Number, Number> seriesMain = new XYChart.Series<Number, Number>();
	private XYChart.Series<Number, Number> serieslogY = new XYChart.Series<Number, Number>();
	private XYChart.Series<Number, Number> serieslogX = new XYChart.Series<Number, Number>();
	private XYChart.Series<Number, Number> serieslog = new XYChart.Series<Number, Number>();
	
	private Number lowerBoundY = 0, upperBoundY = 0, lowerBoundX = 0, upperBoundX = 0;
	
	private ScatterChart<Number, Number> logYchart, logXchart, logChart;
	
	private ArrayList<Number> xAxis, yAxis;
	
	private boolean mainchart = false, logxchart = false, logychart = false, logchart = false;
	
	public GraphWindowController(ObservableList<App> list, String paramOne, String paramTwo, SecondWindowController swc) {
		super();
		this.list = list;
		this.paramOne = paramOne;
		this.paramTwo = paramTwo;
		this.swc = swc;
	}
	
	
	public void saveAsPNG() {
		//get path and name of the file
		String path = "";
		String name = "";
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("File Path Dialog");
		dialog.setHeaderText("Path wehre file will be saved");
		dialog.setContentText("Please enter absolute path:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    //ask for the name of the file
			
			if(result.get().endsWith("/")) {
				path = result.get();
			}else {
				path = result.get() + "/";
			}
			TextInputDialog dialog2 = new TextInputDialog("");
			dialog2.setTitle("File Name Dialog");
			dialog2.setHeaderText("Name of the image file");
			dialog2.setContentText("Please enter the name:");
			
			Optional<String> result2 = dialog2.showAndWait();
			if(result2.isPresent()) {
				if(result2.get().endsWith(".png")) {
					name = result2.get();
				}else {
					name = result2.get() + ".png";
				}
			}

		}
			if(!path.isEmpty() && !name.isEmpty()) {
				if(mainchart) {
					try {
						FileChooser fileChooser = new FileChooser();
			            fileChooser.setTitle("Save Image");
			            // Set extension filter
			            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
			            "PNG files (*.png)", "*.png");
			            fileChooser.getExtensionFilters().add(extFilter);
			            File file = fileChooser.showSaveDialog(swc.graphWindowStage);
			            if (file != null) {
				            	// Make sure it has the correct extension
				            	if (!file.getPath().endsWith(".png")) {
				            		file = new File(file.getPath() + ".png");
				            	}
							WritableImage image = scatterChart.snapshot(new SnapshotParameters(), null);
			                	ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			            }
						
						
				    } catch (IOException e) {
				        // TODO: handle exception here
				    		Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText(null);
						alert.setContentText("Soory, an unexpected error occured. Graph could not be exported.");
						alert.showAndWait();
				    }
				}
				else if(logxchart) {
					try {
						FileChooser fileChooser = new FileChooser();
			            fileChooser.setTitle("Save Image");
			            // Set extension filter
			            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
			            "PNG files (*.png)", "*.png");
			            fileChooser.getExtensionFilters().add(extFilter);
			            File file = fileChooser.showSaveDialog(swc.graphWindowStage);
			            if (file != null) {
				            	// Make sure it has the correct extension
				            	if (!file.getPath().endsWith(".png")) {
				            		file = new File(file.getPath() + ".png");
				            	}
							WritableImage image = logXchart.snapshot(new SnapshotParameters(), null);
			                	ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			            }
				    } catch (IOException e) {
				        // TODO: handle exception here
				    		Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText(null);
						alert.setContentText("Soory, an unexpected error occured. Graph could not be exported.");
						alert.showAndWait();
				    }
				}
				else if(logychart) {
					try {
						FileChooser fileChooser = new FileChooser();
			            fileChooser.setTitle("Save Image");
			            // Set extension filter
			            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
			            "PNG files (*.png)", "*.png");
			            fileChooser.getExtensionFilters().add(extFilter);
			            File file = fileChooser.showSaveDialog(swc.graphWindowStage);
			            if (file != null) {
				            	// Make sure it has the correct extension
				            	if (!file.getPath().endsWith(".png")) {
				            		file = new File(file.getPath() + ".png");
				            	}
							WritableImage image = logYchart.snapshot(new SnapshotParameters(), null);
			                	ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			            }
				    } catch (IOException e) {
				        // TODO: handle exception here
				    		Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText(null);
						alert.setContentText("Soory, an unexpected error occured. Graph could not be exported.");
						alert.showAndWait();
				    }
				}
				else if(logchart) {
					try {
						FileChooser fileChooser = new FileChooser();
			            fileChooser.setTitle("Save Image");
			            // Set extension filter
			            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
			            "PNG files (*.png)", "*.png");
			            fileChooser.getExtensionFilters().add(extFilter);
			            File file = fileChooser.showSaveDialog(swc.graphWindowStage);
			            if (file != null) {
				            	// Make sure it has the correct extension
				            	if (!file.getPath().endsWith(".png")) {
				            		file = new File(file.getPath() + ".png");
				            	}
							WritableImage image = logChart.snapshot(new SnapshotParameters(), null);
			                	ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			            }
				    } catch (IOException e) {
				        // TODO: handle exception here
				    		Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText(null);
						alert.setContentText("Soory, an unexpected error occured. Graph could not be exported.");
						alert.showAndWait();
				    }
				}
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Snaphot of the graph was successfully exported as a PNG image.");
				alert.showAndWait();
			}
		
	}
	
	public void showLogAxis() {
		if(logChart == null) {
			if(Double.parseDouble(bigRlabel.getText().split("=")[1].trim()) < 0.85) {
				try {
					System.out.println("In logChart == null");
					LogarithmicAxis logAxisX = new LogarithmicAxis(lowerBoundX.doubleValue()*0.5, upperBoundX.doubleValue()*1.5);
					logAxisX.setLabel(paramOne.toUpperCase());
					LogarithmicAxis logAxisY = new LogarithmicAxis(lowerBoundY.doubleValue()*0.5, upperBoundY.doubleValue()*1.5);
					logAxisY.setLabel(paramTwo.toUpperCase());
					logChart = new ScatterChart<Number, Number>(logAxisX, logAxisY);
					serieslog.setName(paramOne + " / " + paramTwo);
					logChart.setLegendSide(Side.TOP);
					logChart.getData().addAll(serieslog);
					//setup tooltip
					
					for (XYChart.Data<Number, Number> d : serieslog.getData()) {
						System.out.println("In tooltip");
				    		Tooltip.install(d.getNode(), new Tooltip("x = " +
				                d.getXValue() + "\n" +
				                        "y = " + d.getYValue()));
				
				        //Adding class on hover
				        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
				
				        //Removing class on exit
				        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
					}
					
					
					stackpane.getChildren().clear();
					stackpane.getChildren().add(logChart);
					mainchart = false;
					logxchart = false;
					logychart = false;
					logchart = true;
				
				}catch(Exception e) {
					Alert alert = new Alert(AlertType.ERROR);
		    			alert.setTitle("Error Dialog");
		    			alert.setHeaderText(null);
		    			alert.setContentText("Soory, an unexpected error occured. Graph could not be shown.");
		    			alert.showAndWait();
		    			//show main graph
		    			showMainGraph();
				}
			}else {
				Alert alert = new Alert(AlertType.ERROR);
	    			alert.setTitle("Error Dialog");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Soory, Log Axis graph cannot be shown for this amount of data.");
	    			alert.showAndWait();
			}
		}else {
			System.out.println("In else");
			stackpane.getChildren().clear();
			stackpane.getChildren().add(logChart);
			mainchart = false;
			logxchart = false;
			logychart = false;
			logchart = true;
		}
	}
	
	
	
	public void showLogAxisX() {
		if(logXchart == null) {
			if(Double.parseDouble(bigRlabel.getText().split("=")[1].trim()) < 0.85) {
				try {
					LogarithmicAxis logAxis = new LogarithmicAxis(lowerBoundX.doubleValue()*0.5, upperBoundX.doubleValue()*1.5);
					logAxis.setLabel(paramOne.toUpperCase());
					NumberAxis numberAxis = new NumberAxis();
					numberAxis.setLabel(paramTwo.toUpperCase());
					logXchart = new ScatterChart<Number, Number>(logAxis, numberAxis);
					serieslogX.setName(paramOne + " / " + paramTwo);
					logXchart.setLegendSide(Side.TOP);
					logXchart.getData().addAll(serieslogX);
					//setup tooltip
					
					for (XYChart.Data<Number, Number> d : serieslogX.getData()) {
			        	
				    		Tooltip.install(d.getNode(), new Tooltip("x = " +
				                d.getXValue() + "\n" +
				                        "y = " + d.getYValue()));
				
				        //Adding class on hover
				        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
				
				        //Removing class on exit
				        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
					}
			
					stackpane.getChildren().clear();
					stackpane.getChildren().add(logXchart);
					mainchart = false;
					logxchart = true;
					logychart = false;
					logchart = false;
				}catch(Exception e) {
					Alert alert = new Alert(AlertType.ERROR);
		    			alert.setTitle("Error Dialog");
		    			alert.setHeaderText(null);
		    			alert.setContentText("Soory, an unexpected error occured. Graph could not be shown.");
		    			alert.showAndWait();
		    			//show main graph
		    			showMainGraph();
				}
			}else {
				Alert alert = new Alert(AlertType.ERROR);
	    			alert.setTitle("Error Dialog");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Soory, Log Axis graph cannot be shown for this amount of data.");
	    			alert.showAndWait();
			}
		}else {
			stackpane.getChildren().clear();
			stackpane.getChildren().add(logXchart);
			mainchart = false;
			logxchart = true;
			logychart = false;
			logchart = false;
		}
	}
	
	
	public void showLogAxisY() {
		if(logYchart == null) {
			if(Double.parseDouble(bigRlabel.getText().split("=")[1].trim()) < 0.85) {
				try {
					LogarithmicAxis logAxis = new LogarithmicAxis(lowerBoundY.doubleValue()*0.5, upperBoundY.doubleValue()*1.5);
					logAxis.setLabel(paramTwo.toUpperCase());
					NumberAxis numberAxis = new NumberAxis();
					numberAxis.setLabel(paramOne.toUpperCase());
					logYchart = new ScatterChart<Number, Number>(numberAxis, logAxis);
					serieslogY.setName(paramOne + " / " + paramTwo);
					logYchart.setLegendSide(Side.TOP);
					logYchart.getData().addAll(serieslogY);
					//setup tooltip
					
					for (XYChart.Data<Number, Number> d : serieslogY.getData()) {
			        	
				    		Tooltip.install(d.getNode(), new Tooltip("x = " +
				                d.getXValue() + "\n" +
				                        "y = " + d.getYValue()));
				
				        //Adding class on hover
				        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
				
				        //Removing class on exit
				        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
					}
					stackpane.getChildren().clear();
					stackpane.getChildren().add(logYchart);
					mainchart = false;
					logxchart = false;
					logychart = true;
					logchart = false;
				}catch(Exception e) {
					Alert alert = new Alert(AlertType.ERROR);
		    			alert.setTitle("Error Dialog");
		    			alert.setHeaderText(null);
		    			alert.setContentText("Soory, an unexpected error occured. Graph could not be shown.");
		    			alert.showAndWait();
		    			//show main graph
		    			showMainGraph();
				}
			}else {
				Alert alert = new Alert(AlertType.ERROR);
	    			alert.setTitle("Error Dialog");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Soory, Log Axis graph cannot be shown for this amount of data.");
	    			alert.showAndWait();
			}
		}else {
			stackpane.getChildren().clear();
			stackpane.getChildren().add(logYchart);
			mainchart = false;
			logxchart = false;
			logychart = true;
			logchart = false;
		}
	}
	
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Collections.sort(list, new CustomSort.SortByReviews());
//		final NumberAxis xAxis = new NumberAxis();
//        final NumberAxis yAxis = new NumberAxis();        
        
       // scatterChart
        //lineChart = new LineChart<Number, Number>(xAxis, yAxis);
//		xAxis.setLabel("Reviews");                
//        yAxis.setLabel("Score");
		
		
		
//		lineChart.getXAxis().setLabel("Reviews");
//		lineChart.getYAxis().setLabel("Score");
//		scatterChart.getXAxis().setLabel("Reviews");
//		scatterChart.getYAxis().setLabel("Score");
		
		
		
		
		
		
		
		System.out.println("List size = " + list.size());
		
		showMainGraph();
		
		//get big r calculation
		double bigR = (double)calculateBigR(xAxis, yAxis);
		
		bigRlabel.setText("Big R = " + bigR);
		
	}
	
	public void showMainGraph() {
		if(xAxis == null && yAxis == null) {
			//get the axis first
			ArrayList<ArrayList<Number>> bothAxis = setupAxisValue(paramOne, paramTwo);
			xAxis = bothAxis.get(0);
			yAxis = bothAxis.get(1);
			setupGraphSeries(xAxis, yAxis);	
		}else {
			stackpane.getChildren().clear();
			stackpane.getChildren().add(scatterChart);
		}
		mainchart = true;
		logxchart = false;
		logychart = false;
		logchart = false;
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void setupGraphSeries(ArrayList<Number> xAxis, ArrayList<Number> yAxis) {
		//XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		if(!yAxis.isEmpty()) {
			lowerBoundY = yAxis.get(0);
			upperBoundY = yAxis.get(0);
			lowerBoundX = xAxis.get(0);
			upperBoundX = xAxis.get(0);
		}
		
		for(int i=0; i<xAxis.size(); i++) {
			try {
				Number x = xAxis.get(i);
				Number y = yAxis.get(i);
				if(y.doubleValue() < lowerBoundY.doubleValue()) {
					lowerBoundY = y;
				}
				if(y.doubleValue() > upperBoundY.doubleValue()) {
					upperBoundY = y;
				}
				
				if(x.doubleValue() < lowerBoundX.doubleValue()) {
					lowerBoundX = x;
				}
				if(x.doubleValue() > upperBoundX.doubleValue()) {
					upperBoundX = x;
				}
				
				
				seriesMain.getData().addAll(new XYChart.Data<Number, Number>(x,y));
				serieslogY.getData().addAll(new XYChart.Data<Number, Number>(x,y));
				serieslogX.getData().addAll(new XYChart.Data<Number, Number>(x,y));
				serieslog.getData().addAll(new XYChart.Data<Number, Number>(x,y));
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}		
		
		seriesMain.setName(paramOne + " / " + paramTwo);
		scatterChart.setLegendSide(Side.TOP);
		scatterChart.getData().addAll(seriesMain);
		
		for (XYChart.Data<Number, Number> d : seriesMain.getData()) {
        	
	    		Tooltip.install(d.getNode(), new Tooltip("x = " +
	                d.getXValue() + "\n" +
	                        "y = " + d.getYValue()));
	
	        //Adding class on hover
	        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
	
	        //Removing class on exit
	        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
		}
		
		
	}
	
	private Object getScore(App app) {
		try {
			return Double.parseDouble(app.getScore());
		}catch(Exception e ) {
			System.out.println("Something went wrong in score axis" + app.getScore());
			e.printStackTrace();
			return null;
		}
	}
	
	private Object getReviews(App app) {
		try {
			return Integer.parseInt(app.getReviews().replaceAll(",", ""));
		}catch(Exception e ) {
			System.out.println("Something went wrong in reviews axis" + app.getReviews());
			e.printStackTrace();
			return null;
		}
	}
	
	private Object getPrice(App app) {
		try {
			return Double.parseDouble(app.getPrice().replaceAll("£", "").trim());
		}catch(Exception e ) {
			System.out.println("Something went wrong in price axis" + app.getPrice());
			e.printStackTrace();
			return null;
		}
	}
	
	private Object getAverageDown(App app) {
		try {
			String[] down = app.getInstalls().replaceAll(",", "").split("-");
			int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
			return average;
		}catch(Exception e ) {
			System.out.println("Something went wrong in average downloads axis" + app.getInstalls());
			e.printStackTrace();
			return null;
		}
	}
	
	private Object getAverageInApp(App app) {
		try {
			if(app.getInAppPurchases().contains("-")) {
				String[] inapp = app.getInAppPurchases().replaceAll("per item", "").trim().split("-");
				double averagePrice = (Double.parseDouble(inapp[0].replace("£", "").trim()) + 
						Double.parseDouble(inapp[1].replace("£", "").trim()))/2;
				return averagePrice;
			}else {
				String inapp = app.getInAppPurchases().replaceAll("per item", "").trim();
				double averagePrice = Double.parseDouble(inapp.replace("£", "").trim());
				return averagePrice;
			}
		}catch(Exception e ) {
			System.out.println("Something went wrong in average in app axis" + app.getInAppPurchases());
			//e.printStackTrace();
			return null;
		}
	}
	
	private Object getScore5(App app) {
		try {
			return Integer.parseInt(app.getScore5().replaceAll(",", "").trim());
		}catch(Exception e ) {
			System.out.println("Something went wrong in score 5 axis" + app.getScore5());
			e.printStackTrace();
			return null;
		}
	}
	
	private Object getScore4(App app) {
		try {
			return Integer.parseInt(app.getScore4().replaceAll(",", "").trim());
		}catch(Exception e ) {
			System.out.println("Something went wrong in score 4 axis" + app.getScore4());
			e.printStackTrace();
			return null;
		}
	}
	
	private Object getScore3(App app) {
		try {
			return Integer.parseInt(app.getScore3().replaceAll(",", "").trim());
		}catch(Exception e ) {
			System.out.println("Something went wrong in score 3 axis" + app.getScore3());
			e.printStackTrace();
			return null;
		}
	}
	
	private Object getScore2(App app) {
		try {
			return Integer.parseInt(app.getScore2().replaceAll(",", "").trim());
		}catch(Exception e ) {
			System.out.println("Something went wrong in score 2 axis" + app.getScore2());
			e.printStackTrace();
			return null;
		}
	}
	
	private Object getScore1(App app) {
		try {
			return Integer.parseInt(app.getScore1().replaceAll(",", "").trim());
		}catch(Exception e ) {
			System.out.println("Something went wrong in score 1 axis" + app.getScore1());
			e.printStackTrace();
			return null;
		}
	}
	
	private Object getLastUpdatedDate(App app) {
		String actualValue = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
			Date date = null;
			try {
				date = sdf.parse(app.getLastUpdate());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int day = cal.DATE;
			int month = cal.MONTH + 1;
			int year = cal.YEAR;
			
			actualValue = ""+year;
			
			if(month < 10) {
				actualValue += "0" + month;
			}else {
				actualValue += month;
			}
			
			if(day < 10) {
				actualValue += "0" + day;
			}else {
				actualValue += day;
			}
			
			
				int intActual = Integer.parseInt(actualValue);
			return intActual;
		}catch(Exception e) {
			//pass
			System.out.println("Something went wrong in last updated date axis" + actualValue);
			e.printStackTrace();
			return null;
		}
	}
	
	private ArrayList<ArrayList<Number>> setupAxisValue(String parameterX, String parameterY) {
		ArrayList<Number> listX = new ArrayList<Number>();
		ArrayList<Number> listY = new ArrayList<Number>();
		
		Number tempX = 0;
		Number tempY = 0;
		boolean flag = false;
		
		for(int i=0; i<list.size(); i++) {
			App appX = list.get(i);
			App appY = list.get(i);
			
			if(parameterX.equals("score") || parameterY.equals("score")) {
				//add score values to returnList
				if(parameterX.equals("score")) {
					Object x = getScore(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getScore(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
				
			}if(parameterX.equals("reviews") || parameterY.equals("reviews")) {
				//add review values to returnList
				if(parameterX.equals("reviews")) {
					Object x = getReviews(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getReviews(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
				
			}if(parameterX.equals("price") || parameterY.equals("price")) {
				//add price values to returnList
				if(parameterX.equals("price")) {
					Object x = getPrice(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getPrice(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
			}if(parameterX.equals("avg. downloads") || parameterY.equals("avg. downloads")) {
				//add avg. downloads values to returnList
				if(parameterX.equals("avg. downloads")) {
					Object x = getAverageDown(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getAverageDown(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
			}if(parameterX.equals("avg. in-app purchase") || parameterY.equals("avg. in-app purchase")) {
				//add avg. in-app purchase values to returnList
				if(parameterX.equals("avg. in-app purchase")) {
					Object x = getAverageInApp(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getAverageInApp(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
			}if(parameterX.equals("number of score 5") || parameterY.equals("number of score 5")) {
				//add number of score 5 values to returnList
				if(parameterX.equals("number of score 5")) {
					Object x = getScore5(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getScore5(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
			}if(parameterX.equals("number of score 4") || parameterY.equals("number of score 4")) {
				//add number of score 4 values to returnList
				if(parameterX.equals("number of score 4")) {
					Object x = getScore4(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getScore4(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
			}if(parameterX.equals("number of score 3") || parameterY.equals("number of score 3")) {
				//add number of score 3 values to returnList
				if(parameterX.equals("number of score 3")) {
					Object x = getScore3(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getScore3(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
			}if(parameterX.equals("number of score 2") || parameterY.equals("number of score 2")) {
				//add number of score 2 values to returnList
				if(parameterX.equals("number of score 2")) {
					Object x = getScore2(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getScore2(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
			}if(parameterX.equals("number of score 1") || parameterY.equals("number of score 1")) {
				//add number of score 1 values to returnList
				if(parameterX.equals("number of score 1")) {
					Object x = getScore1(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase());
				}else {
					Object y = getScore1(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase());
				}
			}if(parameterX.equals("last updated date") || parameterY.equals("last updated date")) {
				//add number of last updated date values to returnList
				if(parameterX.equals("last updated date")) {
					Object x = getLastUpdatedDate(appX);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getXAxis().setLabel(paramOne.toUpperCase() + " (yymmdd)");
				}else {
					Object y = getLastUpdatedDate(appY);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					//setup axis label
					scatterChart.getYAxis().setLabel(paramTwo.toUpperCase() + " (yymmdd)");
				}
				
			}
			
			
			if(!flag) {
				listX.add(tempX);
				listY.add(tempY);
			}else {
				flag = false;
			}
			
		}
			
			ArrayList<ArrayList<Number>> returnlist = new ArrayList<ArrayList<Number>>();
			returnlist.add(listX);
			returnlist.add(listY);
			return returnlist;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Number calculateBigR(ArrayList<Number> xaxis, ArrayList<Number> yaxis) {
		double sumX = 0;
		double sumY = 0;
		double sumXsq = 0;
		double sumYsq = 0;
		double sumXY = 0;
		
		int counter = 0;
		for(int i=0; i<xaxis.size() && i<yaxis.size(); i++) {
			double x = xaxis.get(i).doubleValue();
			double y = yaxis.get(i).doubleValue();
			
			sumX += x;
			sumY += y;
			
			double xsq = x * x;
			double ysq = y * y;
			
			sumXsq += xsq;
			sumYsq += ysq;
			
			double xy = x * y;
			
			sumXY += xy;
			counter++;
		}
		System.out.println("Counter = " + counter);
		double numerator = (counter * sumXY) - (sumX * sumY);
		
		double demfirst = (counter * sumXsq) - (sumX * sumX);
		double demsecond = (counter * sumYsq) - (sumY * sumY);
				
		double denominator = Math.sqrt(demfirst) * Math.sqrt(demsecond);
		
		double bigR = numerator / denominator;
		
		bigR = Math.round(bigR * 100.0);
		bigR = bigR/100.0;
		
		return bigR;
		
	}
	
	
	
	
	
	
	
	
}
