package com.cube9.gmarket.Category.ModelClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.ModelClass.CategoryProductsPojo;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;

public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickListener mItemClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.layout_section;
    private static final int VIEW_TYPE_ITEM = R.layout.row_latest_products; //TODO : change this

    public SectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                          final GridLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                          SectionStateChangeListener sectionStateChangeListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isSection(position)?gridLayoutManager.getSpanCount():1;
            }
        });
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof Section;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_ITEM :
                final CategoryProductsPojo items = (CategoryProductsPojo) mDataArrayList.get(position);
                holder.tv_product_name.setText(items.getProduct_name());
                // holder.tv_original_price.setText(items.getProduct_original_price());
                holder.tv_discount.setText(items.getProduct_discount());
                holder.tv_discounted_pice.setText(items.getProduct_price());


                if (items.getProduct_special_price().equals("null"))
                {
                    holder.tv_discounted_pice.setVisibility(View.GONE);
                }
                if (items.getProduct_discount().equals("null"))
                {
                    holder.tv_discount.setVisibility(View.GONE);
                }
                if (!items.getProduct_price().equals("null"))
                {

                    //holder.tv_original_price.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_price())));
                    holder.tv_original_price.setText(items.getProduct_price()+" FCFA");
                }
                if ( !items.getProduct_special_price().equals("null"))
                {
                    // holder.tv_discounted_pice.setText("FCFA "+new DecimalFormat("##.##").format(Double.parseDouble(items.getProduct_special_price())));
                    holder.tv_discounted_pice.setText(items.getProduct_special_price()+" FCFA");
                    holder.tv_original_price.setPaintFlags( holder.tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                }


                //set drawable to imageview
        /*Glide.with(context)
                .load(items.getProduct_image())
                .apply(new RequestOptions())
                .into(holder.iv_product_image);*/

       /* Glide.with(context).load(items.getProduct_image())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_product_image);*/

             //   Picasso.with(mContext).load(items.getProduct_image()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.iv_product_image);

                if (!items.getProduct_image().isEmpty()) {
                    Picasso.with(mContext)
                            .load(items.getProduct_image())
                            .noFade()
                            .resize(150, 150)
                            .placeholder(mContext.getResources().getDrawable(R.drawable.ic_g_market_logo))
                            .into(holder.iv_product_image);
                }



                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(mContext, ProductDetailsNewActivity.class);
                        i.putExtra("product_id",items.getProduct_id());
                        i.putExtra("product_name",items.getProduct_name());
                        i.putExtra("category_id",items.getCategory_id());
                        mContext.startActivity(i);
                    }
                });
                break;
            case VIEW_TYPE_SECTION :
                final Section section = (Section) mDataArrayList.get(position);
                holder.sectionTextView.setText(section.getName());
                holder.iv_cat_image.setVisibility(View.GONE);
              //  Picasso.with(mContext).load(section.getImage()).noFade().into(holder.iv_cat_image);
               /* holder.sectionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     /   mItemClickListener.itemClicked(section);
                    }
                });*/
                holder.sectionToggleButton.setChecked(section.isExpanded);
                holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position))
            return VIEW_TYPE_SECTION;
        else return VIEW_TYPE_ITEM;
    }



    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;

        //for section
        TextView sectionTextView;
        ImageView iv_cat_image;
        ToggleButton sectionToggleButton;

        //for item
        ImageView iv_product_image;
        Button btn_add_to_cart;
        TextView tv_product_name,tv_discounted_pice,tv_original_price,tv_discount,tv_rating,tv_no_cost_emi;
        SparkButton spark_button;
        LinearLayout ll_product;
        RatingBar ratingBar;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                iv_product_image = (ImageView) view.findViewById(R.id.iv_product_image);
                tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
                tv_discounted_pice = (TextView) view.findViewById(R.id.tv_discounted_pice);
                tv_original_price = (TextView) view.findViewById(R.id.tv_original_price);
                tv_discount = (TextView) view.findViewById(R.id.tv_discount);
                tv_rating = (TextView) view.findViewById(R.id.tv_rating);
                tv_no_cost_emi = (TextView) view.findViewById(R.id.tv_no_cost_emi);
                spark_button = (SparkButton) view.findViewById(R.id.spark_button);
                ll_product = (LinearLayout) view.findViewById(R.id.ll_product);
                btn_add_to_cart=(Button)view.findViewById(R.id.btn_add_to_cart);
                ratingBar=(RatingBar) view.findViewById(R.id.ratingBar);
            } else {
                sectionTextView = (TextView) view.findViewById(R.id.text_section);
                iv_cat_image = (ImageView) view.findViewById(R.id.iv_cat_image);
                sectionToggleButton = (ToggleButton) view.findViewById(R.id.toggle_button_section);
            }
        }
    }
}
