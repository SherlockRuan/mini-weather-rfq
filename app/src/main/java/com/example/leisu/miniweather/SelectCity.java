package com.example.leisu.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.leisu.app.MyApplication;
import com.example.leisu.bean.City;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by leisu on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView backBtn;
    private ListView getCityListLv;
    private EditText searchEt;
    private ImageView searchBtn;

    private ImageView mBackBtn;
    private ListView cityListLv;

    private List<City> mCityList;
    private MyApplication mApplication;
    private ArrayList<String> mArrayList;

    public String updateCityCode = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        searchEt = (EditText)findViewById(R.id.select_input);
        searchBtn = (ImageView)findViewById(R.id.search_city_b);
        searchBtn.setOnClickListener(this);

        mApplication = (MyApplication)getApplication();
        mCityList = mApplication.getCityList();
        mArrayList = new ArrayList<String>();
        for(int i = 0;i<mCityList.size();i++) {
            String number = Integer.toString(i + 1);
            String numCity = mCityList.get(i).getNumber();
            String proName = mCityList.get(i).getProvince();
            String cityName = mCityList.get(i).getCity();
            mArrayList.add("NO." + number + " : " + numCity + "-" + proName + "-" + cityName);
        }
        cityListLv = (ListView)findViewById(R.id.selectcity_lv);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_expandable_list_item_1,mArrayList);
        cityListLv.setAdapter(adapter);

        //添加ListView的点击事件
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int position, long id) {
                updateCityCode = mCityList.get(position).getNumber();
                Log.d("upcityc",updateCityCode);
            }
        };
        //为组件绑定监听
        cityListLv.setOnItemClickListener(itemClickListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("cityCode", updateCityCode);
                Log.d("upcitycc",updateCityCode);
                startActivity(intent);
                //setResult(RESULT_OK,intent);
                //finish();
                break;
            case R.id.search_city_b:
                String citycode = searchEt.getText().toString();
                Log.d("Search",citycode);
                ArrayList<String>mSearchList = new ArrayList<String>();
                for(int i = 0; i < mCityList.size();i++) {
                    String number = Integer.toString(i + 1);
                    String numCity = mCityList.get(i).getNumber();
                    String proName = mCityList.get(i).getProvince();
                    String cityName = mCityList.get(i).getCity();
                    if (numCity.equals(citycode)) {
                        mSearchList.add("NO." + number + " : " + numCity + "-" + proName + "-" + cityName);
                        Log.d("upcity3", "NO." + number + " : " + numCity + "-" + proName + "-" + cityName);
                    }
                    ArrayAdapter<String>adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_expandable_list_item_1,mSearchList);
                    cityListLv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                Intent intent1 = new Intent(this,MainActivity.class);
                intent1.putExtra("cityCode",citycode);
                Log.d("upcitycc1",updateCityCode);
                startActivity(intent1);
            default:
                break;
        }
    }


}
