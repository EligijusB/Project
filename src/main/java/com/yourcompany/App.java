package com.yourcompany;

import java.util.ArrayList;
import java.util.Arrays;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class App extends RecursiveTreeObject<App> {
    private String url;
    private String title;
    private String subtitle;
    private ArrayList<String> genre;
    private String price;
    private boolean free;;
    private String reviews;
    private String description;
    private String score;
    private String score5;
    private String score4;
    private String score3;
    private String score2;
    private String score1;
    private String whatsnew;
    private String size;
    private String lastUpdate;
    private String installs;
    private String currentVersion;
    private String androidReq;
    private ArrayList<String> contentRating;
    private String interactiveElements;
    private String inAppPurchases;
    private String developer;
    private String developerWebsite;
    private String developerEmail;
    private String developerAddress;
    private BooleanProperty isSelected = new SimpleBooleanProperty();
    
    public BooleanProperty getIsSelected() {
    		return isSelected;
    }
    
    public void setIsSelected(boolean val) {
    		this.isSelected.set(val);
    }

    @Override
	public String toString() {
		return "App [url=" + url + ", title=" + title + ", subtitle=" + subtitle + ", genre=" + genre + ", price="
				+ price + ", free=" + free + ", reviews=" + reviews + ", description=" + description + ", score="
				+ score + ", score5=" + score5 + ", score4=" + score4 + ", score3=" + score3 + ", score2=" + score2
				+ ", score1=" + score1 + ", whatsnew=" + whatsnew + ", size=" + size + ", lastUpdate=" + lastUpdate
				+ ", installs=" + installs + ", currentVersion=" + currentVersion + ", androidReq=" + androidReq
				+ ", contentRating=" + contentRating + ", interactiveElements=" + interactiveElements
				+ ", inAppPurchases=" + inAppPurchases + ", permissions="
				+ ", developer=" + developer + ", developerWebsite=" + developerWebsite + ", developerEmail="
				+ developerEmail + ", developerAddress=" + developerAddress + "]";
	}

	public App() {
		isSelected.set(false);
		isSelected.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
					Boolean new_val) {

				if(new_val) {
					if(!ContainsApp()) {
						addToSelectedList();
					}
				}else {
					//app.setIsSelected(false);
					System.out.println("In new value is false");
					removeFromSelectedList();
				}
			}
		});
    }
	
	private boolean ContainsApp() {
		return SecondWindowController.selectedAppsList.contains(this);
	}
	
	private void addToSelectedList() {
		SecondWindowController.selectedAppsList.add(this);
	}
	
	private void removeFromSelectedList() {
		SecondWindowController.selectedAppsList.remove(this);
	}
	
	

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScore5() {
        return score5;
    }

    public void setScore5(String score5) {
        this.score5 = score5;
    }

    public String getScore4() {
        return score4;
    }

    public void setScore4(String score4) {
        this.score4 = score4;
    }

    public String getScore3() {
        return score3;
    }

    public void setScore3(String score3) {
        this.score3 = score3;
    }

    public String getScore2() {
        return score2;
    }

    public void setScore2(String score2) {
        this.score2 = score2;
    }

    public String getScore1() {
        return score1;
    }

    public void setScore1(String score1) {
        this.score1 = score1;
    }

    public String getWhatsnew() {
        return whatsnew;
    }

    public void setWhatsnew(String whatsnew) {
        this.whatsnew = whatsnew;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getInstalls() {
        return installs;
    }

    public void setInstalls(String installs) {
        this.installs = installs;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getAndroidReq() {
        return androidReq;
    }

    public void setAndroidReq(String androidReq) {
        this.androidReq = androidReq;
    }

    public ArrayList<String> getContentRating() {
        return contentRating;
    }

    public void setContentRating(ArrayList<String> contentRating) {
        this.contentRating = contentRating;
    }

    public String getInteractiveElements() {
        return interactiveElements;
    }

    public void setInteractiveElements(String interactiveElements) {
        this.interactiveElements = interactiveElements;
    }

    public String getInAppPurchases() {
        return inAppPurchases;
    }

    public void setInAppPurchases(String inAppPurchases) {
        this.inAppPurchases = inAppPurchases;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getDeveloperWebsite() {
        return developerWebsite;
    }

    public void setDeveloperWebsite(String developerWebsite) {
        this.developerWebsite = developerWebsite;
    }

    public String getDeveloperEmail() {
        return developerEmail;
    }

    public void setDeveloperEmail(String developerEmail) {
        this.developerEmail = developerEmail;
    }

    public String getDeveloperAddress() {
        return developerAddress;
    }

    public void setDeveloperAddress(String developerAddress) {
        this.developerAddress = developerAddress;
    }
    
    
    
}
