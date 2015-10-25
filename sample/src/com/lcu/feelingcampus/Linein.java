package com.lcu.feelingcampus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.R;
import com.lcu.JavaTool.CommUtil;
import com.lcu.JavaTool.ImageDownloader;
import com.lcu.JavaTool.OnImageDownload;
import com.zondy.mapgis.android.annotation.Annotation;
import com.zondy.mapgis.android.mapview.MapView;
import com.zondy.mapgis.geometry.Dot;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/10/11.
 */
public class Linein extends Activity implements MapView.MapViewRenderContextListener {
    private HashMap<String, Object> searcgdata;
    private ImageView oneinLinePic;
    private TextView oneinLineContent;
    private TextView title1;
    private ImageDownloader mDownloader;
    private MapView mapView;
    private String MapURL = Environment.getExternalStorageDirectory().getPath()+"/MapGIS/map/lcuall/lcu.mapx";
    private float zoomNumber = 14.360808f;
    private Dot centerDot = new Dot(1.2930726729227861E7,4335528.133396462);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linein);

        MainApplication app = (MainApplication)getApplicationContext();
        searcgdata = app.getSearchdata();
        init();
        title1.setText("推荐地点：" + searcgdata.get("name0").toString());
        oneinLineContent.setText(searcgdata.get("content0").toString());
        setImange(searcgdata.get("URL0").toString(), oneinLinePic);


    }

    @Override
    public void mapViewRenderContextCreated() {
        mapView.zoomToCenter(centerDot, zoomNumber, true);
        addAnnotation(searcgdata.get("x0").toString(), searcgdata.get("y0").toString(), 1);
        mapView.refresh();
    }

    @Override
    public void mapViewRenderContextDestroyed() {

    }

    public void init(){

        mapView = (MapView)findViewById(R.id.lineinmap2);

        mapView.loadFromFile(MapURL);
        mapView.setRenderContextListener(Linein.this);
        mapView.setMyLocationButtonEnabled(false);
        mapView.setShowLogo(false);
        mapView.setShowScaleBar(false);
        mapView.setShowNorthArrow(false);

        oneinLineContent = (TextView) findViewById(R.id.oneinLineContentone);
        oneinLinePic = (ImageView) findViewById(R.id.oneinLinePicone);
        title1 = (TextView) findViewById(R.id.titleone);
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

        mDownloader.imageDownload(URL, sWhere, "/lcu", Linein.this, new OnImageDownload() {
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

    public  void addAnnotation(String xin,String yin,int i){
        // TODO: 2015/10/11 坐标位置有偏移 不知道是数据问题还是什么问题
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

        Annotation action = new Annotation("point"+i, "" , dot, bmp);
        mapView.getAnnotationLayer().addAnnotation(action);

    }

}
