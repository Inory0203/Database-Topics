package com.example.leo09_000.house;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Collection_ListView extends AppCompatActivity {
    MyDBHelper helper =  new MyDBHelper(this,"addressdata.db",null,1);
    ListView listview;
    //delete button , origin button , calculate button
    Button edit_bt , cancel_bt  ,selectAll_bt;
    /****************************************************/
    Map<Integer,String> map  = new HashMap<>();
    //ArrayList<String>delete_list = new ArrayList<String>();
    String[] string_address_arr;
    String[] string_address_arr_list;
    boolean edit_type = false;
    boolean ort_type = false;
    boolean cal_type = false;
    boolean selAll_type = false;
    /*****************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection__list_view);
        get_data_from_db();
        listview = findViewById(R.id.fav_listview);
        edit_bt = findViewById(R.id.fav_edit);
        cancel_bt =findViewById(R.id.cancel_bt);
        selectAll_bt = findViewById(R.id.SelectAll);

        origin_show_list(string_address_arr_list);

        edit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //編輯與刪除建 第一次點為切換編輯畫面 第二次為刪除所選
                if(edit_type == false)
                {
                    edit_list(string_address_arr_list);
                    edit_bt.setText("刪除");
                    selectAll_bt.setVisibility(View.VISIBLE);
                    cancel_bt.setVisibility(View.VISIBLE);
                    edit_type = true;
                }
                else
                {
                    //delete from map
                    if(map.size() > 0)
                    {
                        for(Object key : map.keySet()) {
                            if(map.get(key).toString() != null)
                                helper.delete_from_favtb(map.get(key).toString());
                        }
                        helper.getAllData();
                        // Log.e("map collecting",map_data.toString());
                    }
                    edit_bt.setText("編輯");
                    selectAll_bt.setVisibility(View.GONE);
                    cancel_bt.setVisibility(View.GONE);
                    edit_type = false;
                    //UPDATE DATA and re-show origin list
                    get_data_from_db();
                    origin_show_list(string_address_arr_list);
                }
            }
        });
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                origin_show_list(string_address_arr_list);
            }
        });
        selectAll_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selAll_type == false)
                {
                    for(int i = 0 ; i < listview.getAdapter().getCount() ; i++)
                        listview.setItemChecked(i,true);
                    selAll_type = true;
                    map_edit();
                    selectAll_bt.setText("取消全選");
                }
                else
                {
                    for(int i = 0 ; i < listview.getAdapter().getCount() ; i++)
                        listview.setItemChecked(i,false);
                    selAll_type = false;
                    map_edit();
                    selectAll_bt.setText("全選");
                }
            }
        });


    }
    private void origin_show_list(String[] arr)
    {
        ArrayAdapter<String> list = new ArrayAdapter<>(Collection_ListView.this,android.R.layout.simple_list_item_1,arr);
        //  listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(list);
        listview.setOnItemClickListener(onClickListView);
        map.clear();
    }
    private void edit_list(String[] arr)
    {
        ArrayAdapter<String> list = new ArrayAdapter<String>(Collection_ListView.this,android.R.layout.simple_list_item_multiple_choice,arr);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(list);
        listview.setOnItemClickListener(onClickListView2);
    }
    private void get_data_from_db()
    {
        Cursor res = helper.get_fav_address();
        ArrayList<String> list = new ArrayList<>();
        while(res.moveToNext())
            list.add(res.getString(0));
        string_address_arr = list.toArray(new String[0]);
        list.clear();
        res = helper.get_fav_data();
        while(res.moveToNext())
            list.add(res.getString(0));
        string_address_arr_list = list.toArray(new String[0]);
    }

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Toast.makeText(Collection_ListView.this,"點選第 "+(position +1) +" 個 \n內容："+string_address_arr[position], Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("position_address",string_address_arr[position]);
            intent.setClass(Collection_ListView.this,Main3Activity.class);
            startActivity(intent);
        }
    };

    private AdapterView.OnItemClickListener onClickListView2 = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            map_edit();
        }
    };
    private void map_edit()
    {
        for(int i = 0; i< listview.getCheckedItemPositions().size(); i++)
        {
            int list_position =listview.getCheckedItemPositions().keyAt(i);
            if(listview.getCheckedItemPositions().valueAt(i) == true){
                map.put( list_position ,string_address_arr[list_position]);
            }
            else {
                map.remove(list_position);
            }
        }
    }


}