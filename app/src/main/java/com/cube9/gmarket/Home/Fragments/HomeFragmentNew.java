package com.cube9.gmarket.Home.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Adapter.BannerAdapter;
import com.cube9.gmarket.Home.Adapter.BestSellerAdapter;
import com.cube9.gmarket.Home.Adapter.CategoriesAdapter;
import com.cube9.gmarket.Home.Adapter.FeaturedProductAdapterList;
import com.cube9.gmarket.Home.Adapter.ItemDecorationAlbumColumns;
import com.cube9.gmarket.Home.Adapter.LatestProductAdapterMen;
import com.cube9.gmarket.Home.Adapter.LatestProductAdapterWomen;
import com.cube9.gmarket.Home.Adapter.MostViewedElectronicsAdapter;
import com.cube9.gmarket.Home.Adapter.MostViewedMenAdapter;
import com.cube9.gmarket.Home.Adapter.MostViewedWomenAdapter;
import com.cube9.gmarket.Home.ModelClass.BestSellerPojo;
import com.cube9.gmarket.Home.ModelClass.CategoryListPojo;
import com.cube9.gmarket.Home.ModelClass.FeaturedProductPojo;
import com.cube9.gmarket.Home.ModelClass.LatestProductItemPojo;
import com.cube9.gmarket.Home.ModelClass.LatestProductMenPojo;
import com.cube9.gmarket.Home.ModelClass.MostViewdElectronics;
import com.cube9.gmarket.Home.ModelClass.MostViewedMenPojo;
import com.cube9.gmarket.Home.ModelClass.MostViewedWomenPojo;
import com.cube9.gmarket.Products.Activity.ProductListActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragmentNew extends Fragment {

    View v;

  ProgressBar p_bar;
    Button btn_show_all_latest_products_women,btn_show_all_latest_products_men;
    private ShimmerFrameLayout mShimmerViewContainer;
    NestedScrollView sv_main;
    RecyclerView rv_featured_product,rv_most_viewed_women,rv_most_viewed_men,rv_most_viewed_electronics;
    ViewPager main_banner;
    SharedPrefManager pref;
    List<BestSellerPojo> bestSellerPojoList;
    String latest_cat_id_men="",latest_cat_name="",latest_cat_name_men="",latest_cat_id="";
    List<CategoryListPojo>categoryListPojoList;
    RecyclerView rv_best_seller;
    List<String> mainBannerList;
    int currentPage = 0;
    int NUM_PAGES;
    Timer timer;
    TextView tv_cart_count;
    long DELAY_MS;//delay in milliseconds before task is to be executed
    long PERIOD_MS;
    List<FeaturedProductPojo> featuredProductPojoList;
    NetworkChangeReceiver mNetworkReceiver;
    RecyclerView rv_latest_men_products,rv_latest_women_products;
    GridView gv_latest_products_women;
    ImageView iv_menu,iv_search,iv_cart;
    CircleIndicator indicator ;
    List<LatestProductItemPojo> latestProductItemPojoList;
    List<LatestProductMenPojo> latestProductMenPojoList;
    List<MostViewedMenPojo> mostViewedMenPojoList;
    List<MostViewedWomenPojo> mostViewedWoMenPojoList;
    List<MostViewdElectronics> mostViewdElectronicsList;
    LatestProductAdapterWomen adapter;
    String languageToLoad="";
    RecyclerView rv_categories;
    public HomeFragmentNew() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_home_new, container, false);
        initView(v);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
        setUpHeaderViewSlider(v);
        setUpFreaturedProducts(v);

        getData();


        btn_show_all_latest_products_women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),ProductListActivity.class);
                i.putExtra("cat_id",latest_cat_id);
                i.putExtra("cat_name",latest_cat_name);
                getActivity().startActivity(i);
            }
        });

        btn_show_all_latest_products_men.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),ProductListActivity.class);
                i.putExtra("cat_id",latest_cat_id_men);
                i.putExtra("cat_name",latest_cat_name_men);
                getActivity().startActivity(i);
            }
        });

    return v;
    }

    private void getData() {


      //  super.onResume();

        String language = pref.getLanguage();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        sv_main.setVisibility(View.GONE);
        if (language.isEmpty()) {
            language = CustomUtils.FRENCH;
            languageToLoad = CustomUtils.FRENCH_CODE;
        }


        if (language.equals("1"))
        {
            languageToLoad = CustomUtils.ENGLISH_CODE;
        }

        else
        {
            languageToLoad = CustomUtils.FRENCH_CODE;
        }
        Log.i("SelectedLanguage", "" + languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;


        if (NetworkChangeReceiver.isOnline(getActivity()))
        {
            currentPage = 0;
            NUM_PAGES=3;
            DELAY_MS = 1000;
            PERIOD_MS = 7000;
            getCategories();
            getMainBanner();
            getBestSeller();
            getFeaturedProducts();
            getLatestProducts();
            if (pref.IsLogin())
            {tv_cart_count.setVisibility(View.VISIBLE);}

            tv_cart_count.setText(pref.getCartCount());
            indicator = (CircleIndicator)
                    v.findViewById(R.id.indicator);


        }
        else
            startActivity(new Intent(getActivity(), NoInternetActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        String language = pref.getLanguage();
       /* mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        sv_main.setVisibility(View.GONE);*/
        if (language.isEmpty()) {
            language = CustomUtils.FRENCH;
            languageToLoad = CustomUtils.FRENCH_CODE;
        }


        if (language.equals("1"))
        {
            languageToLoad = CustomUtils.ENGLISH_CODE;
        }

        else
        {
            languageToLoad = CustomUtils.FRENCH_CODE;
        }
        Log.i("SelectedLanguage", "" + languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        /*


        String language = pref.getLanguage();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        sv_main.setVisibility(View.GONE);
        if (language.isEmpty()) {
            language = CustomUtils.FRENCH;
            languageToLoad = CustomUtils.FRENCH_CODE;
        }


        if (language.equals("1"))
        {
            languageToLoad = CustomUtils.ENGLISH_CODE;
        }

        else
        {
            languageToLoad = CustomUtils.FRENCH_CODE;
        }
        Log.i("SelectedLanguage", "" + languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;


        if (NetworkChangeReceiver.isOnline(getActivity()))
        {
            currentPage = 0;
            NUM_PAGES=3;
            DELAY_MS = 1000;
            PERIOD_MS = 7000;
            getCategories();
            getMainBanner();
            getBestSeller();
            getFeaturedProducts();
            getLatestProducts();
            if (pref.IsLogin())
            {tv_cart_count.setVisibility(View.VISIBLE);}

            tv_cart_count.setText(pref.getCartCount());
            indicator = (CircleIndicator)
                    v.findViewById(R.id.indicator);


        }
        else
            startActivity(new Intent(getActivity(), NoInternetActivity.class));*/
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();

    }
    public void initView(View v)
    {
        //   FirebaseCrash.report(new Exception("My first Firebase non-fatal error on Android"));
        mostViewedMenPojoList=new ArrayList<MostViewedMenPojo>();
        mostViewedWoMenPojoList=new ArrayList<MostViewedWomenPojo>();
        mostViewdElectronicsList=new ArrayList<MostViewdElectronics>();
        categoryListPojoList=new ArrayList<CategoryListPojo>();
        pref= new SharedPrefManager(getActivity());
        mShimmerViewContainer = (ShimmerFrameLayout)v.findViewById(R.id.shimmer_view_container);
        sv_main = (NestedScrollView) v.findViewById(R.id.sv_main);
        rv_categories=(RecyclerView)v.findViewById(R.id.rv_categories);
        rv_most_viewed_women=(RecyclerView)v.findViewById(R.id.rv_most_viewed_women);
        rv_most_viewed_men=(RecyclerView)v.findViewById(R.id.rv_most_viewed_men);
        rv_most_viewed_electronics=(RecyclerView)v.findViewById(R.id.rv_most_viewed_electronics);
        rv_featured_product=(RecyclerView)v.findViewById(R.id.rv_featured_product);
        p_bar=(ProgressBar)v.findViewById(R.id.p_bar);
        btn_show_all_latest_products_women=(Button)v.findViewById(R.id.btn_show_all_latest_products_women);
        btn_show_all_latest_products_men=(Button)v.findViewById(R.id.btn_show_all_latest_products_men);
        mainBannerList=new ArrayList<String>();
        featuredProductPojoList=new ArrayList<FeaturedProductPojo>();
        tv_cart_count=(TextView)getActivity().findViewById(R.id.tv_cart_count);
        latestProductItemPojoList=new ArrayList<LatestProductItemPojo>();
        latestProductMenPojoList=new ArrayList<LatestProductMenPojo>();
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(),3);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_categories.setLayoutManager(gridLayoutManager1);

        rv_categories.addItemDecoration(new ItemDecorationAlbumColumns(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),
                getResources().getInteger(R.integer.photo_list_preview_columns)));

        GridLayoutManager gridLayoutManager3 = new GridLayoutManager(getActivity(),1);
        gridLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_featured_product.setLayoutManager(gridLayoutManager3);


        GridLayoutManager gridLayoutMostViedMen = new GridLayoutManager(getActivity(),1);
        gridLayoutMostViedMen.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_most_viewed_men.setLayoutManager(gridLayoutMostViedMen);

        GridLayoutManager gridLayoutMostViedWoMen = new GridLayoutManager(getActivity(),1);
        gridLayoutMostViedWoMen.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_most_viewed_women.setLayoutManager(gridLayoutMostViedWoMen);
        GridLayoutManager gridLayoutMostViedElec = new GridLayoutManager(getActivity(),1);
        gridLayoutMostViedElec.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_most_viewed_electronics.setLayoutManager(gridLayoutMostViedElec);
      //  getMostViewedProductsElectronics();

    }//initViewClose
    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getActivity(). registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity(). registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            getActivity().unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }



    public void setUpHeaderViewSlider(View v)
    {

        LayoutInflater inflater = (LayoutInflater)getActivity(). getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        main_banner=(ViewPager) v.findViewById(R.id.main_banner);



        final float density = getResources().getDisplayMetrics().density;
    }
    public void setUpFreaturedProducts(View v)
    {
        LayoutInflater inflater1 = (LayoutInflater)getActivity(). getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       // featured_brand_slider=(ViewPager)v.findViewById(R.id.featured_brand_slider);

        rv_best_seller = (RecyclerView) v.findViewById(R.id.rv_best_seller);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_best_seller.setLayoutManager(gridLayoutManager);





        bestSellerPojoList=new ArrayList<BestSellerPojo>();
        rv_latest_men_products=(RecyclerView)v.findViewById(R.id.rv_latest_men_products);
        rv_latest_women_products=(RecyclerView) v.findViewById(R.id.rv_latest_women_products);


        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(),2);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_latest_men_products.setLayoutManager(gridLayoutManager1);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                gridLayoutManager1.getOrientation());
        rv_latest_men_products.addItemDecoration(dividerItemDecoration);
        rv_latest_men_products.setNestedScrollingEnabled(false);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(),2);
        gridLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        rv_latest_women_products.setLayoutManager(gridLayoutManager2);
        rv_latest_women_products.setNestedScrollingEnabled(false);

       rv_categories.setNestedScrollingEnabled(false);

        rv_latest_men_products.addItemDecoration(new ItemDecorationAlbumColumns(
                2,
                2));
    }//setUpFeaturedProductsClose
    public void getCategories()
    {
       // p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.CATEGORIES_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  p_bar.setVisibility(View.GONE);
                        try {

                            //converting response to json object
                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                categoryListPojoList.clear();
                                JSONObject category=Jsonobj.getJSONObject("category");
                                JSONArray category_array=category.getJSONArray("category");
                                for (int i=0;i<category_array.length();i++)
                                {
                                    JSONObject j1=category_array.getJSONObject(i);
                                    String cat_id=j1.getString("cat_id");
                                    String cat_name=j1.getString("cat_name");
                                    String image=j1.getString("image");
                                    String Sub_cats=j1.getString("Sub_cats");
                                  //  JSONArray Sub_cats=j1.getJSONArray("Sub_cats");
                                    /*String sub_cat_id="",sub_cat_name="";
                                    if (Sub_cats.length()>0)
                                    {
                                        for (int k=0;k<Sub_cats.length();k++)
                                        {
                                            JSONObject j2=Sub_cats.getJSONObject(k);
                                             sub_cat_id=j2.getString("cat_id");
                                             sub_cat_name=j2.getString("cat_name");
                                        }
                                    }*/

                                    categoryListPojoList.add(new CategoryListPojo(cat_id,cat_name,image,"",0,Sub_cats));
                                }

                            }


                        } catch (JSONException e) {
                            Log.i("jsnnnnn",""+e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //    p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",pref.getLanguage());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }//getCategories


    public void getMostViewedProductsElectronics()
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.MOST_VIEWED_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            //converting response to json object
                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            Log.i("jssss",""+Jsonobj.toString());

                            if (status.equals("1"))
                            {
                                mostViewedMenPojoList.clear();
                                mostViewedWoMenPojoList.clear();
                                mostViewdElectronicsList.clear();

                                String men_cat_image=Jsonobj.getString("men_cat_image");
                                String women_cat_image=Jsonobj.getString("women_cat_image");
                                String ele_cat_image=Jsonobj.getString("women_cat_image");
                                JSONArray men_prducts=Jsonobj.getJSONArray("men_prducts");


                                for (int k=0;k<men_prducts.length();k++)
                                {
                                    JSONObject j1=men_prducts.getJSONObject(k);
                                    String product_id=j1.getString("product_id");
                                    String product_name=j1.getString("product_name");
                                    String product_price=j1.getString("product_price");
                                    String product_special_price=j1.getString("product_special_price");
                                    String product_image=j1.getString("product_image");
                                    String product_discount=j1.getString("product_discount");
                                    String product_desc=j1.getString("product_desc");
                                    String wishlist_flag=j1.getString("wishlist_flag");
                                    String quantity_available="";//j1.getString("quantity_available");
                                    String category_id=j1.getString("category_id");
                                    String category_name=j1.getString("category_name");
                                    String review_count=j1.getString("review_count");
                                    String rating=j1.getString("rating");


                                    mostViewedMenPojoList.add(new MostViewedMenPojo(product_id,product_name,product_price,product_image,product_discount,product_desc,wishlist_flag,quantity_available,category_id,product_special_price,rating,review_count));


                                }
                                JSONArray women_prducts =Jsonobj.getJSONArray("women_prducts");


                                for (int j=0;j<women_prducts.length();j++)
                                {
                                    JSONObject j2=women_prducts.getJSONObject(j);
                                    String product_id=j2.getString("product_id");
                                    String product_name=j2.getString("product_name");
                                    String product_price=j2.getString("product_price");
                                    String product_special_price=j2.getString("product_special_price");
                                    String product_image=j2.getString("product_image");
                                    String product_discount=j2.getString("product_discount");
                                    String product_desc=j2.getString("product_desc");
                                    String wishlist_flag=j2.getString("wishlist_flag");
                                    String quantity_available="";//j1.getString("quantity_available");
                                    String category_id=j2.getString("category_id");
                                    String category_name=j2.getString("category_name");
                                    String review_count=j2.getString("review_count");
                                    String rating=j2.getString("rating");

                                    mostViewedWoMenPojoList.add(new MostViewedWomenPojo(product_id,product_name,product_price,product_image,product_discount,product_desc,wishlist_flag,quantity_available,category_id,product_special_price,rating,review_count));

                                }
                                JSONArray ele_prducts =Jsonobj.getJSONArray("ele_prducts");

                                for (int l=0;l<ele_prducts.length();l++)
                                {
                                    JSONObject j3=ele_prducts.getJSONObject(l);
                                    String product_id=j3.getString("product_id");
                                    String product_name=j3.getString("product_name");
                                    String product_price=j3.getString("product_price");
                                    String product_special_price=j3.getString("product_special_price");
                                    String product_image=j3.getString("product_image");
                                    String product_discount=j3.getString("product_discount");
                                    String product_desc=j3.getString("product_desc");
                                    String wishlist_flag=j3.getString("wishlist_flag");
                                    String quantity_available="";//j1.getString("quantity_available");
                                    String category_id=j3.getString("category_id");
                                    String category_name=j3.getString("category_name");
                                    String review_count=j3.getString("review_count");
                                    String rating=j3.getString("rating");
                                    mostViewdElectronicsList.add(new MostViewdElectronics(product_id,product_name,product_price,product_image,product_discount,product_desc,wishlist_flag,quantity_available,category_id,product_special_price,rating,review_count));


                                }

                                if (ele_prducts.length()>0 )
                                {
                                    MostViewedElectronicsAdapter mostViewedElectronicsAdapter=new MostViewedElectronicsAdapter(mostViewdElectronicsList,getActivity());
                                    rv_most_viewed_electronics.setAdapter(mostViewedElectronicsAdapter);
                                }
                                if (men_prducts.length()>0)
                                {
                                    MostViewedMenAdapter mostViewedMenAdapter=new MostViewedMenAdapter(mostViewedMenPojoList,getActivity());
                                    rv_most_viewed_men.setAdapter(mostViewedMenAdapter);
                                }
                                if (women_prducts.length()>0)
                                {
                                    MostViewedWomenAdapter mostViewedMenAdapter=new MostViewedWomenAdapter(mostViewedWoMenPojoList,getActivity());
                                    rv_most_viewed_women.setAdapter(mostViewedMenAdapter);
                                }

                            }
                            //p_bar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("Lates",""+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",pref.getLanguage());
                params.put("user_id",pref.GetCustomerId());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);

       /* for (int i=0;i<10;i++)
        {
            mostViewedMenPojoList.add(new MostViewedMenPojo("1","Cargo","100",""));

        }
        MostViewedMenAdapter mostViewedMenAdapter=new MostViewedMenAdapter(mostViewedMenPojoList,getActivity());
        rv_most_viewed_electronics.setAdapter(mostViewedMenAdapter);*/

    }


    public void getLatestProducts()
    {
        //    p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.LATEST_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            //converting response to json object
                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                latestProductItemPojoList.clear();
                                latestProductMenPojoList.clear();

                                String men_cat_image=Jsonobj.getString("men_cat_image");
                                String women_cat_image=Jsonobj.getString("women_cat_image");
                                JSONArray men_prducts=Jsonobj.getJSONArray("men_prducts");
                                int tottal_size=0;
                                if (men_prducts.length()>4)
                                 {
                                     tottal_size=4;
                                 }
                                 else
                                {
                                    tottal_size=men_prducts.length();
                                }

                                for (int i=0;i<tottal_size;i++)
                                {
                                    JSONObject j1=men_prducts.getJSONObject(i);
                                    String product_id=j1.getString("product_id");
                                    String product_name=j1.getString("product_name");
                                    String product_price=j1.getString("product_price");
                                    String product_special_price=j1.getString("product_special_price");
                                    String product_image=j1.getString("product_image");
                                    String product_discount=j1.getString("product_discount");
                                    String product_desc=j1.getString("product_desc");
                                    String wishlist_flag=j1.getString("wishlist_flag");
                                    String quantity_available="";//j1.getString("quantity_available");
                                    String category_id=j1.getString("category_id");
                                    String category_name=j1.getString("category_name");
                                    String review_count=j1.getString("review_count");
                                    String rating=j1.getString("rating");

                                    latest_cat_id_men=category_id;
                                    latest_cat_name_men=category_name;
                                    latestProductMenPojoList.add(new LatestProductMenPojo(product_id,product_name,product_price,product_image,product_discount,product_desc,wishlist_flag,quantity_available,category_id,product_special_price,rating,review_count));
                                }
                                JSONArray women_products =Jsonobj.getJSONArray("women_products");

                                int tottal_size_womenr=0;
                                if (women_products.length()>4)
                                {
                                    tottal_size_womenr=4;
                                }
                                else
                                {
                                    tottal_size_womenr=women_products.length();
                                }
                                for (int i=0;i<tottal_size_womenr;i++)
                                {
                                    JSONObject j1=women_products.getJSONObject(i);
                                    String product_id=j1.getString("product_id");
                                    String product_name=j1.getString("product_name");
                                    String product_price=j1.getString("product_price");
                                    String product_special_price=j1.getString("product_special_price");
                                    String product_image=j1.getString("product_image");
                                    String product_discount=j1.getString("product_discount");
                                    String product_desc=j1.getString("product_desc");
                                    String wishlist_flag=j1.getString("wishlist_flag");
                                    String quantity_available="";//j1.getString("quantity_available");
                                    String category_id=j1.getString("category_id");
                                    String category_name=j1.getString("category_name");
                                    String review_count=j1.getString("review_count");
                                    String rating=j1.getString("rating");
                                    latest_cat_name=category_name;
                                    latest_cat_id=category_id;
                                    latestProductItemPojoList.add(new LatestProductItemPojo(product_id,product_name,product_price,product_image,product_discount,product_desc,wishlist_flag,quantity_available,category_id,product_special_price,rating,review_count));
                                }

                                adapter = new LatestProductAdapterWomen(latestProductItemPojoList, getActivity(),p_bar);
                                rv_latest_women_products.setAdapter(adapter);
                      //          LatestProductAdapterMen adapterMen = new LatestProductAdapterMen(latestProductMenPojoList, getActivity(),p_bar,HomeFragmentNew.this);
                          //      rv_latest_men_products.setAdapter(adapterMen);
                            }
                            //p_bar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("Lates",""+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",pref.getLanguage());
                params.put("user_id",pref.GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }//getCategories

    public void getBestSeller()
    {
        // p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.BEST_SELLER_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   p_bar.setVisibility(View.GONE);
                        try {

                            //converting response to json object
                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                bestSellerPojoList.clear();
                                JSONArray best_seller=Jsonobj.getJSONArray("best_seller");
                                for (int i=0;i<best_seller.length();i++)
                                {
                                    JSONObject j1=best_seller.getJSONObject(i);
                                    String product_name=j1.getString("product_name");
                                    String product_id=j1.getString("product_id");
                                    String product_price=j1.getString("product_price");
                                    String product_image=j1.getString("product_image");
                                    String category_id=j1.getString("category_id");
                                    bestSellerPojoList.add(new BestSellerPojo(product_name,product_image,product_price,product_id,category_id));
                                }
                                BestSellerAdapter adapter=new BestSellerAdapter(bestSellerPojoList,getActivity());
                                rv_best_seller.setAdapter(adapter);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   p_bar.setVisibility(View.GONE);
                        // Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",pref.getLanguage());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    public void getMainBanner()
    {


//Set circle indicator radius



        // p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.HOME_PAGE_BANNER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //     p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                mainBannerList.clear();
                                String cart_counter =Jsonobj.getString("cart_counter");
                                pref.setCartCount(cart_counter);
                                tv_cart_count.setText(cart_counter);
                                JSONObject bannerdata=Jsonobj.getJSONObject("bannerdata");
                                String banner1=bannerdata.getString("banner1");
                                String banner2=bannerdata.getString("banner2");
                                String banner3=bannerdata.getString("banner3");
                                mainBannerList.add(banner1);
                                mainBannerList.add(banner2);
                                mainBannerList.add(banner3);
                                try{
                                    BannerAdapter bannerAdapter=new BannerAdapter(getActivity(),mainBannerList);
                                    main_banner.setAdapter(bannerAdapter);
                                    indicator.setViewPager(main_banner);
                                }
                                catch(NullPointerException e)
                                {
                                    e.printStackTrace();
                                }

                                final Handler handler = new Handler();
                                final Runnable Update = new Runnable() {
                                    public void run() {
                                        if (currentPage <= mainBannerList.size()) {

                                            main_banner.setCurrentItem(currentPage, true);
                                            currentPage++;
                                        }
                                        if (currentPage == mainBannerList.size()) {
                                            currentPage = -1;
                                        } else {
                                            main_banner.setCurrentItem(currentPage);
                                        }
                                    }
                                };

                                timer = new Timer(); // This will create a new Thread
                                timer .schedule(new TimerTask() { // task to be scheduled

                                    @Override
                                    public void run() {
                                        handler.post(Update);


                                    }
                                }, 1000, 7000);



                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //      p_bar.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",pref.getLanguage());
                params.put("user_id",pref.GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }//getMainBanner


    public void
    getFeaturedProducts()
    {
        // p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_FEATURED_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //     p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");
                            Log.i("Featured",""+Jsonobj.toString());

                            if (status.equals("1"))
                            {
                                featuredProductPojoList.clear();
                                JSONArray productinfo=Jsonobj.getJSONArray("productinfo");
                                for (int i=0;i<productinfo.length();i++)
                                {
                                    JSONObject j1=productinfo.getJSONObject(i);
                                    String product_name=j1.getString("product_name");
                                    String product_id=j1.getString("product_id");
                                    String product_price=j1.getString("product_price");
                                    String product_image=j1.getString("product_image");
                                    String product_special_price=j1.getString("product_special_price");
                                    String product_discount=j1.getString("product_discount");
                                    String category_id=j1.getString("category_id");
                                    String review_count=j1.getString("review_count");
                                    String rating=j1.getString("rating");
                                    featuredProductPojoList.add(new FeaturedProductPojo(product_name,product_id,product_price,product_image,product_discount,product_special_price,category_id,rating,review_count));
                                }

                                FeaturedProductAdapterList featuredBannerAdapter = new FeaturedProductAdapterList(getActivity(),featuredProductPojoList);
                                rv_featured_product.setAdapter(featuredBannerAdapter);
                            }

                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            sv_main.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //    p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",pref.getLanguage());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
    public void AddtoCart(final String product_id) {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");
                            String message = Jsonobj.getString("message");
                            if (status.equals("1")) {
                                String my_cart_count = Jsonobj.getString("my_cart_count");
                                tv_cart_count.setText(my_cart_count);
                                new SharedPrefManager(getActivity()).setCartCount(my_cart_count);

                                CustomUtils.showToast(message, getActivity());


                            } else {
                                CustomUtils.showToast(message, getActivity());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("add_to_cart_exception",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        //    Toast.makeText(ProductDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(getActivity()).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(getActivity()).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterNetworkChanges();
    }
}
