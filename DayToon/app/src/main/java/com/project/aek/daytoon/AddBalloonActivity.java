package com.project.aek.daytoon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by aek on 2016-12-31.
 */

public class AddBalloonActivity extends AppCompatActivity {
    private final static int getImage = 1;
    private ImageView getImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addballoon);
        setCameraActionBar();
        Button homeBtm = (Button)findViewById(R.id.homeBtm);
        homeBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Log.d("풍선 인텐트","인텐트 시작");
        getImageView = (ImageView)findViewById(R.id.getimageView);
        Intent intent = new Intent(AddBalloonActivity.this,EditPotoGallery.class);
        Log.d("풍선 인텐트","인텐트 시작");
        startActivityForResult(intent,getImage);
    }
    private void setCameraActionBar()
    {
        ActionBar actionBar = getSupportActionBar();      //제공되는 액션바를 가져온다.

        //CustomEnabled를 true로 하고 필요없는건 false
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        //LayoutInflater 는 XML에 정의된 리소스들을 뷰의 형태로 변환한다.
        View mCustomView = LayoutInflater.from(this).inflate(R.layout.back_actionbar,null);       //만들어둔 xml을 뷰로 변경
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
