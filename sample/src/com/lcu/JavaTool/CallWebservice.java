
package com.lcu.JavaTool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class CallWebservice extends Thread{

	private final static String Server=CommUtil.url;
	Handler handler=null;
    String url=null;
    
    private final static String namespace="http://tempuri.org/";
    String methodname=null;
    private boolean isRun = false ;
    HashMap<String ,Object> params=new HashMap<String,Object>();
    
    public CallWebservice(Handler handler){
    		
    	 this.handler=handler;
	       
    }
    

	public void Stop()
    {
       if(!isRun){ 
    	 
    	 super.interrupt();
       
    	 super.stop(); 
       }

    }


    public void doStart(String url,String methodname,HashMap<String,Object> params)
    {
        this.url=Server+url;
        this.methodname=methodname;
        this.params=params;
        this.start();
    }
   

	@SuppressWarnings("null")
	@Override
    public void run()
    {
     
    	 isRun = true ;
     if(isRun){
        try
        { 
            SoapObject result=CallWebService(); 
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            if(result!=null || result.getPropertyCount()>0)
            {        Log.e("callwabserver", "CALLʼ");
            Object value = result.getProperty(0);
			if (value instanceof SoapPrimitive) {
			  String keys = value.toString();	
			 Boolean isFalse=Boolean.valueOf(keys);
				bundle.putBoolean("isFalse", isFalse);
			}else{
            
                    SoapObject detial1 = (SoapObject) result.getProperty(0);
                    SoapObject detial2 = (SoapObject) detial1.getProperty(0);
                    ArrayList<String> Datas = new ArrayList<String>();
                    
			        for ( int index=0;index<detial2.getPropertyCount(); index++)
			        { 
			         SoapObject detial3 = (SoapObject) detial2.getProperty(index); 
			         	for(int i=0;i<detial3.getPropertyCount();i++)
			         		{
			         		SoapPrimitive valu=(SoapPrimitive) detial3.getProperty(i); //���ص������  
			         		Datas.add(valu.toString());
			         		}	                  
			        }         
			        bundle.putStringArrayList("data", Datas);
				} 
            
            	message.setData(bundle);
            	handler.sendMessage(message);
            	isRun=false;
            	try {
            		Thread.sleep(100);
				
            	} catch (InterruptedException e) {
			
            		e.printStackTrace();
            	}
           
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
     }
 }
    
    private SoapObject CallWebService()
    {
       
        String soapaction=namespace+methodname;
        SoapObject request=new SoapObject(namespace,methodname);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet=true;
        if(params.size()>0)
        {
        	Log.i("CallWebService", "params:"+params.size());
            for (Iterator<?> it = params.entrySet().iterator(); it.hasNext();)
            {
                Entry<String, Object> e = (Entry) it.next();
                
                request.addProperty(e.getKey().toString(),e.getValue().toString());
            }
        }
     
        envelope.setOutputSoapObject(request);
        HttpTransportSE ht=new HttpTransportSE(url);
        Log.i("CallWebService", "url:"+url);
        SoapObject result=null;
        try
        {
            ht.call(soapaction, envelope); 
            result=(SoapObject)envelope.bodyIn;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
        
}