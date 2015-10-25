package com.lcu.MyCenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.sample.R;
import com.lcu.JavaTool.CallWebservice;
import com.lcu.feelingcampus.MainApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class SendFeelingWords extends Activity{
    private EditText sendEt;
    private Button sendButton;
    private ImageButton backButton;
    private String UserPictuer,mfeeling,sendContent;
    private MainApplication app ;
    private TextView writeTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendfeelingwords);
        //设置书写的时间 2015-10-09
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String nowTime = formatter.format(curDate);
        String[] a = nowTime.split("-");
        writeTime = (TextView)findViewById(R.id.writeTime);
        writeTime.setText(a[0]+" "+"年"+" "+a[1]+" "+"月"+" "+a[2]+" "+"日");

        app = (MainApplication) getApplicationContext();
        sendEt = (EditText)findViewById(R.id.sendEditText);
        backButton = (ImageButton)findViewById(R.id.sendheadback);
        sendButton = (Button)findViewById(R.id.sendWords);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendFeelingWords.this.finish();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入心情文字
                sendContent = sendEt.getText().toString();
                if (sendContent.length()==0) {
                    Toast.makeText(SendFeelingWords.this, "请输入信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                    //获取用户信息
                    UserPictuer = app.getuserurl();
                    mfeeling = app.getfeeling();
                    //获取当前时间
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String sendTime = formatter.format(curDate);
                    //将信息发送到数据库
                    CallWebservice callWeb = new CallWebservice(myhandler);
                    String url = "Service.asmx";
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("Fcontent", sendContent);
                    params.put("Ffeeling", mfeeling);
                    params.put("Ftime", sendTime);
                    params.put("Fpurl", UserPictuer);
                    String methodName = "sendFeelingWords";
                    callWeb.doStart(url, methodName, params);
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
                Toast.makeText(SendFeelingWords.this, "发布成功", Toast.LENGTH_SHORT).show();
                SendFeelingWords.this.finish();
            }else{
                Toast.makeText(SendFeelingWords.this, "发布失败", Toast.LENGTH_LONG).show();
            }
        };
    };
}
