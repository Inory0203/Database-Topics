package com.example.leo09_000.house;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {
    RequestQueue requestqueue;
    String showUrl = "http://140.136.148.228/test4.php";
    String address;
    TextView t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t15, t16, t17, t19, t20, t21, t22, t23, t24,t25,t26,t27;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        t1 = (TextView) findViewById(R.id.textView7);
        t2 = (TextView) findViewById(R.id.textView8);
        t3 = (TextView) findViewById(R.id.textView9);
        t4 = (TextView) findViewById(R.id.textView10);
        t5 = (TextView) findViewById(R.id.textView11);
        t6 = (TextView) findViewById(R.id.textView12);
        t7 = (TextView) findViewById(R.id.textView14);
        t8 = (TextView) findViewById(R.id.textView16);
        t9 = (TextView) findViewById(R.id.textView18);
        t10 = (TextView) findViewById(R.id.textView20);
        t11 = (TextView) findViewById(R.id.textView22);
        t15 = (TextView) findViewById(R.id.textView30);
        t16 = (TextView) findViewById(R.id.textView32);
        t17 = (TextView) findViewById(R.id.textView34);
        t19 = (TextView) findViewById(R.id.textView38);
        t20 = (TextView) findViewById(R.id.textView40);
        t21 = (TextView) findViewById(R.id.textView44);
        t22 = (TextView) findViewById(R.id.textView46);
        t23 = (TextView) findViewById(R.id.textView48);
        t24 = (TextView) findViewById(R.id.textView50);
        t25 = (TextView) findViewById(R.id.textView26);
        t26 = (TextView) findViewById(R.id.textView28);
        t27 = (TextView) findViewById(R.id.textView51);

        requestqueue = Volley.newRequestQueue(getApplicationContext());

        Intent intent = getIntent();
        address = intent.getStringExtra("position_address");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("fn", address);

        CustomRequest request = new CustomRequest(Request.Method.POST, showUrl, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray students = response.getJSONArray("students");
                    for (int i = 0; i < students.length(); i++) {
                        JSONObject student = students.getJSONObject(i);

                        String number = student.getString("編號");
                        String parking_category = student.getString("車位類別");
                        String main_purpose = student.getString("主要用途");
                        String materials = student.getString("主要建材");
                        String age = student.getString("屋齡");
                        String completed_date = student.getString("建築完成日期");
                        String layers = student.getString("總樓層數");
                        String section_location = student.getString("土地區段位置");
                        String transaction = student.getString("交易標的");
                        String address = student.getString("土地區段位置或建物區門牌");
                        String transaction_date = student.getString("交易年月日");
                        String transaction_number = student.getString("交易筆棟數");
                        String building_transfer_area = turn_ping(student.getString("建物移轉總面積平方公尺"));
                        String land_transfer_area = turn_ping(student.getString("土地移轉面積平方公尺"));
                        String building_transfer_area2 = turn_ping(student.getString("建物移轉面積平方公尺"));
                        String make = student.getString("使用分區或編定");
                        String building_layered = student.getString("建物分層");
                        String partition = student.getString("都市土地使用分區");
                        String building_patterns = student.getString("建物型態");
                        String room = student.getString("建物現況格局-房");
                        String hall = student.getString("建物現況格局-廳");
                        String bathroom = student.getString("建物現況格局-衛");
                        String compartment = student.getString("建物現況格局-隔間");
                        String management_organization = student.getString("有無管理組織");
                        String total_price = student.getString("總價元");
                        DecimalFormat df = new DecimalFormat("##");
                        String unit_price = String.valueOf(Integer.parseInt(df.format(Double.parseDouble(total_price) / Double.parseDouble(building_transfer_area))));
                        String parking_total_price = student.getString("車位總價元");
                        if (parking_total_price.equals("0")) parking_total_price = "無";
                        else parking_total_price += " 元";

                        String parking_area = student.getString("車位面積平方公尺");
                        if (parking_area.equals("")) parking_area = "無";
                        else parking_area = turn_ping(parking_area)+" 坪";


                        t1.setText(" " + address);
                        t2.setText(" " + building_patterns);
                        t3.setText(" " + layers);
                        t4.setText(" " + completed_date);
                        t5.setText(" " + age + " 年");
                        t6.setText(" " + total_price + " 元");
                        t9.setText(" " + section_location);
                        t7.setText(" " + main_purpose);
                        t8.setText(" " + materials);
                        t10.setText(" " + building_layered);
                        t16.setText(" " + unit_price + " (元/坪)");
                        t17.setText(" " + parking_total_price);
                        t19.setText(" " + parking_area);
                        t20.setText(" " + parking_category);
                        t15.setText(" " + management_organization);
                        t11.setText(" " + room + " 房 " + hall + " 廳 " + bathroom + " 衛 " + compartment + " 隔間");
                        t21.setText(" " + transaction);
                        t22.setText(" " + transaction_date);
                        t23.setText(" " + building_transfer_area + " 坪");
                        t24.setText(" " + transaction_number);
                        t25.setText(" "+land_transfer_area+ " 坪");
                        t26.setText(" "+make);
                        t27.setText(" "+building_transfer_area2+ " 坪");

                    }

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

    public String turn_ping(String s) {
        DecimalFormat df = new DecimalFormat("##.00");
        double n = Double.parseDouble(s) * 0.3025;
        n = Double.parseDouble(df.format(n));
        return Double.toString(n);
    }
}
