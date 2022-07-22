package com.example.leo09_000.house;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyDBHelper extends SQLiteOpenHelper{
    private String[] country_str = {"基隆市","臺北市","新北市","桃園市","新竹縣","新竹市","苗栗縣","臺中市","南投縣","彰化縣","雲林縣", "嘉義縣","嘉義市","臺南市","高雄市","屏東縣",
            "宜蘭縣", "花蓮縣","金門縣"};
    private String[][] city_str = {
            {"暖暖區","安樂區","中正區","七堵區","仁愛區","信義區"},
            {"內湖區","中山區","南港區","大安區","萬華區","大同區","中正區","文山區","松山區","北投區","士林區","信義區"},
            {"蘆洲區","中和區","永和區","新店區","泰山區","深坑區","新莊區","淡水區","汐止區","三峽區","板橋區","土城區","林口區","樹林區","鶯歌區","三重區","五股區","金山區","瑞芳區","三芝區","八里區"},
            {"桃園區","中壢區","平鎮區","觀音區","楊梅區","龍潭區","八德區","蘆竹區","大園區","龜山區","大溪區","新屋區"},
            {"竹北市","湖口鄉","新豐鄉","竹東鎮","寶山鄉","芎林鄉"},
            {"新竹市"},
            {"苑裡鎮","竹南鎮","頭份巿","苗栗市"},
            {"大肚區","沙鹿區","西屯區","南屯區","南區","西區","東區","北屯區","北區"},
            {"埔里鎮","南投市","草屯鎮"},
            {"員林市","彰化市","和美鎮","福興鄉"},
            {"虎尾鎮","古坑鄉","斗六市","西螺鎮"},
            {"太保市","民雄鄉","朴子市","番路鄉"},
            {"嘉義市"},
            {"北區","善化區","新市區","永康區","安平區","中西區","鹽水區","東區","南區","安南區","學甲區","仁德區","下營區","佳里區"},
            {"左營區","楠梓區","三民區","鼓山區","苓雅區","新興區","小港區","前鎮區","鳳山區","鳥松區","大寮區","前金區","岡山區","仁武區","鹽埕區","大社區","橋頭區","林園區"},
            {"屏東市","潮州鎮","萬丹鄉"},
            {"宜蘭市","五結鄉","羅東鎮","頭城鎮"},
            {"壽豐鄉","花蓮市"},
            {"金湖鎮"}
    };
    public MyDBHelper(Context context, String name,
                      SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE addressdata " +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                "country VARCHAR NOT NULL , " +
                "city VARCHAR)" );
        db.execSQL("CREATE TABLE IF NOT EXISTS user_favorite_table " +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                "address VARCHAR not null unique ,"+
                "other_data text not null unique)"
        );
        initialize_data(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        onCreate(db);
    }
    public Cursor get_country()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct country from addressdata",null);
        return res;
    }

    public Cursor get_city(String country)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select city from addressdata where country = '"+country+"' ;",null);
        return res;
    }

    public Cursor get_fav_address()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select address from user_favorite_table;",null);
        return res;
    }
    public Cursor get_fav_data()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select other_data from user_favorite_table  ;",null);
        return res;
    }

    public void initialize_data( SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        for(int i = 0; i<country_str.length ; i++)
        {
            values.put("country", country_str[i]);
            for(int k = 0 ; k < city_str[i].length;k++) {
                values.put("city", city_str[i][k]);
                db.insert("addressdata", null, values);
            }
        }

    }
    public void getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from user_favorite_table",null);
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        while(res.moveToNext()) {
            list.add(res.getString(1));
            list2.add(res.getString(2));
        }
        Log.e("ALL DATA",list.toString());
        Log.e("ALL DATA_2",list2.toString());
    }
    public void insert_collection(String insert_address ,String insert_data )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("address",insert_address);
        /*****/
        values.put("other_data",insert_data);
        /*****/
        db.insert("user_favorite_table",null,values);
        //  db.insertWithOnConflict("user_favorite_table",null,values,SQLiteDatabase.CONFLICT_IGNORE);
        Log.e("collection_insert",insert_data);

    }
    public void delete_from_favtb(String address)
    {
        int t ;
        SQLiteDatabase db = this.getWritableDatabase();
        t = db.delete("user_favorite_table","address"+"='"+ address + "'",null);
        Log.e("delete suc?" , t+"");
    }
    //public void insert_col_data()
}