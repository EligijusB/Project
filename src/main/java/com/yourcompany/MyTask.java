package com.yourcompany;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class MyTask extends Task {

	@Override
	protected Object call() throws Exception {
		if (isCancelled()) {
			this.cancel();
		}

		Thread.sleep(2000);
		App app = new App();
		app.setTitle("Test");
		app.setSubtitle("Test");
		app.setPrice("20");
		app.setScore(4.5);
		app.setInstalls("1000-2000");
		return true;
	}
}