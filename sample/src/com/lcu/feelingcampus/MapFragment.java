package com.lcu.feelingcampus;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.astuetz.viewpager.extensions.sample.R;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.lcu.FeelingChart.FeelingChart;
import com.lcu.FeelingChart.myFeelingChart;
import com.lcu.JavaTool.CallWebservice;
import com.zondy.mapgis.android.annotation.Annotation;
import com.zondy.mapgis.android.annotation.AnnotationView;
import com.zondy.mapgis.android.graphic.GraphicImage;
import com.zondy.mapgis.android.mapview.MapView;
import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.spatial.SpaProjection;
import com.zondy.mapgis.android.mapview.MapView.MapViewRenderContextListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MapFragment extends Fragment implements LocationListener,MapViewRenderContextListener {
    private View parentView;
    private MapView mapView;
    private String MapURL = Environment.getExternalStorageDirectory().getPath()+"/MapGIS/map/lcuall/lcu.mapx";
    private ImageButton locationButton;
    private int IsLocation = 0;
    LocalBroadcastManager broadcastManager;
    BroadcastReceiver mItemViewListClickReceiver;
    IntentFilter intentFilter;

    private LocationClient mLocationClient;
    private float zoomNumber = 14.360808f;
    private Dot centerDot = new Dot(1.2930726729227861E7,4335528.133396462);

    private ProgressDialog progressDialog = null;
    private Handler handler1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_map, container, false);

        System.out.println("onCreateView:MapFragment");

        mLocationClient = ((MainApplication)getActivity().getApplication()).mLocationClient;
        initLocation();

        locationButton = (ImageButton) parentView.findViewById(R.id.locationButton);
        locationButton.setImageDrawable(getResources().getDrawable(R.drawable.unlocation_selecter));

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsLocation == 0) {
                    System.out.println("setOnClickListener:IsLocation = 0");
                    mLocationClient.start();
                    locationButton.setImageDrawable(getResources().getDrawable(R.drawable.location_selecter));
                    IsLocation = 1;
                    setUserLocation();
                } else {
                    System.out.println("setOnClickListener:IsLocation = 1");
                    mLocationClient.stop();
                    locationButton.setImageDrawable(getResources().getDrawable(R.drawable.unlocation_selecter));
                    IsLocation = 0;
                    mapView.getGraphicLayer().removeAllGraphics();
                    mapView.refresh();
                }
            }
        });

        mapView = (MapView) parentView.findViewById(R.id.mapview);

        mapView.loadFromFile(MapURL);
        mapView.setRenderContextListener(this);
        mapView.setMyLocationButtonEnabled(false);
        mapView.setShowLogo(false);
        mapView.setZoomControlsEnabled(false);
        mapView.setShowScaleBar(false);
        mapView.setShowNorthArrow(false);
        mapView.setMapPanGesturesEnabled(false);
        mapView.setMapSlopeGesturesEnabled(false);


        mapView.setLongTapListener(new MapView.MapViewLongTapListener() {
            @Override
            public boolean mapViewLongTap(PointF pointF) {
                System.out.println(mapView.getZoom());
                System.out.println(mapView.getCenterPoint());
                mapView.zoomToCenter(centerDot, zoomNumber, true);
                return false;
            }
        });


            //接收广播
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");

        mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){

                final String feel =  intent.getExtras().getString("mfeeling");
                System.out.println("MAP:" + feel);
                mapView.getAnnotationLayer().removeAllAnnotations();
                doThread();
            }
        };

        mapView.setAnnotationListener(new MapView.MapViewAnnotationListener() {
            @Override
            public void mapViewClickAnnotation(MapView mapView, Annotation annotation) {
                //点击标注时响应
                System.out.println("点击标注时响应");
                // TODO: 2015/10/9 点击心情的标注时干什么
            }

            @Override
            public boolean mapViewWillShowAnnotationView(MapView mapView, AnnotationView annotationView) {
                //标注视图即将显示时响应(此标注可以弹出标注视图)
                System.out.println("标注视图即将显示时响应(此标注可以弹出标注视图)");
                return false;
            }

            @Override
            public boolean mapViewWillHideAnnotationView(MapView mapView, AnnotationView annotationView) {
                //标注视图即将隐藏时响应
                System.out.println("标注视图即将隐藏时响应");
                return false;
            }

            @Override
            public AnnotationView mapViewViewForAnnotation(MapView mapView, Annotation annotation) {
                //根据标注获取标注视图
                System.out.println("根据标注获取标注视图");
                return null;
            }

            @Override
            public void mapViewClickAnnotationView(MapView mapView, AnnotationView annotationView) {
                //点击标注视图时响应
                System.out.println("点击标注视图时响应");
            }
        });

        FloatingActionButton persennumber = (FloatingActionButton) parentView.findViewById(R.id.persennumber);
        persennumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), myFeelingChart.class));
            }
        });

        FloatingActionButton allnumber = (FloatingActionButton) parentView.findViewById(R.id.allnumber);
        allnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FeelingChart.class));
            }
        });
        return parentView;
    }

    //设置地图初始位置
    @Override
    public void mapViewRenderContextCreated() {
        System.out.println("mapView:RenderContextCreated");
        mapView.zoomToCenter(centerDot, zoomNumber, true);

        if(mapView.getAnnotationLayer().getAnnotationCount()==0){
            doThread();
        }else{
            mapView.getAnnotationLayer().removeAllAnnotations();
            doThread();
        }
        mapView.refresh();

    }

    @Override
    public void mapViewRenderContextDestroyed() {

    }

    //定位监听
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("MapFragment:onLocationChanged(位置改变了)");
        setUserLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        System.out.println("MapFragment:onStatusChanged");

    }

    @Override
    public void onProviderEnabled(String s) {
        System.out.println("MapFragment:onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        System.out.println("MapFragment:onProviderDisabled");
    }



    public void onResume() {
        super.onResume();
        System.out.println("onResume:MapFragment");
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }
    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onStop:MapFragment");
        broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
    }


    public void setUserLocation(){
        mapView.getGraphicLayer().removeAllGraphics();
        MainApplication app = (MainApplication)getActivity().getApplicationContext();
        if (app.getLatitude()!=0){
        Dot locationDot = new Dot(app.getLongitude(),app.getLatitude());
            SpaProjection spaProjection = new SpaProjection();
            spaProjection.lonLat2Mercator(locationDot);

            double X = locationDot.getX()+16760;
            double Y = locationDot.getY()-24840;
            locationDot.setX(X);
            locationDot.setY(Y);

            Bitmap bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.annotation45)).getBitmap();

            GraphicImage graphicImage = new GraphicImage(locationDot,bmp);
            mapView.getGraphicLayer().addGraphic(graphicImage);
            mapView.zoomToCenter(locationDot, zoomNumber, true);
        }
        mapView.refresh();
    }

    private void initLocation(){

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("gcj02");
        option.setScanSpan(2000);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(true);
        option.setEnableSimulateGps(false);
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);

    }

    //获取数据库返回结果
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message m)
        {
            ArrayList<String> datas = m.getData().getStringArrayList("data");
            System.out.println("相同心情数据数目：" + datas.size());
            drawAnnotation(datas);
            progressDialog.dismiss();
        };
    };


    private void drawAnnotation( ArrayList<String> datas){
        ArrayList<HashMap<String, Object>> mydata = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<datas.size();){
            HashMap<String, Object> map = new HashMap<String, Object>();
            if(datas.get(i)!=null){
                map.put("userLongitude", datas.get(i));
                i++;
                map.put("userLatitude",  datas.get(i));
                i++;
                map.put("userUrl",  datas.get(i));
                i++;
                map.put("userFeeling",  datas.get(i));
                i++;
                mydata.add(map);
            }
        }
        System.out.println("相同心情数据组数"+mydata.size());
        SpaProjection spaProjection = new SpaProjection();
        for (int i=0;i<mydata.size();i++){

            Bitmap  bmp = null;
            double Longitude = Double.parseDouble(mydata.get(i).get("userLongitude").toString());
            double Latitude = Double.parseDouble(mydata.get(i).get("userLatitude").toString());
            Dot dot = new Dot(Longitude,Latitude);
            spaProjection.lonLat2Mercator(dot);

//            double X = dot.getX()+16760;
//            double Y = dot.getY()-24840;
//            dot.setX(X);
//            dot.setY(Y);

            String feeling = mydata.get(i).get("userFeeling").toString();

            if (feeling.equals("C")){
                bmp = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.sadpoint)).getBitmap();

            }else if (feeling.equals("M")){

                bmp = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.happypoint)).getBitmap();

            }else if (feeling.equals("Y")){

                bmp = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.angrypoint)).getBitmap();

            }else if (feeling.equals("K")){

                bmp = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.safepoint)).getBitmap();

            }else if (feeling.equals("D")){

                bmp = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.confusepoint)).getBitmap();

            }else if (feeling.equals("W")){

                bmp = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.nervouspoint)).getBitmap();

            }

            String description = mydata.get(i).get("userUrl").toString();

            Annotation action = new Annotation(mydata.get(i).get("userFeeling").toString(), description , dot, bmp);
            mapView.getAnnotationLayer().addAnnotation(action);
        }
        mapView.refresh();
    }



    //开始连接数据库获取心情数据
    @SuppressWarnings("deprecation")
    @SuppressLint({ "HandlerLeak", "ShowToast" })
    private void doThread(){
        System.out.println("开始连接webservice");
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null &&  networkInfo.isAvailable()){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("请稍等...");
            progressDialog.setMessage("数据正在加载中......");
            progressDialog.setButton("取消", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // 删除消息队列中的消息，来停止计时器。
                    handler1.removeMessages(1);
                    progressDialog.setProgress(0);
                }
            });
            progressDialog.show();
            handler1 = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    super.handleMessage(msg);
                    progressDialog.dismiss();
                }};
            String url = "Service.asmx";
            String methodName="getAllFeelingByMine";
            HashMap<String, Object> params = new HashMap<String, Object>();
            MainApplication app = (MainApplication)getActivity().getApplicationContext();
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");

            Date date= null;
            long timeStemp = 0;
            try {
                Date dt = new Date();
                date = simpleDateFormat.parse(simpleDateFormat.format(dt));
                System.out.println("查询的日期："+simpleDateFormat.format(dt));
                timeStemp = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            params.put("MyFeeling", app.getfeeling());
            params.put("time", timeStemp);

            CallWebservice callWeb=new CallWebservice(handler);
            callWeb.doStart(url, methodName, params);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getActivity(), "加载失败，请检查网络", Toast.LENGTH_LONG).show();
        }
    }



}