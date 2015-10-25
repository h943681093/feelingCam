package com.lcu.feelingcampus;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.lcu.JavaTool.CommUtil;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/10/2.
 */
public class MainApplication extends Application {

    public int IScenterToMain=0;
    public static int ISin = 0;
    public String username = "请登录";
    public String usersex = "男/女";
    public String userbirthday = "";
    public String userurl = CommUtil.imageurl + "1.jpg";
    public String feeling = "K";
    public String feelingByCamera = "K";
    public int isFromCamera = 0;

    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    public Vibrator mVibrator;
    public double Latitude;
    public double Longitude;
    public String address;
    public HashMap<String, Object> Searchdata;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        FrontiaApplication.initFrontiaApplication(getApplicationContext());
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    }

    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            System.out.println("开始定位了:X-" + location.getLongitude()
                    + "Y-" + location.getLatitude()
                    + "地址：" + location.getLocationDescribe());
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
            address = location.getLocationDescribe();
        }


    }

    public double getLatitude(){
        return Latitude;
    }

    public double getLongitude(){
        return Longitude;
    }

    public String getaddress(){
        return address;
    }


    public static void isintozero(){
        ISin = 0;
    }

    /**
     *
     * 获取CenterActivity 到  MainActivity的特征值 centerToMain
     *
     * @param
     */

    public int getIScenterToMain() {
        return IScenterToMain;
    }

    public void setIScenterToMain(int IScenterToMain) {
        this.IScenterToMain = IScenterToMain;
        Log.e("全局变量", "" + IScenterToMain);
    }

    /**
     *
     * 是否登陆 getISin getISin
     *
     * @param
     */

    public int getISin() {
        return ISin;
    }

    public void setISin(int ISin) {
        this.ISin = ISin;
        Log.e("全局变量", "" + ISin);
    }

    /**
     * 获得用户名称
     *
     * @return
     */
    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
        Log.e("全局变量username", username);
    }

    /**
     * 获得用户性别
     *
     * @return
     */
    public String getusersex() {
        return usersex;
    }

    public void setusersex(String usersex) {
        this.usersex = usersex;
        Log.e("全局变量usersex", usersex);
    }

    /**
     * 获得用户生日
     *
     * @return
     */
    public String getuserbirthday() {
        return userbirthday;
    }

    public void setuserbirthday(String userbirthday) {
        this.userbirthday = userbirthday;
        Log.e("全局变量userbirthday", userbirthday);
    }

    /**
     *
     * 传递头像url getuserurl setuserurl
     *
     * @param
     */

    public String getuserurl() {
        return userurl;
    }

    public void setuserurl(String userurl) {
        this.userurl = userurl;
        Log.e("全局变量userurl", userurl);
    }



    /**
     * 存储用户心情
     * @return
     */
    public String getfeeling() {
        return feeling;
    }

    public void setfeeling(String feeling) {
        this.feeling = feeling;
        Log.e("全局变量feeling", feeling);
        System.out.println("全局变量feeling：" + feeling);
    }

    /**
     * 用户心情是否来自相机自动判断
     * @return
     */
    public int getisFromCamera() {
        return isFromCamera;
    }

    public void setisFromCamera(int isFromCamera) {
        this.isFromCamera = isFromCamera;
        Log.e("全局变量isFromCamera", ""+isFromCamera);
        System.out.println("全局变量isFromCamera：" + isFromCamera);
    }


    /**
     * 存储用户心情
     * @return
     */
    public String getfeelingByCamera() {
        return feelingByCamera;
    }

    public void setfeelingByCamera(String feelingByCamera) {
        this.feelingByCamera = feelingByCamera;
        Log.e("全局变量feelingByCamera", feelingByCamera);
        System.out.println("全局变量feelingByCamera：" + feelingByCamera);
    }


    /**
     * 存储随机出的推荐信息
     * @return
     */
    public HashMap<String, Object> getSearchdata() {
        return Searchdata;
    }

    public void setSearchdata(HashMap<String, Object> Searchdata) {
        this.Searchdata = Searchdata;
        System.out.println("全局变量Searchdata：" + Searchdata);
    }

}
