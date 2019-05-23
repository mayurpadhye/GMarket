package com.cube9.gmarket.Payment.ModelClass;

public class StorePickUpPojo {
    String title,price,address;

    public StorePickUpPojo(String title, String price, String address) {
        this.title = title;
        this.price = price;
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
