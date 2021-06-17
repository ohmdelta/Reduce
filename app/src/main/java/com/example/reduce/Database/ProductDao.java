package com.example.reduce.Database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.Date;
import java.util.List;

@Dao
public interface ProductDao {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	void insert(ProductData productData);

	@Query("DELETE FROM product_table")
	void deleteAll();

	@Delete
	void delete(ProductData productData);

	@Update(onConflict = OnConflictStrategy.REPLACE)
	public void updateProduct (ProductData productData);

	@Query("SELECT * FROM product_table")
	LiveData<List<ProductData>> getAll();

}
