package com.yourcompany;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Indexer {
	static int indexer = 0;

	public BooleanProperty property;
	private int index;
	
	public Indexer() {
		this.index = indexer;
		indexer++;
		this.property = new SimpleBooleanProperty();
	}
	
	public int getIndex() {
		return this.index;
	}
	
}
