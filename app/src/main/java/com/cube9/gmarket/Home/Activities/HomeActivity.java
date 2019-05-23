package com.cube9.gmarket.Home.Activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cube9.gmarket.Account.Activity.MyAccountActivity;
import com.cube9.gmarket.Account.ChangeLanguageActivity;
import com.cube9.gmarket.Cart.Activity.MyCartActivity;
import com.cube9.gmarket.Category.Activity.CategoryDetailsActivity;
import com.cube9.gmarket.Category.Activity.SubCategoryDetailsActivity;
import com.cube9.gmarket.Category.Fragment.SubCategoryFragment;
import com.cube9.gmarket.ContactUs.ContactUsActivity;
import com.cube9.gmarket.HelperClass.CustomUtils;
import com.cube9.gmarket.HelperClass.GMarketApplication;
import com.cube9.gmarket.HelperClass.SharedPrefManager;
import com.cube9.gmarket.Home.Adapter.DrawerListAdapter;
import com.cube9.gmarket.Home.Adapter.ExpandableDrawerAdapter;
import com.cube9.gmarket.Home.Fragments.HomeFragmentNew;
import com.cube9.gmarket.Home.Fragments.NewHomeFragment;
import com.cube9.gmarket.Home.ModelClass.ChildDrawerPojo;
import com.cube9.gmarket.Home.ModelClass.ParentDrawerPojo;
import com.cube9.gmarket.Login.Activity.LoginDetailsActivity;
import com.cube9.gmarket.Orders.Activities.MyOrdersActivity;
import com.cube9.gmarket.Products.Activity.ProductListActivity;
import com.cube9.gmarket.R;
import com.cube9.gmarket.SearchProducts.MasterSearchActivity;
import com.cube9.gmarket.SearchProducts.SearchProductsActivity;
import com.cube9.gmarket.WebUrls.WebUrls;
import com.cube9.gmarket.WishList.Activity.WishListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    public static HomeActivity homeActivityInstance;
    Toolbar toolbar;
    RelativeLayout rl_drawer;
    ArrayList<String> navigation_items;
    private DrawerListAdapter drawerListAdapter;
    private ExpandableListView lv_drawer;
    ImageView iv_menu, iv_search, iv_cart;
    TextView tv_cart_count;
    DrawerLayout drawer;
    SharedPrefManager sharedPrefManager;
    String languageToLoad = "",language_code="";
    //RelativeLayout tool;
    CardView cv_search;
    //----------******************--------------------
    //drawerFooter
    TextView tv_cart, tv_orders, tv_change_language, tv_my_account, tv_faq, tv_contact_us, tv_wishlist;
    Button btn_logout;

    //------------------*********************---------------


    private LinkedHashMap<String, ParentDrawerPojo> subjects = new LinkedHashMap<String, ParentDrawerPojo>();
    private ArrayList<ParentDrawerPojo> parentDrawerPojosList = new ArrayList<ParentDrawerPojo>();
    ActionBarDrawerToggle toggle;

    View footer;

    public static HomeActivity getInstance() {
        return homeActivityInstance;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPrefManager.IsLogin()) {
            btn_logout.setText(getResources().getString(R.string.logout));
        } else {
            btn_logout.setText(getResources().getString(R.string.login));
        }

        if (sharedPrefManager.IsLogin()) {
            tv_cart_count.setText(new SharedPrefManager(HomeActivity.this).getCartCount());
        } else {
            tv_cart_count.setVisibility(View.GONE);
        }
        String lang=sharedPrefManager.getAppLanguageCode();

        if(!language_code.equals(sharedPrefManager.getAppLanguageCode()))
        {
            language_code = sharedPrefManager.getAppLanguageCode();
            finish();
            startActivity(getIntent());
        }

        if (lang.length()==0)
        {
            String languageToLoad  = CustomUtils.FRENCH_CODE; // your language
            sharedPrefManager.setLanguage(CustomUtils.FRENCH);
            sharedPrefManager.setAppLanguageCode(CustomUtils.FRENCH_CODE);
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            finish();
            startActivity(getIntent());

        }

        if (CustomUtils.isNetworkAvailable(HomeActivity.this)) {
            getCategories();
        }



    }
    public void loadAppInLanguage() {




    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
       if (new SharedPrefManager(HomeActivity.this).IsIconCreated())
        {
            new SharedPrefManager(HomeActivity.this).setIsIconCreated(true);
        }
        else
        {
            addShortcut();
        }


        init();
        if (new SharedPrefManager(HomeActivity.this).getLanguage().length()==0)
        {
            String languageToLoad  = CustomUtils.FRENCH_CODE; // your language
            sharedPrefManager.setLanguage(CustomUtils.FRENCH);
            sharedPrefManager.setAppLanguageCode(CustomUtils.FRENCH_CODE);
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            finish();
            startActivity(getIntent());

        }
        else if (new SharedPrefManager(HomeActivity.this).getLanguage().equals("2"))
        {
            String languageToLoad  = CustomUtils.FRENCH_CODE; // your language
            sharedPrefManager.setLanguage(CustomUtils.FRENCH);
            sharedPrefManager.setAppLanguageCode(CustomUtils.FRENCH_CODE);
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

        }



        initDrawerFooter();
        SetDrawer();

        onClick();

        cv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MasterSearchActivity.class));
            }
        });

        ViewTreeObserver vto = lv_drawer.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lv_drawer.setIndicatorBounds(lv_drawer.getRight() - 60, lv_drawer.getWidth());
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new NewHomeFragment()).commit();
    }

    private void init() {


        sharedPrefManager = new SharedPrefManager(HomeActivity.this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        cv_search =  findViewById(R.id.cv_search);
        rl_drawer = (RelativeLayout) findViewById(R.id.rl_drawer);
        setSupportActionBar(toolbar);
        iv_menu = (ImageView) toolbar.findViewById(R.id.iv_menu);
        tv_cart_count = (TextView) toolbar.findViewById(R.id.tv_cart_count);
        iv_cart = (ImageView) toolbar.findViewById(R.id.iv_cart);
        iv_search = (ImageView) toolbar.findViewById(R.id.iv_search);
        navigation_items = new ArrayList<>();
        navigation_items.add("Call");
        navigation_items.add("Favorite");
        navigation_items.add("Search");
        lv_drawer = (ExpandableListView) findViewById(R.id.lv_drawer);
        language_code = sharedPrefManager.getAppLanguageCode();


    }//initClose
    private void addShortcut() {
        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getApplicationContext(),
                HomeActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);


    }
    public void initDrawerFooter() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = inflater.inflate(R.layout.drawer_footer, null);
        tv_cart = (TextView) footer.findViewById(R.id.tv_cart);
        tv_wishlist = (TextView) footer.findViewById(R.id.tv_wishlist);
        tv_orders = (TextView) footer.findViewById(R.id.tv_orders);
        tv_change_language = (TextView) footer.findViewById(R.id.tv_change_language);
        tv_contact_us = (TextView) footer.findViewById(R.id.tv_contact_us);
        tv_faq = (TextView) footer.findViewById(R.id.tv_faq);
        tv_my_account = (TextView) footer.findViewById(R.id.tv_my_account);
        btn_logout = (Button) footer.findViewById(R.id.btn_logout);
        lv_drawer.addFooterView(footer);


    }//initDrawerFooterClose

    private void SetDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(HomeActivity.this, drawer,
                // Navigation menu toggle icon
                R.string.navigation_drawer_open, // Navigation drawer open description
                R.string.navigation_drawer_close // Navigation drawer close description
        );
        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        lv_drawer.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                ParentDrawerPojo headerInfo = parentDrawerPojosList.get(groupPosition);

                ChildDrawerPojo detailInfo = headerInfo.getSubCatList().get(childPosition);
                //display it or do something with it
               /* Toast.makeText(getBaseContext(), " Clicked on :: " + headerInfo.getCat_id()
                        + "/" + detailInfo.getSub_cat_id(), Toast.LENGTH_LONG).show();*/

                Intent i = new Intent(HomeActivity.this, ProductListActivity.class);
                i.putExtra("cat_id", detailInfo.getSub_cat_id());
                i.putExtra("cat_name",detailInfo.getSub_cat_name());
                closeDrawer();
                startActivity(i);
                return false;
            }
        });
        // setOnGroupClickListener listener for group heading click
        lv_drawer.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //get the group header
                ParentDrawerPojo headerInfo = parentDrawerPojosList.get(groupPosition);
                //display it or do something with it

                if (parentDrawerPojosList.get(groupPosition).getSub_cat().equals("1")) {
                    Bundle b=new Bundle();
                    b.putString("cat_id",parentDrawerPojosList.get(groupPosition).getCat_id());
                    b.putString("cat_name",parentDrawerPojosList.get(groupPosition).getCat_name());


                   Intent i = new Intent(HomeActivity.this, SubCategoryDetailsActivity.class);
                    i.putExtra("cat_id", parentDrawerPojosList.get(groupPosition).getCat_id());
                    i.putExtra("cat_name", parentDrawerPojosList.get(groupPosition).getCat_name());
                    closeDrawer();
                    startActivity(i);
                    closeDrawer();

                } else {
                    Intent i = new Intent(HomeActivity.this, ProductListActivity.class);
                    i.putExtra("cat_id", parentDrawerPojosList.get(groupPosition).getCat_id());
                    i.putExtra("cat_name", parentDrawerPojosList.get(groupPosition).getCat_name());
                    closeDrawer();
                    startActivity(i);
                }

                return false;
            }
        });
    }//setDrawerClose

    public void closeDrawer() {
        drawer.closeDrawer(Gravity.LEFT);
    }

    public void onClick() {
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(rl_drawer);
            }
        });
        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    closeDrawer();
                }
                if (!new SharedPrefManager(HomeActivity.this).IsLogin()) {
                    Intent i = new Intent(HomeActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "home");
                    startActivityForResult(i, 1);
                } else {
                    startActivity(new Intent(HomeActivity.this, MyCartActivity.class));
                }

            }
        });

        tv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    closeDrawer();
                }
                if (!new SharedPrefManager(HomeActivity.this).IsLogin()) {
                    Intent i = new Intent(HomeActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "home");
                    startActivityForResult(i, 1);

                } else {
                    startActivity(new Intent(HomeActivity.this, MyCartActivity.class));

                }
            }
        });

        tv_change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ChangeLanguageActivity.class));
                closeDrawer();
            }
        });

        tv_my_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new SharedPrefManager(HomeActivity.this).IsLogin()) {
                    Intent i = new Intent(HomeActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "my_account");
                    startActivityForResult(i, 1);
                    closeDrawer();
                } else {
                    startActivity(new Intent(HomeActivity.this, MyAccountActivity.class));
                    closeDrawer();
                }

            }
        });

        tv_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new SharedPrefManager(HomeActivity.this).IsLogin()) {
                    Intent i = new Intent(HomeActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "wishlist_home");
                    startActivityForResult(i, 1);
                    closeDrawer();
                } else {
                    startActivity(new Intent(HomeActivity.this, WishListActivity.class));
                    closeDrawer();
                }
            }
        });
        tv_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new SharedPrefManager(HomeActivity.this).IsLogin()) {
                    Intent i = new Intent(HomeActivity.this, LoginDetailsActivity.class);
                    i.putExtra("intent_from", "order_home");
                    startActivityForResult(i, 1);
                    closeDrawer();
                } else {
                    startActivity(new Intent(HomeActivity.this, MyOrdersActivity.class));
                    closeDrawer();
                }
            }
        });


        tv_contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ContactUsActivity.class));
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new SharedPrefManager(HomeActivity.this).IsLogin()) {
                    sharedPrefManager.Logout();
                    startActivity(new Intent(HomeActivity.this, LoginDetailsActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginDetailsActivity.class));
                    finish();
                    closeDrawer();
                }

            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MasterSearchActivity.class));
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String intent_from = data.getStringExtra("intent_from");
                String product_id = data.getStringExtra("product_id");
                if (intent_from.equals("wishlist")) {
                    AddToWishList(product_id);
                } else if (intent_from.equals("home")) {
                    startActivity(new Intent(HomeActivity.this, MyCartActivity.class));
                } else if (intent_from.equals("my_account")) {
                    startActivity(new Intent(HomeActivity.this, MyAccountActivity.class));

                } else if (intent_from.equals("wishlist_home")) {
                    startActivity(new Intent(HomeActivity.this, WishListActivity.class));
                } else if (intent_from.equals("order_home")) {
                    startActivity(new Intent(HomeActivity.this, MyOrdersActivity.class));
                }

            }
        }
    }

    public void AddToWishList(final String product_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.ADD_TO_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                CustomUtils.showToast(Jsonobj.getString("message"), HomeActivity.this);


                            } else {
                                CustomUtils.showToast(Jsonobj.getString("message"), HomeActivity.this);

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
                        // Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(HomeActivity.this).getLanguage());
                params.put("product_id", product_id);
                params.put("user_id", new SharedPrefManager(HomeActivity.this).GetCustomerId());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);
    }

    public void getCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebUrls.CATEGORIES_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            JSONObject Jsonobj = new JSONObject(response);
                            String status = Jsonobj.getString("status");

                            if (status.equals("1")) {
                                JSONObject category = Jsonobj.getJSONObject("category");
                                JSONArray category_array = category.getJSONArray("category");
                                parentDrawerPojosList.clear();
                                for (int i = 0; i < category_array.length(); i++) {
                                    ParentDrawerPojo parentDrawerPojo = new ParentDrawerPojo();
                                    JSONObject j1 = category_array.getJSONObject(i);
                                    String cat_id = j1.getString("cat_id");
                                    String Sub_cats=j1.getString("Sub_cats");
                                    parentDrawerPojo.setCat_id(cat_id);
                                    String cat_name = j1.getString("cat_name");
                                    parentDrawerPojo.setCat_name(cat_name);
                                    parentDrawerPojo.setSub_cat(Sub_cats);

                                   /* JSONArray Sub_cats = j1.getJSONArray("Sub_cats");
                                    if (Sub_cats.length() > 0) {
                                        for (int j = 0; j < Sub_cats.length(); j++) {
                                            JSONObject jsonObject = Sub_cats.getJSONObject(j);
                                            String sub_cat_id = jsonObject.getString("cat_id");
                                            String sub_cat_name = jsonObject.getString("cat_name");
                                            ChildDrawerPojo detailInfo = new ChildDrawerPojo();
                                            ArrayList<ChildDrawerPojo> productList = parentDrawerPojo.getSubCatList();
                                            detailInfo.setSub_cat_id(sub_cat_id);
                                            detailInfo.setSub_cat_name(sub_cat_name);
                                            productList.add(detailInfo);
                                            parentDrawerPojo.SetSubCatList(productList);
                                        }

                                    }*/

                                    parentDrawerPojosList.add(parentDrawerPojo);
                                }
                                ExpandableDrawerAdapter expandableDrawerAdapter = new ExpandableDrawerAdapter(HomeActivity.this, parentDrawerPojosList);
                                lv_drawer.setAdapter(expandableDrawerAdapter);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("language", new SharedPrefManager(HomeActivity.this).getLanguage());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GMarketApplication.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);

    }
    boolean doubleBackToExitPressedOnce = false;
 @Override
    public void onBackPressed() {

     super.onBackPressed();

     Intent a = new Intent(Intent.ACTION_MAIN);
     a.addCategory(Intent.CATEGORY_HOME);
     a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     startActivity(a);
    }


}
