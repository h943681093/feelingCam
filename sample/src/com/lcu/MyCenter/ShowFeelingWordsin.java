package com.lcu.MyCenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.sample.R;
import com.lcu.JavaTool.CallWebservice;
import com.lcu.feelingcampus.MainApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
public class ShowFeelingWordsin extends Activity{
    private TextView ftime,fcontent,Replyin;
    private ImageView feeling;
    private MainApplication app ;
    private String hisFeeling,hisWords;
    private EditText wordsEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showfeelingwordsin);
        app = (MainApplication) getApplicationContext();

        ftime = (TextView) findViewById(R.id.showTimein);
        feeling=  (ImageView) findViewById(R.id.showFeelingin);
        fcontent= (TextView) findViewById(R.id.showContentin);
        wordsEdit = (EditText)findViewById(R.id.showEditin);
        Replyin = (TextView)findViewById(R.id.showReplyin);

        final String showContent =  getIntent().getExtras().getString("showContent");
        final String showTime =  getIntent().getExtras().getString("showTime");
        final String showFeeling =  getIntent().getExtras().getString("showFeeling");
        final String Fpurl =  getIntent().getExtras().getString("Fpurl");
        ftime.setText(showTime);
        fcontent.setText(showContent);
        //根据心情设置心情小图标
        switch (showFeeling) {
            case "C":
                feeling.setImageResource(R.drawable.cen_sadi);
                break;
            case "M":
                feeling.setImageResource(R.drawable.cen_happyi);
                break;
            case "Y":
                feeling.setImageResource(R.drawable.cen_angryi);
                break;
            case "K":
                feeling.setImageResource(R.drawable.cen_safei);
                break;
            case "D":
                feeling.setImageResource(R.drawable.cen_confusei);
                break;
            case "W":
                feeling.setImageResource(R.drawable.cen_noversi);
                break;
            default:
                break;
        }
        //发送回复点击事件
        Replyin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hisWords = wordsEdit.getText().toString();
                if (hisWords.length() == 0) {
                    Toast.makeText(ShowFeelingWordsin.this, "请输入信息", Toast.LENGTH_SHORT).show();
                }else{
                //获取用户信息
                hisFeeling = app.getfeeling();
                //获取当前时间
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String sendTime = formatter.format(curDate);
                //将信息发送到数据库
                CallWebservice callWeb = new CallWebservice(myhandler);
                String url = "Service.asmx";
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("onlyUrl", Fpurl);
                params.put("Ffeeling", hisFeeling);
                params.put("sendTime", sendTime);
                params.put("hisWords", hisWords);
                params.put("isSee", "0");
                String methodName = "sendHisWords";
                callWeb.doStart(url, methodName, params);
            }
            }
        });
    }
    // 开始线程，显示发布是否成功
    @SuppressLint("ShowToast")
    private Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message m)
        {
            Boolean isFalse = m.getData().getBoolean("isFalse");
            if(isFalse){
                Toast.makeText(ShowFeelingWordsin.this, "发布成功", Toast.LENGTH_SHORT).show();
                ShowFeelingWordsin.this.finish();
            }else{
                Toast.makeText(ShowFeelingWordsin.this, "发布失败", Toast.LENGTH_LONG).show();
            }
        };
    };
}
