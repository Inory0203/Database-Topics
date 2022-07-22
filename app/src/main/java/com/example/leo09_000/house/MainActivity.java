package com.example.leo09_000.house;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button show,show2,collection_bt;
    AutoCompleteTextView autoinfo,autoinfo2;
    EditText road ;
    String showUrl = "http://140.136.148.228/get_user_input.php";
    String town,country,more_information,country_city;
    ArrayList<String> addresslist = new ArrayList<String>();
    RequestQueue requestqueue;
    private MyDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        show = (Button) findViewById(R.id.button2);
        show2= (Button) findViewById(R.id.button);
        road = findViewById(R.id.road);
        collection_bt = (Button) findViewById(R.id.collection_bt);

        autoinfo = (AutoCompleteTextView) findViewById(R.id.autotext_country);
        autoinfo.addTextChangedListener(textWatcher);
        autoinfo2 = (AutoCompleteTextView) findViewById(R.id.autotext_city);
        helper = new MyDBHelper(this,"addressdata.db",null,1);
        //get country
        setupAdapter();
        //get city / town / area
        setupAdapter2();


        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestqueue = Volley.newRequestQueue(getApplicationContext());
                town = autoinfo2.getText().toString();
                country = autoinfo.getText().toString();
                more_information = road.getText().toString();
                country_city = country + town + more_information;
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("fn", country_city);

                CustomRequest request = new CustomRequest(Request.Method.POST, showUrl, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray students = response.getJSONArray("search_results");
                            for (int i = 0; i < students.length(); i++) {
                                JSONObject student = students.getJSONObject(i);

                                String address = student.getString("土地區段位置或建物區門牌");
                                addresslist.add(address);
                            }

                            Intent intent=new Intent();
                            intent.putExtra("address_list",addresslist);
                            intent.setClass(MainActivity.this,MapsActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestqueue.add(request);
            }
        });

        show2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                town = autoinfo2.getText().toString();
                country = autoinfo.getText().toString();
                more_information = road.getText().toString();
                Intent intent=new Intent();

                intent.putExtra("country",country);
                intent.putExtra("town",town);
                intent.putExtra("road",more_information);
                intent.setClass(MainActivity.this,Main5Activity.class);
                startActivity(intent);
            }
        });

        collection_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,Collection_ListView.class);
                startActivity(intent);
            }
        });
    }

    private void setupAdapter() {
        ArrayList<String> addressdata = new ArrayList<>();
        addressdata =get_country();
        String ad = addressdata.toString();
        Log.e("011",ad);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                addressdata);
        autoinfo.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        autoinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoinfo.showDropDown();
            }
        });

    }
    private void setupAdapter2()
    {
        doupdate();
    }
    private void doupdate()
    {
        ArrayList<String> addressdata = new ArrayList<>();
        addressdata = get_city(autoinfo.getText().toString().trim());
        String ad = addressdata.toString();
        Log.e("...",ad);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                addressdata);
        autoinfo2.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        autoinfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoinfo2.showDropDown();
                if("".equals(autoinfo.getText().toString().trim()))
                {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("提示視窗")
                            .setMessage("請先選擇/輸入縣市")
                            .setNeutralButton("我知道了",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }


            }
        });
    }
    private ArrayList<String> get_country()
    {
        Cursor res = helper.get_country();
        ArrayList<String> list = new ArrayList<>();
        while(res.moveToNext())
            list.add(res.getString(0));
        return list;
    }
    private ArrayList<String> get_city(String input_country)
    {
        Cursor res = helper.get_city(input_country);
        StringBuffer buffer = new StringBuffer();
        ArrayList<String> list = new ArrayList<>();
        while(res.moveToNext())
        {
            list.add(res.getString(0));
            // Log.e("...",res.getString(0));
        }
        return list;
    }

    TextWatcher textWatcher = new TextWatcher() {
        private CharSequence temp;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Toast.makeText(ShowAddressActivity.this,"beforeTextChanged ",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp  =  s ;
            doupdate();
        }

        @Override
        public void afterTextChanged(Editable s) {
            //doupdate();
        }
    };

}
