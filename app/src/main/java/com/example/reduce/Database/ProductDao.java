package com.example.reduce.Database;

import androidx.room.*;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.Date;

@Dao
public interface ProductDao {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	void insert(ProductData productData);

	@Query("DELETE FROM product_table")
	void deleteAll();

	@Update(onConflict = OnConflictStrategy.REPLACE)
	public void updateProduct (ProductData productData);

}
