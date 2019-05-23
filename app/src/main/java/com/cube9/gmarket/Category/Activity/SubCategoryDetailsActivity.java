package com.cube9.gmarket.Category.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
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
import com.cube9.gmarket.Cart.Activity.MyCartActivity;
import com.cube9.gmarket.Category.Adapter.SubCategoryExpandableAdapter;
import com.cube9.gmarket.Category.ModelClass.CatProductsPojo;
import com.cube9.gmarket.Category.ModelClass.ItemClickListener;
import com.cube9.gmarket.Category.ModelClass.ParentSubCatPojo;
import com.cube9.gmarket.Category.ModelClass.Section;
import com.cube9.gmarket.Category.ModelClass.SectionedExpandableLayoutHelper;
import com.cube9.gmarket.Category.ModelClass.SubCatPojo;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Adapter.ItemAdapter;
import com.cube9.gmarket.Home.Adapter.LatestProductAdapterWomen;
import com.cube9.gmarket.Home.Adapter.RecyclerViewClickListener;
import com.cube9.gmarket.Home.ModelClass.CategoryProductsPojo;
import com.cube9.gmarket.Home.ModelClass.ItemViewModel;
import com.cube9.gmarket.Home.ModelClass.LatestProductItemPojo;
import com.cube9.gmarket.Home.ModelClass.LatestProductMenPojo;
import com.cube9.gmarket.Home.ModelClass.NonScrollExpandableListView;
import com.cube9.gmarket.Home.ModelClass.productinfo;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.Products.Activity.ProductListActivity;
import com.cube9.gmarket.Products.Adapter.ProductListAdapter;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.MasterSearchActivity;
import com.cube9.gmarket.SearchProducts.SearchProductsActivity;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.network.Api;
import com.cube9.gmarket.network.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SubCategoryDetailsActivity extends AppCompatActivity  implements ItemClickListener, RecyclerViewClickListener {
    NonScrollExpandableListView ex_subcat;
    RecyclerView rv_products;
    TextView tv_title,tv_cart_count;
    SubCategoryExpandableAdapter expandableListAdapter;
    String cat_id,cat_name;
    ProgressBar p_bar;
    public  static  boolean isViewWithCatalog=false;
    String latest_cat_id_men="",latest_cat_name="",latest_cat_name_men="",latest_cat_id="";
    private ArrayList<ParentSubCatPojo> parentSubCatPojos = new ArrayList<ParentSubCatPojo>();
    private ArrayList<CatProductsPojo> catProductsPojoList = new ArrayList<>();
    ImageView iv_back,iv_search,iv_cart;
   LinearLayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_details);
        initView();

        getSubCat();

        ex_subcat.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {


                startActivity(new Intent(SubCategoryDetailsActivity.this,ProductListActivity.class).putExtra("cat_id",parentSubCatPojos.get(groupPosition).getSubCatList().get(childPosition).getCat_id()).putExtra("cat_name",parentSubCatPojos.get(groupPosition).getSubCatList().get(childPosition).getCat_name()));
                return false;
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new SharedPrefManager(SubCategoryDetailsActivity.this).IsLogin())
                {
                    Intent i = new Intent(SubCategoryDetailsActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from","product_list");
                    startActivityForResult(i, 1);
                }
                else
                {
                    startActivity(new Intent(SubCategoryDetailsActivity.this,MyCartActivity.class));
                }

            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubCategoryDetailsActivity.this, MasterSearchActivity.class));
            }
        });

    }

    public void initView()
    {
        ex_subcat=findViewById(R.id.ex_subcat);
        rv_products=findViewById(R.id.rv_products);
        tv_cart_count=findViewById(R.id.tv_cart_count);
        p_bar=findViewById(R.id.p_bar);
        tv_title=findViewById(R.id.tv_title);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        iv_search=(ImageView)findViewById(R.id.iv_search);
        iv_cart=(ImageView)findViewById(R.id.iv_cart);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        cat_name = getIntent().getStringExtra("cat_name");
        cat_id = getIntent().getStringExtra("cat_id");
        tv_title.setText(cat_name);
        iv_search.setVisibility(View.GONE);
        iv_cart.setVisibility(View.GONE);
        ex_subcat.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        tv_cart_count.setVisibility(View.GONE);
       /* GridLayoutManager gridLayoutManager4 = new GridLayoutManager(SubCategoryDetailsActivity.this, 2);
        gridLayoutManager4.setOrientation(LinearLayoutManager.VERTICAL);
        rv_products.setLayoutManager(gridLayoutManager4);*/

        GetProductList();


    }//initViewClose;

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
    public void getSubCat() {
        p_bar.setVisibility(View.VISIBLE);
        // List<AllProductsModel> movies = AllProductsModel.createMovies(adapter.getItemCount());
        // progressBar.setVisibility(View.GONE);
        RetrofitClient retrofitClient = new RetrofitClient();
        Api service = retrofitClient.getAPIClient(WebUrls.DOMAIN_URL);
        service.getSubCategory(cat_id,  new SharedPrefManager(SubCategoryDetailsActivity.this).getLanguage(),
                new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response result, retrofit.client.Response response) {
                        try {

                            String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jsonObject = new JSONObject(bodyString);
                            Log.i("JSONNNNNNNN", "" + jsonObject.toString());

                            String status=jsonObject.getString("status");
                            if (status.equals("1"))
                            {
                                p_bar.setVisibility(View.GONE);
                                JSONObject category=jsonObject.getJSONObject("category");
                                JSONArray category_array=category.getJSONArray("category");
                                for (int k=0;k<category_array.length();k++)
                                {
                                    ParentSubCatPojo parentSubCatPojo=new ParentSubCatPojo();
                                    JSONObject j1=category_array.getJSONObject(k);
                                    String cat_id=j1.getString("cat_id");
                                    String cat_name=j1.getString("cat_name");
                                    String image=j1.getString("image");
                                    JSONArray Sub_cats=j1.getJSONArray("Sub_cats");
                                    parentSubCatPojo.setCat_id(cat_id);
                                    parentSubCatPojo.setCat_name(cat_name);


                                    for (int j=0;j<Sub_cats.length();j++)
                                    {

                                        ArrayList<SubCatPojo> subCatPojoList = parentSubCatPojo.getSubCatList();
                                        JSONObject j2=Sub_cats.getJSONObject(j);
                                        String sub_cat_id=j2.getString("cat_id");
                                        String sub_cat_name=j2.getString("cat_name");
                                        String sub_image=j2.getString("image");
                                        subCatPojoList.add(new SubCatPojo(sub_cat_id,sub_cat_name,sub_image));
                                        parentSubCatPojo.SetSubCatList(subCatPojoList);


                                    }
                                    parentSubCatPojos.add(parentSubCatPojo);


                                }
                                expandableListAdapter=new SubCategoryExpandableAdapter(SubCategoryDetailsActivity.this,parentSubCatPojos);
                                ex_subcat.setAdapter(expandableListAdapter);
                            }
                            else
                            {
                                p_bar.setVisibility(View.GONE);
                            }


                        } catch (JSONException je) {
                            p_bar.setVisibility(View.GONE);
                            Log.i("JSONNNNNNNN", "" + je.toString());
                            je.printStackTrace();

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        p_bar.setVisibility(View.GONE);
                        Log.i("JSONNNNNNNN", "" + error.getMessage());
                        error.printStackTrace();
                    }
                });


   /* if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
    else isLastPage = true;*/
    }


    @Override
    public void itemClicked(CategoryProductsPojo item) {

    }

    @Override
    public void itemClicked(Section section) {

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {

    }

    public void GetProductList()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PRODUCT_LIST_NEW,
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

                                    mLayoutManager = new LinearLayoutManager(SubCategoryDetailsActivity.this);
                                    rv_products.setLayoutManager(mLayoutManager);
                                    ProductListAdapter productListAdapter=new ProductListAdapter(catProductsPojoList,SubCategoryDetailsActivity.this,p_bar);
                                    rv_products.setAdapter(productListAdapter);
                                }
                                else if (!isViewWithCatalog)
                                {
                                    rv_products.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(SubCategoryDetailsActivity.this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                    ProductListAdapter    productListAdapter=new ProductListAdapter(catProductsPojoList,SubCategoryDetailsActivity.this,p_bar);
                                    rv_products.setAdapter(productListAdapter);
                                }



                            }
                            else
                            {

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
                params.put("language",new SharedPrefManager(SubCategoryDetailsActivity.this).getLanguage());
                params.put("category_id",cat_id);
                params.put("user_id",new SharedPrefManager(SubCategoryDetailsActivity.this).GetCustomerId());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(SubCategoryDetailsActivity.this).addToRequestQueue(stringRequest);
    }
}
