package com.cube9.gmarket.Category.ModelClass;

import org.json.JSONArray;

public class SubCategoryListPojo {
    String categoryId,categoryName,categoryImage,categoryDescrtion;
    int sub_cat_length;
    JSONArray sub_cats;



    public SubCategoryListPojo(String categoryId, String categoryName, String categoryImage, String categoryDescrtion, int sub_cat_length,JSONArray sub_cats) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.categoryDescrtion = categoryDescrtion;
        this.sub_cat_length = sub_cat_length;
        this.sub_cats=sub_cats;
    }



    public JSONArray getSub_cats() {
        return sub_cats;
    }

    public void setSub_cats(JSONArray sub_cats) {
        this.sub_cats = sub_cats;
    }

    public int getSub_cat_length() {
        return sub_cat_length;
    }

    public void setSub_cat_length(int sub_cat_length) {
        this.sub_cat_length = sub_cat_length;
    }

    public SubCategoryListPojo(String categoryId, String categoryName, String categoryImage, String categoryDescrtion) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.categoryDescrtion = categoryDescrtion;
    }



    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryDescrtion() {
        return categoryDescrtion;
    }

    public void setCategoryDescrtion(String categoryDescrtion) {
        this.categoryDescrtion = categoryDescrtion;
    }
}
