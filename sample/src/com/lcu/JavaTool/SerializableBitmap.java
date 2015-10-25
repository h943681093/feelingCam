package com.lcu.JavaTool;

import java.io.Serializable;

import android.graphics.Bitmap;

//扩展Serializable实例化Object对象
public class SerializableBitmap implements Serializable 
{
	private Bitmap bitmap;
	
	public SerializableBitmap(Bitmap pic){
		this.bitmap=pic;
		
	}
	public void setBitmap(Bitmap pic){
		this.bitmap=pic;
		
	}
	public Bitmap getBitmap(){
		
		return bitmap;
	}
}
