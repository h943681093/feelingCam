package com.lcu.FeelingChart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lcu.JavaTool.CallWebservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

public class FeelingChart extends Activity{
    private BarChart mChart;
    private  EditText selectDate;
    private String time;
    private String myfeel = "";
//    private String[] fells;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Date day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.feelingchart);
        //选择时间
        selectDate = (EditText) findViewById(R.id.selectDate);
        //获取本地时间设置为初十时间
        Date curDate = new Date(System.currentTimeMillis());
        String nowTime = sdf.format(curDate);
        selectDate.setText("2015-10-12");

         selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(FeelingChart.this).create();
                dialog.show();
                DatePicker picker = new DatePicker(FeelingChart.this);
                picker.setDate(2015, 10);
                picker.setMode(DPMode.SINGLE);
                picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
                    @Override
                    public void onDatePicked(String date) {
                        selectDate.setText(date);
                        dialog.dismiss();
                    }
                });
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setContentView(picker, params);
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });

        //生成图表点击事件
        Button charting = (Button)findViewById(R.id.charting);
        charting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getfeeldata();
            }
        });
        //声明图表
        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setDescription("校园内各种心情图表");
        mChart.setMaxVisibleValueCount(60);
        mChart.setPinchZoom(true);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setSpaceBetweenLabels(6);
        xAxis.setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.animateY(2500);
        mChart.getLegend().setEnabled(true);
        startChart(30,50,90,80,40,20);
    }
    //生成图表方法
   private void startChart(int ic,int im,int iy,int ik,int id,int iw){
       ArrayList<BarEntry> a = new ArrayList<BarEntry>();
       a.add(new BarEntry((int) ic, 0));
       a.add(new BarEntry((int) im, 1));
       a.add(new BarEntry((int) iy, 2));
       a.add(new BarEntry((int) ik, 3));
       a.add(new BarEntry((int) id, 4));
       a.add(new BarEntry((int) iw, 5));

       BarDataSet b = new BarDataSet(a, "伤心  高兴  生气  平静  迷茫  焦虑");
       b.setColors(ColorTemplate.VORDIPLOM_COLORS);
       b.setDrawValues(false);
       ArrayList<BarDataSet> bs = new ArrayList<BarDataSet>();
       bs.add(b);
       ArrayList<String> x1 = new ArrayList<String>();
       x1.add(0+"");  x1.add(1+"");  x1.add(2+"");  x1.add(3+"");  x1.add(4 + "");  x1.add(5+"");
       BarData datac = new BarData(x1, bs);
       mChart.setData(datac);
       for (DataSet<?> set : mChart.getData().getDataSets())
           set.setDrawValues(!set.isDrawValuesEnabled());
       mChart.invalidate();
       Legend l = mChart.getLegend();
       l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
       l.setFormSize(8f);
       l.setXEntrySpace(4f);
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
       TextView setText = (TextView)findViewById(R.id.barResult);
       setText.setText("聊城大学一天心情主要是 ："+myfeel);
    }
    //开启线程
    @SuppressLint("HandlerLeak")
    private android.os.Handler handler=new android.os.Handler()
    {
        @Override
        public void handleMessage(Message m)
        {
            ArrayList<String> datas = m.getData().getStringArrayList("data");
            doResult(datas);
        };
    };
    //显示webservice结果
    private void doResult( ArrayList<String> datas){
        ArrayList<HashMap<String, Object>> mydata = new ArrayList<HashMap<String, Object>>();
        Log.i("doResult", "数据个数:" + datas.size() + "个");
        for(int i=0;i<datas.size();){
            HashMap<String, Object> map = new HashMap<String, Object>();
            if(datas.get(i)!=null){
                map.put("userFeeling", datas.get(i));
                i++;
                mydata.add(map);
            }
        }
        setchart(mydata);
    }
    //解析数据库并填表
    private void  setchart( ArrayList<HashMap<String, Object>> mydata) {
        int ic =0;int im =0;int iy =0;int ik =0;int id =0;int iw =0;
        String[] fells =new String[mydata.size()];
        for (int i = 0; i < mydata.size();i++ ) {
            fells[i] = mydata.get(i).get("userFeeling").toString();
        }
        for (int a = 0; a < fells.length;a++){
            switch (fells[a])
            {
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
        startChart(ic,im,iy,ik,id,iw);
    }
    //从数据库拿数据
    public  void getfeeldata(){
        String sjc = "";
        time = selectDate.getText().toString();
        try {
            day = sdf.parse(time);
            long lsjc = day.getTime()/1000;
            sjc = String.valueOf(lsjc);
        }catch (ParseException e){
            e.printStackTrace();
        }
        System.out.println(sjc);
        String url = "Service.asmx";
        String methodName="getAllFeelingByTime";
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("TimeString",sjc);
        CallWebservice callWeb=new CallWebservice(handler);
        callWeb.doStart(url, methodName, params);
    }
}
