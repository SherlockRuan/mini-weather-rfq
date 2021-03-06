package com.example.leisu.miniweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.leisu.bean.TodayWeather;
import com.example.leisu.util.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Created by leisu on 2017/9/26.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private String updateCityCode;
    TodayWeather todayWeather = null;

    private ImageView mUpdateBtn;

    private ImageView mCitySelect;

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv, tempTv;
    private ImageView weatherImg, pmImg;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    Log.d("terror+m.obj", msg.obj.toString());
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) !=NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络断开");
            Toast.makeText(MainActivity.this,"网络断开！", Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();

        Intent intent = this.getIntent();
        updateCityCode = intent.getStringExtra("cityCode");
        //Log.d("upcityccc", updateCityCode);
        if(updateCityCode != "-1"){
            queryWeatherCode(updateCityCode);
        }
    }

    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week);
        pmDataTv = (TextView) findViewById(R.id.pm_num);
        pmQualityTv = (TextView) findViewById(R.id.pm_com);
        pmImg = (ImageView) findViewById(R.id.pm_image);
        temperatureTv = (TextView) findViewById(R.id.wendu);
        tempTv = (TextView) findViewById(R.id.temp);
        climateTv = (TextView) findViewById(R.id.tianqi);
        windTv = (TextView) findViewById(R.id.feng);
        weatherImg = (ImageView) findViewById(R.id.wea_image);

        city_name_Tv.setText("北京天气");
        cityTv.setText("北京");
        timeTv.setText("今天00:00发布");
        humidityTv.setText("湿度:10%");
        pmDataTv.setText("20");
        pmQualityTv.setText("优");
        weekTv.setText("今天 星期三");
        temperatureTv.setText("6℃~17℃");
        tempTv.setText("温度:10℃");
        climateTv.setText("晴");
        windTv.setText("微风");

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String cityCode = sharedPreferences.getString("main_city_code", "101010100");
        Log.d("myWeather", cityCode);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            queryWeatherCode(cityCode);
        } else {
            Log.d("myWeather", "网络断开");
            Toast.makeText(MainActivity.this, "网络断开!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            startActivity(i);
            startActivityForResult(i,1);
        }

        if (view.getId() == R.id.title_update_btn) {

            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络断开");
                Toast.makeText(MainActivity.this, "网络断开!", Toast.LENGTH_LONG).show();
            }
        }

        if (view.getId() == R.id.wea_image) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                Toast.makeText(MainActivity.this, "网络OK!", Toast.LENGTH_LONG).show();
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络断开");
                Toast.makeText(MainActivity.this, "网络断开!", Toast.LENGTH_LONG).show();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode= data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为"+newCityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络断开");
                Toast.makeText(MainActivity.this, "网络断开！", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void queryWeatherCode(String cityCode)  {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather_add", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr=response.toString();
                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("terror:todayWeather.toS", todayWeather.toString());
                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather= new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }

                        break;


                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }


    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        tempTv.setText("温度："+todayWeather.getWendu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow()+"~"+todayWeather.getHigh());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());

        String init_city_name_Tv = todayWeather.getCity()+"天气";
        String init_cityTv = todayWeather.getCity();
        String init_timeTv = todayWeather.getUpdatetime()+ "发布";
        String init_humidityTv = "湿度："+todayWeather.getShidu();
        String init_tempTv = "温度："+todayWeather.getWendu();
        String init_pmDataTv = todayWeather.getPm25();
        String init_pmQualityTv = todayWeather.getQuality();
        String init_weekTv = todayWeather.getDate();
        String init_temperatureTv = todayWeather.getLow()+"~"+todayWeather.getHigh();
        String init_climateTv = todayWeather.getType();
        String init_windTv = "风力:"+todayWeather.getFengli();

        if (Integer.parseInt(todayWeather.getPm25()) >= 0 && Integer.parseInt(todayWeather.getPm25()) <= 50) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        } else if (Integer.parseInt(todayWeather.getPm25()) >= 51 && Integer.parseInt(todayWeather.getPm25()) <= 100) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        } else if (Integer.parseInt(todayWeather.getPm25()) >= 101 && Integer.parseInt(todayWeather.getPm25()) <= 150) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
        } else if (Integer.parseInt(todayWeather.getPm25()) >= 151 && Integer.parseInt(todayWeather.getPm25()) <= 200) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
        } else if (Integer.parseInt(todayWeather.getPm25()) >= 201 && Integer.parseInt(todayWeather.getPm25()) <= 300) {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
        } else {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
        }
        switch(todayWeather.getType()){
            case("多云"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);break;
            case("暴雨"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);break;
            case("暴雪"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);break;
            case("大暴雨"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);break;
            case("大雪"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);break;
            case("大雨"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);break;
            case("雷阵雨"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);break;
            case("雷阵雨冰雹"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);break;
            case("晴"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);break;
            case("沙尘暴"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);break;
            case("特大暴雨"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);break;
            case("雾"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);break;
            case("小雨"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);break;
            case("小雪"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);break;
            case("阴"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);break;
            case("雨夹雪"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);break;
            case("阵雨"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);break;
            case("阵雪"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);break;
            case("中雪"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);break;
            case("中雨"):weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);break;
            default:weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);break;
        }

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();

    }




}
