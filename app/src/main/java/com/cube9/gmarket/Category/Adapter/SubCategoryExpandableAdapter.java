package com.cube9.gmarket.Category.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cube9.gmarket.Category.ModelClass.ParentSubCatPojo;
import com.cube9.gmarket.Category.ModelClass.SubCatPojo;
import com.cube9.gmarket.Home.ModelClass.ChildDrawerPojo;
import com.cube9.gmarket.Home.ModelClass.ParentDrawerPojo;
import com.cube9.gmarket.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubCategoryExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;

    private ArrayList<ParentSubCatPojo> parentSubCatPojos;

    public SubCategoryExpandableAdapter(Context context,
                                        ArrayList<ParentSubCatPojo> parentSubCatPojos) {
        this.context = context;
        this.parentSubCatPojos = parentSubCatPojos;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        List<SubCatPojo> productList = parentSubCatPojos.get(listPosition).getSubCatList();
        return productList.get(expandedListPosition);

    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        SubCatPojo child_items = (SubCatPojo) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_cat, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.tv_child_cat);
        expandedListTextView.setText(child_items.getCat_name());
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {

        ArrayList<SubCatPojo> productList = parentSubCatPojos.get(listPosition).getSubCatList();
        return productList.size();

    }

    @Override
    public ParentSubCatPojo getGroup(int listPosition) {
        return parentSubCatPojos.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return parentSubCatPojos.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ParentSubCatPojo headerInfo = (ParentSubCatPojo) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.sub_cat_parent, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.tv_sub_cat_parent);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(headerInfo.getCat_name());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}