package com.lcu.feelingcampus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.astuetz.viewpager.extensions.sample.R;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hymusic.hymusicsdk.InitListener;
import com.hymusic.hymusicsdk.senseface.ExpressionListener;
import com.hymusic.hymusicsdk.senseface.Expressiondetect;
import com.hymusic.hymusicsdk.util.SDKUtil;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

public class Face extends Activity {

	private Expressiondetect expressdetect;
	private LocationClient mLocationClient;
	Handler mainhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

				case SDKUtil.VIEWFINSIH:

					Toast.makeText(Face.this,"请求失败，请重新尝试",Toast.LENGTH_LONG).show();
					StartJudge();

					break;
				case 1901:

					System.out.println("JSON字符串：" + msg.obj);
					String jsonString1901 = (String) msg.obj;
					try {
						JSONObject jObj = new JSONObject(jsonString1901);

						if (jObj.get("retcode").equals("-10099")){

							Toast.makeText(Face.this,"表情识别失败，请注意姿势哦",Toast.LENGTH_LONG).show();

							AlertDialog.Builder builder = new AlertDialog.Builder(Face.this);
							builder.setMessage("是否重新尝试识别情绪？");
							builder.setTitle("识别出错");
							builder.setPositiveButton("进入应用", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									MainApplication app = (MainApplication) getApplicationContext();
									//默认为开心
									app.setfeeling("K");
									startActivity(new Intent(Face.this, MainActivity.class));
									Face.this.finish();
								}
							});
							builder.setNegativeButton("重新识别", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									StartJudge();
								}
							});
							builder.create().show();


						}else if (jObj.get("retcode").equals("0")){

							JSONObject fellingjson = jObj.getJSONObject("emotions");

							double K = Double.parseDouble(fellingjson.get("K").toString());//k冷静
							double C = Double.parseDouble(fellingjson.get("C").toString());//c伤心
							double Y = Double.parseDouble(fellingjson.get("Y").toString());//y生气
							double M = Double.parseDouble(fellingjson.get("M").toString());//m高兴
							double D = Double.parseDouble(fellingjson.get("D").toString());//d迷茫
							double W = Double.parseDouble(fellingjson.get("W").toString());//w焦虑


							HashMap<String, String> mymap = new HashMap<String, String>();
							mymap.put(fellingjson.get("K").toString(), "K");
							mymap.put(fellingjson.get("C").toString(), "C");
							mymap.put(fellingjson.get("Y").toString(), "Y");
							mymap.put(fellingjson.get("M").toString(), "M");
							mymap.put(fellingjson.get("D").toString(), "D");
							mymap.put(fellingjson.get("W").toString(), "W");

							double[] ismax = {K, C, Y, M, D, W};
							Arrays.sort(ismax);
							String max = mymap.get(String.valueOf(ismax[5]));

							MainApplication app = (MainApplication) getApplicationContext();

							app.setfeeling(max);
							app.setfeelingByCamera(max);
							app.setisFromCamera(1);

							startActivity(new Intent(Face.this, MainActivity.class));

							if (max.equals("K")){
								//k冷静
								Toast.makeText(Face.this,"系统感觉您现在十分冷静",Toast.LENGTH_LONG).show();
							}else if (max.equals("C")){
								//c伤心
								Toast.makeText(Face.this,"系统感觉您现在心情有点伤心",Toast.LENGTH_LONG).show();
							}else if (max.equals("Y")){
								//y生气
								Toast.makeText(Face.this,"系统感觉您可能有点生气",Toast.LENGTH_LONG).show();
							}else if (max.equals("M")){
								//m高兴
								Toast.makeText(Face.this,"HAPPY!HAPPY!系统感觉您很开心",Toast.LENGTH_LONG).show();
							}else if (max.equals("D")){
								//m高兴
								Toast.makeText(Face.this,"系统感觉您好像有点迷茫，疑惑",Toast.LENGTH_LONG).show();
							}else if (max.equals("W")){
								//m高兴
								Toast.makeText(Face.this,"系统感觉您好像有点紧张，焦虑",Toast.LENGTH_LONG).show();
							}

							Face.this.finish();

						}

					} catch (JSONException e) {
						Log.e("MYAPP", "unexpected JSON exception");
					}
					break;
				default:

					Toast.makeText(Face.this,"请求失败，请重新尝试",Toast.LENGTH_LONG).show();
					StartJudge();
					break;
			}

		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.face);

		mLocationClient = ((MainApplication)getApplication()).mLocationClient;
		initLocation();
		mLocationClient.start();
		StartJudge();

	}

	private void StartJudge(){

		expressdetect = expressdetect.createRecognizer(this, mInitListener);

		final Map<String, Object> param = new HashMap<String, Object>();
		param.put("front_camera", "" + SDKUtil.Front_carmera);

		expressdetect.startratelistening(param, expresslisten);

	}


	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Toast.makeText(Face.this,"我们开始测试您的心情喽",Toast.LENGTH_SHORT).show();
		}
	};

	private ExpressionListener expresslisten = new ExpressionListener() {

		@Override
		public void Enddetect(String result) {
			Message msg = new Message();
			msg.what = 1901;
			msg.obj = result;
			mainhandler.sendMessage(msg);
			System.out.println("THE RESULT INFO IS:" + result);
		}

		@Override
		public void Begindetect() {
			// TODO Auto-generated method stub
			System.out.println("Begindetect");
		}
	};

	private void initLocation(){

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("gcj02");
		//可选，默认gcj02，设置返回的定位结果坐标系，
		option.setScanSpan(2000);
		//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);
		//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);
		//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);
		//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);
		//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.setEnableSimulateGps(false);
		//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		option.setIsNeedLocationDescribe(true);
		//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		mLocationClient.setLocOption(option);

	}

	@Override
	protected void onDestroy() {
		mLocationClient.stop();
		super.onDestroy();
	}
}