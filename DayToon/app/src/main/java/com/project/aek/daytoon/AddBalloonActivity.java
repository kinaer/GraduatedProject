package com.project.aek.daytoon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.project.aek.daytoon.widget.EditBalloonView;
import com.project.aek.daytoon.widget.TextBalloon;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by aek on 2016-12-31.
 */

public class AddBalloonActivity extends AppCompatActivity {
    private final static int getImage = 1;
   // private ImageView getImageView;
    private EditBalloonView getImageView;
    private ArrayList<TextBalloon> mBalloon = new ArrayList<TextBalloon>();
    private FrameLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addballoon);
        setActionBar();
        layout = (FrameLayout)findViewById(R.id.BalloonLayout);
       // Button homeBtm = (Button)findViewById(R.id.homeBtm);
        /*
        homeBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        */
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
                TextBalloon t = new TextBalloon(this);
                layout.addView(t);
               // layout.addView(t,new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
                Log.d("풍선버튼","눌림"+t.getWidth()+"  "+t.getHeight());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)  //액티비티가 정상 종료되야하고
        {
            if(requestCode == getImage)     //호출했던 코드가 와야되
            {
                Log.d("풍선 인텐트","넘어옴");
                String mPath = (String)data.getExtras().get("bm");
                Bitmap bm = BitmapFactory.decodeFile(mPath);
                getImageView.setImageBitmap(bm);

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
