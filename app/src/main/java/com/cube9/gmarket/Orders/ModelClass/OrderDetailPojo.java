package com.cube9.gmarket.Orders.ModelClass;

public class OrderDetailPojo {
    String product_id, name, product_type, sku, description, price, Image,rating;

    public OrderDetailPojo(String product_id, String name, String product_type, String sku, String description, String price, String image,String rating) {
        this.product_id = product_id;
        this.name = name;
        this.rating=rating;
        this.product_type = product_type;
        this.sku = sku;
        this.description = description;
        this.price = price;
        Image = image;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
