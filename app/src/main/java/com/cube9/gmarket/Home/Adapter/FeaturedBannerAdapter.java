package com.cube9.gmarket.Home.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cube9.gmarket.Home.ModelClass.FeaturedProductPojo;
import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeaturedBannerAdapter extends PagerAdapter {
    private Context mCtx;
   List<FeaturedProductPojo>featuredProductPojoList;
    private LayoutInflater layoutInflater;

    public FeaturedBannerAdapter(Context mCtx,  List<FeaturedProductPojo>featuredProductPojoList) {
        this.mCtx = mCtx;
        this.featuredProductPojoList = featuredProductPojoList;
    }

    @Override
    public int getCount() {
        return featuredProductPojoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.row_featured_products, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_banner_image);
        final FeaturedProductPojo item=featuredProductPojoList.get(position);
       // Glide.with(mCtx).load(item.getProduct_image()).into(imageView);


        /*Glide.with(mCtx).load(item.getProduct_image())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);*/

        Picasso.with(mCtx).load(item.getProduct_image()).noFade().placeholder(mCtx.getResources().getDrawable(R.drawable.ic_g_market_logo)).into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mCtx, ProductDetailsNewActivity.class);
                i.putExtra("product_id",item.getProduct_id());
                i.putExtra("product_name",item.getProduct_name());
                i.putExtra("category_id",item.getCategory_id());
                mCtx.startActivity(i);


            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
