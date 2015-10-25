package com.lcu.FeelingChart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lcu.JavaTool.CallWebservice;
import com.lcu.feelingcampus.MainApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class myFeelingChart extends Activity {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Date day;
    private PieChart pieChart;
    private Typeface tf;
    private String myfeel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.myfeelingchart);
        //生成历史一月心情图表点击事件
        Button weekchart = (Button) findViewById(R.id.amoonChart);
        weekchart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getfeeldata("0");
            }
        });
        //生成历史一周心情图表点击事件
        Button moonchart = (Button) findViewById(R.id.aweekChart);
        weekchart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getfeeldata("1");
            }
        });
        //饼图
        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        pieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText("心情校园");
        setpieData(17,21,33,9,10,10);
        pieChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
    }
    //饼图
    private void setpieData(int ic, int im, int iy, int ik, int id, int iw) {
        ArrayList<Entry> a = new ArrayList<Entry>();
        a.add(new Entry((int) ic, 0));
        a.add(new Entry((int) im, 1));
        a.add(new Entry((int) iy, 2));
        a.add(new Entry((int) ik, 3));
        a.add(new Entry((int) id, 4));
        a.add(new Entry((int) iw, 5));
        ArrayList<String> x1 = new ArrayList<String>();
        x1.add("伤心");
        x1.add("高兴");
        x1.add("生气");
        x1.add("平静");
        x1.add("迷茫");
        x1.add("忧愁");
        PieDataSet dataSet = new PieDataSet(a, "心情图例");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(x1, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tf);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
        //文字提示
        HashMap<String,String> mymap = new HashMap<String,String>();

        mymap.put(String.valueOf(ic), "C");
        mymap.put(String.valueOf(im), "M");
        mymap.put(String.valueOf(iy), "Y");
        mymap.put(String.valueOf(ik), "K");
        mymap.put(String.valueOf(id), "D");
        mymap.put(String.valueOf(iw), "W");

        int[] ismax = {ic, im, iy, ik, id, iw};
        Arrays.sort(ismax);
        String maxStr = String.valueOf(ismax[5]);

        String maxnumStr = mymap.get(maxStr);
        myfeel = maxnumStr;
        switch (maxnumStr)
        {
            case  "C" :
                myfeel="伤感、郁闷、痛心、压抑";
                break;
            case  "M" :
                myfeel="开心、甜蜜、欢快、舒畅";
                break;
            case  "Y" :
                myfeel="生气、失控、兴奋、宣泄";
                break;
            case  "K" :
                myfeel="平静、放松、专注、出神";
                break;
            case  "D" :
                myfeel="忧愁、疑惑、迷茫、无助";
                break;
            case  "W" :
                myfeel="害怕、焦虑、紧张、激情";
                break;
        }
        //请求数据库，开启线程
        String url = "Service.asmx";
        String methodName = "getSysWords";
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("MyFeeling", maxnumStr);
        CallWebservice callWeb = new CallWebservice(myhandler);
        callWeb.doStart(url, methodName, params);
    }
    //开启线程，拿到系统友情提示
    private android.os.Handler myhandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message m) {
            ArrayList<String> datas = m.getData().getStringArrayList("data");
            goResult(datas);
        }

        ;
    };
    //显示webservice结果
    private void goResult(ArrayList<String> datas) {
        ArrayList<HashMap<String, Object>> mydata = new ArrayList<HashMap<String, Object>>();
        Log.i("doResult", "数据个数:" + datas.size() + "个");
        for (int i = 0; i < datas.size(); ) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (datas.get(i) != null) {
                map.put("userFeeling", datas.get(i));
                i++;
                mydata.add(map);
            }
        }
        setText(mydata);
    }
    private void  setText(ArrayList<HashMap<String, Object>> mydata){
        String[] words = new String[mydata.size()];
        for (int i = 0; i < mydata.size(); i++) {
            words[i] = mydata.get(i).get("userFeeling").toString();
        }
        TextView upword = (TextView)findViewById(R.id.pieResult);
        int c=  (int) (Math.random() * words.length);
        upword.setText("您这段时间的心情主要是 ："+myfeel+"\n友情提示 ："+words[c]);
    }
    //开启线程
    @SuppressLint("HandlerLeak")
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message m) {
            ArrayList<String> datas = m.getData().getStringArrayList("data");
            doResult(datas);
        }

        ;
    };
    //显示webservice结果
    private void doResult(ArrayList<String> datas) {
        ArrayList<HashMap<String, Object>> mydata = new ArrayList<HashMap<String, Object>>();
        Log.i("doResult", "数据个数:" + datas.size() + "个");
        for (int i = 0; i < datas.size(); ) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (datas.get(i) != null) {
                map.put("userFeeling", datas.get(i));
                i++;
                mydata.add(map);
            }
        }
        setchart(mydata);
    }
    //解析数据库并填表
    private void setchart(ArrayList<HashMap<String, Object>> mydata) {
        int ic = 0;
        int im = 0;
        int iy = 0;
        int ik = 0;
        int id = 0;
        int iw = 0;
        String[] fells = new String[mydata.size()];
        for (int i = 0; i < mydata.size(); i++) {
            fells[i] = mydata.get(i).get("userFeeling").toString();
        }
        for (int a = 0; a < fells.length; a++) {
            switch (fells[a]) {
                case "C":
                    ic++;
                    break;
                case "M":
                    im++;
                    break;
                case "Y":
                    iy++;
                    break;
                case "K":
                    ik++;
                    break;
                case "D":
                    id++;
                    break;
                case "W":
                    iw++;
                    break;
                default:
            }
        }
        setpieData(ic, im, iy, ik, id, iw);
    }
    //从数据库拿数据
    public void getfeeldata(String wemo) {
        MainApplication app = (MainApplication) getApplicationContext();
        String picurl = app.getuserurl();
        //获取当前时间
        Date curDate = new Date(System.currentTimeMillis());
        String sendTime = sdf.format(curDate);
        //转换成时间戳
        try {
            day = sdf.parse(sendTime);
            long lsjc = day.getTime() / 1000;
            sendTime = String.valueOf(lsjc);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //请求数据库，开启线程
        String url = "Service.asmx";
        String methodName = "getAllFeelingByTimeAndUrl";
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("TimeString", sendTime);
        params.put("URL", "http://himg.bdimg.com/sys/portraitn/item/a93d203865e54debb67543.jpg");
        params.put("type", wemo);
        CallWebservice callWeb = new CallWebservice(handler);
        callWeb.doStart(url, methodName, params);
    }
}