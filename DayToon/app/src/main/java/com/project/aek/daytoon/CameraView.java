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
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.BufferedOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private Mat mGray;
    private Context mContext;



    private CascadeClassifier mCascadeFace;         //얼굴검출 Cascade
    //ArrayList<Rect> faces;                          //검출된 얼굴



    public CameraView(Context context, AttributeSet attr){
        super(context,attr);
        mContext =context;

    }
    public void loadCascade()
    {
        try{
            InputStream inStream = getResources().openRawResource(R.raw.lbpcascade_frontalface);

            File cascadeDir = mContext.getDir("cascade", Context.MODE_PRIVATE);  //주어진 이름의 응용 프로그램 하위 디렉터리를 얻거나 생성한다.
            //getDir 로 얻어서 만든 파일은 영구보관
            //getDir() : 내부스토리지 공간에 디렉토리를 생성하고나 오픈한다.
            //MODE_PRIVATE(사적인 파일), MODE_APPEND(파일의 끝에 추가) ,
            //MODE_WORLD_READABLE(다른 애플리케이션이 읽을수있음), MODE_WORLD_WRITEABLE(다른 애플리케이션일 쓸수있음)
            File cascadeFaceFile = new File(cascadeDir,"lbpcascade_frontalface.xml");  //cascade만들거나 호출
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(cascadeFaceFile);

            byte[] buf = new byte[4096];      //파일 내용을 읽을 버퍼
            int rdBytes;
            while((rdBytes = inStream.read(buf)) != -1)         //읽은 내용이 있으면
            {
                outputStream.write(buf,0,rdBytes);                //파일에 쓴다.
                Log.d("파일에 쓴다","쓴다");
            }
            outputStream.close();
            inStream.close();
            //=======================================lbpcascade_frontalface.xml를 읽은 부분=================
            //만든 파일을 읽어 Cascade생성
            mCascadeFace = new CascadeClassifier(cascadeFaceFile.getAbsolutePath());
            mCascadeFace.load(cascadeFaceFile.getAbsolutePath());       //생성한다음 반드시 읽어와야 로드가 된다.
            if(mCascadeFace.empty())
            {
                Log.e("CameraView :: Cascade","Failed to load cascade classifier");
                mCascadeFace = null;
            }
            else
            {
                Log.i("CameraView :: Cascade", "Loaded cascade classifier from "+ cascadeFaceFile.getAbsolutePath());
                //성공적으로 읽었으면 파일을 지워준다.
                cascadeFaceFile.delete();
            }
        }
        catch (IOException e)
        {

        }
        mGray = new Mat();
        //mPreviewFrame = new Mat(getWidth(),getHeight(),CV_8UC3);
       // faces = new ArrayList<Rect>();
    }
    //얼굴을 검출하는거
    public Rect[] startFaceDetect(Mat gray)
    {
        Imgproc.cvtColor(mPreviewFrame,mGray,Imgproc.COLOR_BGR2GRAY);
       // mGray = gray;
        MatOfRect faces = new MatOfRect();
        mCascadeFace.detectMultiScale(mGray,faces,1.1,2,2,new org.opencv.core.Size(80,80), new org.opencv.core.Size());

        Rect[] facesArray = faces.toArray();

        return facesArray;
    }
    public Mat getPreviewFrame()
    {
        return mPreviewFrame;
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
                setDisplayOrientation(mCamera, 90);
                param.setRotation(0);
               // mCamera.setPreviewDisplay(getHolder());
            }
            else if(degree == 180)  //180 세로
            {
                setDisplayOrientation(mCamera, 90);
                param.setRotation(270);
                //mCamera.setPreviewDisplay(getHolder());
            }
            else if(degree == 270)  //270 가로
            {
                setDisplayOrientation(mCamera, 90);
                param.setRotation(180);
               // mCamera.setPreviewDisplay(getHolder());
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

        mFaceBitamp = StickerRotate(mFaceBitamp);

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
       // mCamera.setPreviewCallback(this);
    }
    protected int getCameraId()
    {
        return mCameraIndex;
    }

    protected Bitmap StickerRotate(Bitmap bitmap)
    {
        if(degree == 0)
        {

        }
        else if(degree == 90)
        {
            bitmap = BitmapControl.bmpRotate(bitmap,270);
        }
        else if(degree == 180)
        {
            bitmap = BitmapControl.bmpRotate(bitmap, 180);
        }
        else if(degree == 270)
        {
            bitmap = BitmapControl.bmpRotate(bitmap, 90);
        }
        return bitmap;

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