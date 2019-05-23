package com.cube9.gmarket.Home.ModelClass;

public class LatestProductItemPojo  {
    public LatestProductItemPojo(String product_id, String product_name, String product_price, String product_image, String product_discount, String product_desc, String wishlist_flag, String quantity_available, String category_id, String product_special_price, String rating, String review_count) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_image = product_image;
        this.product_discount = product_discount;
        this.product_desc = product_desc;
        this.wishlist_flag = wishlist_flag;
        this.quantity_available = quantity_available;
        this.category_id = category_id;
        this.product_special_price=product_special_price;
        this.rating=rating;
        this.review_count=review_count;
    }

    String product_id,product_name, product_price,product_image, product_discount, product_desc, wishlist_flag, quantity_available, category_id;
    String product_special_price;
    String rating , review_count;
    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_special_price() {
        return product_special_price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview_count() {
        return review_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }

    public void setProduct_special_price(String product_special_price) {
        this.product_special_price = product_special_price;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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

    public String getProduct_discount() {
        return product_discount;
    }

    public void setProduct_discount(String product_discount) {
        this.product_discount = product_discount;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getWishlist_flag() {
        return wishlist_flag;
    }

    public void setWishlist_flag(String wishlist_flag) {
        this.wishlist_flag = wishlist_flag;
    }

    public String getQuantity_available() {
        return quantity_available;
    }

    public void setQuantity_available(String quantity_available) {
        this.quantity_available = quantity_available;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
