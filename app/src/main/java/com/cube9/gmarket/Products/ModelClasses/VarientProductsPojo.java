package com.cube9.gmarket.Products.ModelClasses;

public class VarientProductsPojo {
    String product_id,id,name,price,color_id,color_name,discount,description,available_qty,image;
    boolean setSelected;
    String Size_id,is_in_stock;

    public String getIs_in_stock() {
        return is_in_stock;
    }

    public void setIs_in_stock(String is_in_stock) {
        this.is_in_stock = is_in_stock;
    }

    public VarientProductsPojo(String product_id, String id, String name, String price, String color_id, String color_name, String discount, String description, String available_qty, String image, boolean setSelected, String Size_id, String is_in_stock) {
        this.id = id;
        this.name = name;
        this.is_in_stock=is_in_stock;

        this.price = price;
        this.color_id = color_id;
        this.color_name = color_name;
        this.discount = discount;
        this.description = description;
        this.available_qty = available_qty;
        this.image = image;
        this.product_id = product_id;
        this.setSelected=setSelected;
        this.Size_id=Size_id;
    }

    public String getSize_id() {
        return Size_id;
    }

    public void setSize_id(String size_id) {
        Size_id = size_id;
    }

    public boolean isSetSelected() {
        return setSelected;
    }

    public void setSetSelected(boolean setSelected) {
        this.setSelected = setSelected;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getColor_id() {
        return color_id;
    }

    public void setColor_id(String color_id) {
        this.color_id = color_id;
    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvailable_qty() {
        return available_qty;
    }

    public void setAvailable_qty(String available_qty) {
        this.available_qty = available_qty;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
