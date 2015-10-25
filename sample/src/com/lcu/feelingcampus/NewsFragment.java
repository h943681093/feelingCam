package com.lcu.feelingcampus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.astuetz.viewpager.extensions.sample.R;
import com.zondy.mapgis.android.mapview.MapView;
import com.zondy.mapgis.attr.Field;
import com.zondy.mapgis.attr.Fields;
import com.zondy.mapgis.attr.Record;
import com.zondy.mapgis.featureservice.Feature;
import com.zondy.mapgis.featureservice.FeaturePagedResult;
import com.zondy.mapgis.featureservice.FeatureQuery;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.map.VectorLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsFragment extends Fragment {

	private View parentView;
	LocalBroadcastManager broadcastManager;
	BroadcastReceiver mItemViewListClickReceiver;
	IntentFilter intentFilter;
    private String MapURL = Environment.getExternalStorageDirectory().getPath()+"/MapGIS/map/lcupoi/lcupoi.xml";
	private ArrayList<HashMap<String, Object>> mydata = new ArrayList<HashMap<String, Object>>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		System.out.println("onCreateView:NewsFragment");

		parentView = inflater.inflate(R.layout.fragment_news, container, false);

		//接收广播
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.CART_BROADCAST");

		mItemViewListClickReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent){

				final String feel =  intent.getExtras().getString("mfeeling");
				System.out.println("NewsFragment:"+feel);
				//改变颜色时重新填装数据
				update(feel);
			}
		};

		return parentView;
	}

	@Override
	public void onStart() {
		super.onStart();
		System.out.println("onStart:NewsFragment");
		broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("onStop：NewsFragment");
		broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
	}

	public void update(String feeling){

		MapView myMapView = new MapView(getActivity());
		myMapView.loadFromFile(MapURL);
		System.out.println("地图数" + myMapView.getMap().getLayerCount());
		System.out.println("地图" + myMapView.getMap().getLayer(1).getName());
		MapLayer searchlayer = myMapView.getMap().getLayer(1);

		FeatureQuery featureQuery = new FeatureQuery();
		featureQuery.setVectorLayer((VectorLayer) searchlayer);

		String strWhere = "feeling like '%"+feeling+"%'";
		featureQuery.setWhereClause(strWhere);
		FeaturePagedResult featurePagedResult = featureQuery.query();
		int totalCount = featurePagedResult.getTotalFeatureCount();
		System.out.println("结果数："+totalCount);
		System.out.println("结果页数："+featurePagedResult.getPageCount());
		if (totalCount>0){
		System.out.println("结果："+featurePagedResult.getPage(1));
		for (int i=0;i<featurePagedResult.getPageCount();i++){
			List<Feature> featureList = new ArrayList<Feature>();
			featureList = featurePagedResult.getPage(i);

			for (int k=0;k<featureList.size();k++){
				HashMap<String, Object> map = new HashMap<String, Object>();
				Feature feature = featureList.get(k);
				Record attribute = feature.getAtt();
				Fields fields = feature.getFields();
				for (short j=0;j<fields.getFieldCount();j++){

					Field field = fields.getField(j);
					//System.out.print("属性：" + field.getFieldName());
					//System.out.println("值：" + attribute.getFldVal(field.getFieldName()).toString());
					map.put(field.getFieldName(),attribute.getFldVal(field.getFieldName()).toString());
					/**
					 *
					 10-09 21:17:52.168      656-656/? I/System.out﹕ 属性：name值：东门
					 10-09 21:17:52.168      656-656/? I/System.out﹕ 属性：content值：这里交通方便，不想待学校可以出校外逛一圈
					 10-09 21:17:52.169      656-656/? I/System.out﹕ 属性：URL值：dongmen.jpg
					 10-09 21:17:52.169      656-656/? I/System.out﹕ 属性：feeling值：M,Y,K,C
					 10-09 21:17:52.169      656-656/? I/System.out﹕ 属性：x值：1.291489129E7
					 10-09 21:17:52.170      656-656/? I/System.out﹕ 属性：y值：4351572.821928
					 */
				}
				mydata.add(map);
			}
		}
		getOnePoint();
		getLine();
		}else{
			System.out.println("路线推荐未得到有效点");
		}
	}


	public void getOnePoint(){

		//获取数据长度
		int x=(int)(Math.random()*mydata.size());

		//随机数据位置
		HashMap<String, Object> map = mydata.get(x);

		//获取数据 填装布局
		System.out.println("选中的点的位置：" + map.get("name"));
	}

	public void getLine(){

		int count =1+(int)(Math.random()*5);

		for (int i=0;i<count;i++){

			int x=(int)(Math.random()*mydata.size());

			//随机数据位置
			HashMap<String, Object> map = mydata.get(x);

			//获取数据 填装布局
			System.out.println("选中的第"+i+"个点的位置：" + map.get("name"));

		}
	}






}
