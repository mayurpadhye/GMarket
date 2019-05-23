package com.cube9.gmarket.SearchProducts.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.ModelClass.SearchProductModel;

import java.util.ArrayList;
import java.util.List;

public class SearchProductListAdapter extends RecyclerView.Adapter<SearchProductListAdapter.MyViewHolder>  {
    private List<SearchProductModel> searchProductModelList, tempItems, suggestions;
    Context context;
    private ArrayList<String> names;
    public SearchProductListAdapter(List<SearchProductModel> searchProductModelList, Context context) {
        this.searchProductModelList = searchProductModelList;
        this.context = context;
        tempItems = new ArrayList<SearchProductModel>(searchProductModelList); // this makes the difference.
        suggestions = new ArrayList<SearchProductModel>();
        names=new ArrayList<String>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_search, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final SearchProductModel product_items=searchProductModelList.get(position);


        if (product_items != null) {

            if (holder.tv_product_name != null)
                holder.tv_product_name.setText(product_items.getName());
            holder.cv_searched_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context, ProductDetailsNewActivity.class);
                    i.putExtra("product_id",product_items.getEntity_id());
                    i.putExtra("category_id",product_items.getCategory_id());
                    context.startActivity(i);

                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return searchProductModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_product_name;
        CardView cv_searched_item;

        public MyViewHolder(View view) {
            super(view);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            cv_searched_item = (CardView) view.findViewById(R.id.cv_searched_item);


        }

    }



    /**
     * Custom Filter implementation for custom suggestions we provide.
     */


    public void filterList(List<SearchProductModel> filterdNames) {
        this.searchProductModelList = filterdNames;
        notifyDataSetChanged();
    }
}
