package com.cube9.gmarket.Category.ModelClass;

import com.cube9.gmarket.Home.ModelClass.ChildDrawerPojo;

import java.util.ArrayList;
import java.util.List;

public class ParentSubCatPojo  {
    String cat_id,cat_name;
    private ArrayList<SubCatPojo> list = new ArrayList<SubCatPojo>();

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


    public void SetSubCatList(ArrayList<SubCatPojo> sub_cat_list) {

        this.list = sub_cat_list;
    }
    public ArrayList<SubCatPojo> getSubCatList() {
        return list;
    }
}
