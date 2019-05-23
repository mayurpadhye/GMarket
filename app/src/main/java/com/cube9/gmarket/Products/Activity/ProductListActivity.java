package com.cube9.gmarket.Products.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Cart.Activity.MyCartActivity;
import com.cube9.gmarket.Category.ModelClass.CatProductsPojo;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.Products.Adapter.ProductListAdapter;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.MasterSearchActivity;
import com.cube9.gmarket.SearchProducts.SearchProductsActivity;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.nointernet.NoInternetActivity;
import com.cube9.gmarket.receivers.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListActivity extends AppCompatActivity {
//GridView gv_product_list;
ProgressBar p_bar;
LinearLayout ll_filter;
TextView tv_no_products_available;
    RecyclerView rv_products;
    String cat_id="",cat_name="";
    Toolbar toolbar;
    TextView tv_title,tv_cart_count;
    ImageView iv_back,iv_search,iv_cart;
    FloatingActionButton sort_by;
    BottomSheetDialog bottomSheetDialog;
    ProductListAdapter productListAdapter;
    String intent_from="";
    float min_price=0,max_price=0;
    LinearLayoutManager mLayoutManager;
    List<CatProductsPojo> catProductsPojoList;
    ImageView iv_view_by;
    ImageView iv_view_by_grid;
    RelativeLayout rl_filter,rl_sort_by;
    public  static  boolean isViewWithCatalog=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        initView();
        onClick();
        if (NetworkChangeReceiver.isOnline(ProductListActivity.this))
        {
            if (!intent_from.equals("product_filter"))
                GetProductList();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkChangeReceiver.isOnline(ProductListActivity.this))
        {
            //if (!intent_from.equals("product_filter"))
             //   GetProductList();
        }


        else{  startActivity(new Intent(ProductListActivity.this, NoInternetActivity.class));}

try {
    Intent intent=getIntent();
    String intent_from = intent.getStringExtra("intent_from");
    String product_id = intent.getStringExtra("product_id");
    if (intent_from.equals("wishlist"))
    {
        AddToWishList(product_id);
    }
}
catch (Exception e)
{
    e.printStackTrace();
}

if (new SharedPrefManager(ProductListActivity.this).IsLogin())
{
    tv_cart_count.setText(new SharedPrefManager(ProductListActivity.this).getCartCount());
}
else
{
    tv_cart_count.setVisibility(View.GONE);
}

    }

    public void initView()
    {
        rl_filter=(RelativeLayout)findViewById(R.id.rl_filter);
        rl_sort_by=(RelativeLayout)findViewById(R.id.rl_sort_by);
        iv_view_by=(ImageView) findViewById(R.id.iv_view_by_linear);
        iv_view_by_grid=(ImageView) findViewById(R.id.iv_view_by_grid);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
        tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        tv_no_products_available=(TextView) findViewById(R.id.tv_no_products_available);
        sort_by=(FloatingActionButton)findViewById(R.id.sort_by);
      //  gv_product_list=(GridView)findViewById(R.id.gv_product_list);
        rv_products=(RecyclerView) findViewById(R.id.rv_products);
        p_bar=(ProgressBar) findViewById(R.id.p_bar);
        ll_filter=(LinearLayout) findViewById(R.id.ll_filter);
        catProductsPojoList=new ArrayList<CatProductsPojo>();
        Intent i=getIntent();
        cat_id=i.getStringExtra("cat_id");
        cat_name=i.getStringExtra("cat_name");
        tv_title.setText(cat_name);
        bottomSheetDialog=new BottomSheetDialog(ProductListActivity.this);
        mLayoutManager = new LinearLayoutManager(this);
        rv_products.setLayoutManager(mLayoutManager);

        iv_view_by_grid.setVisibility(View.VISIBLE);
        iv_view_by.setVisibility(View.GONE);

    }//initViewClose

    public void onClick()
    {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new SharedPrefManager(ProductListActivity.this).IsLogin())
                {
                    Intent i = new Intent(ProductListActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from","product_list");
                    startActivityForResult(i, 1);
                }
                else
                {
                    startActivity(new Intent(ProductListActivity.this,MyCartActivity.class));
                }

            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, MasterSearchActivity.class));
            }
        });

        rl_sort_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSortingDialog();
            }
        });

        rl_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openFilterDialog();
            }
        });

        iv_view_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isViewWithCatalog=true;
                iv_view_by_grid.setVisibility(View.VISIBLE);
                iv_view_by.setVisibility(View.GONE);
                mLayoutManager = new LinearLayoutManager(ProductListActivity.this);
                rv_products.setLayoutManager(mLayoutManager);
                rv_products.setAdapter(productListAdapter);



            }
        });

        iv_view_by_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isViewWithCatalog=false;
                iv_view_by_grid.setVisibility(View.GONE);
                iv_view_by.setVisibility(View.VISIBLE);
                rv_products.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(ProductListActivity.this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                rv_products.setAdapter(productListAdapter);


            }
        });

    }//onClickClose

    public void OpenSortingDialog()
    {
        final Dialog sorting_dailog=new Dialog(ProductListActivity.this);
        sorting_dailog.setContentView(R.layout.dialog_sorting_option);
        RadioButton rb_sort_by_name=(RadioButton)sorting_dailog.findViewById(R.id.rb_sort_by_name);
        RadioButton rb_sort_by_price_low_to_high=(RadioButton)sorting_dailog.findViewById(R.id.rb_sort_by_price_low_to_high);
        RadioButton rb_sort_by_price_high_to_low=(RadioButton)sorting_dailog.findViewById(R.id.rb_sort_by_price_high_to_low);

        rb_sort_by_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortData(0);
                sorting_dailog.dismiss();

            }
        });
        rb_sort_by_price_low_to_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortData(1);
                sorting_dailog.dismiss();

            }
        });
        rb_sort_by_price_high_to_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortData(2);
                sorting_dailog.dismiss();

            }
        });
        sorting_dailog.show();



    }

    public void openFilterDialog()
    {
        double max,min;

        max=  getMax();
        min =getMin();

        Intent i=new Intent(ProductListActivity.this,ProductFilterActivity.class);
        i.putExtra("intent_from","product_filter");
        i.putExtra("min_price",min);
        i.putExtra("max_price",max);
        i.putExtra("filter_min_price",min_price);
        i.putExtra("filter_max_price",max_price);
        startActivityForResult(i,1);
    }

    public double getMax(){
        double max = Double.MIN_VALUE;


        for(int i=0; i<catProductsPojoList.size(); i++){
            if(Double.parseDouble(catProductsPojoList.get(i).getProduct_original_price()) > max){
                max = Double.parseDouble(catProductsPojoList.get(i).getProduct_original_price());
            }
        }
        return max;
    }

    public double getMin(){
        double min = Double.MAX_VALUE;
        for(int i=0; i<catProductsPojoList.size(); i++){
            if(Double.parseDouble(catProductsPojoList.get(i).getProduct_original_price()) < min){
                min = Double.parseDouble(catProductsPojoList.get(i).getProduct_original_price());
            }
        }
        return min;
    }


   private void sortData(int which)
    {

        switch (which) {

            case 0:  // Name
                Collections.sort(catProductsPojoList, new Comparator<CatProductsPojo>() {
                    @Override
                    public int compare(CatProductsPojo p1, CatProductsPojo p2) {

                        return p1.getProduct_name().toLowerCase().compareTo(p2.getProduct_name().toLowerCase());
                    }
                });
                if (isViewWithCatalog) {
                    iv_view_by_grid.setVisibility(View.VISIBLE);
                    iv_view_by.setVisibility(View.GONE);
                    mLayoutManager = new LinearLayoutManager(ProductListActivity.this);
                    rv_products.setLayoutManager(mLayoutManager);
                    productListAdapter = new ProductListAdapter(catProductsPojoList, ProductListActivity.this, p_bar);
                    rv_products.setAdapter(productListAdapter);
                }
                else if(!isViewWithCatalog)
                {
                    iv_view_by_grid.setVisibility(View.GONE);
                    iv_view_by.setVisibility(View.VISIBLE);
                    rv_products.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(ProductListActivity.this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    productListAdapter = new ProductListAdapter(catProductsPojoList, ProductListActivity.this, p_bar);
                    rv_products.setAdapter(productListAdapter);
                }
                break;
            case 1:  // low to high price
                Collections.sort(catProductsPojoList, new Comparator<CatProductsPojo>() {
                    @Override
                    public int compare(CatProductsPojo p1, CatProductsPojo p2) {

                        return (int) (Float.parseFloat(p1.getProduct_original_price())
                                - Float.parseFloat(p2.getProduct_original_price()));
                    }
                });
                if (isViewWithCatalog) {
                    iv_view_by_grid.setVisibility(View.VISIBLE);
                    iv_view_by.setVisibility(View.GONE);
                    mLayoutManager = new LinearLayoutManager(ProductListActivity.this);
                    rv_products.setLayoutManager(mLayoutManager);
                    productListAdapter = new ProductListAdapter(catProductsPojoList, ProductListActivity.this,p_bar);
                    rv_products.setAdapter(productListAdapter);
                }
                else if(!isViewWithCatalog)
                {
                    iv_view_by_grid.setVisibility(View.GONE);
                    iv_view_by.setVisibility(View.VISIBLE);
                    rv_products.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(ProductListActivity.this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    productListAdapter = new ProductListAdapter(catProductsPojoList, ProductListActivity.this,p_bar);
                    rv_products.setAdapter(productListAdapter);
                }


                break;

            case 2: // high to low price
                Collections.sort(catProductsPojoList, new Comparator<CatProductsPojo>() {
                    @Override
                    public int compare(CatProductsPojo p1, CatProductsPojo p2) {

                        return (int) (Float.parseFloat(p2.getProduct_original_price())
                                - Float.parseFloat(p1.getProduct_original_price()));
                    }
                });
                if (isViewWithCatalog) {
                    iv_view_by_grid.setVisibility(View.VISIBLE);
                    iv_view_by.setVisibility(View.GONE);
                    mLayoutManager = new LinearLayoutManager(ProductListActivity.this);
                    rv_products.setLayoutManager(mLayoutManager);
                    productListAdapter = new ProductListAdapter(catProductsPojoList, ProductListActivity.this, p_bar);
                    rv_products.setAdapter(productListAdapter);
                }
                else if (!isViewWithCatalog)
                {
                    iv_view_by_grid.setVisibility(View.GONE);
                    iv_view_by.setVisibility(View.VISIBLE);
                    rv_products.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(ProductListActivity.this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    productListAdapter = new ProductListAdapter(catProductsPojoList, ProductListActivity.this, p_bar);
                    rv_products.setAdapter(productListAdapter);
                }
                break;
        }

    }

    public void GetProductList()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PRODUCT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {
                            catProductsPojoList.clear();
                            Log.i("productttt",""+response.toString());
                            JSONObject Jsonobj = new JSONObject(response);

                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                String category_id=Jsonobj.getString("category_id");

                                JSONArray category_products=Jsonobj.getJSONArray("category_products");
                                for (int i=0;i<category_products.length();i++)
                                {
                                    JSONObject j1=category_products.getJSONObject(i);
                                    String product_id=j1.getString("product_id");
                                    String product_name=j1.getString("product_name");
                                    String product_price=j1.getString("product_price");
                                    String product_special_price=j1.getString("product_special_price");
                                    String product_image=j1.getString("product_image");
                                    String product_discount=j1.getString("product_discount");
                                    String product_desc=j1.getString("product_desc");
                                    String wishlist_flag=j1.getString("wishlist_flag");
                                    String is_in_stock=j1.getString("is_in_stock");
                                    catProductsPojoList.add(new CatProductsPojo(product_name,product_special_price,product_image,product_price,product_discount,product_id,wishlist_flag,is_in_stock,cat_id));
                                }
                                if (isViewWithCatalog)
                                {
                                    iv_view_by_grid.setVisibility(View.VISIBLE);
                                    iv_view_by.setVisibility(View.GONE);
                                    mLayoutManager = new LinearLayoutManager(ProductListActivity.this);
                                    rv_products.setLayoutManager(mLayoutManager);
                                    productListAdapter=new ProductListAdapter(catProductsPojoList,ProductListActivity.this,p_bar);
                                    rv_products.setAdapter(productListAdapter);
                                }
                                else if (!isViewWithCatalog)
                                {
                                    iv_view_by_grid.setVisibility(View.GONE);
                                    iv_view_by.setVisibility(View.VISIBLE);
                                    rv_products.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(ProductListActivity.this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                    productListAdapter=new ProductListAdapter(catProductsPojoList,ProductListActivity.this,p_bar);
                                    rv_products.setAdapter(productListAdapter);
                                }

                                if (category_products.length()>0)
                                {
                                    tv_no_products_available.setVisibility(View.GONE);
                                    ll_filter.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    tv_no_products_available.setVisibility(View.VISIBLE);
                                    ll_filter.setVisibility(View.GONE);
                                }

                           }
                           else
                            {
                                tv_no_products_available.setVisibility(View.VISIBLE);
                                ll_filter.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("jsonException",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);

                        Log.i("Errorrrrxxxx",""+error.toString());
                        // Toast.makeText(ProductListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(ProductListActivity.this).getLanguage());
                params.put("category_id",cat_id);
                params.put("user_id",new SharedPrefManager(ProductListActivity.this).GetCustomerId());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductListActivity.this).addToRequestQueue(stringRequest);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                 intent_from = data.getStringExtra("intent_from");
                String product_id = data.getStringExtra("product_id");
                if (intent_from.equals("wishlist"))
                {
                    AddToWishList(product_id);
                }
                else if (intent_from.equals("product_list"))
                {
                    startActivity(new Intent(ProductListActivity.this,MyCartActivity.class));
                }
                else if (intent_from.equals("product_filter"))
                {
                     min_price = data.getFloatExtra("min",0);
                      max_price = data.getFloatExtra("max",0);

                    ArrayList temp = new ArrayList<CatProductsPojo>();
                    for (int i= 0; i < catProductsPojoList.size(); i++) {

                        if (Float.parseFloat(catProductsPojoList.get(i).getProduct_original_price()) >= min_price && Float.parseFloat(catProductsPojoList.get(i).getProduct_original_price()) <=max_price) {
                            temp.add(catProductsPojoList.get(i));
                        }
                    }
                    if (isViewWithCatalog) {
                        iv_view_by_grid.setVisibility(View.VISIBLE);
                        iv_view_by.setVisibility(View.GONE);
                        mLayoutManager = new LinearLayoutManager(ProductListActivity.this);
                        rv_products.setLayoutManager(mLayoutManager);
                        productListAdapter = new ProductListAdapter(temp, ProductListActivity.this,p_bar);
                        rv_products.setAdapter(productListAdapter);
                    }
                    else
                    {
                        iv_view_by_grid.setVisibility(View.GONE);
                        iv_view_by.setVisibility(View.VISIBLE);
                        rv_products.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(ProductListActivity.this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                        productListAdapter=new ProductListAdapter(temp,ProductListActivity.this,p_bar);
                        rv_products.setAdapter(productListAdapter);
                    }

                }

            }
        }
    }

    public void AddToWishList(final String product_id)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_TO_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                CustomUtils.showToast(Jsonobj.getString("message"),ProductListActivity.this);
                                ProductListAdapter productListAdapter=new ProductListAdapter(catProductsPojoList,ProductListActivity.this,p_bar);
                                rv_products.setAdapter(productListAdapter);

                            }
                            else
                            {
                                CustomUtils.showToast(Jsonobj.getString("message"),ProductListActivity.this);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                    //    Toast.makeText(ProductListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(ProductListActivity.this).getLanguage());
                params.put("product_id",product_id);
                params.put("user_id",new SharedPrefManager(ProductListActivity.this).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(ProductListActivity.this).addToRequestQueue(stringRequest);
    }



}
