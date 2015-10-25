package com.lcu.Article;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.RefreshableListView;
import com.astuetz.viewpager.extensions.sample.R;
import com.lcu.JavaTool.CallWebservice;
import com.lcu.feelingcampus.MainApplication;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class ArticleFragment extends Fragment {

	private View parentView;
	private RefreshableListView mListView;
	private Handler handler1;
	private String url = "Service.asmx";
	private String methodName="getArticle";
	private SpotsDialog sportdialog;
	LocalBroadcastManager broadcastManager;
	BroadcastReceiver mItemViewListClickReceiver;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		System.out.println("onCreateView：ArticleFragment");
		sportdialog= new SpotsDialog(getActivity(),"正在加载...", R.style.Custom);

		parentView = inflater.inflate(R.layout.articlefragment, container, false);
		mListView = (RefreshableListView)parentView.findViewById(R.id.listview);
		mListView.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
			@Override
			public void onRefresh(RefreshableListView listView) {
				new NewDataTask().execute();
			}

		});
		MainApplication app = (MainApplication) getActivity().getApplicationContext();
		doThread(app.getfeeling());
		//接收广播
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.CART_BROADCAST");
		mItemViewListClickReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent){
				final String feel = intent.getExtras().getString("mfeeling");
				doThread(feel);
			}
		};
		broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
		return parentView;
	}

	//开启线程
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler()
	{
		@Override
		public void handleMessage(Message m)
		{
			ArrayList<String> datas = m.getData().getStringArrayList("data");
			doResult(datas);
		};
	};
	//显示webservice结果
	private void doResult( ArrayList<String> datas){
		ArrayList<HashMap<String, Object>> mydata = new ArrayList<HashMap<String, Object>>();
		for(int i=0;i<datas.size();){
			HashMap<String, Object> map = new HashMap<String, Object>();
			if(datas.get(i)!=null){
				map.put("Aname", datas.get(i));
				i++;
				map.put("Acontent", datas.get(i));
				i++;
				map.put("Atime",  datas.get(i));
				i++;
				map.put("Apicture",  datas.get(i));
				i++;
				mydata.add(map);

			}
		}
		LeagueAdapter adapter=new LeagueAdapter(mydata);
		System.out.println("得到的文章数据个数:" + mydata.size() + "个");
		mListView.setAdapter(adapter);
		sportdialog.dismiss();
	}

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

			if(convertView==null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.article_listview_item,null);
				views=new MyView();
				views.Aname=(TextView)convertView.findViewById(R.id.artName);
				views.Acontent=(TextView)convertView.findViewById(R.id.artContent);
				views.Atime=(TextView)convertView.findViewById(R.id.arcTime);
				convertView.setTag(views);
			}else{
				views=(MyView)convertView.getTag();
			}
			final String Aname = data.get(position).get("Aname").toString();
			final String Acontent = data.get(position).get("Acontent").toString();
			final String Atime = data.get(position).get("Atime").toString();
			final String Apicture = data.get(position).get("Apicture").toString();
			views.Aname.setText(Aname);
			views.Acontent.setText(Acontent);
			views.Atime.setText(Atime);
			//设置点击进入详情的点击事件
			views.Aname.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Articlein.class);
					intent.putExtra("Aname", Aname);
					intent.putExtra("Acontent", Acontent);
					intent.putExtra("Atime", Atime);
					intent.putExtra("Apicture", Apicture);
					getActivity().startActivity(intent);
					Log.e("进入详情", "现在进入了" + Aname);
				}

			});
			views.Acontent.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Articlein.class);
					intent.putExtra("Aname", Aname);
					intent.putExtra("Acontent", Acontent);
					intent.putExtra("Atime", Atime);
					intent.putExtra("Apicture", Apicture);
					getActivity().startActivity(intent);
					Log.e("进入详情", "现在进入了" + Aname);
				}

			});
			return convertView;
		}
		class MyView{
			public TextView Aname;
			public TextView Acontent;
			public TextView Atime;
		}
	}


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
			MainApplication app = (MainApplication) getActivity().getApplicationContext();
			HashMap<String, Object> params = new HashMap<String, Object>();
			System.out.println("下拉刷新"+app.getfeeling());
			params.put("Afeeling", app.getfeeling());
			CallWebservice callWeb=new CallWebservice(handler);
			callWeb.doStart(url, methodName, params);
			mListView.completeRefreshing();

			super.onPostExecute(result);
		}
	}

	@SuppressWarnings("deprecation")
	private void doThread(String mfeeling){
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("Afeeling", mfeeling);
			CallWebservice callWeb=new CallWebservice(handler);
			callWeb.doStart(url, methodName, params);
			sportdialog.show();
	}

}
