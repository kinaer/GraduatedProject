package com.project.aek.daytoon;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;

import android.os.Environment;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.BufferedOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC3;


/**
 * Created by aek on 2016-10-12.
 * JavaCameraView를 토대로 카메라 클래스를 만든다.
 * JavaCameraView는 CameraBridgeViewBase을 상속 받고 있다.
 * CameraBridgeViewBase는 프리뷰와 관련된 클래스다
 * CameraBridgeViewBase는 프리뷰를 띄워주는 SurfaceView이다.
 */

public class CameraView extends JavaCameraView implements Camera.PictureCallback,Camera.ShutterCallback {
    private String mPictureFileName;
    public MangaEffect mMangaEffectData;
    private int mFWidth, mFHeight;
    public boolean mApplyManga =true;
    private int degree;
    protected Bitmap mFaceBitamp;
    private boolean isStiker = false;



    public CameraView(Context context, AttributeSet attri){
        super(context,attri);

    }
    public Camera.Face[] getFaces()
    {

        return detectFace;
    }
    public List<String> getEffectList(){
        //카메라에서 제공하는 기본적인 컬러 이펙트를 리스트로 가져온다.
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public void setFrameSize(int widht, int height){
        mFWidth=widht;
        mFHeight=height;
    }
    public int getmFWidth(){
        return mFWidth;
    }
    public int getmFHeight(){
        return mFHeight;
    }
    public boolean isEffectSupported(){
        //제공되는 컬러이펙트가 있는지 없는지 확인
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public void setViewSize(int width, int height)
    {
        disconnectCamera(); //카메라 Thread중지
        mMaxHeight = height; //CameraBridgeViewBase에 있다.
        mMaxWidth = width;   //카메라 해상도를 결정하는 변수다
        connectCamera(mMaxWidth,mMaxHeight);    //이 크기로 다시 카메라 Thread실행
    }

    public int getDegree(){
        return degree;
    }
    public boolean isCameraOpen()
    {
        if(mCamera==null)
            return false;
        else
            return true;
    }
    public void setOrientation(boolean isLand,int degree){
        Camera.Parameters param = mCamera.getParameters();
        this.isLand=isLand;
        this.degree=degree;

        try{
            if(degree == 0)     //0 세로
            {
                setDisplayOrientation(mCamera, 90);
                param.setRotation(90);
                mCamera.setPreviewDisplay(getHolder());
            }
            else if(degree == 90)       //90 가로
            {
                setDisplayOrientation(mCamera, 0);
                param.setRotation(0);
                mCamera.setPreviewDisplay(getHolder());
            }
            else if(degree == 180)  //180 세로
            {
                //setDisplayOrientation(mCamera, 90);
                param.setRotation(270);
                mCamera.setPreviewDisplay(getHolder());
            }
            else if(degree == 270)  //270 가로
            {
                setDisplayOrientation(mCamera, 180);
                param.setRotation(180);
                mCamera.setPreviewDisplay(getHolder());
            }
            if(degree==0  && mCameraIndex == Camera.CameraInfo.CAMERA_FACING_FRONT)
            {
                param.setRotation(270);
            }
            else if(degree==180  && mCameraIndex == Camera.CameraInfo.CAMERA_FACING_FRONT)
            {
                param.setRotation(90);
            }

        }catch (Exception e)
        {
            e.getMessage();
        }
        mCamera.setParameters(param);
    }
    @Override
    public void onShutter() {

    }

    public void takePicture(final String fileName, Bitmap faceBitmap){
        Log.d("카메라뷰","take Picture");
        this.mPictureFileName = fileName;
        mFaceBitamp = faceBitmap;

        //버퍼 클리어 프리뷰 이미지나 비디오를 초기화 하는 매소드
        mCamera.setPreviewCallback(null);
        //사진을 찍는다. 여기서 onPictureTaken을 불러온다
        mCamera.takePicture(this,null,this);

    }
    public void takePicture(final String fileName){
        Log.d("카메라뷰","take Picture");
        this.mPictureFileName = fileName;

        //버퍼 클리어 프리뷰 이미지나 비디오를 초기화 하는 매소드
        mCamera.setPreviewCallback(null);
        //사진을 찍는다. 여기서 onPictureTaken을 불러온다
        mCamera.takePicture(this,null,this);

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d("카메라뷰", "비트맵 파일을 저장한다.");
      //  mCamera.startPreview();
       // mCamera.setPreviewCallback(this);

        Camera.Parameters parameters = mCamera.getParameters();
        Log.d("프리뷰포멧", " : " + parameters.getPreviewFormat());

        Bitmap temp = BitmapFactory.decodeByteArray(data,0,data.length); //바이트를 비트맵으로 변환
        temp = PictureRotate(temp);

        //리사이즈  영상이 너무크면 처리하는데 시간이 오래걸림
        temp = BitmapControl.resizeBitmap(temp);


            mFaceBitamp = BitmapControl.resizeBitmap(mFaceBitamp);

            temp = BitmapControl.combineBitmap(temp, mFaceBitamp);



        if (mApplyManga) {
            org.opencv.core.Size newSize = new org.opencv.core.Size(temp.getWidth(), temp.getHeight());
            Mat mtmp = new Mat(newSize, CV_8UC3);
            Utils.bitmapToMat(temp,mtmp);
            Mat tmp = new Mat(newSize, CV_8UC3);

            mMangaEffectData.cartoonFilter(mtmp,tmp);
            Bitmap bmp = Bitmap.createBitmap(temp.getWidth(), temp.getHeight(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, bmp);
            Log.d("스케치 크기","크기 : "+temp.getWidth()+"   "+temp.getHeight());
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(mPictureFileName));
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Log.d("메인", "스캐치 사진 찍기");
                out.flush();
                out.close();
                MediaScanner scanner = MediaScanner.newInstance(getContext());
                scanner.mediaScanning(mPictureFileName);
            } catch (IOException e) {
                Log.e("메인", "사진 저장 실패");

                e.printStackTrace();
            }
        }
        /*
        else {
            try {
                if (!mApplyManga) {
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(mPictureFileName));

                    out.write(data);
                    out.flush();
                    out.close();
                    //갤러리에 파일 추가
                    MediaScanner scanner = MediaScanner.newInstance(getContext());
                    scanner.mediaScanning(mPictureFileName);

                }

            } catch (IOException e) {
                Log.e("카메라뷰", "포토콜벡 익셉션");
            }
        }
        */
        cameraReStart();

    }
    public void cameraReStart()
    {
        mMaxWidth = getWidth();
        mMaxHeight = getHeight();
        disconnectCamera();
        connectCamera(mMaxWidth,mMaxHeight);
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);
    }
    protected int getCameraId()
    {
        return mCameraIndex;
    }

    protected Bitmap PictureRotate(Bitmap bitmap)
    {
        if(degree==0 &&  mCameraIndex == Camera.CameraInfo.CAMERA_FACING_BACK)
            bitmap = BitmapControl.bmpRotate(bitmap,90);
        else if(degree==180 && mCameraIndex == Camera.CameraInfo.CAMERA_FACING_BACK)
            bitmap = BitmapControl.bmpRotate(bitmap, 270);
        else if(degree==270 && mCameraIndex == Camera.CameraInfo.CAMERA_FACING_BACK)
            bitmap = BitmapControl.bmpRotate(bitmap, 180);
        else if(degree == 0 &&  mCameraIndex == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            bitmap = BitmapControl.bmpRotate(bitmap, 270);
            bitmap= BitmapControl.bmpSideInversion(bitmap);
        }
        else if(degree == 90 &&  mCameraIndex == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            bitmap= BitmapControl.bmpSideInversion(bitmap);
        }
        else if(degree == 180 &&  mCameraIndex == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            bitmap = BitmapControl.bmpRotate(bitmap, 90);
            bitmap= BitmapControl.bmpSideInversion(bitmap);
        }
        else if(degree == 270 &&  mCameraIndex == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            bitmap = BitmapControl.bmpRotate(bitmap, 180);
            bitmap= BitmapControl.bmpSideInversion(bitmap);
        }
        return bitmap;
    }


}