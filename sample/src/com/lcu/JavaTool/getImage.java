package com.lcu.JavaTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class getImage extends Thread
{
	Handler handler=null;
    String url=null;
    private boolean isRun = false ;
    
    public getImage(Handler handler)
    {
        this.handler=handler;
    }
    public void doStart(String url){
    	if(!url.contains(CommUtil.url)){
    	this.url=CommUtil.imageurl+url;
    	}
    	else{
    		this.url=url;
    	}
    	super.start();
    	
    
    }
    public void Stop()
    {
       if(!isRun){ 
    	 
    	 super.interrupt();
       
    	 super.stop(); 
       }

    }
    
    @Override
    public void run(){
    	try{
    	Bitmap pic=getPic(url);
    	if(pic!=null){	
    		 ImageTools.savePhotoToSDCard(pic,Environment.getExternalStorageDirectory().getAbsolutePath()+"/yanbin", String.valueOf(System.currentTimeMillis()));
    		 SerializableBitmap mage=new SerializableBitmap(pic);
    		 Message message = handler.obtainMessage();
             Bundle bundle = new Bundle();
             bundle.putSerializable("data", mage);
             message.setData(bundle);
             handler.sendMessage(message);
             Log.e("getImage", "����ͼƬ");
             isRun=false;
             try {
   				Thread.sleep(100);
   				
   			} catch (InterruptedException e) {
   			
   				e.printStackTrace();
   			}
    	}
    	}
    	catch(Exception e){
    		e.getStackTrace();
    	}
    	
    }
    private static Bitmap getPic(String uriPic) 
		{  
		     URL imageUrl = null;   
		     Bitmap bitmap=null;
		try {        
			
			imageUrl = new URL(uriPic);    }
		
		catch (MalformedURLException e)
		{       
			e.printStackTrace();    }   
		
		try {      
			HttpURLConnection conn = (HttpURLConnection) 
		    imageUrl.openConnection();        
			conn.connect();       
			InputStream is = conn.getInputStream(); 
			BitmapFactory.Options opts = new BitmapFactory.Options();   
			opts.inSampleSize=1;
			 bitmap = BitmapFactory.decodeStream(is, null, opts);
			is.close();
			
			} 
		catch (IOException e) 
		{        e.printStackTrace();   
		}  
		return bitmap;
		}
	
	public static Bitmap getBitmapFromServer(String imagePath) { 
	      
	     HttpGet get = new HttpGet(imagePath); 
	     HttpClient client = new DefaultHttpClient(); 
	     Bitmap pic = null; 
	     try { 
	         HttpResponse response = client.execute(get); 
	         HttpEntity entity = response.getEntity(); 
	         InputStream is = entity.getContent();    
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize=2;
				pic = BitmapFactory.decodeStream(is, null, opts);
			    pic.recycle();
	          
	     } catch (ClientProtocolException e) { 
	         e.printStackTrace(); 
	     } catch (IOException e) { 
	         e.printStackTrace(); 
	     } 
	     return pic; 
	 } 
}
