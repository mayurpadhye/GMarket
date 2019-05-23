package com.cube9.gmarket.Category.ModelClass;

public class CatProductsPojo {
    String product_name,product_price,product_image,product_original_price,product_discount,product_id,wishlist_flag,is_in_stock;
String cat_id;
    public CatProductsPojo(String product_name, String product_price, String product_image, String product_original_price, String product_discount,String product_id,String wishlist_flag,String is_in_stock,String cat_id) {
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_image = product_image;
        this.cat_id=cat_id;
        this.is_in_stock=is_in_stock;
        this.product_original_price = product_original_price;
        this.product_discount = product_discount;
        this.product_id=product_id;
        this.wishlist_flag=wishlist_flag;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getIs_in_stock() {
        return is_in_stock;
    }

    public void setIs_in_stock(String is_in_stock) {
        this.is_in_stock = is_in_stock;
    }

    public String getWishlist_flag() {
        return wishlist_flag;
    }

    public void setWishlist_flag(String wishlist_flag) {
        this.wishlist_flag = wishlist_flag;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_original_price() {
        return product_original_price;
    }

    public void setProduct_original_price(String product_original_price) {
        this.product_original_price = product_original_price;
    }

    public String getProduct_discount() {
        return product_discount;
    }

    public void setProduct_discount(String product_discount) {
        this.product_discount = product_discount;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}
