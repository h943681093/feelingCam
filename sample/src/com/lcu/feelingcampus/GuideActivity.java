package com.lcu.feelingcampus;


import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.astuetz.viewpager.extensions.sample.R;
import com.lcu.JavaTool.GuideViewPagerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {
    private ViewPager vp;
    private GuideViewPagerAdapter vpAdapter;
    private List<View> views;

    public static String DATAPATH = Environment.getExternalStorageDirectory().getPath() + "/MapGIS/";

    private void initPaths() {

        createDir(DATAPATH);
        createDir(DATAPATH + "map/lcupoi");
        createDir(DATAPATH + "map/lcuall");
    }


    private void createDir(String foldername) {
        File dir = new File(foldername);
        if (!dir.exists()) {
            System.out.println("创建文件目录："+foldername);
            dir.mkdirs();
        }
    }
    /**
     * 从工程资源里面复制相关文件到存储卡上
     */
    public void copyAssetsFileToSdcard(String filename, String desFolder) {
        InputStream inputFile = null;
        OutputStream outputFile = null;
        try {
            String desFileName = DATAPATH + desFolder + "/" + filename;
            File desFile = new File(desFileName);
            if (!desFile.exists()) {
                inputFile = getAssets().open(filename);
                outputFile = new FileOutputStream(desFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputFile.read(buffer)) > 0) {
                    outputFile.write(buffer, 0, length);
                }
                outputFile.flush();
                outputFile.close();
                inputFile.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);


        initPaths();
        copyAssetsFileToSdcard("lcupoi.db", "map/lcupoi");
        copyAssetsFileToSdcard("lcupoi.xml", "map/lcupoi");
        copyAssetsFileToSdcard("lcuall.db", "map/lcuall");
        copyAssetsFileToSdcard("lcuall.xml", "map/lcuall");
        copyAssetsFileToSdcard("lcu.mapx", "map/lcuall");
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        // 初始化引导图片列表
        views.add(inflater.inflate(R.layout.one_main, null));
        views.add(inflater.inflate(R.layout.two_main, null));
        views.add(inflater.inflate(R.layout.three_main, null));
        views.add(inflater.inflate(R.layout.four_main, null));

        // 初始化Adapter
        vpAdapter = new GuideViewPagerAdapter(views, this);

        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}