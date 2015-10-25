package com.lcu.MyCenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.RefreshableListView;
import com.astuetz.viewpager.extensions.sample.R;
import com.lcu.JavaTool.CallWebservice;
import com.lcu.feelingcampus.MainApplication;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class ShowFeelingWords extends Activity{
    private RefreshableListView lv;
    private ImageView showHeadBack;
    private SpotsDialog sportdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showfeelingwords);
        sportdialog= new SpotsDialog(this,"正在加载...", R.style.Custom);
        lv=(RefreshableListView)findViewById(R.id.showLv);
        lv.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
            @Override
            public void onRefresh(RefreshableListView listView) {
                new NewDataTask().execute();
            }

        });
        showHeadBack = (ImageView) findViewById(R.id.showheadback);
        showHeadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFeelingWords.this.finish();
            }
        });
        doThread();
    }
    //下拉刷新事件    -
    private class NewDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {}

            return "A new list item";
        }

        @Override
        protected void onPostExecute(String result) {
            doThread();
            lv.completeRefreshing();
            super.onPostExecute(result);
        }
    }
    //开启线程
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler()
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
                map.put("showContent", datas.get(i));
                i++;
                map.put("showFeeling", datas.get(i));
                i++;
                map.put("showTime",  datas.get(i));
                i++;
                map.put("showPurl",  datas.get(i));
                i++;
                mydata.add(map);
            }
        }
        LeagueAdapter adapter=new LeagueAdapter(mydata);
        lv.setAdapter(adapter);
        sportdialog.dismiss();
    }
    //ListView中Adapter
    class LeagueAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> data;
        private MyView views;
        public LeagueAdapter(ArrayList<HashMap<String, Object>>  data){
            this.data=data;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.showfeelingwords_item, null);
                views=new MyView();
                views.showContent=(TextView)convertView.findViewById(R.id.showContent);
                views.showFeeling=(ImageView)convertView.findViewById(R.id.showFeeling);
                views.showTime=(TextView)convertView.findViewById(R.id.showTime);
                convertView.setTag(views);
            }else{
                views=(MyView)convertView.getTag();
            }
            final String Fcontent = data.get(position).get("showContent").toString();
            final String Ffeeling = data.get(position).get("showFeeling").toString();
            final String Ftime = data.get(position).get("showTime").toString();
            final String Fpurl = data.get(position).get("showPurl").toString();
            views.showContent.setText(Fcontent);
            views.showTime.setText(Ftime);
            //根据心情设置心情小图标
            switch (Ffeeling) {
                case "C":
                    views.showFeeling.setImageResource(R.drawable.cen_sadi);
                    break;
                case "M":
                    views.showFeeling.setImageResource(R.drawable.cen_happyi);
                    break;
                case "Y":
                    views.showFeeling.setImageResource(R.drawable.cen_angryi);
                    break;
                case "K":
                    views.showFeeling.setImageResource(R.drawable.cen_safei);
                    break;
                case "D":
                    views.showFeeling.setImageResource(R.drawable.cen_confusei);
                    break;
                case "W":
                    views.showFeeling.setImageResource(R.drawable.cen_noversi);
                    break;
                default:
                    break;
            }
            views.showTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ShowFeelingWords.this,ShowFeelingWordsin.class);
                    intent.putExtra("showContent", Fcontent);
                    intent.putExtra("showTime", Ftime);
                    intent.putExtra("showFeeling", Ffeeling);
                    intent.putExtra("Fpurl", Fpurl);
                    ShowFeelingWords.this.startActivity(intent);
                }
            });
            views.showContent.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ShowFeelingWords.this,ShowFeelingWordsin.class);
                    intent.putExtra("showContent", Fcontent);
                    intent.putExtra("showTime", Ftime);
                    intent.putExtra("showFeeling", Ffeeling);
                    intent.putExtra("Fpurl", Fpurl);
                    ShowFeelingWords.this.startActivity(intent);
                }
            });
            return convertView;
        }
        class MyView{
            public TextView showContent;
            public ImageView showFeeling;
            public TextView showTime;
        }
    }
    @SuppressWarnings("deprecation")
    private void doThread(){
            String url = "Service.asmx";
            String methodName="showFeelingWords";
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("Fpurl","");
            sportdialog.show();
            CallWebservice callWeb=new CallWebservice(handler);
            callWeb.doStart(url, methodName, params);
    }
}
