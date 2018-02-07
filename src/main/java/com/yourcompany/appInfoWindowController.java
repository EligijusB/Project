package com.yourcompany;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class appInfoWindowController implements Initializable {
	
	@FXML
	Label title, subtitle, genre, price, reviews, installs, score, 
	score5, score4, score3, score2, score1, whatsnew, description, 
	size, updated, androidreq, version, interactive, inapp, developer, website, email, address, rating;

	private App app;
	
	public appInfoWindowController(App app) {
		this.app = app;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
		title.setText(app.getTitle());
		//subtitle.setText(app.getSubtitle());
		
		String genreStr = "";
		for(String str : app.getGenre()) {
			genreStr = genreStr + str + " ";
		} 
		genre.setText(genreStr);
		
		if(app.isFree()) {
			price.setText("FREE");
		}else {
			price.setText(app.getPrice());
		}
		
		reviews.setText(app.getReviews());
		
		installs.setText(app.getInstalls());
		
		if(app.getSize() == null || app.getSize().isEmpty()) {
			size.setText("Unknown");
		}else {
			size.setText(app.getSize());
		}
		
		if(app.getLastUpdate() == null || app.getLastUpdate().isEmpty()) {
			updated.setText("Never");
		}else {
			updated.setText(app.getLastUpdate());
		}
		
		if(app.getAndroidReq() == null || app.getAndroidReq().isEmpty()) {
			androidreq.setText("Unknown");
		}else {
			androidreq.setText(app.getAndroidReq());
		}
		
		if(app.getCurrentVersion() == null || app.getCurrentVersion().isEmpty()) {
			version.setText("Unknown");
		}else {
			version.setText(app.getCurrentVersion());
		}
		
		if(app.getInteractiveElements() == null || app.getInteractiveElements().isEmpty()) {
			interactive.setText("None");
		}else {
			interactive.setText(app.getInteractiveElements());
		}
		
		if(app.getInAppPurchases() == null || app.getInAppPurchases().isEmpty()) {
			inapp.setText("None");
		}else {
			inapp.setText(app.getInAppPurchases());
		}
		
		if(app.getSubtitle() == null || app.getSubtitle().isEmpty()) {
			developer.setText("Unknown");
		}else {
			developer.setText(app.getSubtitle());
		}
		
		if(app.getDeveloperWebsite() == null || app.getDeveloperWebsite().isEmpty()) {
			website.setText("Unknown");
		}else {
			website.setText(app.getDeveloperWebsite());
		}
		
		if(app.getDeveloperEmail() == null || app.getDeveloperEmail().isEmpty()) {
			email.setText("Unknown");
		}else {
			email.setText(app.getDeveloperEmail());
		}
		
		if(app.getDeveloperAddress() == null || app.getDeveloperAddress().isEmpty()) {
			address.setText("Unknown");
		}else {
			address.setText(app.getDeveloperAddress());
		}
		
		if(app.getContentRating() == null || app.getContentRating().isEmpty()) {
			rating.setText("Unknown");
		}else {
			String ratingStr = "";
			for(String str : app.getContentRating()) {
				ratingStr = ratingStr + str + " ";
			}
			
			rating.setText(ratingStr);
		}
		
		
		
		
		
		score.setText(app.getScore());
		score5.setText(app.getScore5());
		score4.setText(app.getScore4());
		score3.setText(app.getScore3());
		score2.setText(app.getScore2());
		score1.setText(app.getScore1());
		
		
		
		
		whatsnew.setText(app.getWhatsnew());
		
		description.setText(app.getDescription());
		
	}
	
	
	
}
