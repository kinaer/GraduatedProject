package com.project.aek.daytoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by aek on 2016-12-30.
 */

public class MainMenuActivity extends AppCompatActivity {
    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        setCameraActionBar();
    }
    /*//////////////////////////////////////
            메인메뉴 액티비티에서
            버튼 눌렀을 때의 이벤트
     ///////////////////////////////////////*/
    public void goActivity(View v)
    {
        Log.d("_test","goActivity()");
        switch(v.getId())
        {
            case R.id.goCamera:
                Log.d("_test","goCamera");
                startActivity(new Intent(MainMenuActivity.this,CameraActivity.class));
                break;
            case R.id.goBalloon:
                Log.d("_test","goBalloon");
                startActivity(new Intent(MainMenuActivity.this,AddBalloonActivity.class));
                break;
            case R.id.capture:
                Log.d("_test","capture");
                startActivity(new Intent(MainMenuActivity.this,SignUpPhotoActivity2.class));
                break;

        }

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
        View mCustomView = LayoutInflater.from(this).inflate(R.layout.mainmenu_actionbar,null);       //만들어둔 xml을 뷰로 변경
        actionBar.setCustomView(mCustomView);       //뷰를 적용

        Toolbar parent = (Toolbar)mCustomView.getParent();
        parent.setContentInsetsAbsolute(0,0);       //패딩 제거
    }//setCameraActionBar
}
