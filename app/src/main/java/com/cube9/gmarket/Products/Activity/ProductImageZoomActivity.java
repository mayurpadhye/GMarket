package com.cube9.gmarket.Products.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cube9.gmarket.R;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;




public class ProductImageZoomActivity extends AppCompatActivity {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private LayoutInflater inflater;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Toolbar toolbar;
  //  ArrayList<String> list;

    ImageView iv_back,iv_search,iv_cart;
    SubsamplingScaleImageView imageView;
    TextView tv_title,tv_cart_count;
    Context context;
    LinearLayout id_gallery;
    List<String> product_images_list;

    LayoutInflater layoutInflater;
    // List<String> list;
    private ArrayList<Integer> IMAGES1;
    String imag="";
    OkHttpClient client;
    PhotoView pv_product;
    private ArrayList<Integer> ImagesArray;

    @Override
    protected void onResume() {
        super.onResume();
       tv_cart_count.setVisibility(View.GONE);
       iv_cart.setVisibility(View.GONE);
       iv_search.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_image_zoom);
        context=this;
        product_images_list=new ArrayList<String>();
        client=new OkHttpClient();
       // list = (ArrayList<String>) getIntent().getSerializableExtra("mylist");
        // list=new ArrayList<String>();
        initView();

        Intent intent = getIntent();
        String jsonArray = intent.getStringExtra("jsonArray");
        mAdapter = new HLVAdapter();
        mRecyclerView.setAdapter(mAdapter);


        try {
            JSONArray array = new JSONArray(jsonArray);
            Picasso.with(ProductImageZoomActivity.this).load( imag = String.valueOf(array.get(0))).networkPolicy(NetworkPolicy.NO_CACHE).into(pv_product);

            for (int i = 0; i < array.length(); i++) {
                 imag = String.valueOf(array.get(i));
                product_images_list.add(imag);

            }
          //  Glide.with(context).load(product_images_list.get(0)).into(pv_product);

        /*    Glide.with(ProductImageZoomActivity.this).load(product_images_list.get(0))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(pv_product);*/
           } catch (JSONException e) {
            e.printStackTrace();
        }

        imageView = (SubsamplingScaleImageView)findViewById(R.id.imageView);



        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void initView()
    {   ImagesArray = new ArrayList<Integer>();
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        // mLayoutManager.
        mRecyclerView.setLayoutManager(mLayoutManager);
        IMAGES1=new ArrayList<Integer>();
        pv_product=(PhotoView)findViewById(R.id.pv_product);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_back = (ImageView) toolbar.findViewById(R.id.iv_back);
        iv_search = (ImageView) toolbar.findViewById(R.id.iv_search);
        iv_cart = (ImageView) toolbar.findViewById(R.id.iv_cart);
        tv_title = (TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count = (TextView) toolbar.findViewById(R.id.tv_cart_count);
        tv_title.setText(getResources().getString(R.string.product_image));
        preferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=preferences.edit();
        final float density = getResources().getDisplayMetrics().density;
        id_gallery=(LinearLayout)findViewById(R.id.id_gallery);
        mPager = (ViewPager)findViewById(R.id.pager);
    }
    public class HLVAdapter extends RecyclerView.Adapter<HLVAdapter.ViewHolder> {




        public HLVAdapter() {
            super();


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_image_zoom, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            // viewHolder.tvSpecies.setText(alName.get(i));
            // viewHolder.iv_sub_image.setImageResource(list.get(i));
         //   Picasso.with(context).load(product_images_list.get(i)).into(viewHolder.iv_sub_image);
            Picasso.with(ProductImageZoomActivity.this).load(product_images_list.get(i)).networkPolicy(NetworkPolicy.NO_CACHE).into(viewHolder.iv_sub_image);
            /*Glide.with(ProductImageZoomActivity.this).load(product_images_list.get(i))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.iv_sub_image);*/
            viewHolder.iv_sub_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
              //  Glide.with(context).load(product_images_list.get(i)).into(pv_product) ;

                   /* Glide.with(ProductImageZoomActivity.this).load(product_images_list.get(i))
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(pv_product);*/
                    Picasso.with(ProductImageZoomActivity.this).load(product_images_list.get(i)).networkPolicy(NetworkPolicy.NO_CACHE).into(pv_product);
                }
            });


        }

        @Override
        public int getItemCount() {
            return product_images_list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder  {

            public ImageView iv_sub_image;



            public ViewHolder(View itemView) {
                super(itemView);
                iv_sub_image = (ImageView) itemView.findViewById(R.id.iv_sub_image);


            }




        }

    }
}
