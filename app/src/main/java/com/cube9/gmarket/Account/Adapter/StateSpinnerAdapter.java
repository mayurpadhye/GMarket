package com.cube9.gmarket.Account.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cube9.gmarket.R;

import java.util.List;

public class StateSpinnerAdapter extends BaseAdapter {
    Context context;

    List<String> stateNames;
    LayoutInflater inflter;

    public StateSpinnerAdapter(Context context, List<String> stateNames) {
        this.context = context;
        this.stateNames = stateNames;
        inflter = (LayoutInflater.from(context));
    }
    @Override
    public int getCount() {
        return stateNames.size();
    }

    @Override
    public Object getItem(int i) {
        return stateNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.row_country, null);

        TextView names = (TextView) view.findViewById(R.id.tv_country);
        if (stateNames.get(i).equals(context.getResources().getString(R.string.select_state)))
        {
            names.setTextColor(context.getResources().getColor(R.color.gray_dark));
        }
        else
        {
            names.setTextColor(context.getResources().getColor(R.color.black));
        }
        names.setText(stateNames.get(i));
        return view;
    }
}
