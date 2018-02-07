package com.yourcompany;

import javafx.concurrent.Task;

public class MyTask extends Task<App> {
	private App app;
	
	public App getApp() {
		return this.app;
	}
	
	@Override
	protected App call() throws Exception {
		if (isCancelled()) {
			this.cancel();
		}
		System.out.println("IN MY TASK");
		String appLink = AppLinks.getLink();
		
		if(appLink == null) {
			this.cancel();
			System.out.println("APPLINK NULL");
			return null;
		}else {
			try {
				this.app = JsoupClass.getSingleAppInfo(appLink);
				return app;
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	}
}