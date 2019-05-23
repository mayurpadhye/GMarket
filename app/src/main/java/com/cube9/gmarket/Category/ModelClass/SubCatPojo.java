package com.cube9.gmarket.Category.ModelClass;

public class SubCatPojo {

    String cat_id,cat_name,image;

    public SubCatPojo(String cat_id, String cat_name, String image) {
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.image = image;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
