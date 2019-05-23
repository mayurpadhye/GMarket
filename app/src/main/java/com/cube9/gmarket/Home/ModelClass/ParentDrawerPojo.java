package com.cube9.gmarket.Home.ModelClass;

import java.util.ArrayList;

public class ParentDrawerPojo {
    String cat_id,cat_name,sub_cat;

    public String getSub_cat() {
        return sub_cat;
    }

    public void setSub_cat(String sub_cat) {
        this.sub_cat = sub_cat;
    }

    private ArrayList<ChildDrawerPojo> list = new ArrayList<ChildDrawerPojo>();
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


    public void SetSubCatList(ArrayList<ChildDrawerPojo> sub_cat_list) {
        this.list = sub_cat_list;
    }
    public ArrayList<ChildDrawerPojo> getSubCatList() {
        return list;
    }

}
