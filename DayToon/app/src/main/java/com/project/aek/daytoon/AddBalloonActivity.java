package com.project.aek.daytoon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.aek.daytoon.widget.EditBalloonView;
import com.project.aek.daytoon.widget.TextBalloon;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by aek on 2016-12-31.
 */

public class AddBalloonActivity extends AppCompatActivity {
    private final static int getImage = 1;
   // private ImageView getImageView;
    private EditBalloonView getImageView;
    private ArrayList<TextBalloon> mBalloon = new ArrayList<TextBalloon>();
    private FrameLayout layout;
    TextBalloon t;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addballoon);
        setActionBar();
        layout = (FrameLayout)findViewById(R.id.BalloonLayout);

        Log.d("풍선 인텐트","인텐트 시작");
       // getImageView = (ImageView)findViewById(R.id.getimageView);
        getImageView = (EditBalloonView)findViewById(R.id.getimageView);
        Intent intent = new Intent(AddBalloonActivity.this,EditPotoGallery.class);
        Log.d("풍선 인텐트","인텐트 시작");
        startActivityForResult(intent,getImage);
    }



    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.homeBtm:
                finish();
                break;
            case R.id.addBalloonBtm:
                //mBalloon.add(new TextBalloon(this));
                t = new TextBalloon(this);
                mBalloon.add(t);
                layout.addView(t);
               // layout.addView(t,new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
                Log.d("풍선버튼","이미지"+getImageView.getWidth()+"  "+getImageView.getHeight());
                Log.d("풍선버튼","레이아웃"+layout.getWidth()+"  "+layout.getHeight());
                Log.d("풍선버튼","풍선"+t.getWidth()+"  "+t.getHeight());
                break;
            case R.id.saveBtm:
                ViewGroup.LayoutParams param = layout.getLayoutParams();
                param.width = getImageView.getWidth();
                param.height = getImageView.getHeight();

                layout.setLayoutParams(param);
                layout.setDrawingCacheEnabled(true);
                Bitmap AddBalloonBm = layout.getDrawingCache();

               // getImageView.setDrawingCacheEnabled(true);
               // Bitmap AddBalloonBm = getImageView.getDrawingCache();
              // t.setDrawingCacheEnabled(true);
               // Bitmap ntext = t.getDrawingCache();
                AddBalloonBm = BitmapControl.resizeBitmap(AddBalloonBm);
               // Log.d("풍선버튼","비트맵 사진"+AddBalloonBm.getWidth()+"  "+AddBalloonBm.getHeight());
               // Log.d("풍선버튼","비트맵 풍선"+ntext.getWidth()+"  "+ntext.getHeight());
               // AddBalloonBm = BitmapControl.combineBitmap(AddBalloonBm,ntext);
               // AddBalloonBm = BitmapControl.resizeBitmap(AddBalloonBm);

                String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File mfile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"DayToon");
                if(!mfile.exists()){
                    if(!mfile.mkdir()){
                        Log.d("메인", "사진파일 디렉터리ㅣ 생성실패");
                    }
                }
                String filename=File.separator+"IMG_"+name+".jpg";
                String path =(mfile.getPath()+filename);

                try{
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
                    AddBalloonBm.compress(Bitmap.CompressFormat.JPEG,100,out);
                    out.flush();
                    out.close();
                    MediaScanner scanner = MediaScanner.newInstance(this);
                    scanner.mediaScanning(path);
                }
                catch (IOException e)
                {
                    e.getMessage();
                }


                break;
        }
    }
    private void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();      //제공되는 액션바를 가져온다.

        //CustomEnabled를 true로 하고 필요없는건 false
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        //LayoutInflater 는 XML에 정의된 리소스들을 뷰의 형태로 변환한다.
        View mCustomView = LayoutInflater.from(this).inflate(R.layout.balloon_actionbar,null);       //만들어둔 xml을 뷰로 변경
        actionBar.setCustomView(mCustomView);       //뷰를 적용

        Toolbar parent = (Toolbar)mCustomView.getParent();
        parent.setContentInsetsAbsolute(0,0);       //패딩 제거
    }//setCameraActionBar

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setActionBar();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)  //액티비티가 정상 종료되야하고
        {
            if(requestCode == getImage)     //호출했던 코드가 와야되
            {
                Log.d("풍선 인텐트","넘어옴");

                String mPath = (String)data.getExtras().get("bm");
                Bitmap bm = BitmapFactory.decodeFile(mPath);
                Drawable dr = new BitmapDrawable(bm);       //비트맵을 Drawable로 바꾼다.

                //비트맵 크기에 따라서 세팅변경
                if(bm.getWidth() > bm.getHeight())
                {
                    //가로가 세로보다 크면
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                   // setActionBar(R.layout.balloon_actionbar_land);


                }
                else
                {
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                getImageView.setBackground(dr);
                // getImageView.setImageBitmap(bm);


            }
            else{
                Log.d("풍선 인텐트","코드가 달라?");
            }
        }
        else{
            Log.d("풍선 인텐트","안넘어옴");
            finish();
        }
    }


}
