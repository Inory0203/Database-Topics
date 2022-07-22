package com.example.leo09_000.house;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class Main5Activity extends AppCompatActivity {

    RequestQueue requestqueue;
    String showUrl = "http://140.136.148.228/get_user_input.php";
    private static final int LONG_DELAY = 3500;
    ArrayList<String> addresslist = new ArrayList<String>();
    ArrayList<String> agelist = new ArrayList<String>();
    ArrayList<String> completed_datelist = new ArrayList<String>();
    ArrayList<String> layerslist = new ArrayList<String>();
    ArrayList<String> building_patternslist = new ArrayList<String>();
    ArrayList<String> total_pricelist = new ArrayList<String>();
    ArrayList<String> building_total_area_list = new ArrayList<String>();

    Map<Integer, String> map = new HashMap<>();
    Map<Integer, String> map_data = new HashMap<>();
    String[] string_address_arr;
    String[] string_address_arr_list;
    ListView listview;
    Button fav_button, list_button, Select_all;
    MyDBHelper helper = new MyDBHelper(this, "addressdata.db", null, 1);
    int fav_button_type = 0;
    boolean select_all_button_type = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        requestqueue = Volley.newRequestQueue(getApplicationContext());
        listview = (ListView) findViewById(R.id.listview);
        fav_button = (Button) findViewById(R.id.fav_button);
        list_button = (Button) findViewById(R.id.list_button);
        Select_all = (Button) findViewById(R.id.SelectAll);
        Intent intent = getIntent();
        String town = intent.getStringExtra("town");
        String country = intent.getStringExtra("country");
        String road = intent.getStringExtra("road");
        String country_city = country + town + road;
        Log.e("000", country_city);
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
                        Log.e("020202", address);
                        String age = student.getString("屋齡");
                        String completed_date = student.getString("建築完成日期");
                        String layers = student.getString("總樓層數");
                        String building_patterns = student.getString("建物型態");
                        String total_price = student.getString("總價元");
                        String building_total_area = student.getString("建物移轉總面積平方公尺");
                        String building_translate = String.valueOf((Float.valueOf(building_total_area) * 0.3025));

                        building_translate = building_translate.substring(0, building_translate.indexOf('.') + 2);

                        String avg_price = String.valueOf(Float.valueOf(total_price) / Float.valueOf(building_translate));

                        String transaction = student.getString("交易標的");

                        total_price = String.valueOf((Float.valueOf(total_price) / 10000)) + "萬元";
                        avg_price = String.valueOf((Float.valueOf(avg_price) / 10000));
                        avg_price = avg_price.substring(0, avg_price.indexOf('.') + 2) + "萬元";
                        addresslist.add(address);
                        agelist.add(age);
                        completed_datelist.add(completed_date);
                        layerslist.add(layers);
                        building_patternslist.add(building_patterns);
                        total_pricelist.add(total_price);
                        String tmp = address + "\n類型 : " + transaction  + "\n建物總面積 : " + building_translate + "  坪" + "\n平均每坪價錢 ：" + avg_price;
                        building_total_area_list.add(tmp);
                    }

                    string_address_arr = addresslist.toArray(new String[0]);
                    string_address_arr_list = building_total_area_list.toArray(new String[0]);
                    origin_choose_list(string_address_arr_list);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                new AlertDialog.Builder(Main5Activity.this).setTitle("連線錯誤")
                        .setMessage("請檢查網路是否有連線，如以連線請重新查詢")
                        .setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
                ;
            }
        });
        requestqueue.add(request);

        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fav_button_type == 0) {
                    favorite_choose_list(string_address_arr_list);
                    fav_button.setText("確認收藏");
                    fav_button_type = 1;
                    list_button.setVisibility(View.VISIBLE);
                    Select_all.setVisibility(View.VISIBLE);
                    /**從map拿資料之後寫個函式連接到SQLite存資料**/
                } else //do favorite and return list_show
                {

                    list_button.setVisibility(View.GONE);
                    Select_all.setVisibility(View.GONE);
                    fav_button.setText("收藏選取");
                    fav_button_type = 0;
                    select_all_button_type = false;
                    // int[] keyset = map.keySet().toArray();
                    //Log.e("map collecting", map.keySet());
                    if (map.size() > 0) {
                        for (Object key : map.keySet()) {
                            if (map.get(key).toString() != null)
                                helper.insert_collection(map.get(key).toString(), map_data.get(key).toString());
                        }
                        helper.getAllData();
                        // Log.e("map collecting",map_data.toString());
                    }
                    origin_choose_list(string_address_arr_list);
                }
            }
        });


        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fav_button_type == 1) {
                    origin_choose_list(string_address_arr_list);
                    fav_button_type = 0;
                    list_button.setVisibility(View.GONE);
                    Select_all.setVisibility(View.GONE);
                    fav_button.setText("收藏選取");
                    select_all_button_type = false;
                }
            }
        });


        Select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select_all_button_type == false) {
                    for (int i = 0; i < listview.getAdapter().getCount(); i++)
                        listview.setItemChecked(i, true);
                    select_all_button_type = true;
                    map_edit();
                    Select_all.setText("取消全選");
                } else {
                    for (int i = 0; i < listview.getAdapter().getCount(); i++)
                        listview.setItemChecked(i, false);
                    select_all_button_type = false;
                    map_edit();
                    Select_all.setText("全選");

                }

            }
        });
    }

    private void origin_choose_list(String[] string_address_arr) {
        ArrayAdapter<String> list = new ArrayAdapter<String>(Main5Activity.this, android.R.layout.simple_list_item_1, string_address_arr);
        //  listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(list);
        listview.setOnItemClickListener(onClickListView);
        map.clear();
        map_data.clear();
    }

    private void favorite_choose_list(String[] string_address_arr) {
        ArrayAdapter<String> list = new ArrayAdapter<String>(Main5Activity.this, android.R.layout.simple_list_item_multiple_choice, string_address_arr);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(list);
        listview.setOnItemClickListener(onClickListView2);
        // listview.setOnItemClickListener(onClickListView);
    }

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Toast.makeText(Main5Activity.this, "點選第 " + (position + 1) + " 個 \n內容：" + string_address_arr[position], Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(Main5Activity.this)
                    .setTitle(addresslist.get(position))
                    .setMessage("建築完成日期 : " + completed_datelist.get(position) + "\n" + "建物型態 : " + building_patternslist.get(position) + "\n" +
                            "總樓層數 : " + layerslist.get(position) + "\n" + "屋齡 : " + agelist.get(position) + "\n" + "總價元 : " + total_pricelist.get(position))
                    .setPositiveButton("選擇", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.putExtra("position_address", string_address_arr[position]);
                            intent.setClass(Main5Activity.this, Main3Activity.class);
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

    private AdapterView.OnItemClickListener onClickListView2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            map_edit();
        }
    };

    private void map_edit() {
        for (int i = 0; i < listview.getCheckedItemPositions().size(); i++) {
            int list_position = listview.getCheckedItemPositions().keyAt(i);
            if (listview.getCheckedItemPositions().valueAt(i) == true) {
                map.put(list_position, string_address_arr[list_position]);
                map_data.put(list_position, string_address_arr_list[list_position]);
            } else {
                map.remove(list_position);
                map_data.remove(list_position);
            }
        }
    }
}
