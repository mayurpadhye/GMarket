package com.cube9.gmarket.Category.ModelClass;

import com.cube9.gmarket.Home.ModelClass.CategoryProductsPojo;

public interface ItemClickListener {
    void itemClicked(CategoryProductsPojo item);
    void itemClicked(Section section);
}
