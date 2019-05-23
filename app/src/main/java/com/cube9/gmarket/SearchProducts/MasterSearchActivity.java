package com.cube9.gmarket.SearchProducts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.Adapters.SearchProductListAdapter;
import com.cube9.gmarket.SearchProducts.ModelClass.SearchProductModel;
import com.cube9.gmarket.WebUrls.WebUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterSearchActivity extends AppCompatActivity {
AutoCompleteTextView search;
RecyclerView rv_search_details;
ProgressBar p_bar;
    TextView tv_title, tv_cart_count,tv_no_matching_products;
    ImageView iv_back, iv_search, iv_cart;
List<SearchProductModel> searchProductModels=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_search);
        search=findViewById(R.id.search);
        p_bar=findViewById(R.id.p_bar);
        rv_search_details=findViewById(R.id.rv_search_details);
        tv_title=findViewById(R.id.tv_title);
        tv_cart_count=findViewById(R.id.tv_cart_count);
        tv_no_matching_products=findViewById(R.id.tv_no_matching_products);
        iv_back=findViewById(R.id.iv_back);
        iv_search=findViewById(R.id.iv_search);
        iv_cart=findViewById(R.id.iv_cart);
        iv_cart.setVisibility(View.GONE);
        tv_cart_count.setVisibility(View.GONE);
        iv_search.setVisibility(View.GONE);
        tv_title.setText(getResources().getString(R.string.search));
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rv_search_details.setLayoutManager(new LinearLayoutManager(this));
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length()>2)
                {
                    getSearchData(s.toString());
                }
                else
                {
                    tv_no_matching_products.setVisibility(View.VISIBLE);
                    searchProductModels.clear();
                    rv_search_details.setAdapter(null);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void getSearchData(final String keyword)
    {
        p_bar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.URL_SEARCH_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        p_bar.setVisibility(View.GONE);
                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            Log.i("SearchResult",""+Jsonobj.toString());
                            String status=Jsonobj.getString("status");

                            if (status.equals("1"))
                            {
                                searchProductModels.clear();
                                JSONArray productinfo=Jsonobj.getJSONArray("productinfo");
                                for (int i=0;i<productinfo.length();i++)
                                {
                                    JSONObject j1=productinfo.getJSONObject(i);
                                    String entity_id=j1.getString("entity_id");
                                    String type_id=j1.getString("type_id");
                                    String sku=j1.getString("sku");
                                    String created_at=j1.getString("created_at");
                                    String updated_at=j1.getString("updated_at");
                                    String price=j1.getString("price");
                                    String name=j1.getString("name");
                                    String category_id=j1.getString("category_id");
                                    searchProductModels.add(new SearchProductModel(entity_id,type_id,sku,created_at,updated_at,price,name,category_id));
                                }
                                if (productinfo.length()>0)
                                {
                                    tv_no_matching_products.setVisibility(View.GONE);
                                    SearchProductListAdapter    adapter = new SearchProductListAdapter(searchProductModels,MasterSearchActivity.this);
                                    rv_search_details.setAdapter(adapter);

                                }
                                else
                                {
                                    tv_no_matching_products.setVisibility(View.VISIBLE);
                                }
                               // tempItems = new ArrayList<SearchProductModel>(searchProductModelList);


                            }
                            else
                            {
                                //auto_search.setEnabled(true);
                            }


                        } catch (JSONException e) {
                            //auto_search.setEnabled(true);
                            e.printStackTrace();
                            Log.i("JSONNNNNNNNNN",""+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        p_bar.setVisibility(View.GONE);
                        //  Toast.makeText(SearchProductsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language",new SharedPrefManager(MasterSearchActivity.this).getLanguage());
                params.put("search",keyword);

                return params;
            }
        };

        GMarketApplication.getInstance(MasterSearchActivity.this).addToRequestQueue(stringRequest);
    }


}
