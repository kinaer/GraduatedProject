package com.project.aek.daytoon;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.LinearLayout;
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
        SensorEventListener,View.OnClickListener, StikerFragment.OnGetItemIdListener{
//, Camera.FaceDetectionListener
    private CameraView mCvCameraView;
    private Mat mPictureFrame;
    private Mat mDisplayFrame;

    private Animation BtmAnim;
    private ImageView changeBtm;
    private ImageView facingBtm;
    private Button stikerBtm;

    Toast tt;
    FaceDrawView faceView;
    SensorManager sensorManager;            //방향을 감지할 센서매니저
    Sensor sensor;                          //방향을 감지할 센서
    int effect;                             //흑백 컬러 구분
    int chageBtnImg;                        //버튼의 이미지
    int mStikerId;
   // public static boolean FACESTATE = false;
    //상태 저장에 쓸 이름들
    private final static String CameraFacing = "CameraFacing";
    private final static String MangaEffect = "MangaEffect";
    private final static String ChangeBtnImg = "ChangeBtnImg";
    private final static String StikerId = "StikerId";
    private final static String StikerOn = "StikerOn";



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
        chageBtnImg = android.R.drawable.presence_online;       //기본이미지

        if(savedInstanceState != null)      //저장된 상태가 있으면
        {
            mCameraFacing = savedInstanceState.getInt(CameraFacing);
            effect = savedInstanceState.getInt(MangaEffect);
            chageBtnImg = savedInstanceState.getInt(ChangeBtnImg);
            mStikerId = savedInstanceState.getInt(StikerId);
            //전부 불러온후 적용할거 적용
            //카메라 포커스는 OnResume에서 호출될거니 다른것들만 적용
            mCvCameraView.mMangaEffectData.setEffect(effect);   //이펙트 적용
            changeBtm.setBackground(getDrawable(chageBtnImg));  //이미지 적용
            faceView.setBitmapId(mStikerId);                    //스티커 적용
            faceView.setStikerOn(savedInstanceState.getBoolean(StikerOn));
        }
    }

    /*/////////////////////////////////////////////////
        onSaveInstanceState 는 액티비티가 제거되기전의 상태
        를 저장한다.
        Bundle객체에 이름-값의 형식으로 동적상태 기록가능
   ////////////////////////////////////////////////////  */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CameraFacing,mCameraFacing);        //카메라 전후면 설정
        outState.putInt(MangaEffect,effect);                //흑백인지 컬러인지 설정
        outState.putInt(ChangeBtnImg,chageBtnImg);          //흑백 컬러 체인지 버튼 이미지 설정
        outState.putInt(StikerId,mStikerId);                //스티커 아이디
        outState.putBoolean(StikerOn,faceView.getStikerOn());             //스티커 on off
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.changeBtm:            //전면 후면 체인지 버튼
                effect = mCvCameraView.mMangaEffectData.getmMangeEffect();
                if(effect == mCvCameraView.mMangaEffectData.SKETCH)
                {
                    chageBtnImg = android.R.drawable.presence_online;
                    changeBtm.setBackground(getDrawable(chageBtnImg));
                    mCvCameraView.mMangaEffectData.setEffect(mCvCameraView.mMangaEffectData.SKETCH_C);
                    effect = mCvCameraView.mMangaEffectData.SKETCH_C;
                }
                else if(effect == mCvCameraView.mMangaEffectData.SKETCH_C)
                {
                    chageBtnImg = android.R.drawable.presence_invisible;
                    changeBtm.setBackground(getDrawable(android.R.drawable.presence_invisible));
                    mCvCameraView.mMangaEffectData.setEffect(mCvCameraView.mMangaEffectData.SKETCH);
                    effect = mCvCameraView.mMangaEffectData.SKETCH;
                }
                break;
            case R.id.stikerBtm:            //스티커 on off 버튼
                faceView.setVisibility(View.VISIBLE);

                faceView.setSize(mCvCameraView.getWidth(),mCvCameraView.getHeight());
                LinearLayout container = (LinearLayout)findViewById(R.id.stikerContainer);
                if(container.getChildCount() <=0)
                {
                    StikerFragment fr = new StikerFragment();
                    FragmentManager fm = getFragmentManager();  //프래그먼트 추가 제거를 위해 매니저 필요
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.add(R.id.stikerContainer,fr);
                    fragmentTransaction.addToBackStack(null);   //Back을 눌렀을때 이전 상태로 되돌아간다.
                    fragmentTransaction.commit();
                }

                break;
            case R.id.homeBtm:
                finish();
                break;
        }

    }//onClick
    /*//////////////////////////////////////////////////
        onGetItemId로 클릭된 스티커의 아이디를 최신화 한다.
     //////////////////////////////////////////////////*/
    @Override
    public void onGetItemId(int id) {
        mStikerId = id;
        faceView.setBitmapId(id);
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

            faceView.setIsFront(true);
            Toast.makeText(this,"전면으로 전환",Toast.LENGTH_SHORT).show();
            //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else{
            mCameraFacing=Camera.CameraInfo.CAMERA_FACING_BACK;
            Toast.makeText(this,"후면으로 전환",Toast.LENGTH_SHORT).show();

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




            faceView.setDrawingCacheEnabled(true);
            Bitmap faceBitmap = faceView.getDrawingCache();
            mCvCameraView.takePicture(path, faceBitmap);


        Toast.makeText(this, path + " saved", Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //센서의 각도로부터 화면 전환
    @Override
    public void onSensorChanged(SensorEvent event) {

            faceView.setFaces(mCvCameraView.getFaces());
            faceView.invalidate();


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees=0;
        if(mCvCameraView.isCameraOpen()) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    mCvCameraView.setOrientation(false, degrees);

                     faceView.setIsLand(false);
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                     mCvCameraView.setOrientation(true,degrees);

                    faceView.setIsLand(true);
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                     mCvCameraView.setOrientation(false,degrees);

                    faceView.setIsLand(false);
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                     mCvCameraView.setOrientation(true,degrees);

                    faceView.setIsLand(true);
                    break;
            }

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
