package com.cube9.gmarket.SearchProducts.ModelClass;

public class SearchProductModel {
    String entity_id,type_id, sku, created_at, updated_at,price, name, is_salable,is_in_stock;
String category_id;
    public SearchProductModel(String entity_id, String type_id, String sku, String created_at, String updated_at, String price, String name,String category_id ) {
        this.entity_id = entity_id;


        this.type_id = type_id;
        this.sku = sku;
        this.category_id=category_id;

        this.created_at = created_at;
        this.updated_at = updated_at;
        this.price = price;
        this.name = name;


    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getIs_in_stock() {
        return is_in_stock;
    }

    public void setIs_in_stock(String is_in_stock) {
        this.is_in_stock = is_in_stock;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }





    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }



    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIs_salable() {
        return is_salable;
    }

    public void setIs_salable(String is_salable) {
        this.is_salable = is_salable;
    }
}
