package com.example.reduce.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product_table")
public class ProductData {
		@PrimaryKey
		@NonNull
		@ColumnInfo(name = "database_ID")
		public int dataBaseID;

		@ColumnInfo(name = "product_name")
		public String productName;

		@ColumnInfo (name = "exp_date")
		public Long expDate;

		@ColumnInfo(name = "location")
		public String location;

		@ColumnInfo(name = "quantity")
		public int quantity;

		@ColumnInfo(name = "barcodeID")
		public String barcodeID;

}
