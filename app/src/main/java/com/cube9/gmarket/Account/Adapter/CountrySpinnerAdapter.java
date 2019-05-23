package com.cube9.gmarket.Account.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cube9.gmarket.R;

import java.util.List;

public class CountrySpinnerAdapter extends BaseAdapter {
    Context context;

    List<String> countryNames;
    LayoutInflater inflter;

    public CountrySpinnerAdapter(Context context, List<String>countryNames) {
        this.context = context;
        this.countryNames = countryNames;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return countryNames.size();
    }

    @Override
    public Object getItem(int i) {
        return countryNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.row_country, null);

        TextView names = (TextView) view.findViewById(R.id.tv_country);
        if (countryNames.get(i).equals(context.getResources().getString(R.string.select_country)))
        {
            names.setTextColor(context.getResources().getColor(R.color.gray_dark));
        }
        else
        {
            names.setTextColor(context.getResources().getColor(R.color.black));
        }
        names.setText(countryNames.get(i));
        return view;
    }


}
