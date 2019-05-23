package com.cube9.gmarket.Home.Fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Category.ModelClass.ItemClickListener;
import com.cube9.gmarket.Category.ModelClass.Section;
import com.cube9.gmarket.Category.ModelClass.SectionedExpandableLayoutHelper;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Adapter.AllTopOfferAdapter;
import com.cube9.gmarket.Home.Adapter.BannerAdapter;
import com.cube9.gmarket.Home.Adapter.CategoriesAdapter;
import com.cube9.gmarket.Home.Adapter.DealsAdapter;
import com.cube9.gmarket.Home.Adapter.FeaturedBannerAdapter;
import com.cube9.gmarket.Home.Adapter.ItemAdapter;
import com.cube9.gmarket.Home.Adapter.LatestProductAdapterMen;
import com.cube9.gmarket.Home.Adapter.RecyclerViewClickListener;
import com.cube9.gmarket.Home.ModelClass.AllProductsModel;
import com.cube9.gmarket.Home.ModelClass.AllTopOffers;
import com.cube9.gmarket.Home.ModelClass.CategoryListPojo;
import com.cube9.gmarket.Home.ModelClass.CategoryProductsPojo;
import com.cube9.gmarket.Home.ModelClass.DealsPjo;
import com.cube9.gmarket.Home.ModelClass.EndlessScrollListener;
import com.cube9.gmarket.Home.ModelClass.FeaturedProductPojo;
import com.cube9.gmarket.Home.ModelClass.GridSpacingItemDecoration;
import com.cube9.gmarket.Home.ModelClass.ItemViewModel;
import com.cube9.gmarket.Home.ModelClass.LatestProductMenPojo;
import com.cube9.gmarket.Home.ModelClass.productinfo;
import com.cube9.gmarket.Products.Activity.ProductDetailsNewActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.network.Api;
import com.cube9.gmarket.network.RetrofitClient;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewHomeFragment extends Fragment implements ItemClickListener, RecyclerViewClickListener {

    ViewPager main_banner;
    List<AllTopOffers> allTopOffersList = new ArrayList<>();
    View v;
    LinearLayout ll_main;
    List<AllProductsModel> movies = new ArrayList<AllProductsModel>();
    List<LatestProductMenPojo> latestProductMenPojoList = new ArrayList<LatestProductMenPojo>();
    String latest_cat_id_men = "", latest_cat_name = "", latest_cat_name_men = "", latest_cat_id = "";
    List<String> mainBannerList = new ArrayList<String>();
    List<CategoryListPojo> categoryListPojoList = new ArrayList<CategoryListPojo>();
    SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    int currentPage = 0;
    ItemAdapter adapter;
    Timer timer;
    ProgressBar p_bar;
    ViewPager vp_feateured;
    PageIndicatorView indicator;
    RecyclerView rv_deals;
    RecyclerView rv_category;
    RecyclerView rv_all_products;
    NestedScrollView sv_main;
    RecyclerView rv_top_offers;
    List<FeaturedProductPojo> featuredProductPojoList = new ArrayList<FeaturedProductPojo>();
    private EndlessScrollListener scrollListener;
    private static int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    RecyclerView rv_cat_wise_product;
    List<DealsPjo> dealPojo = new ArrayList<>();
    private String EVENT_DATE_TIME = "2019-12-31 10:30:00";
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    GridLayoutManager gridLayoutManager3;
    private Handler handler = new Handler();
    private Runnable runnable;
    SharedPrefManager sharedPrefManager;
    String language_code = "";
    TextView tv_timer;

    public NewHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_new_home, container, false);
        initView(v);
        getMainBanner();
        getFeaturedProducts();
        getCategories();
        GetTopOffers();
        getCatWiseProducts();
        gridLayoutManager3 = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager3.setOrientation(LinearLayoutManager.VERTICAL);
        rv_all_products.setLayoutManager(gridLayoutManager3);
        rv_all_products.setNestedScrollingEnabled(false);
        rv_all_products.setItemViewCacheSize(20);
        rv_all_products.setDrawingCacheEnabled(true);
        rv_all_products.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rv_cat_wise_product.setItemViewCacheSize(20);
        rv_cat_wise_product.setDrawingCacheEnabled(true);
        rv_cat_wise_product.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rv_top_offers.setItemViewCacheSize(20);
        rv_top_offers.setDrawingCacheEnabled(true);
        rv_top_offers.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rv_category.setItemViewCacheSize(20);
        rv_category.setDrawingCacheEnabled(true);
        rv_category.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rv_deals.setItemViewCacheSize(20);
        rv_deals.setDrawingCacheEnabled(true);
        rv_deals.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        ItemViewModel itemViewModel = ViewModelProviders.of(getActivity()).get(ItemViewModel.class);
        adapter = new ItemAdapter(getActivity(), this);
        itemViewModel.itemPagedList.observe(this, new Observer<PagedList<productinfo>>() {
            @Override
            public void onChanged(@Nullable PagedList<productinfo> items) {


                adapter.submitList(items);
            }
        });
        rv_all_products.setAdapter(adapter);
        return v;
    }


    public void initView(View v) {
        sharedPrefManager = new SharedPrefManager(getActivity());
        rv_top_offers = v.findViewById(R.id.rv_top_offers);
        rv_cat_wise_product = v.findViewById(R.id.rv_cat_wise_product);
        rv_cat_wise_product.setNestedScrollingEnabled(false);
        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(getActivity(),
                rv_cat_wise_product, this, 2);
        tv_timer = v.findViewById(R.id.tv_timer);
        main_banner = v.findViewById(R.id.main_banner);
        sv_main = v.findViewById(R.id.sv_main);
        ll_main = v.findViewById(R.id.ll_main);
        vp_feateured = v.findViewById(R.id.vp_feateured);
        indicator = v.findViewById(R.id.indicator);
        rv_deals = v.findViewById(R.id.rv_deals);
        rv_category = v.findViewById(R.id.rv_category);
        rv_all_products = v.findViewById(R.id.rv_all_products);
        p_bar = v.findViewById(R.id.p_bar);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        rv_deals.setLayoutManager(gridLayoutManager2);


        GridLayoutManager gridLayoutManager4 = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager4.setOrientation(LinearLayoutManager.VERTICAL);
        rv_top_offers.setLayoutManager(gridLayoutManager4);


        rv_deals.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(rv_deals, false);


        int spanCount = 4; // 3 columns
        int spacing = 0; // 50px
        boolean includeEdge = true;
        rv_category.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        rv_category.setLayoutManager(new GridLayoutManager(getActivity(), 4));
    }//initViewClose

    public void getCatWiseProducts() {

        p_bar.setVisibility(View.VISIBLE);
        // List<AllProductsModel> movies = AllProductsModel.createMovies(adapter.getItemCount());
        // progressBar.setVisibility(View.GONE);
        RetrofitClient retrofitClient = new RetrofitClient();
        Api service = retrofitClient.getAPIClient(WebUrls.DOMAIN_URL);
        service.getCatWiseProducts(new SharedPrefManager(getActivity()).getLanguage(),
                new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response result, retrofit.client.Response response) {
                        try {

                            String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jsonObject = new JSONObject(bodyString);
                            Log.i("JSONNNNNNNN", "" + jsonObject.toString());


                            JSONArray prducts = jsonObject.getJSONArray("prducts");
                            for (int k = 0; k < prducts.length(); k++) {

                                JSONObject j1 = prducts.getJSONObject(k);
                                String cat_id = j1.getString("catid");
                                String cat_name = j1.getString("catname");
                                String cat_image="";
                           //     String cat_image = j1.getString("cat_image");

                                JSONArray product_details = j1.getJSONArray("product_details");
                                ArrayList<CategoryProductsPojo> listsubcat = new ArrayList<CategoryProductsPojo>();
                                for (int j = 0; j < product_details.length(); j++) {


                                    JSONObject j2 = product_details.getJSONObject(j);
                                    String product_id = j2.getString("product_id");
                                    String product_name = j2.getString("product_name");
                                    String product_price = j2.getString("product_price");
                                    String product_special_price = j2.getString("product_special_price");
                                    String product_image = j2.getString("product_image");
                                    String product_discount = j2.getString("product_discount");
                                    String product_desc = j2.getString("product_desc");
                                    String wishlist_flag = j2.getString("wishlist_flag");
                                    String review_count = j2.getString("review_count");
                                    String rating = j2.getString("rating");
                                    String category_id = j2.getString("category_id");
                                    listsubcat.add(new CategoryProductsPojo(product_id, product_name, product_price, product_image, product_discount, product_desc, wishlist_flag, category_id, product_special_price));

                                }
                                sectionedExpandableLayoutHelper.addSection(cat_name, cat_image, listsubcat);


                            }
                            sectionedExpandableLayoutHelper.notifyDataSetChanged();
                            p_bar.setVisibility(View.GONE);
                            ll_main.setVisibility(View.VISIBLE);


                        } catch (JSONException je) {

                            Log.i("JSONNNNNNNN", "" + je.toString());
                            je.printStackTrace();

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Log.i("JSONNNNNNNN", "" + error.getMessage());
                        error.printStackTrace();
                    }
                });


   /* if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
    else isLastPage = true;*/
    }

    public void getCategories() {
        // p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.CATEGORIES_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  p_bar.setVisibility(View.GONE);
                        try {

                            //converting response to json object
                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                categoryListPojoList.clear();
                                JSONObject category = Jsonobj.getJSONObject("category");
                                JSONArray category_array = category.getJSONArray("category");
                                for (int i = 0; i < category_array.length(); i++) {
                                    JSONObject j1 = category_array.getJSONObject(i);
                                    String cat_id = j1.getString("cat_id");
                                    String cat_name = j1.getString("cat_name");
                                    String image = j1.getString("image");
                                    String Sub_cats = j1.getString("Sub_cats");
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

                                    categoryListPojoList.add(new CategoryListPojo(cat_id, cat_name, image, "", 0, Sub_cats));
                                }
                                CategoriesAdapter categoryListAdapter = new CategoriesAdapter(getActivity(), categoryListPojoList, NewHomeFragment.this);
                                rv_category.setAdapter(categoryListAdapter);

                            }


                        } catch (JSONException e) {
                            Log.i("jsnnnnn", "" + e.toString());
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
                params.put("language", new SharedPrefManager(getActivity()).getLanguage());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }//getCategories


    public void getMainBanner() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.HOME_PAGE_BANNER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //     p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                mainBannerList.clear();
                                String cart_counter = Jsonobj.getString("cart_counter");
                                new SharedPrefManager(getActivity()).setCartCount(cart_counter);
                                // tv_cart_count.setText(cart_counter);
                                JSONObject bannerdata = Jsonobj.getJSONObject("bannerdata");
                                String banner1 = bannerdata.getString("banner1");
                                String banner2 = bannerdata.getString("banner2");
                                String banner3 = bannerdata.getString("banner3");
                                mainBannerList.add(banner1);
                                mainBannerList.add(banner2);
                                mainBannerList.add(banner3);
                                try {
                                    BannerAdapter bannerAdapter = new BannerAdapter(getActivity(), mainBannerList);
                                    main_banner.setAdapter(bannerAdapter);
                                    // indicator.setViewPager(main_banner);
                                } catch (NullPointerException e) {
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
                                timer.schedule(new TimerTask() { // task to be scheduled

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
                params.put("language", new SharedPrefManager(getActivity()).getLanguage());
                params.put("user_id", new SharedPrefManager(getActivity()).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }//getMainBanner

    public void getFeaturedProducts() {
        // p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_FEATURED_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //     p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                featuredProductPojoList.clear();
                                JSONArray productinfo = Jsonobj.getJSONArray("productinfo");
                                for (int i = 0; i < productinfo.length(); i++) {
                                    JSONObject j1 = productinfo.getJSONObject(i);
                                    String product_name = j1.getString("product_name");
                                    String product_id = j1.getString("product_id");
                                    String product_price = j1.getString("product_price");
                                    String product_image = j1.getString("product_image");
                                    featuredProductPojoList.add(new FeaturedProductPojo(product_name, product_id, product_price, product_image));
                                }

                                FeaturedBannerAdapter featuredBannerAdapter = new FeaturedBannerAdapter(getActivity(), featuredProductPojoList);
                                vp_feateured.setAdapter(featuredBannerAdapter);
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
                params.put("language", new SharedPrefManager(getActivity()).getLanguage());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        GMarketApplication.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    public void GetTopOffers() {

        RetrofitClient retrofitClient = new RetrofitClient();
        Api service = retrofitClient.getAPIClient(WebUrls.DOMAIN_URL);
        service.getTopOffers(new SharedPrefManager(getActivity()).getLanguage(), "",
                new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response result, retrofit.client.Response response) {
                        try {

                            Log.i("errrrrrrrrrrrr", String.valueOf(response.getBody()));
                            String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jsonObject = new JSONObject(bodyString);

                            String cart_counter = jsonObject.getString("cart_counter");
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            JSONArray topofferres = jsonObject.getJSONArray("topofferres");
                            for (int i = 0; i < topofferres.length(); i++) {
                                JSONObject j1 = topofferres.getJSONObject(i);
                                String product_id = j1.getString("product_id");
                                String product_type = j1.getString("product_type");
                                String product_name = j1.getString("product_name");
                                String product_price = j1.getString("product_price");
                                String product_special_price = j1.getString("product_special_price");
                                String product_image = j1.getString("product_image");
                                String product_discount = j1.getString("product_discount");
                                String product_desc = j1.getString("product_desc");
                                String wishlist_flag = j1.getString("wishlist_flag");
                                String review_count = j1.getString("review_count");
                                String rating = j1.getString("rating");
                                String delivery_time = j1.getString("delivery_time");
                                String quantity_available = j1.getString("quantity_available");
                                String is_in_stock = j1.getString("is_in_stock");


                                allTopOffersList.add(new AllTopOffers(product_id, product_type, product_name, product_price, product_special_price, product_image,
                                        product_discount, product_desc, wishlist_flag, review_count, rating, delivery_time, quantity_available, is_in_stock));
                            }
                            AllTopOfferAdapter adapter = new AllTopOfferAdapter(allTopOffersList, getActivity(), p_bar, NewHomeFragment.this);
                            rv_top_offers.setAdapter(adapter);
                            JSONArray dealoftheday = jsonObject.getJSONArray("dealoftheday");
                            for (int k = 0; k < dealoftheday.length(); k++) {
                                JSONObject j1 = dealoftheday.getJSONObject(k);
                                String product_id = j1.getString("product_id");
                                String product_type = j1.getString("product_type");
                                String product_name = j1.getString("product_name");
                                String product_price = j1.getString("product_price");
                                String product_special_price = j1.getString("product_special_price");
                                String product_image = j1.getString("product_image");
                                String product_discount = j1.getString("product_discount");
                                String product_desc = j1.getString("product_desc");
                                String wishlist_flag = j1.getString("wishlist_flag");
                                String review_count = j1.getString("review_count");
                                String rating = j1.getString("rating");
                                String delivery_time = j1.getString("delivery_time");
                                String quantity_available = j1.getString("quantity_available");
                                String is_in_stock = j1.getString("is_in_stock");
                                latest_cat_id_men = "";
                                latest_cat_name_men = "";
                                dealPojo.add(new DealsPjo(product_id, product_name, product_price, product_image, product_discount, product_desc, wishlist_flag, quantity_available, "", product_special_price, rating, review_count));
                            }
                            DealsAdapter adapterMen = new DealsAdapter(dealPojo, getActivity(), p_bar, NewHomeFragment.this);
                            rv_deals.setAdapter(adapterMen);
                        } catch (JSONException je) {

                            Log.i("JSONNNNNNNN", "" + je.toString());
                            je.printStackTrace();

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Log.i("JSONNNNNNNN", "" + error.getMessage());
                        error.printStackTrace();
                    }
                });


    }


    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }


    @Override
    public void itemClicked(CategoryProductsPojo item) {

    }

    @Override
    public void itemClicked(Section section) {

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        /*Intent i=new Intent(getActivity(), ProductDetailsNewActivity.class);
        i.putExtra("product_id);
        i.putExtra("product_name");
        i.putExtra("category_id","");
        mCtx.startActivity(i);*/
    }
}
