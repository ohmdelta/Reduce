package com.example.reduce.Database;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

class ProductRepo {

	private ProductDao productDao;
	private LiveData<List<ProductData>> productList;

	ProductRepo(Application application) {
		ProductDatabase db = ProductDatabase.getDatabase(application);
		productDao = db.productDao();
		productList = productDao.getAll();
	}

	LiveData<List<ProductData>> getAllProducts() {
		return productList;
	}

	void insert(ProductData productData) {
		ProductDatabase.databaseWriteExecutor.execute(() -> {
			productDao.insert(productData);
		});
	}

}
