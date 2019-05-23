package com.cube9.gmarket.Home.ModelClass;

public class BestSellerPojo {
    String product_name,product_image,product_price,product_id;
    String category_id;
    public BestSellerPojo(String product_name, String product_image, String product_price,String product_id,String category_id) {
        this.product_name = product_name;
        this.product_image = product_image;
        this.product_price = product_price;
        this.product_id=product_id;
        this.category_id=category_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }
}
