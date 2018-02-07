package com.yourcompany;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.axis.LabelOrientation;
import com.orsoncharts.axis.LogAxis3D;
import com.orsoncharts.axis.NumberAxis3D;
import com.orsoncharts.data.xyz.XYZDataset;
import com.orsoncharts.data.xyz.XYZSeries;
import com.orsoncharts.data.xyz.XYZSeriesCollection;
import com.orsoncharts.fx.Chart3DViewer;
import com.orsoncharts.graphics3d.Dimension3D;
import com.orsoncharts.graphics3d.ViewPoint3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.xyz.ScatterXYZRenderer;
import com.orsoncharts.style.ChartStyler;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ScatterChart3D {
	
	private static ArrayList<ArrayList<Number>> sharedAxisList;
	
	public static ArrayList<ArrayList<Number>> getSharedAxisList() {
		if(sharedAxisList != null) {
			return sharedAxisList;
		}
		return null;
	}
	
	public static Node create3DGraphNode(String paramX, String paramY, String paramZ, ObservableList<App> list) {
        XYZDataset dataset = createDataset(paramX, paramY, paramZ, list);
        Chart3D chart = createChart(dataset, paramX, paramY, paramZ);
        Chart3DViewer viewer = new Chart3DViewer(chart);
        BorderPane node = new BorderPane();
        node.setCenter(viewer);
        HBox container = new HBox();
        CheckBox checkBox = new CheckBox("Logarithmic Axis?");
        checkBox.setSelected(false);
        checkBox.setOnAction((e) -> {
            XYZPlot plot = (XYZPlot) chart.getPlot();
            if (checkBox.isSelected()) {
            		try {
	                LogAxis3D logAxis = new LogAxis3D(paramY + " (log scale)");
	                logAxis.setTickLabelOrientation(LabelOrientation.PERPENDICULAR);
	                logAxis.receive(new ChartStyler(chart.getStyle()));
	                plot.setYAxis(logAxis);
            		}catch(Exception ex) {
            			//pass
            			if(ex.getMessage().equals("Param 'reference' must be finite and positive.")) {
	            			Alert alert = new Alert(AlertType.ERROR);
	            			alert.setTitle("Error Dialog");
	            			alert.setHeaderText(null);
	            			alert.setContentText("Soory, Logarithmic axis is not compatible with the data inside the graph.");
	            			alert.showAndWait();
	         
            			}else {
            				Alert alert = new Alert(AlertType.ERROR);
	            			alert.setTitle("Error Dialog");
	            			alert.setHeaderText(null);
	            			alert.setContentText("Soory, an unexpected error occured.");
	            			alert.showAndWait();
            			}
            			NumberAxis3D yAxis = new NumberAxis3D("paramY");
            			yAxis.setTickLabelOrientation(LabelOrientation.PERPENDICULAR);
            			yAxis.receive(new ChartStyler(chart.getStyle()));
            			plot.setYAxis(yAxis);
            			checkBox.setSelected(false);
                    
            		}
            } else {
                NumberAxis3D yAxis = new NumberAxis3D("paramY");
                yAxis.setTickLabelOrientation(LabelOrientation.PERPENDICULAR);
                yAxis.receive(new ChartStyler(chart.getStyle()));
                plot.setYAxis(yAxis);
            }
        });
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(4.0, 4.0, 4.0, 4.0));
        container.getChildren().add(checkBox);
        node.setBottom(container);
        return node;
    }

    private static Chart3D createChart(XYZDataset dataset, String x, String y, String z) {
        Chart3D chart = Chart3DFactory.createScatterChart(null, 
                null, dataset, x, y, z);
        XYZPlot plot = (XYZPlot) chart.getPlot();
        ScatterXYZRenderer renderer = (ScatterXYZRenderer) plot.getRenderer();
        plot.setDimensions(new Dimension3D(10, 6, 10));
        renderer.setSize(0.1);
        renderer.setColors(new Color(255, 128, 128));
        NumberAxis3D yAxis = new NumberAxis3D("Y");
        yAxis.setTickLabelOrientation(LabelOrientation.PERPENDICULAR);
        yAxis.receive(new ChartStyler(chart.getStyle()));
        plot.setYAxis(yAxis);
        chart.setViewPoint(ViewPoint3D.createAboveLeftViewPoint(40));
        return chart;
    }

 
    public static XYZDataset<String> createDataset(String paramX, String paramY, String paramZ, ObservableList<App> list) {
        XYZSeries<String> series = new XYZSeries<>("" + paramX + " / " + paramY + " / " + paramZ);
        
        ArrayList<ArrayList<Number>> axis = getXYZAxis(paramX, paramY, paramZ, list);
        ArrayList<Number> xaxis = axis.get(0);
        ArrayList<Number> yaxis = axis.get(1);
        ArrayList<Number> zaxis = axis.get(2);
        
        for(int i=0; i<xaxis.size() && i<yaxis.size() && i<zaxis.size(); i++) {
        		series.add(xaxis.get(i).doubleValue(), yaxis.get(i).doubleValue(), zaxis.get(i).doubleValue());
        }
        
        XYZSeriesCollection<String> dataset = new XYZSeriesCollection<>();
        dataset.add(series);
        return dataset;
    }

	private static ArrayList<ArrayList<Number>> getXYZAxis(String paramX, String paramY, String paramZ, ObservableList<App> list) {
		// TODO Auto-generated method stub
		ArrayList<Number> listX = new ArrayList<Number>();
		ArrayList<Number> listY = new ArrayList<Number>();
		ArrayList<Number> listZ = new ArrayList<Number>();
		
		Number tempX = 0;
		Number tempY = 0;
		Number tempZ = 0;
		
		boolean flag = false;
		
		for(int i=0; i<list.size(); i++) {
			App app = list.get(i);
			
			if(paramX.equals("score") || paramY.equals("score") || paramZ.equals("score")) {
				//add score values to returnList
				if(paramX.equals("score")) {
					Object x = getScore(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
					
				}else if(paramY.equals("score")) {
					Object y = getScore(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					
				}else {
					Object z = getScore(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
				
			}if(paramX.equals("reviews") || paramY.equals("reviews") || paramZ.equals("reviews")) {
				//add review values to returnList
				if(paramX.equals("reviews")) {
					Object x = getReviews(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("reviews")){
					Object y = getReviews(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
			
				}else {
					Object z = getReviews(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
				
			}if(paramX.equals("price") || paramY.equals("price") || paramZ.equals("price")) {
				//add price values to returnList
				if(paramX.equals("price")) {
					Object x = getPrice(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("price")) {
					Object y = getPrice(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
					
				}else {
					Object z = getPrice(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
			}if(paramX.equals("avg. downloads") || paramY.equals("avg. downloads") || paramZ.equals("avg. downloads")) {
				//add avg. downloads values to returnList
				if(paramX.equals("avg. downloads")) {
					Object x = getAverageDown(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("avg. downloads")) {
					Object y = getAverageDown(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
				}else {
					Object z = getAverageDown(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
			}if(paramX.equals("avg. in-app purchase") || paramY.equals("avg. in-app purchase") || paramZ.equals("avg. in-app purchase")) {
				//add avg. in-app purchase values to returnList
				if(paramX.equals("avg. in-app purchase")) {
					Object x = getAverageInApp(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("avg. in-app purchase")) {
					Object y = getAverageInApp(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
				}else {
					Object z = getAverageInApp(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
			}if(paramX.equals("number of score 5") || paramY.equals("number of score 5")  || paramZ.equals("number of score 5")) {
				//add number of score 5 values to returnList
				if(paramX.equals("number of score 5")) {
					Object x = getScore5(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("number of score 5")) {
					Object y = getScore5(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
				}else {
					Object z = getScore5(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
			}if(paramX.equals("number of score 4") || paramY.equals("number of score 4") || paramZ.equals("number of score 4")) {
				//add number of score 4 values to returnList
				if(paramX.equals("number of score 4")) {
					Object x = getScore4(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("number of score 4")){
					Object y = getScore4(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
				}else {
					Object z = getScore4(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
			}if(paramX.equals("number of score 3") || paramY.equals("number of score 3") || paramZ.equals("number of score 3")) {
				//add number of score 3 values to returnList
				if(paramX.equals("number of score 3")) {
					Object x = getScore3(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("number of score 3")){
					Object y = getScore3(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
				}else {
					Object z = getScore3(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
			}if(paramX.equals("number of score 2") || paramY.equals("number of score 2") || paramZ.equals("number of score 2")) {
				//add number of score 2 values to returnList
				if(paramX.equals("number of score 2")) {
					Object x = getScore2(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("number of score 2")){
					Object y = getScore2(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
				}else {
					Object z = getScore2(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
			}if(paramX.equals("number of score 1") || paramY.equals("number of score 1") || paramZ.equals("number of score 1")) {
				//add number of score 1 values to returnList
				if(paramX.equals("number of score 1")) {
					Object x = getScore1(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("number of score 1")){
					Object y = getScore1(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
				}else {
					Object z = getScore1(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
			}if(paramX.equals("last updated date") || paramY.equals("last updated date") || paramZ.equals("last updated date")) {
				//add number of last updated date values to returnList
				if(paramX.equals("last updated date")) {
					Object x = getLastUpdatedDate(app);
					if(x != null) {
						tempX = (Number) x;
					}else {
						flag = true;
					}
				}else if(paramY.equals("last updated date")){
					Object y = getLastUpdatedDate(app);
					if(y != null) {
						tempY = (Number) y;
					}else {
						flag = true;
					}
				}else {
					Object z = getLastUpdatedDate(app);
					if(z != null) {
						tempZ = (Number) z;
					}else {
						flag = true;
					}
				}
				
			}
			
			
			if(!flag) {
				listX.add(tempX);
				listY.add(tempY);
				listZ.add(tempZ);
			}else {
				flag = false;
			}
			
		}
			
			ArrayList<ArrayList<Number>> returnlist = new ArrayList<ArrayList<Number>>();
			returnlist.add(listX);
			returnlist.add(listY);
			returnlist.add(listZ);
			sharedAxisList = returnlist;
			return returnlist;
		
			
	}
	
	private static Object getScore(App app) {
		try {
			return Double.parseDouble(app.getScore());
		}catch(Exception e ) {
//			System.out.println("Something went wrong in score axis" + app.getScore());
//			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getReviews(App app) {
		try {
			return Integer.parseInt(app.getReviews().replaceAll(",", ""));
		}catch(Exception e ) {
//			System.out.println("Something went wrong in reviews axis" + app.getReviews());
//			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getPrice(App app) {
		try {
			return Double.parseDouble(app.getPrice().replaceAll("£", "").trim());
		}catch(Exception e ) {
//			System.out.println("Something went wrong in price axis" + app.getPrice());
//			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getAverageDown(App app) {
		try {
			String[] down = app.getInstalls().replaceAll(",", "").split("-");
			int average = (Integer.parseInt(down[0].trim()) + Integer.parseInt(down[1].trim()))/2;
			return average;
		}catch(Exception e ) {
//			System.out.println("Something went wrong in average downloads axis" + app.getInstalls());
//			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getAverageInApp(App app) {
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
			//System.out.println("Something went wrong in average in app axis" + app.getInAppPurchases());
			//e.printStackTrace();
			return null;
		}
	}
	
	private static Object getScore5(App app) {
		try {
			return Integer.parseInt(app.getScore5().replaceAll(",", "").trim());
		}catch(Exception e ) {
//			System.out.println("Something went wrong in score 5 axis" + app.getScore5());
//			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getScore4(App app) {
		try {
			return Integer.parseInt(app.getScore4().replaceAll(",", "").trim());
		}catch(Exception e ) {
//			System.out.println("Something went wrong in score 4 axis" + app.getScore4());
//			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getScore3(App app) {
		try {
			return Integer.parseInt(app.getScore3().replaceAll(",", "").trim());
		}catch(Exception e ) {
//			System.out.println("Something went wrong in score 3 axis" + app.getScore3());
//			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getScore2(App app) {
		try {
			return Integer.parseInt(app.getScore2().replaceAll(",", "").trim());
		}catch(Exception e ) {
//			System.out.println("Something went wrong in score 2 axis" + app.getScore2());
//			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getScore1(App app) {
		try {
			return Integer.parseInt(app.getScore1().replaceAll(",", "").trim());
		}catch(Exception e ) {
//			System.out.println("Something went wrong in score 1 axis" + app.getScore1());
//			e.printStackTrace();
			return null;
		}
	}
	
	private static Object getLastUpdatedDate(App app) {
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
//			System.out.println("Something went wrong in last updated date axis" + actualValue);
//			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
}
