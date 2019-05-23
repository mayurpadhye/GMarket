package com.cube9.gmarket.SearchProducts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.ModelClass.SearchProductModel;

import java.util.ArrayList;
import java.util.List;

public class SearchProductAdapter extends ArrayAdapter<SearchProductModel> {
    Context context;
    int resource, textViewResourceId;
    List<SearchProductModel> items, tempItems, suggestions;

    public SearchProductAdapter(Context context, int resource, int textViewResourceId, List<SearchProductModel> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<SearchProductModel>(items); // this makes the difference.
        suggestions = new ArrayList<SearchProductModel>();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_product_search, parent, false);
        }
        SearchProductModel product_items = items.get(position);
        if (product_items != null) {
            TextView tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            if (tv_product_name != null)
                tv_product_name.setText(product_items.getName());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((SearchProductModel) resultValue).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (SearchProductModel people : tempItems) {
                    if (people.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<SearchProductModel> filterList = (ArrayList<SearchProductModel>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (SearchProductModel people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
