package com.example.reduce.database;

public enum ProductStatus {
		Open("Open"),
		InProgress("In Progress"),
		Complete("Complete");

		String displayName;
		ProductStatus(String displayName) {
			this.displayName = displayName;
		}

}

