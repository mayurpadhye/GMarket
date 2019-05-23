package com.cube9.gmarket.Home.Adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cube9.gmarket.R;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


public class BannerAdapter extends PagerAdapter {
    private Context mCtx;
    List<String> main_banner_list;
    LayoutInflater layoutInflater;
    public BannerAdapter(Context mCtx, List<String> main_banner_list) {
        this.mCtx = mCtx;
        this.main_banner_list = main_banner_list;
        layoutInflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return main_banner_list.size();
    }

   /* @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }*/

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.main_slider, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_banner_image);
        if (mCtx!=null)
        //Glide.with(getApplicationContext()).load(main_banner_list.get(position)).into(imageView);


       /* Glide.with(mCtx).load(main_banner_list.get(position))
                .thumbnail(0.5f)
               
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);*/

  Picasso.with(mCtx).load(main_banner_list.get(position)).networkPolicy(NetworkPolicy.NO_CACHE).into(imageView);
        container.addView(itemView);

        //listening to image clickSDX

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
