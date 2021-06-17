package com.example.reduce.Database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

	private ProductRepo productRepo;
	private final LiveData<List<ProductData>> productData;

	public ProductViewModel(@NonNull @NotNull Application application) {
		super(application);
		productRepo = new ProductRepo(application);
		productData = productRepo.getAllProducts();
	}

	public LiveData<List<ProductData>> getProductData() {
		return productData;
	}

	public void insert(ProductData data) {
		productRepo.insert(data);
	}
}
