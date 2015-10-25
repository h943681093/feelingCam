package com.lcu.MyCenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.RefreshableListView;
import com.astuetz.viewpager.extensions.sample.R;
import com.baidu.frontia.Frontia;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.lcu.JavaTool.CallWebservice;
import com.lcu.JavaTool.ImageDownloader;
import com.lcu.JavaTool.OnImageDownload;
import com.lcu.feelingcampus.Conf;
import com.lcu.feelingcampus.MainApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class MyCenter extends Activity{
    private String UserName,UserSex,UserBirthDay,UserPictuer,mfeeling;
    private TextView cenName,cenSex,cenAge,pouroutFeeling,browseFeeling;
    private ImageView cenPicture,cenFeeling,cenHeadBack,cenbg;
    private ImageDownloader mDownloader;
    private FrontiaAuthorization mAuthorization;
    private  TextView qsxq,cenLlxq;
    private ImageButton AllQuit;
    private RefreshableListView lv;

    private SpotsDialog sportdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycenter);
        sportdialog= new SpotsDialog(this,"正在加载...", R.style.Custom);
        Frontia.init(this.getApplicationContext(), Conf.APIKEY);
        mAuthorization = Frontia.getAuthorization();
        //获取个人信息
        MainApplication app = (MainApplication) getApplicationContext();
        UserName = app.getusername();
        UserSex = app.getusersex();
        UserBirthDay = app.getuserbirthday();
        UserPictuer = app.getuserurl();
        mfeeling = app.getfeeling();
        //根据生日计算年龄
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        String nowTime=sdf.format(new java.util.Date());
        int a =Integer.parseInt(UserBirthDay);
        int b = 2015;
        String UserAge = "20";
        if (a!=0){
             b = Integer.parseInt(nowTime.split("-")[0])-a;
             UserAge = Integer.toString(b);
        }else {
            UserAge = "22";
        }
        cenName =(TextView)findViewById(R.id.cenName);
        cenSex =(TextView)findViewById(R.id.cenSex);
        cenAge =(TextView)findViewById(R.id.cenAge);
        cenPicture =(ImageView)findViewById(R.id.cenPicture);
        cenFeeling = (ImageView)findViewById(R.id.cenFeeling);
        cenbg = (ImageView)findViewById(R.id.cen_bg);
        //倾诉心情和浏览心情
        pouroutFeeling = (TextView)findViewById(R.id.pouroutFeeling);
        browseFeeling = (TextView)findViewById(R.id.browseFeeling);
        cenName.setText(UserName);
        cenSex.setText(UserSex);
        cenAge.setText(UserAge);

        //下载头像
        cenPicture.setTag(UserPictuer);
        if (mDownloader == null) {
            mDownloader = new ImageDownloader();
        }
        cenPicture.setImageResource(R.drawable.sy);
        mDownloader.imageDownload(UserPictuer, cenPicture, "/lcu",
                MyCenter.this, new OnImageDownload() {
                    @Override
                    public void onDownloadSucc(Bitmap bitmap, String c_url,
                                               ImageView mimageView) {

                        ImageView imageView = (ImageView) cenPicture
                                .findViewWithTag(c_url);

                        if (imageView != null) {
                            imageView.setImageBitmap(bitmap);
                            imageView.setTag("");
                        }
                    }
                });
        //根据心情设置心情小图标
        switch (mfeeling) {
            case "C":
                cenFeeling.setImageResource(R.drawable.cen_sad);
                cenbg.setImageResource(R.drawable.mycentersad);
                break;
            case "M":
                cenFeeling.setImageResource(R.drawable.cen_happy);
                cenbg.setImageResource(R.drawable.mycenterhappy);
                break;
            case "Y":
                cenFeeling.setImageResource(R.drawable.cen_angry);
                cenbg.setImageResource(R.drawable.mycenterangry);
                break;
            case "K":
                cenFeeling.setImageResource(R.drawable.cen_safe);
                cenbg.setImageResource(R.drawable.mycenterclam);
                break;
            case "W":
                cenFeeling.setImageResource(R.drawable.cen_confuse);
                cenbg.setImageResource(R.drawable.mycenterconfuse);
                break;
            case "D":
                cenFeeling.setImageResource(R.drawable.cen_novers);
                cenbg.setImageResource(R.drawable.mycenternovers);
                break;
            default:
                break;
        }
        AllQuit = (ImageButton)findViewById(R.id.cenAllQuit);
        AllQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllLogout();
            }
        });
        //倾诉心情点击事件
        qsxq = (TextView)findViewById(R.id .pouroutFeeling);
        qsxq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MyCenter.this,SendFeelingWords.class);
                startActivity(intent);
//
            }
        });
        //心情园点击事件
        cenLlxq = (TextView)findViewById(R.id.browseFeeling);
        cenLlxq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MyCenter.this,ShowFeelingWords.class);
                startActivity(intent);
            }
        });
        lv = (RefreshableListView)findViewById(R.id.cenLv);
        lv.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
            @Override
            public void onRefresh(RefreshableListView listView) {
                new NewDataTask().execute();
            }
        });

        syswords();
        doThread();
    }
    //个人中心系统提示
    private void syswords(){
        MainApplication app = (MainApplication) getApplicationContext();
      String  feeling = app.getfeeling();

        String url = "Service.asmx";
        String methodName = "getSysWords";
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("MyFeeling", feeling);
        CallWebservice callWeb = new CallWebservice(myhandler);
        callWeb.doStart(url, methodName, params);

    }
    //
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
        TextView upword = (TextView)findViewById(R.id.cenSysWords);
        int c =  (int) (Math.random() * words.length);
        upword.setText(words[c]);
    }
    //退出登录
    private void startAllLogout() {
        boolean result = mAuthorization.clearAllAuthorizationInfos();
        if (result) {
            MainApplication app = (MainApplication) getApplicationContext();
            app.setISin(0);
            Toast.makeText(MyCenter.this, "所有登录退出成功", Toast.LENGTH_SHORT)
                    .show();
            MyCenter.this.finish();
        } else {
            Toast.makeText(MyCenter.this, "所有登录退出失败", Toast.LENGTH_SHORT)
                    .show();
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
            MainApplication app = (MainApplication) getApplicationContext();
            String onlyUrl = app.getuserurl();
            String url = "Service.asmx";
            String methodName="setIsin";
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("onlyUrl",onlyUrl);
            CallWebservice callWeb=new CallWebservice(null);
            callWeb.doStart(url, methodName, params);
        };
    };
    //显示webservice结果
    private void doResult( ArrayList<String> datas){
        ArrayList<HashMap<String, Object>> mydata = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<datas.size();){
            HashMap<String, Object> map = new HashMap<String, Object>();
            if(datas.get(i)!=null){
                map.put("hisFeeling", datas.get(i));
                i++;
                map.put("sendTime", datas.get(i));
                i++;
                map.put("hisWords",  datas.get(i));
                i++;
                mydata.add(map);
            }
        }
        LeagueAdapter adapter=new LeagueAdapter(mydata);
        lv.setAdapter(adapter);
         sportdialog.dismiss();
    }
    class LeagueAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> data;
        private MyView views;
        public LeagueAdapter(ArrayList<HashMap<String, Object>>  data){
            this.data=data;
        }
        @Override
        public int getCount() {
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.center_listview_item, null);
                views=new MyView();
                views.hisFeeling=(ImageView)convertView.findViewById(R.id.cenListFeeling);
                views.sendTime=(TextView)convertView.findViewById(R.id.cenListTime);
                views.hisWords=(TextView)convertView.findViewById(R.id.cenListContent);
                convertView.setTag(views);
            }else{
                views=(MyView)convertView.getTag();
            }
            final String Ffeeling = data.get(position).get("hisFeeling").toString();
            final String Ftime = data.get(position).get("sendTime").toString();
            final String Fcontent = data.get(position).get("hisWords").toString();

            views.sendTime.setText(Ftime);
            views.hisWords.setText(Fcontent);
            //根据心情设置心情小图标
            switch (Ffeeling) {
                case "C":
                    views.hisFeeling.setImageResource(R.drawable.cen_sadi);
                    break;
                case "M":
                    views.hisFeeling.setImageResource(R.drawable.cen_happyi);
                    break;
                case "Y":
                    views.hisFeeling.setImageResource(R.drawable.cen_angryi);
                    break;
                case "K":
                    views.hisFeeling.setImageResource(R.drawable.cen_safei);
                    break;
                case "D":
                    views.hisFeeling.setImageResource(R.drawable.cen_confusei);
                    break;
                case "W":
                    views.hisFeeling.setImageResource(R.drawable.cen_noversi);
                    break;
                default:
                    break;
            }
            return convertView;
        }
        class MyView{
            public TextView hisWords;
            public ImageView hisFeeling;
            public TextView sendTime;
        }
    }
    //请求数据库
    private void doThread(){
        MainApplication app = (MainApplication) getApplicationContext();
        String onlyUrl = app.getuserurl();
        String url = "Service.asmx";
        String methodName="getInfos";
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("onlyUrl", onlyUrl);
        CallWebservice callWeb=new CallWebservice(handler);
        callWeb.doStart(url, methodName, params);
        sportdialog.show();
    }
    //下拉刷新事件
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
            syswords();
            lv.completeRefreshing();
            super.onPostExecute(result);
        }
    }

}
