package com.yourcompany;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class App extends RecursiveTreeObject<App> {
    private String url;
    private StringProperty title;
    private StringProperty subtitle;
    private String[] genre;
    private StringProperty price;
    private boolean free;
    private int reviews;
    private String description;
    private double score;
    private int score5;
    private int score4;
    private int score3;
    private int score2;
    private int score1;
    private String[] whatsnew;
    private String size;
    private String lastUpdate;
    private StringProperty installs;
    private double currentVersion;
    private String androidReq;
    private String contentRating;
    private String interactiveElements;
    private String inAppPurchases;
    private String[] permissions;
    private String developer;
    private String developerWebsite;
    private String developerEmail;
    private String developerAddress;

    public App() {
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
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

    public StringProperty getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = new SimpleStringProperty(title);
    }

    public StringProperty getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = new SimpleStringProperty(subtitle);
    }

    public StringProperty getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = new SimpleStringProperty(price);
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getScore5() {
        return score5;
    }

    public void setScore5(int score5) {
        this.score5 = score5;
    }

    public int getScore4() {
        return score4;
    }

    public void setScore4(int score4) {
        this.score4 = score4;
    }

    public int getScore3() {
        return score3;
    }

    public void setScore3(int score3) {
        this.score3 = score3;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public String[] getWhatsnew() {
        return whatsnew;
    }

    public void setWhatsnew(String[] whatsnew) {
        this.whatsnew = whatsnew;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public StringProperty getInstalls() {
        return installs;
    }

    public void setInstalls(String installs) {
        this.installs = new SimpleStringProperty(installs);
    }

    public double getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(double currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getAndroidReq() {
        return androidReq;
    }

    public void setAndroidReq(String androidReq) {
        this.androidReq = androidReq;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setContentRating(String contentRating) {
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

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
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
