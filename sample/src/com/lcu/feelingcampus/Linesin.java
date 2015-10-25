package com.lcu.feelingcampus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.sample.R;
import com.lcu.JavaTool.CommUtil;
import com.lcu.JavaTool.ImageDownloader;
import com.lcu.JavaTool.OnImageDownload;
import com.zondy.mapgis.android.annotation.Annotation;
import com.zondy.mapgis.android.annotation.AnnotationView;
import com.zondy.mapgis.android.mapview.MapView;
import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.android.mapview.MapView.MapViewRenderContextListener;

import java.util.HashMap;


public class Linesin extends Activity implements MapViewRenderContextListener {

    private HashMap<String, Object> searcgdata;
    private ImageView oneinLinePic,oneinLinePic2,oneinLinePic3,oneinLinePic4,oneinLinePic5;
    private TextView oneinLineContent,oneinLineContent1,oneinLineContent2,oneinLineContent3,oneinLineContent4,oneinLineContent5;
    private TextView title1,title2,title3,title4,title5;
    private ImageDownloader mDownloader;
    private MapView mapView;
    private String MapURL = Environment.getExternalStorageDirectory().getPath()+"/MapGIS/map/lcuall/lcu.mapx";
    private float zoomNumber = 13.7f;
    private Dot centerDot = new Dot(1.2930726729227861E7,4335528.133396462);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linesin);

        MainApplication app = (MainApplication)getApplicationContext();
        searcgdata = app.getSearchdata();

        init();

        System.out.println(searcgdata.size());

        /**
         * 	数据示范
         map.put("name"+i, data.get(i).get("name"));
         map.put("content" + i, data.get(i).get("content"));
         map.put("URL" + i, data.get(i).get("URL"));
         map.put("feeling" + i, data.get(i).get("feeling"));
         map.put("x" + i, data.get(i).get("x"));
         map.put("y" + i, data.get(i).get("y"));
         */

        try{
            title1.setText("第一站："+searcgdata.get("name1").toString());
            oneinLineContent.setText(searcgdata.get("content1").toString());
            setImange(searcgdata.get("URL1").toString(), oneinLinePic);
        }catch (NullPointerException e){}
        try{
            title2.setText("第二站：" + searcgdata.get("name2").toString());
            oneinLineContent2.setText(searcgdata.get("content2").toString());
            setImange(searcgdata.get("URL2").toString(), oneinLinePic2);
        }catch (NullPointerException e){}
        try{
            title3.setText("第三站："+searcgdata.get("name3").toString());
            oneinLineContent3.setText(searcgdata.get("content3").toString());
            setImange(searcgdata.get("URL3").toString(), oneinLinePic3);
        }catch (NullPointerException e){}
        try{
            title4.setText("第四站："+searcgdata.get("name4").toString());
            oneinLineContent4.setText(searcgdata.get("content4").toString());
            setImange(searcgdata.get("URL4").toString(), oneinLinePic4);
        }catch (NullPointerException e){}
        try{
            title5.setText("第五站："+searcgdata.get("name5").toString());
            oneinLineContent5.setText(searcgdata.get("content5").toString());
            setImange(searcgdata.get("URL5").toString(), oneinLinePic5);
        }catch (NullPointerException e){}


    }

    @Override
    public void mapViewRenderContextCreated() {
        mapView.zoomToCenter(centerDot, zoomNumber, true);
        try{
            System.out.println("1x:" + searcgdata.get("x1").toString());
            System.out.println("1y:" + searcgdata.get("y1").toString());
            addAnnotation(searcgdata.get("x1").toString(), searcgdata.get("y1").toString(), 1, searcgdata.get("name1").toString());
        }catch (NullPointerException e){}
        try{
            addAnnotation(searcgdata.get("x2").toString(), searcgdata.get("y2").toString(), 2, searcgdata.get("name2").toString());
        }catch (NullPointerException e){}
        try{
            addAnnotation(searcgdata.get("x3").toString(), searcgdata.get("y3").toString(), 3, searcgdata.get("name3").toString());
        }catch (NullPointerException e){}
        try{
            addAnnotation(searcgdata.get("x4").toString(), searcgdata.get("y4").toString(), 4, searcgdata.get("name4").toString());
        }catch (NullPointerException e){}
        try{
            addAnnotation(searcgdata.get("x5").toString(), searcgdata.get("y5").toString(), 5, searcgdata.get("name5").toString());
        }catch (NullPointerException e){}
        mapView.refresh();
    }

    @Override
    public void mapViewRenderContextDestroyed() {

    }


    public void init(){

        mapView = (MapView)findViewById(R.id.lineinmap);

        mapView.loadFromFile(MapURL);
        mapView.setRenderContextListener(Linesin.this);
        mapView.setMyLocationButtonEnabled(false);
        mapView.setShowLogo(false);
        mapView.setShowScaleBar(false);
        mapView.setShowNorthArrow(false);

        mapView.setAnnotationListener(new MapView.MapViewAnnotationListener() {
            @Override
            public void mapViewClickAnnotation(MapView mapView, Annotation annotation) {
                Toast.makeText(Linesin.this, annotation.getDescription(),Toast.LENGTH_SHORT);
            }

            @Override
            public boolean mapViewWillShowAnnotationView(MapView mapView, AnnotationView annotationView) {
                return false;
            }

            @Override
            public boolean mapViewWillHideAnnotationView(MapView mapView, AnnotationView annotationView) {
                return false;
            }

            @Override
            public AnnotationView mapViewViewForAnnotation(MapView mapView, Annotation annotation) {
                return null;
            }

            @Override
            public void mapViewClickAnnotationView(MapView mapView, AnnotationView annotationView) {

            }
        });

        oneinLineContent = (TextView) findViewById(R.id.oneinLineContent);
        oneinLineContent2 = (TextView) findViewById(R.id.oneinLineContent2);
        oneinLineContent3 = (TextView) findViewById(R.id.oneinLineContent3);
        oneinLineContent4 = (TextView) findViewById(R.id.oneinLineContent4);
        oneinLineContent5 = (TextView) findViewById(R.id.oneinLineContent5);

        oneinLinePic = (ImageView) findViewById(R.id.oneinLinePic);
        oneinLinePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movetoCenter(searcgdata.get("x1").toString(), searcgdata.get("y1").toString());
            }
        });
        oneinLinePic2 = (ImageView) findViewById(R.id.oneinLinePic2);
        oneinLinePic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movetoCenter(searcgdata.get("x2").toString(), searcgdata.get("y2").toString());
            }
        });
        oneinLinePic3 = (ImageView) findViewById(R.id.oneinLinePic3);
        oneinLinePic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movetoCenter(searcgdata.get("x3").toString(), searcgdata.get("y3").toString());
            }
        });
        oneinLinePic4 = (ImageView) findViewById(R.id.oneinLinePic4);
        oneinLinePic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movetoCenter(searcgdata.get("x4").toString(), searcgdata.get("y4").toString());
            }
        });
        oneinLinePic5 = (ImageView) findViewById(R.id.oneinLinePic5);
        oneinLinePic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movetoCenter(searcgdata.get("x5").toString(), searcgdata.get("y5").toString());
            }
        });

        title1 = (TextView) findViewById(R.id.title1);
        title2 = (TextView) findViewById(R.id.title2);
        title3 = (TextView) findViewById(R.id.title3);
        title4 = (TextView) findViewById(R.id.title4);
        title5 = (TextView) findViewById(R.id.title5);
    }

    public void movetoCenter(String xin,String yin){

        double x = Double.parseDouble(xin);
        double y = Double.parseDouble(yin);
        Dot dot = new Dot(x,y);
        double X = dot.getX()+16500;
        double Y = dot.getY()-15950;
        dot.setX(X);
        dot.setY(Y);

        mapView.zoomToCenter(dot,14,true);

    }

    public void setImange(String URL, final ImageView sWhere){

        if (mDownloader == null) {mDownloader = new ImageDownloader();}

        if(!URL.contains(".")){
            URL= CommUtil.imageurl+CommUtil.image;
        }
        else{
            URL=CommUtil.imageurl+URL;
        }

        sWhere.setTag(URL);

        sWhere.setImageResource(R.drawable.photo_default_img);

        mDownloader.imageDownload(URL, sWhere, "/lcu", Linesin.this, new OnImageDownload() {
            @Override
            public void onDownloadSucc(Bitmap bitmap, String c_url, ImageView mimageView) {

                ImageView imageView = (ImageView) sWhere.findViewWithTag(c_url);

                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag("");
                }
            }
        });


    }

    public  void addAnnotation(String xin,String yin,int i,String name){
        Bitmap  bmp = null;
        double x = Double.parseDouble(xin);
        double y = Double.parseDouble(yin);
        Dot dot = new Dot(x,y);
            double X = dot.getX()+16500;
            double Y = dot.getY()-15950;
            dot.setX(X);
            dot.setY(Y);

        if (i==1){
            bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.pointa)).getBitmap();

        }else if (i==2){

            bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.pointb)).getBitmap();

        }else if (i==3){

            bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.pointc)).getBitmap();

        }else if (i==4){

            bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.pointd)).getBitmap();

        }else if (i==5){

            bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.pointe)).getBitmap();

        }

        Annotation action = new Annotation("point"+i, "第"+i+"个站点："+name , dot, bmp);
        mapView.getAnnotationLayer().addAnnotation(action);

    }

}