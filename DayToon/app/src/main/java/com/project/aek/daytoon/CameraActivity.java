package com.project.aek.daytoon;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;


import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


import static org.opencv.core.CvType.CV_8UC3;

public class CameraActivity extends AppCompatActivity implements CvCameraViewListener2,View.OnTouchListener,
        SensorEventListener,View.OnClickListener{
//, Camera.FaceDetectionListener
    private CameraView mCvCameraView;
    private List<Camera.Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;
    private Mat mPictureFrame;
    private Mat mDisplayFrame;

    private Animation BtmAnim;
    private ImageView changeBtm;
    private ImageView facingBtm;
    private Button stikerBtm;

    private boolean stikerOn=false;
    Toast tt;
    FaceDrawView faceView;
    SensorManager sensorManager;            //방향을 감지할 센서매니저
    Sensor sensor;                          //방향을 감지할 센서

   // public static boolean FACESTATE = false;



    int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;      //카메라 전후면 전환
    /*
        BaseLoaderCallback 은 opencv 라이브러리를 초기화 한다.(불러올 수 있는지 확인)
     */
private BaseLoaderCallback mLoadeCallback= new BaseLoaderCallback(this){
    @Override
    public void onManagerConnected(int status) {
        switch(status){
            case LoaderCallbackInterface.SUCCESS:
            {
                Log.d("OpenCV","OpenCV로드 성공");
                //여기에다가 초기에 사용할거 적는다.
                mCvCameraView.setCameraIndex(mCameraFacing);
                mCvCameraView.enableView(); //이런 식으로
               // mCvCameraView.setOrientation();
               // mCvCameraView.setParam();
                mCvCameraView.setOnTouchListener(CameraActivity.this);
            }break;
            default: {
                super.onManagerConnected(status);
            }break;
        }

    }
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mCvCameraView = (CameraView) findViewById(R.id.view);
        mCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mCvCameraView.setCvCameraViewListener(this);

        faceView = new FaceDrawView(this);
        addContentView(faceView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mCvCameraView.mMangaEffectData=new MangaEffect();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);    //시스템으로부터 센서 서비스를 받아와
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);   //방향센서를 받아와
        tt= Toast.makeText(this," ",Toast.LENGTH_SHORT);

        setCameraActionBar();

        mCvCameraView.mMangaEffectData.setEffect(mCvCameraView.mMangaEffectData.SKETCH);
        BtmAnim = AnimationUtils.loadAnimation(this,R.anim.rotation);
       // facingBtm = (ImageView)findViewById(R.id.focusingBtn);

        changeBtm = (ImageView) findViewById(R.id.changeBtm);
        stikerBtm = (Button)findViewById(R.id.stikerBtm);
        stikerBtm.setOnClickListener(this);
        changeBtm.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v) {
        if(v == changeBtm)
        {
            int effect = mCvCameraView.mMangaEffectData.getmMangeEffect();
            if(effect == mCvCameraView.mMangaEffectData.SKETCH)
            {
                v.setBackground(getDrawable(android.R.drawable.presence_online));
                mCvCameraView.mMangaEffectData.setEffect(mCvCameraView.mMangaEffectData.SKETCH_C);
            }
            else if(effect == mCvCameraView.mMangaEffectData.SKETCH_C)
            {
                v.setBackground(getDrawable(android.R.drawable.presence_invisible));
                mCvCameraView.mMangaEffectData.setEffect(mCvCameraView.mMangaEffectData.SKETCH);
            }
        }
        else if(v == stikerBtm)
        {
            if(stikerOn)
            {
                faceView.setVisibility(View.GONE);
                stikerOn=false;
            }
            else
            {
                faceView.setVisibility(View.VISIBLE);
                stikerOn=true;
                faceView.setSize(mCvCameraView.getWidth(),mCvCameraView.getHeight());
                //faceView = new FaceDrawView(this);
                //mCvCameraView.cameraReStart();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCvCameraView != null){

            //pause상태면 프리뷰 중지
            mCvCameraView.disableView();
            sensorManager.unregisterListener(this); //중지일땐 센서 필요없어

        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        //어플리케이션을 다시 시작하면
        if(!OpenCVLoader.initDebug()){
            //OpenCV라이브러리가 초기화 되었는지 확인
            Log.d("메인","OpenCV라이브러리를 찾기 못했습니다. 초기화합니다");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,this, mLoadeCallback);
        }else{
            Log.d("메인","OpenCV라이브러리를 찾았습니다");
            mLoadeCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCvCameraView !=null){
            mCvCameraView.disableView();

        }
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        //화면 프레임 Mat파일 생성
        mPictureFrame = new Mat(height,width, CV_8UC3);
        mDisplayFrame = new Mat(height,width, CV_8UC3);
        mCvCameraView.setFrameSize(width,height);
      //  faceView.setSize(height,width);
        if(stikerOn)
         faceView.setSize(mCvCameraView.getWidth(),mCvCameraView.getHeight());

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {


            return inputFrame.rgba();

    }

    @Override
    public void onCameraViewStopped() {
        mPictureFrame.release();
        mDisplayFrame.release();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }
    /*////////////////////////////////////////////
                액션바 세팅
    ////////////////////////////////////////////// */
    private void setCameraActionBar()
    {
        ActionBar actionBar = getSupportActionBar();      //제공되는 액션바를 가져온다.

        //CustomEnabled를 true로 하고 필요없는건 false
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        //LayoutInflater 는 XML에 정의된 리소스들을 뷰의 형태로 변환한다.
        View mCustomView = LayoutInflater.from(this).inflate(R.layout.camera_actionbar,null);       //만들어둔 xml을 뷰로 변경
        actionBar.setCustomView(mCustomView);       //뷰를 적용

        Toolbar parent = (Toolbar)mCustomView.getParent();
        parent.setContentInsetsAbsolute(0,0);       //패딩 제거


    }//setCameraActionBar

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }
    public void ChangeFocusBtnClick(View v){
        if(mCameraFacing==Camera.CameraInfo.CAMERA_FACING_BACK){
            mCameraFacing=Camera.CameraInfo.CAMERA_FACING_FRONT;
            if(stikerOn)
            faceView.setIsFront(true);
            Toast.makeText(this,"전면으로 전환",Toast.LENGTH_SHORT).show();
            //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else{
            mCameraFacing=Camera.CameraInfo.CAMERA_FACING_BACK;
            Toast.makeText(this,"후면으로 전환",Toast.LENGTH_SHORT).show();
            if(stikerOn)
            faceView.setIsFront(false);
        }
        mCvCameraView.disableView();
        mLoadeCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mfile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"DayToon");
        if(!mfile.exists()){
            if(!mfile.mkdir()){
                Log.d("메인", "사진파일 디렉터리ㅣ 생성실패");
            }
        }
        String filename=File.separator+"IMG_"+name+".jpg";
        String path =(mfile.getPath()+filename);



        if(stikerOn) {
            faceView.setDrawingCacheEnabled(true);
            Bitmap faceBitmap = faceView.getDrawingCache();
            mCvCameraView.takePicture(path, faceBitmap, stikerOn);
        }
        else
            mCvCameraView.takePicture(path,stikerOn);

        Toast.makeText(this, path + " saved", Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //센서의 각도로부터 화면 전환
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (stikerOn)
        {
            faceView.setFaces(mCvCameraView.getFaces());
            faceView.invalidate();
        }

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees=0;
        if(mCvCameraView.isCameraOpen()) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    mCvCameraView.setOrientation(false, degrees);
                    if(stikerOn)
                     faceView.setIsLand(false);
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                     mCvCameraView.setOrientation(true,degrees);
                    if(stikerOn)
                    faceView.setIsLand(true);
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                     mCvCameraView.setOrientation(false,degrees);
                    if(stikerOn)
                    faceView.setIsLand(false);
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                     mCvCameraView.setOrientation(true,degrees);
                    if(stikerOn)
                    faceView.setIsLand(true);
                    break;
            }
            tt.setText(degrees + " ");
            tt.show();
        }



/*
        if(event.sensor.getType()==Sensor.TYPE_ORIENTATION)
        {
            // String str = "1 : "+event.values[1]+ "    2 :  "+event.values[2];
           // tt.setText(str);
            // tt.show();

             float pitch = event.values[1];
             float roll = event.values[2];
             float sum = pitch+roll;
            if( (pitch < 0) && (roll > -45) && (roll < 45))   //세로 0
            {
               // tt.setText("0   "+0);
                //tt.show();
                if(mCvCameraView.isCameraOpen())
                {
                    if(mCvCameraView.getDegree() != 0)
                    {
                       // facingBtm.startAnimation(BtmAnim);
                       // changeBtm.startAnimation(BtmAnim);
                        facingBtm.setRotation(0);
                        changeBtm.setRotation(0);
                        faceView.setSize(mCvCameraView.getWidth(),mCvCameraView.getHeight());
                    }
                    mCvCameraView.setOrientation(false ,0);

                }

            }
            else if(roll > 0 && (pitch <45) )  //가로 90
            {
               // tt.setText("0   "+90);
               // tt.show();
                if(mCvCameraView.isCameraOpen())
                {
                    if(mCvCameraView.getDegree() != 90)
                    {
                        //facingBtm.startAnimation(BtmAnim);
                       // changeBtm.startAnimation(BtmAnim);
                        facingBtm.setRotation(90);
                        changeBtm.setRotation(90);
                        faceView.setSize(mCvCameraView.getHeight(),mCvCameraView.getWidth());
                    }
                    mCvCameraView.setOrientation(true ,90);

                }
            }
            else if(roll < 0 && (pitch > -45))
            {
               //  tt.setText("0   "+270);
               //  tt.show();
                if(mCvCameraView.isCameraOpen())
                {
                    if(mCvCameraView.getDegree() != 270)
                    {
                        // facingBtm.startAnimation(BtmAnim);
                        // changeBtm.startAnimation(BtmAnim);
                        facingBtm.setRotation(270);
                        changeBtm.setRotation(270);
                        faceView.setSize(mCvCameraView.getHeight(),mCvCameraView.getWidth());
                    }
                    mCvCameraView.setOrientation(true ,270);

                }
            }
            else if(pitch >0 && (roll >-45 ) && (roll <45))
            {
              //  tt.setText("0   "+180);
              //  tt.show();
                if(mCvCameraView.isCameraOpen())
                {
                    if(mCvCameraView.getDegree() != 180)
                    {
                       // facingBtm.startAnimation(BtmAnim);
                       // changeBtm.startAnimation(BtmAnim);
                        facingBtm.setRotation(180);
                        changeBtm.setRotation(180);
                        faceView.setSize(mCvCameraView.getWidth(),mCvCameraView.getHeight());
                    }
                    mCvCameraView.setOrientation(false ,180);

                }

            }

        }*/

    }//onSensorChanged


}
