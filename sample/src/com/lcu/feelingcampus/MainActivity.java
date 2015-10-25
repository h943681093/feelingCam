package com.lcu.feelingcampus;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.astuetz.PagerSlidingTabStrip;
import com.astuetz.viewpager.extensions.sample.R;
import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaUser;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaAuthorizationListener.AuthorizationListener;
import com.baidu.frontia.api.FrontiaAuthorizationListener.UserInfoListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.lcu.Article.ArticleFragment;
import com.lcu.JavaTool.CallWebservice;
import com.lcu.MyCenter.MyCenter;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    private int ISin;
    private Button menuButton1, menuButton2, menuButton3, menuButton4;
    private FrontiaAuthorization mAuthorization;

    private String UserName = "";
    private String Usersex = "男/女";
    private String Userurl = "unknow";
    private String Userbirthday = "unknow";

    ConnectivityManager connManager;
    private String url = "Service.asmx";
    private String methodName="setUserFeeling";

    private ImageView mycenter;

    boolean isExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(1);

        //初始化颜色
        MainApplication app = (MainApplication) getApplicationContext();
        String userFeeling = app.getfeeling();
        if (userFeeling.equals("K")){
            //k冷静 紫色
            changeColor(Color.parseColor("#9b59b6"));
            System.out.println("k冷静");
        }else if (userFeeling.equals("C")){
            //c伤心 绿色
            changeColor(Color.parseColor("#2ECC71"));
            System.out.println("c伤心");
        }else if (userFeeling.equals("Y")){
            //y生气 蓝色
            changeColor(Color.parseColor("#3498db"));
            System.out.println("y生气");
        }else if (userFeeling.equals("M")){
            //m高兴 橙色
            changeColor(Color.parseColor("#e67e22"));
            System.out.println("m高兴");
        }else if (userFeeling.equals("D")){
            //d迷茫 红色
            changeColor(Color.parseColor("#E74C3C"));
            System.out.println("d迷茫");
        }else if (userFeeling.equals("W")){
            //w焦虑 淡绿色
                changeColor(Color.parseColor("#f39c12"));
            System.out.println("w焦虑");
        }

        Frontia.init(this.getApplicationContext(), Conf.APIKEY);
        mAuthorization = Frontia.getAuthorization();

        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(MainActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        //mycenter
        mycenter = (ImageView) findViewById(R.id.mycenter);
        mycenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogin();
            }
        });

        //菜单内的心情按钮
        FloatingActionButton buttonD = (FloatingActionButton) findViewById(R.id.feeling_D);
        buttonD.setTitle("迷茫 疑惑");
        buttonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(Color.parseColor("#E74C3C"));
                MainApplication app = (MainApplication) getApplicationContext();
                sentfeel("D");
                app.setfeeling("D");
                app.setisFromCamera(0);
                ChangeTitle();
            }
        });

        FloatingActionButton buttonW = (FloatingActionButton) findViewById(R.id.feeling_W);
        buttonW.setTitle("紧张 焦虑");
        buttonW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(Color.parseColor("#f39c12"));
                MainApplication app = (MainApplication) getApplicationContext();
                sentfeel("W");
                app.setfeeling("W");
                app.setisFromCamera(0);
                ChangeTitle();
            }
        });

        FloatingActionButton camera = (FloatingActionButton) findViewById(R.id.openCamera);
        camera.setTitle("自动识别");
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开启相机
                startActivity(new Intent(MainActivity.this, Face.class));

            }
        });

        FloatingActionButton feelingC = (FloatingActionButton) findViewById(R.id.feelingC);
        feelingC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(Color.parseColor("#2ecc71"));
                MainApplication app = (MainApplication) getApplicationContext();
                sentfeel("C");
                app.setfeeling("C");
                app.setisFromCamera(0);
                ChangeTitle();
            }
        });

        FloatingActionButton feelingM = (FloatingActionButton) findViewById(R.id.feelingM);
        feelingM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(Color.parseColor("#e67e22"));
                MainApplication app = (MainApplication) getApplicationContext();
                sentfeel("M");
                app.setfeeling("M");
                app.setisFromCamera(0);
                ChangeTitle();
            }
        });

        FloatingActionButton feelingY = (FloatingActionButton) findViewById(R.id.feelingY);
        feelingY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(Color.parseColor("#3498db"));
                MainApplication app = (MainApplication) getApplicationContext();
                sentfeel("Y");
                app.setfeeling("Y");
                app.setisFromCamera(0);
                ChangeTitle();
            }
        });

        FloatingActionButton feelingK = (FloatingActionButton) findViewById(R.id.feelingK);
        feelingK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(Color.parseColor("#9b59b6"));
                MainApplication app = (MainApplication) getApplicationContext();
                sentfeel("K");
                app.setfeeling("K");
                app.setisFromCamera(0);
                ChangeTitle();
            }
        });

    }

    public void sentfeel(String feel){
        Intent intent = new Intent("android.intent.action.CART_BROADCAST");
        intent.putExtra("mfeeling", feel);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);
    }

    /**
     * 百度、腾讯、人人第三方登录
     */

    public void isLogin(){

        MainApplication app = (MainApplication) getApplicationContext();
        int Isin = app.getISin();

        Log.i("", "" + ISin);
        if (Isin == 1){

            Intent intent = new Intent(MainActivity.this, MyCenter.class);
            this.startActivity(intent);

        } else {
            //百度第三方登录
            View view1 = getLayoutInflater().inflate(R.layout.enter,null);
            final Dialog dialog = new Dialog(MainActivity.this,R.style.transparentFrameWindowStyle);
            dialog.setContentView(view1,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));

            menuButton1 = (Button) dialog.findViewById(R.id.menubut1);
            menuButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 自动生成的方法存根
                    startBaidu();
                    dialog.dismiss();
                }
            });

            menuButton2 = (Button) dialog.findViewById(R.id.menubut2);
            menuButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 自动生成的方法存根
                    startQQZone();
                    dialog.dismiss();
                }
            });

            menuButton3 = (Button) dialog.findViewById(R.id.menubut3);
            menuButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 自动生成的方法存根
                    startRenren();
                    dialog.dismiss();
                }
            });

            menuButton4 = (Button) dialog.findViewById(R.id.menubut4);
            menuButton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 自动生成的方法存根
                    dialog.dismiss();
                }
            });

            Window window = dialog.getWindow();
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = getWindowManager().getDefaultDisplay().getHeight();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.onWindowAttributesChanged(wl);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }

    }

    protected void startBaidu() {

        ArrayList<String> scope = new ArrayList<String>();
        scope.add("basic");
        scope.add("netdisk");
        Log.i("baidu", "baidu");
        mAuthorization.authorize(this,
                FrontiaAuthorization.MediaType.BAIDU.toString(), scope,
                new AuthorizationListener() {
                    @Override
                    public void onSuccess(FrontiaUser result) {
                        Log.e("百度登陆测试", "onSuccess");
                        Toast.makeText(MainActivity.this, "登陆成功！",
                                Toast.LENGTH_SHORT).show();
                        // 获取信息 存储照片和名字
                        userinfo(MediaType.BAIDU.toString());
                        MainApplication app = (MainApplication) getApplicationContext();
                        app.setISin(1);

                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        Log.e("百度登陆测试", "errCode:" + errCode + ", errMsg:"
                                + errMsg);
                        Toast.makeText(MainActivity.this, "登陆失败！",
                                Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancel() {
                        Log.e("百度登陆测试", "onCancel");
                    }
                });
    }

    private void startQQZone() {
        mAuthorization.authorize(this,
                FrontiaAuthorization.MediaType.QZONE.toString(),
                new AuthorizationListener() {

                    @Override
                    public void onSuccess(FrontiaUser result) {
                        Log.e("qq登陆测试", "onSuccess");
                        Toast.makeText(MainActivity.this, "登陆成功！",
                                Toast.LENGTH_SHORT).show();
                        // 获取信息 存储照片和名字
                        userinfo(MediaType.QZONE.toString());
                        MainApplication app = (MainApplication) getApplicationContext();
                        app.setISin(1);

                    }

                    @Override
                    public void onFailure(int errorCode, String errorMessage) {
                        Log.e("qq登陆测试", "errCode:" + errorCode + ", errMsg:"
                                + errorMessage);
                        Toast.makeText(MainActivity.this, "登陆失败！",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("qq登陆测试", "onCancel");
                    }

                });
    }

    private void startRenren() {
        mAuthorization.authorize(this,
                FrontiaAuthorization.MediaType.RENREN.toString(),
                new AuthorizationListener() {

                    @Override
                    public void onSuccess(FrontiaUser result) {
                        Log.e("人人登陆测试", "onSuccess");
                        Toast.makeText(MainActivity.this, "登陆成功！",
                                Toast.LENGTH_SHORT).show();
                        // 获取信息 存储照片和名字
                        userinfo(MediaType.RENREN.toString());
                        MainApplication app = (MainApplication) getApplicationContext();
                        app.setISin(1);

                    }

                    @Override
                    public void onFailure(int errorCode, String errorMessage) {
                        Log.e("人人登陆测试", "errCode:" + errorCode + ", errMsg:"
                                + errorMessage);
                        Toast.makeText(MainActivity.this, "登陆失败！",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Log.e("人人登陆测试", "onCancel");
                    }

                });
    }

    private void userinfo(String accessToken) {
        mAuthorization.getUserInfo(accessToken, new UserInfoListener() {

            @Override
            public void onSuccess(FrontiaUser.FrontiaUserDetail result) {
                UserName = result.getName();
                Userurl = result.getHeadUrl();
                Usersex = result.getSex().toString();
                Userbirthday = result.getBirthday();

                if (Usersex.equals("MAN")) {
                    Usersex = "男";
                } else if (Usersex.equals("WOMAN")) {
                    Usersex = "女";
                } else {
                    Usersex = "未知";
                }
                setMenuInfo();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                Log.e("获取个人信息", "errCode:" + errCode + ", errMsg:" + errMsg);
            }
        });
    }

    private void setMenuInfo() {

        MainApplication app = (MainApplication) getApplicationContext();
        app.setuserurl(Userurl);
        app.setusername(UserName);
        app.setusersex(Usersex);
        app.setuserbirthday(Userbirthday);

        if (Usersex.equals("女")){

            Bitmap bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.loginwomen)).getBitmap();
            mycenter.setImageBitmap(bmp);
        }else{

            Bitmap bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.loginman)).getBitmap();
            mycenter.setImageBitmap(bmp);
        }

        //上传用户当前心情状态
        if (app.getisFromCamera()==1){
            doThread(app.getfeelingByCamera());
        }
    }

    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = ld;
        currentColor = newColor;
        ChangeTitle();
    }

    public void ChangeTitle() {
        MainApplication app = (MainApplication) getApplicationContext();

        if (app.getfeeling().equals("K")){
            //k冷静 紫色
            getSupportActionBar().setTitle("平静 放松 专注 出神");
        }else if (app.getfeeling().equals("C")){
            //c伤心 绿色
            getSupportActionBar().setTitle("伤感 郁闷 痛心 压抑");
        }else if (app.getfeeling().equals("Y")){
            //y生气 蓝色
            getSupportActionBar().setTitle("生气 失控 兴奋 宣泄");
        }else if (app.getfeeling().equals("M")){
            //m高兴 橙色
            getSupportActionBar().setTitle("开心 甜蜜 欢快 舒畅");
        }else if (app.getfeeling().equals("D")){
            //d迷茫 红色
            getSupportActionBar().setTitle("忧愁 疑惑 迷茫 无助");
        }else if (app.getfeeling().equals("W")){
            //w焦虑 淡绿色
            getSupportActionBar().setTitle("害怕 焦虑 紧张 激情");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"情绪路标", "心灵鸡汤", "心情聊大"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return new LinesFragment();
                case 1:
                    return new ArticleFragment();
                case 2:
                    return new MapFragment();
                default:
                    return null;
            }
        }
    }

    //开启上传心情的线程
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message m)
        {
            System.out.println("上传心情数据成功");
        };
    };

    @SuppressWarnings("deprecation")
    @SuppressLint({"HandlerLeak", "ShowToast" })
    private void doThread(String mfeeling) {
        System.out.println("开始连接webservice上传心情数据");

        try {
            connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {}

        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo != null &&  networkInfo.isAvailable()){
            MainApplication app = (MainApplication) getApplicationContext();
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            System.out.println(app.getusername()+app.getusersex()+app.getuserurl()+mfeeling+app.getLongitude()+app.getLatitude()+ts+app.getaddress());
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("userName",app.getusername());
            params.put("userSex",app.getusersex());
            params.put("userUrl",app.getuserurl());
            params.put("userFeeling",mfeeling);
            params.put("userLongitude",app.getLongitude());
            params.put("userLatitude",app.getLatitude());
            params.put("updataTime",ts);

            CallWebservice callWeb=new CallWebservice(handler);
            callWeb.doStart(url, methodName, params);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(MainActivity.this, "加载失败，请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication app = (MainApplication) getApplicationContext();
        if (app.getISin()==0){
            Bitmap bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.unlogin)).getBitmap();
            mycenter.setImageBitmap(bmp);
        }
    }

    //菜单键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {

            View view = getLayoutInflater().inflate(R.layout.menudialog, null);

            final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
            dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));


            menuButton1 = (Button) dialog.findViewById(R.id.menubut1);
            menuButton1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    MainApplication app = (MainApplication) getApplicationContext();

                    if (app.getISin()==1){

                        app.setISin(0);
                        //退出第三方账号
                        boolean result = mAuthorization.clearAllAuthorizationInfos();
                        if (result) {
                            Toast.makeText(MainActivity.this, "退出已登陆账号",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "退出失败",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "您还没有登陆",Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });
            menuButton2 = (Button) dialog.findViewById(R.id.menubut2);
            menuButton2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    tuichuLogin();
                    System.exit(0);
                    dialog.dismiss();
                }
            });
            menuButton3 = (Button) dialog.findViewById(R.id.menubut3);
            menuButton3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Window window = dialog.getWindow();
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = getWindowManager().getDefaultDisplay().getHeight();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.onWindowAttributesChanged(wl);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else if(keyCode == KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit(){
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            tuichuLogin();
            System.exit(0);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    public void tuichuLogin(){
        boolean result = mAuthorization.clearAllAuthorizationInfos();
        if (result) {
            Log.e("第三方登陆", "退出成功");
            MainApplication app = (MainApplication) getApplicationContext();
            app.setISin(0);
            Bitmap bmp = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.unlogin)).getBitmap();
            mycenter.setImageBitmap(bmp);
        } else {
            Log.e("第三方登陆", "退出失败");
        }
    }
}