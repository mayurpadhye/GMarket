package com.cube9.gmarket.Home.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cube9.gmarket.Home.ModelClass.ChildDrawerPojo;
import com.cube9.gmarket.Home.ModelClass.ParentDrawerPojo;
import com.cube9.gmarket.R;

import java.util.ArrayList;

public class ExpandableDrawerAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ParentDrawerPojo> parentDrawerPojos;

    public ExpandableDrawerAdapter(Context context, ArrayList<ParentDrawerPojo> parentDrawerPojos) {
        this.context = context;
        this.parentDrawerPojos = parentDrawerPojos;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ChildDrawerPojo> productList = parentDrawerPojos.get(groupPosition).getSubCatList();
        return productList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        ChildDrawerPojo child_items = (ChildDrawerPojo) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.row_child_drawer, null);
        }


        TextView childItem = (TextView) view.findViewById(R.id.childItem);
        childItem.setText(child_items.getSub_cat_name().trim());

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<ChildDrawerPojo> productList = parentDrawerPojos.get(groupPosition).getSubCatList();
        return productList.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentDrawerPojos.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return parentDrawerPojos.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        ParentDrawerPojo headerInfo = (ParentDrawerPojo) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.row_parent_drawer, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.heading);
        heading.setText(headerInfo.getCat_name().trim());

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
