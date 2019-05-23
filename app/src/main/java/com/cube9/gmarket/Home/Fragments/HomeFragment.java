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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.cube9.gmarket.Home.Adapter.CategoryListAdapter;
import com.cube9.gmarket.Home.Adapter.LatestProductAdapterWomen;
import com.cube9.gmarket.Home.ModelClass.BestSellerPojo;
import com.cube9.gmarket.Home.ModelClass.CategoryListPojo;
import com.cube9.gmarket.Home.ModelClass.FeaturedProductPojo;
import com.cube9.gmarket.Home.ModelClass.LatestProductItemPojo;
import com.cube9.gmarket.Home.ModelClass.LatestProductMenPojo;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;

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

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import me.relex.circleindicator.CircleIndicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    View v;
    View footer,header;
    ProgressBar p_bar;
    ViewPager featured_brand_slider;
  ViewPager main_banner;
SharedPrefManager pref;
  List<BestSellerPojo> bestSellerPojoList;
    GridViewWithHeaderAndFooter gv_categories;
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
    LatestProductAdapterWomen adapter;
    String languageToLoad="";
    public HomeFragment() {
        // Required empty public constructor
    }
 

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_home, container, false);
        initView(v);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
        setUpHeaderViewSlider(v);
        setUpFreaturedProducts(v);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
              String language = pref.getLanguage();



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
                    header.findViewById(R.id.indicator);


        }
       else
            startActivity(new Intent(getActivity(), NoInternetActivity.class));
    }


    public void initView(View v)
    {
     //   FirebaseCrash.report(new Exception("My first Firebase non-fatal error on Android"));
        categoryListPojoList=new ArrayList<CategoryListPojo>();
       pref= new SharedPrefManager(getActivity());
        gv_categories=(GridViewWithHeaderAndFooter)v.findViewById(R.id.gv_categories);
        p_bar=(ProgressBar)v.findViewById(R.id.p_bar);
        mainBannerList=new ArrayList<String>();
        featuredProductPojoList=new ArrayList<FeaturedProductPojo>();
        tv_cart_count=(TextView)getActivity().findViewById(R.id.tv_cart_count);
        latestProductItemPojoList=new ArrayList<LatestProductItemPojo>();
        latestProductMenPojoList=new ArrayList<LatestProductMenPojo>();

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
        header = inflater.inflate(R.layout.header_main_banner, null);
        main_banner=(ViewPager) header.findViewById(R.id.main_banner);
        gv_categories.addHeaderView(header);


        final float density = getResources().getDisplayMetrics().density;
    }
    public void setUpFreaturedProducts(View v)
    {
         LayoutInflater inflater1 = (LayoutInflater)getActivity(). getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         footer = inflater1.inflate(R.layout.footer_featured_products, null);
         featured_brand_slider=(ViewPager)footer.findViewById(R.id.featured_brand_slider);
         gv_categories.addFooterView(footer);
         rv_best_seller = (RecyclerView) footer.findViewById(R.id.rv_best_seller);
         GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
         gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
         rv_best_seller.setLayoutManager(gridLayoutManager);
         bestSellerPojoList=new ArrayList<BestSellerPojo>();
        rv_latest_men_products=(RecyclerView)footer.findViewById(R.id.rv_latest_men_products);
        rv_latest_women_products=(RecyclerView) footer.findViewById(R.id.rv_latest_women_products);


        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(),2);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_latest_men_products.setLayoutManager(gridLayoutManager1);
       /* DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_latest_men_products.getActivity(),
                gridLayoutManager1.getOrientation());
        rv_latest_men_products.addItemDecoration(dividerItemDecoration);*/
        rv_latest_men_products.setNestedScrollingEnabled(false);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(),2);
        gridLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        rv_latest_women_products.setLayoutManager(gridLayoutManager2);
        rv_latest_women_products.setNestedScrollingEnabled(false);
    }//setUpFeaturedProductsClose
   public void getCategories()
    {
        p_bar.setVisibility(View.VISIBLE);
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
                                    categoryListPojoList.add(new CategoryListPojo(cat_id,cat_name,image,""));
                                }
                                CategoryListAdapter categoryListAdapter=new CategoryListAdapter(getActivity(),categoryListPojoList);
                                gv_categories.setAdapter(categoryListAdapter);

                            }


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

    }//getCategories



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


                                for (int i=0;i<men_prducts.length();i++)
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
                                    String quantity_available=j1.getString("quantity_available");
                                    String category_id=j1.getString("category_id");
                                   // latestProductMenPojoList.add(new LatestProductMenPojo(product_id,product_name,product_price,product_image,product_discount,product_desc,wishlist_flag,quantity_available,category_id,product_special_price, rating, review_count));
                                }
                                JSONArray women_products =Jsonobj.getJSONArray("women_products");
                                for (int i=0;i<women_products.length();i++)
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
                                    String quantity_available=j1.getString("quantity_available");
                                    String category_id=j1.getString("category_id");
                                    //latestProductItemPojoList.add(new LatestProductItemPojo(product_id,product_name,product_price,product_image,product_discount,product_desc,wishlist_flag,quantity_available,category_id,product_special_price, rating, review_count));
                                }

                                adapter = new LatestProductAdapterWomen(latestProductItemPojoList, getActivity(),p_bar);
                                rv_latest_women_products.setAdapter(adapter);
                               /*LatestProductAdapterMen adapterMen = new LatestProductAdapterMen(latestProductMenPojoList, getActivity(),p_bar, HomeFragmentNew.this);
                                rv_latest_men_products.setAdapter(adapterMen);*/
                            }
                            p_bar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
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


    public void getFeaturedProducts()
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
                                  // featuredProductPojoList.add(new FeaturedProductPojo(product_name,product_id,product_price,product_image));
                               }

     /*  FeaturedBannerAdapter featuredBannerAdapter = new FeaturedBannerAdapter(getActivity(),featuredProductPojoList);
        featured_brand_slider.setAdapter(featuredBannerAdapter);*/
                            }


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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterNetworkChanges();
    }
}
