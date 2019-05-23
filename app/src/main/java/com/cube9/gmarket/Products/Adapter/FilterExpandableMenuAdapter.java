package com.cube9.gmarket.Products.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.cube9.gmarket.Products.ModelClasses.FilterDetailsPojo;
import com.cube9.gmarket.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Expandable Menu Adapter*/

public class FilterExpandableMenuAdapter extends BaseExpandableListAdapter{
    private Context mContext;
    private List<String> mListDataHeader; // header titles
    ArrayList<String> selectedFilter;
    String COLOR_ID="";
    String BRAND_ID="";
    // child data in format of header title, child title
    private HashMap<String, ArrayList<FilterDetailsPojo>> mListDataChild;
    ExpandableListView expandList;

    public FilterExpandableMenuAdapter(Context context, List<String> listDataHeader,
                                       HashMap<String,ArrayList<FilterDetailsPojo>> listChildData,
                                       ExpandableListView mView) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
        this.expandList = mView;
        selectedFilter= new ArrayList<>();
        for(int k=0;k<listDataHeader.size();k++)
        {
            selectedFilter.add("");
        }
        COLOR_ID="";
        BRAND_ID="";
    }

    @Override
    public int getGroupCount() {
        int i = mListDataHeader.size();
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount = 0;
     /*   if (groupPosition <limit) {
            childCount = this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                    .size();
        }*/
        return mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        //   CustomUtil.showLog("value ",this.mListDataChild.get(groupPosition).get(childPosition).getName()+"");
        return this.mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition).getName();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.filter_parent_row, null);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.tv_title);

        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(mListDataHeader.get(groupPosition));

        if(getChildrenCount(groupPosition)>0) {
            if (isExpanded) {
                lblListHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle, 0, R.drawable.ic_remove, 0);
            } else {
                lblListHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle, 0, R.drawable.ic_add, 0);
            }
        }
        else {
            lblListHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle, 0, 0, 0);
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.filter_child_row, null);
        }

        AppCompatCheckBox chkListChild = convertView.findViewById(R.id.cb_sub_filter);

        chkListChild.setText(mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition).getName());


        chkListChild.setOnCheckedChangeListener(null);
        if(mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition).isSelected())
        {
            chkListChild.setChecked(true);
        }
        else
        {
            chkListChild.setChecked(false);
        }

        chkListChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if(checked) {

                    for (int p = 0; p < mListDataChild.get(mListDataHeader.get(groupPosition)).size(); p++) {
                        mListDataChild.get(mListDataHeader.get(groupPosition)).get(p).setSelected(false);
                    }

                   /* if (groupPosition == 0) {
                        setSelectedColorId(groupPosition, childPosition);
                    } else if (groupPosition == 1) {
                        setSelectedBrandId(groupPosition, childPosition);
                    }*/
                    setSelectedFilterId(groupPosition,childPosition);
                    mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition).setSelected(true);

                    notifyDataSetChanged();
                }
                else
                {
                    for (int p = 0; p < mListDataChild.get(mListDataHeader.get(groupPosition)).size(); p++) {
                        mListDataChild.get(mListDataHeader.get(groupPosition)).get(p).setSelected(false);
                    }

                /*    if (groupPosition == 0) {
                        setSelectedColorEmpty();
                    } else if (groupPosition == 1) {
                        setSelectedBrandEmpty();
                    }*/

                    removeValueFromSelectedFilter(groupPosition);
                    //  mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition).setSelected(true);

                    notifyDataSetChanged();
                }

            }
        });
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void setSelectedFilterId(int groupPosition, int childPosition)
    {
        selectedFilter.set(groupPosition,mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition).getId());
    }

    private void removeValueFromSelectedFilter(int groupPosition)
    {
        selectedFilter.set(groupPosition,"");
    }

    public ArrayList<String> getSelectedFilter() {
        return selectedFilter;
    }

    private void setSelectedColorEmpty()
    {
        COLOR_ID= "";
    }

    private void setSelectedBrandEmpty()
    {
        BRAND_ID= "";
    }

    private void setSelectedColorId(int groupPosition, int childPosition)
    {
        COLOR_ID= mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition).getId();
    }

    public String getSeletedColorId()
    {
        return COLOR_ID;
    }

    private void setSelectedBrandId(int groupPosition, int childPosition)
    {
        BRAND_ID= mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition).getId();
    }

    public String getSeletedBrandId()
    {
        return BRAND_ID;
    }

}
