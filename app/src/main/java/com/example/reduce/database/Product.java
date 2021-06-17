package com.example.reduce.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Product extends RealmObject {
	@PrimaryKey
	private String productName;

	@Required
	private String status = ProductStatus.Open.name();


	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductName() {
		return productName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Product (String productName) {
		this.productName = productName;
	}

	public Product() {}
}
