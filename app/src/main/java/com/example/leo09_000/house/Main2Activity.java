package com.example.leo09_000.house;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Main2Activity extends AppCompatActivity {

    RequestQueue requestqueue;
    String showUrl = "http://140.136.148.228/get_user_input.php";
    String address;
    ArrayList<String> addresslist = new ArrayList<String>();
    ArrayList<String> agelist = new ArrayList<String>();
    ArrayList<String> completed_datelist = new ArrayList<String>();
    ArrayList<String> layerslist = new ArrayList<String>();
    ArrayList<String> building_patternslist = new ArrayList<String>();
    ArrayList<String> total_pricelist = new ArrayList<String>();

    String[] string_address_arr;
    ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        requestqueue = Volley.newRequestQueue(getApplicationContext());
        listview = (ListView) findViewById(R.id.listview);

        Intent intent = getIntent();
        address = intent.getStringExtra("marker_address");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("fn", address);

        CustomRequest request = new CustomRequest(Request.Method.POST, showUrl, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray students = response.getJSONArray("search_results");
                    for (int i = 0; i < students.length(); i++) {
                        JSONObject student = students.getJSONObject(i);

                        String address = student.getString("土地區段位置或建物區門牌");
                        Log.e("003",address);
                        String age = student.getString("屋齡");
                        String completed_date = student.getString("建築完成日期");
                        String layers = student.getString("總樓層數");
                        String building_patterns = student.getString("建物型態");
                        String total_price = student.getString("總價元");
                        total_price = String.valueOf((Float.valueOf(total_price) / 10000)) + "萬元";

                        addresslist.add(address);
                        agelist.add(age);
                        completed_datelist.add(completed_date);
                        layerslist.add(layers);
                        building_patternslist.add(building_patterns);
                        total_pricelist.add(total_price);
                    }

                    string_address_arr=addresslist.toArray(new String[0]);

                    ArrayAdapter<String> list = new ArrayAdapter<String>(Main2Activity.this,android.R.layout.simple_list_item_1,string_address_arr);
                    listview.setAdapter(list);
                    listview.setOnItemClickListener(onClickListView);

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

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Toast.makeText(Main2Activity.this,"點選第 "+(position +1) +" 個 \n內容："+string_address_arr[position], Toast.LENGTH_SHORT).show();
            /*int pos=listview.getSelectedItemPosition();*/
            new AlertDialog.Builder(Main2Activity.this)
                    .setTitle(addresslist.get(position))
                    .setMessage("建築完成日期 : "+completed_datelist.get(position)+"\n"+"建物型態 : " +building_patternslist.get(position)+"\n"+
                            "總樓層數 : "+layerslist.get(position)+"\n"+"屋齡 : "+agelist.get(position)+"\n"+"總價元 : "+total_pricelist.get(position))
                    .setPositiveButton("選擇", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent();
                                    intent.putExtra("position_address",string_address_arr[position]);
                                    intent.setClass(Main2Activity.this,Main3Activity.class);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    };
}
