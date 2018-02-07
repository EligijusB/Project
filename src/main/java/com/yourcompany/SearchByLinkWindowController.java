package com.yourcompany;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SearchByLinkWindowController {

	@FXML
	JFXTextField searchTF;
	
	private static Stage secondwindowstage;
	private static String searchLink;
	
	private String regex = "(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$";
	
	public static Stage getMyStage() {
		return secondwindowstage;
	}
	
	public void onSearch() throws IOException {
		if(validate()) {
			searchLink = searchTF.getText();
			Stage stage = new Stage();
			secondwindowstage = stage;
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SecondWindow.fxml"));
			fxmlLoader.setController(new SecondWindowController(searchLink, false));
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
			alert.setTitle("ERROR Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please provide a link to search apps.");
			alert.showAndWait();
			return false;
		}
		
		//test the the link using regex
		try {
            Pattern patt = Pattern.compile(regex);
            Matcher matcher = patt.matcher(searchTF.getText());
            boolean result = matcher.matches();
            if(!result) {
	            	Alert alert = new Alert(AlertType.ERROR);
	    			alert.setTitle("ERROR Dialog");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Sorry, the link provided is not of valid format.");
	    			alert.showAndWait();
	    			return false;
            }else {
            		return true;
            }
        } catch (RuntimeException e) {
        		return false;
        }
		
	}
}
