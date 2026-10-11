package com.j4mb.ims.product.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private final Long id;
    private final String sku;
    private  String name;
    private  String description;
    private  String unitOfMeasure;
    private BigDecimal unitCost;
    private int quantityOnHand;
    private  int reorderLevel;
    private boolean active;
// constructor for creating a new product
    public  Product(String sku, String name, String description, String unitOfMeasure, int quantityOnHand,BigDecimal unitCost, int reorderedLevel, boolean active) {
      this(null,sku,name,description,unitOfMeasure,unitCost,quantityOnHand,reorderedLevel,active);
    }
    // reconsturcting an existing product
    public Product(Long id, String sku, String name, String description, String unitOfMeasure,BigDecimal unitCost, int quantityOnHand, int reorderLevel, boolean active) {
        this.id = id;
        this.sku = requireText(sku, "SKU").toUpperCase();
        this.name = requireText(name, "Product name");
        this.description = description == null ? null : description.trim();
        this.unitOfMeasure = requireText(unitOfMeasure, "Unit of measure")
                .toUpperCase();
        this.unitCost = requireNonNegative(unitCost, "Unit cost");
        this.quantityOnHand = requireNonNegative(quantityOnHand, "Quantity on hand");
        this.reorderLevel = requireNonNegative(reorderLevel, "Reorder level");
        this.active = active;
    }
    public void updateDetails(
            String name,
            String description,
            String unitOfMeasure,
            BigDecimal unitCost,
            int reorderLevel
    ) {
        // 1. Validate and normalize everything first.
        String validatedName = requireText(name, "Product name");
        String validatedDescription =
                description == null ? null : description.trim();
        String validatedUnitOfMeasure =
                requireText(unitOfMeasure, "Unit of measure").toUpperCase();
        BigDecimal validatedUnitCost =
                requireNonNegative(unitCost, "Unit cost");
        int validatedReorderLevel =
                requireNonNegative(reorderLevel, "Reorder level");

        // 2. Change the object only after all validations succeed.
        this.name = validatedName;
        this.description = validatedDescription;
        this.unitOfMeasure = validatedUnitOfMeasure;
        this.unitCost = validatedUnitCost;
        this.reorderLevel = validatedReorderLevel;

    }
    private static  String requireText(String value,String field){
        if(value==null|| value.isEmpty()||value.trim().isEmpty()){
            throw new IllegalArgumentException(field+" could not be empty");
        }
        return value;
    }
    private BigDecimal requireNonNegative(BigDecimal value,String field) {
        Objects.requireNonNull(value, field + " could not be null");
        if (value.signum() < 0) {
            throw new IllegalArgumentException(field + " must not be negative");
        }
        return value;
    }
    private int requireNonNegative(int value,String field) {
        if(value<0) {
            throw new IllegalArgumentException(field + " must not be negative");
        }
        return value;
    }
    public void deactivate() {
        this.active = false;
    }
    public void activate() {
        this.active = true;
    }
    public boolean isLowStock() {
        return quantityOnHand<=reorderLevel;
    }
    // getters
    public Long getId() {
        return id;
    }
    public String getSKU() {
        return sku;
    }
    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getUnitOfMeasure() {return unitOfMeasure;}
    public int getQuantityOnHand() {return quantityOnHand;}
    public int getReorderLevel() {return reorderLevel;}
    public boolean isActive() {return active;}
    public BigDecimal getUnitCost() {return unitCost;}

}
