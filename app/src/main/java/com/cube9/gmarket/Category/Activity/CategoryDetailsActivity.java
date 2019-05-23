package com.cube9.gmarket.Category.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.cube9.gmarket.Cart.Adapter.MyCartAdapter;
import com.cube9.gmarket.Category.Adapter.CategoryProductsAdapter;
import com.cube9.gmarket.Category.Adapter.SubCategoryAdapter;
import com.cube9.gmarket.Category.ModelClass.CatProductsPojo;
import com.cube9.gmarket.Category.ModelClass.SubCategoryListPojo;
import com.cube9.gmarket.Delivery.ModelClass.CartProductPojo;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Adapter.ItemDecorationAlbumColumns;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.WebUrls.WebUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDetailsActivity extends AppCompatActivity {
//GridView gv_cat_products;
ProgressBar p_bar;
List<CatProductsPojo> catProductsPojoList;
Toolbar toolbar;
TextView tv_title;
ImageView iv_back,iv_search,iv_cart;
String cat_id="",cat_name="";

RecyclerView rv_categories;
List<SubCategoryListPojo> categoryListPojos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);
        initView();
        onClick();
        Intent i=getIntent();
        cat_id=i.getStringExtra("cat_id");
        categoryListPojos=new ArrayList<SubCategoryListPojo>();
        cat_name=i.getStringExtra("cat_name");
        tv_title.setText(cat_name);
        if (CustomUtils.isNetworkAvailable(CategoryDetailsActivity.this))
       // getCategoryDetails();
        //getSubCategories();
            getSubCat();
            else
            CustomUtils.showToast(getResources().getString(R.string.no_internet),CategoryDetailsActivity.this);
    }//onCreateClose

    /*private void getSubCategories() {

        if (jsonArray.length()>0)
        {
            for (int k=0;k<jsonArray.length();k++)
            {

                try {
                    JSONObject    j2 = jsonArray.getJSONObject(k);
                    String sub_cat_id=j2.getString("cat_id");
                    String sub_cat_name=j2.getString("cat_name");
                    String image=j2.getString("image");
                    categoryListPojos.add(new SubCategoryListPojo(sub_cat_id,sub_cat_name,image,""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            SubCategoryAdapter categoryListAdapter=new SubCategoryAdapter(CategoryDetailsActivity.this,categoryListPojos);
            rv_categories.setAdapter(categoryListAdapter);

        }
    }*/


    public void getSubCat()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.GET_SUBCATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status=Jsonobj.getString("status");
                            if (status.equals("1"))
                            {

                                JSONArray sub_cat=Jsonobj.getJSONArray("sub_cat");
                                int k=0;
                                for (int i=0;i<sub_cat.length();i++)
                                {
                                    k=k+1;
                                    JSONObject j1=sub_cat.getJSONObject(i);
                                    String cat_id=j1.getString("cat_id");
                                    String cat_name=j1.getString("cat_name");
                                    String image=j1.getString("image");

                                    categoryListPojos.add(new SubCategoryListPojo(cat_id,cat_name,image,""));
                                }

                                SubCategoryAdapter categoryListAdapter=new SubCategoryAdapter(CategoryDetailsActivity.this,categoryListPojos);
                                rv_categories.setAdapter(categoryListAdapter);


                            }
                            else
                            {

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
                        //    Toast.makeText(MyCartActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(CategoryDetailsActivity.this).getLanguage());
                params.put("user_id",new SharedPrefManager(CategoryDetailsActivity.this).GetCustomerId());
                params.put("cat_id",cat_id);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(CategoryDetailsActivity.this).addToRequestQueue(stringRequest);
    }

    public void initView()
    {
        rv_categories=findViewById(R.id.rv_categories);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(CategoryDetailsActivity.this,3);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_categories.setLayoutManager(gridLayoutManager1);

        rv_categories.addItemDecoration(new ItemDecorationAlbumColumns(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),
                getResources().getInteger(R.integer.photo_list_preview_columns)));
        Bundle b = getIntent().getExtras();


        toolbar=(Toolbar)findViewById(R.id.toolbar);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_back);
        iv_search=(ImageView)toolbar.findViewById(R.id.iv_search);
        iv_cart=(ImageView)toolbar.findViewById(R.id.iv_cart);
        tv_title=(TextView) toolbar.findViewById(R.id.tv_title);
       TextView tv_cart_count=(TextView) toolbar.findViewById(R.id.tv_cart_count);
        iv_cart.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);


       // gv_cat_products=(GridView)findViewById(R.id.gv_cat_products);
        p_bar=(ProgressBar)findViewById(R.id.p_bar);
        catProductsPojoList=new ArrayList<CatProductsPojo>();



    }//initViewClose
public  void onClick()
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
            if (!new SharedPrefManager(CategoryDetailsActivity.this).IsLogin())
            {
                Intent i = new Intent(CategoryDetailsActivity.this, LoginDetailsActivity.class);
                i.putExtra("intent_from","category_details");
                startActivityForResult(i, 1);
            }
            else
            {
                startActivity(new Intent(CategoryDetailsActivity.this,MyCartActivity.class));
            }
        }
    });

}

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String intent_from = data.getStringExtra("intent_from");

                    startActivity(new Intent(CategoryDetailsActivity.this,MyCartActivity.class));

            }
        }
    }
    public void getCategoryDetails()
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.PRODUCT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

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
                                CategoryProductsAdapter categoryProductsAdapter=new CategoryProductsAdapter(catProductsPojoList,CategoryDetailsActivity.this);
                                //gv_cat_products.setAdapter(categoryProductsAdapter);

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
                       // Toast.makeText(CategoryDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(CategoryDetailsActivity.this).getLanguage());
                params.put("category_id",cat_id);


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(CategoryDetailsActivity.this).addToRequestQueue(stringRequest);

    }//get


}
