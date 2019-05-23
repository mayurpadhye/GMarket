package com.cube9.gmarket.WishList.ModelClass;

public class WishListPojo {
    String product_id,product_name,product_price,product_special_price,product_image,product_discount,is_in_stock;
    boolean isConfigurale;

    public WishListPojo(String product_id, String product_name, String product_price, String product_special_price, String product_image, String product_discount, boolean isConfigurale,String is_in_stock) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.is_in_stock=is_in_stock;
        this.product_special_price = product_special_price;
        this.product_image = product_image;
        this.product_discount = product_discount;
        this.isConfigurale=isConfigurale;
    }

    public String getIs_in_stock() {
        return is_in_stock;
    }

    public void setIs_in_stock(String is_in_stock) {
        this.is_in_stock = is_in_stock;
    }

    public boolean isConfigurale() {
        return isConfigurale;
    }

    public void setConfigurale(boolean configurale) {
        isConfigurale = configurale;
    }

    public String getProduct_id() {
        return product_id;
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

    public String getProduct_special_price() {
        return product_special_price;
    }

    public void setProduct_special_price(String product_special_price) {
        this.product_special_price = product_special_price;
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
}
