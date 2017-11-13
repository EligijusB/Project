package com.yourcompany;

import java.util.ArrayList;

public class AppLinks {
	
	static ArrayList<String> listOfLinks;
	
	public static synchronized String getLink() {
		if(listOfLinks != null &&!listOfLinks.isEmpty()) {
			String link = listOfLinks.get(0);
			listOfLinks.remove(0);
			return link;
		}
		return null;
	}
	
	public static void setLinksList(ArrayList<String> list) {
		listOfLinks = list;
		System.out.println("Set Links list");
		System.out.println(listOfLinks);
	}
	
}
