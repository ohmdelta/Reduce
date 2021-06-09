package com.example.reduce;

import com.google.mlkit.vision.barcode.Barcode;

import java.util.Objects;

public class customBarcode {
  private Barcode barcode;

  public Barcode getBarcode() {
    return barcode;
  }

  public customBarcode (Barcode barcode) {
    this.barcode = barcode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    customBarcode that = (customBarcode) o;
    return Objects.equals(barcode.getRawValue(), that.getBarcode().getRawValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(barcode.getRawValue());
  }
}
