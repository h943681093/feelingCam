package com.lcu.feelingcampus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.RefreshableListView;
import com.astuetz.viewpager.extensions.sample.R;
import com.lcu.JavaTool.CommUtil;
import com.lcu.JavaTool.ImageDownloader;
import com.lcu.JavaTool.OnImageDownload;
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

public class LinesFragment extends Fragment {

	private View parentView;
	LocalBroadcastManager broadcastManager;
	BroadcastReceiver mItemViewListClickReceiver;
	IntentFilter intentFilter;
	private String MapURL = Environment.getExternalStorageDirectory().getPath()+"/MapGIS/map/lcupoi/lcupoi.xml";
	private ArrayList<HashMap<String, Object>> mydata = new ArrayList<HashMap<String, Object>>();
	private RefreshableListView mListView;
	private String url;
	private ImageDownloader mDownloader;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		parentView = inflater.inflate(R.layout.fragment_lines, container, false);

		//接收广播
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.CART_BROADCAST");

		mItemViewListClickReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent){

				final String feel =  intent.getExtras().getString("mfeeling");
				System.out.println("LinesFragment:"+feel);
				//改变颜色时重新填装数据
				update(feel);
			}
		};
		//注册可以下拉刷新的listview
		mListView = (RefreshableListView)parentView.findViewById(R.id.lineslistview);
		mListView.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
			@Override
			public void onRefresh(RefreshableListView listView) {
				new NewDataTask().execute();
			}
		});
		MainApplication app = (MainApplication)getActivity().getApplicationContext();
		update(app.getfeeling());
		return parentView;
	}
	//填充数据


	//
	@Override
	public void onStart() {
		super.onStart();
		System.out.println("onStart:LinesFragment");
		broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("onStop：LinesFragment");
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
		System.out.println("结果数：" + totalCount);
		System.out.println("结果页数："+featurePagedResult.getPageCount());
		if (totalCount>0){
		System.out.println("结果：" + featurePagedResult.getPage(1));
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
		getOnePointandLint();
		}else{
			System.out.println("路线推荐未得到有效点");
		}
	}


	public void getOnePointandLint(){
		ArrayList<HashMap<String, Object>> showdata = new ArrayList<HashMap<String, Object>>();
		int count =4+(int)(Math.random()*3);
		for (int i=0;i<count;i++){
			int x=(int)(Math.random()*mydata.size());
			//随机数据位置
			HashMap<String, Object> map = mydata.get(x);
			//获取数据 填装布局
			showdata.add(mydata.get(x));
		}
		doResult(showdata);
	}

	private void doResult(ArrayList<HashMap<String, Object>> data){
		ArrayList<HashMap<String, Object>> listdata = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i=0;i<data.size();i++){
			map.put("name"+i, data.get(i).get("name"));
			map.put("content" + i, data.get(i).get("content"));
			map.put("URL" + i, data.get(i).get("URL"));
			map.put("feeling" + i, data.get(i).get("feeling"));
			map.put("x" + i, data.get(i).get("x"));
			map.put("y" + i, data.get(i).get("y"));
		}
		listdata.add(map);
		LeagueAdapter adapter=new LeagueAdapter(listdata);
		mListView.setAdapter(adapter);
	}
	//适配器
	class LeagueAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> data;
		private MyView views;

		public LeagueAdapter(ArrayList<HashMap<String, Object>> data){

			this.data=data;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			MainApplication app = (MainApplication) getActivity().getApplicationContext();
			app.setSearchdata(data.get(position));


			if(convertView==null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_lines_item,null);
				views=new MyView();

				views.onelinetitle= (TextView)convertView.findViewById(R.id.linesTitleDes);
				views.onelinepic=(ImageView)convertView.findViewById(R.id.oneLinePic);
				views.onelinedes=(TextView)convertView.findViewById(R.id.oneLineContent);

				views.morelinestitle= (TextView)convertView.findViewById(R.id.morelinesDes);
				views.fmorelinepic=(ImageView)convertView.findViewById(R.id.moreLinepics1);
				views.smorelinepic=(ImageView)convertView.findViewById(R.id.moreLinepics2);
				views.tmorelinepic=(ImageView)convertView.findViewById(R.id.moreLinepics3);
				views.morelinedes=(TextView)convertView.findViewById(R.id.morelinesContent);
				convertView.setTag(views);
			}else{
				views=(MyView)convertView.getTag();
			}

			//填装单个活动点数据
			String name0 = data.get(position).get("name0").toString();
			String content0 = data.get(position).get("content0").toString();
			String URL0 = data.get(position).get("URL0").toString();
			String feeling0 = data.get(position).get("feeling0").toString();
			String x0 = data.get(position).get("x0").toString();
			String y0 = data.get(position).get("y0").toString();
			views.onelinetitle.setText("推荐您去"+name0);
			views.onelinedes.setText("推荐理由："+content0);

			//下载图片
			if (mDownloader == null) {mDownloader = new ImageDownloader();}

			System.out.println("地图数据:"+URL0);
			if(!URL0.contains(".")){
				URL0= CommUtil.imageurl+CommUtil.image;
			}
			else{
				URL0=CommUtil.imageurl+URL0;
			}

			views.onelinepic.setTag(URL0);
			System.out.println("url1:"+URL0);

			views.onelinepic.setImageResource(R.drawable.photo_default_img);

			mDownloader.imageDownload(URL0, views.onelinepic, "/lcu", getActivity(), new OnImageDownload() {
				@Override
				public void onDownloadSucc(Bitmap bitmap, String c_url, ImageView mimageView) {

					ImageView imageView = (ImageView) views.onelinepic.findViewWithTag(c_url);

					if (imageView != null) {
						imageView.setImageBitmap(bitmap);
						imageView.setTag("");
					}
				}
			});

			String URL1 = data.get(position).get("URL1").toString();
			System.out.println("URL1:"+URL1);

			if(!URL1.contains(".")){
				URL1= CommUtil.imageurl+CommUtil.image;
			}
			else{
				URL1=CommUtil.imageurl+URL1;
			}
			views.fmorelinepic.setTag(URL1);
			System.out.println("URL1:"+URL1);
			views.fmorelinepic.setImageResource(R.drawable.photo_default_img);
			mDownloader.imageDownload(URL1, views.fmorelinepic, "/lcu", getActivity(), new OnImageDownload() {
				@Override
				public void onDownloadSucc(Bitmap bitmap, String c_url, ImageView mimageView) {

					ImageView imageView = (ImageView) views.fmorelinepic.findViewWithTag(c_url);

					if (imageView != null) {
						imageView.setImageBitmap(bitmap);
						imageView.setTag("");
					}
				}
			});


			String URL2 = data.get(position).get("URL2").toString();
			if(!URL2.contains(".")){
				URL2= CommUtil.imageurl+CommUtil.image;
			}
			else{
				URL2=CommUtil.imageurl+URL2;
			}
			views.smorelinepic.setTag(URL2);
			System.out.println("url3:"+URL2);
			views.smorelinepic.setImageResource(R.drawable.photo_default_img);
			mDownloader.imageDownload(URL2, views.smorelinepic, "/lcu", getActivity(), new OnImageDownload() {
				@Override
				public void onDownloadSucc(Bitmap bitmap, String c_url, ImageView mimageView) {

					ImageView imageView = (ImageView) views.smorelinepic.findViewWithTag(c_url);

					if (imageView != null) {
						imageView.setImageBitmap(bitmap);
						imageView.setTag("");
					}
				}
			});

			String URL3 = data.get(position).get("URL3").toString();
			if(!URL3.contains(".")){
				URL3= CommUtil.imageurl+CommUtil.image;
			}
			else{
				URL3=CommUtil.imageurl+URL3;
			}
			views.tmorelinepic.setTag(URL3);
			System.out.println("URL3:"+URL3);
			views.tmorelinepic.setImageResource(R.drawable.photo_default_img);
			mDownloader.imageDownload(URL3, views.tmorelinepic, "/lcu", getActivity(), new OnImageDownload() {
				@Override
				public void onDownloadSucc(Bitmap bitmap, String c_url, ImageView mimageView) {

					ImageView imageView = (ImageView) views.tmorelinepic.findViewWithTag(c_url);

					if (imageView != null) {
						imageView.setImageBitmap(bitmap);
						imageView.setTag("");
					}
				}
			});


			//获取数据总数
			int count = data.get(position).size()/6;
			System.out.println("路线数据组数："+count);

			//获得路线
			String morelinesname = "";
			for (int k=1;k<count;k++){
				morelinesname += data.get(position).get("name"+k).toString()+"-->";
			}
			morelinesname += "END";
			views.morelinestitle.setText("试试这样游览校园："+morelinesname);
			views.morelinedes.setText("推荐理由：" + data.get(position).get("content1").toString());


			//设置点击进入详情的点击事件（单站点）
			views.onelinetitle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Linein.class);
					getActivity().startActivity(intent);
				}
			});
			views.onelinepic.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Linein.class);
					getActivity().startActivity(intent);
				}
			});
			views.onelinedes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Linein.class);
					getActivity().startActivity(intent);
				}
			});


			//设置点击进入详情的点击事件（路线）
			views.fmorelinepic.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Linesin.class);
					getActivity().startActivity(intent);
				}
			});
			views.smorelinepic.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Linesin.class);
					getActivity().startActivity(intent);
				}
			});
			views.tmorelinepic.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Linesin.class);
					getActivity().startActivity(intent);
				}
			});
			views.morelinestitle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Linesin.class);
					getActivity().startActivity(intent);
				}
			});
			views.morelinedes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Linesin.class);
					getActivity().startActivity(intent);
				}

			});



			return convertView;
		}
		class MyView{
			public TextView onelinetitle;
			public ImageView onelinepic;
			public TextView onelinedes;

			public TextView morelinestitle;
			public ImageView fmorelinepic;
			public ImageView smorelinepic;
			public ImageView tmorelinepic;
			public TextView morelinedes;
		}
	}

	//下拉刷新事件
	private class NewDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {}

			return "A new list item";
		}

		@Override
		protected void onPostExecute(String result) {
			//TODO:下拉刷新事件
			getOnePointandLint();
			mListView.completeRefreshing();
			super.onPostExecute(result);
		}
	}




}
