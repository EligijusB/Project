package com.yourcompany;

import javafx.concurrent.Task;

public class MyGetDriverTask extends Task {

	@Override
	protected Object call() throws Exception {
		if(isCancelled()) {
			//pass
			return null;
		}else {
			try {
				System.out.println("setting up driver");
				Driver.setupDriver();
				System.out.println("Driver Set properly");
				return true;
			}catch(Exception e) {
				return false;
			}
		}
	}

}
