package com.lcu.Article;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.R;
import com.lcu.JavaTool.CommUtil;
import com.lcu.JavaTool.ImageDownloader;
import com.lcu.JavaTool.OnImageDownload;

public class Articlein extends Activity{
    private TextView artNamein,artContentin,artTimein;
    private ImageView artPicturein;
    private ImageButton artheadback;
    private ImageDownloader mDownloader;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.articlein);

        artNamein = (TextView) findViewById(R.id.artNamein);
        artContentin= (TextView) findViewById(R.id.artContentin);
        artPicturein= (ImageView) findViewById(R.id.artPicturein);
        artheadback = (ImageButton) findViewById(R.id.artheadback);
        artTimein = (TextView)findViewById(R.id.artTimein);

        final String artName =  getIntent().getExtras().getString("Aname");
        final String artContent =  getIntent().getExtras().getString("Acontent");
        final String artPicture =  getIntent().getExtras().getString("Apicture");
        final String artTime =  getIntent().getExtras().getString("Atime");

        artNamein.setText(artName);
        artContentin.setText(artContent);
        artTimein.setText(artTime);
        /* 下载图片到本地 --大图 */
        if(!artPicture.contains(".")){
            url=CommUtil.imageurl+CommUtil.image;
        }
        else{
            url= CommUtil.imageurl+artPicture;
        }
        //下载图片
        artPicturein.setTag(url);
        if (mDownloader == null) {mDownloader = new ImageDownloader();}
        artPicturein.setImageResource(R.drawable.sy);
        mDownloader.imageDownload(url, artPicturein, "/lcu", Articlein.this, new OnImageDownload() {
            @Override
            public void onDownloadSucc(Bitmap bitmap, String c_url, ImageView mimageView) {

                ImageView imageView = (ImageView) artPicturein.findViewWithTag(c_url);

                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag("");
                }
            }
        });
        artheadback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Articlein.this.finish();
            }
        });

    }
}
