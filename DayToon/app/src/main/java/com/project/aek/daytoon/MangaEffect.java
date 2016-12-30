package com.project.aek.daytoon;

import android.graphics.drawable.BitmapDrawable;
import android.renderscript.ScriptIntrinsicLUT;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by aek on 2016-11-13.
 */

public class MangaEffect {
    public static final int
            SKETCH = 0,
            SKETCH_C=1;

    private String[] mMangaEffects={
            "Sketch",
            "Sketch-Color"

    };

    private int effectType;
    private boolean mSketchMode = false;

    public MangaEffect(){

    }

    public int getmMangeEffect(){
        return effectType;
    }
    public void setEffect(int i){
        switch (mMangaEffects[i]){
            case "Sketch":
                effectType=SKETCH;
                break;
            case "Sketch-Color":
                effectType=SKETCH_C;
                break;

        }
    }
    public void reSetting(){
        effectType=-1;
    }

    public void setFrame(long mSource, long mDst){
        //CartoonifyImage(mSource,mDst, effectType,blursize);
        //Cartoonify(mSource,mDst,effectType);

        Log.d("만가 이펙트","스케치 호출 완료");
        /*
        ImageView a;
        a.getDrawable();
        BitmapDrawable d;
        d.getBitmap();
        */
    }

    public void cartoonFilter(Mat src,Mat dst)
    {
        Mat gray = new Mat(src.size(),CV_8UC1);
        Mat gray2 = new Mat(src.size(),CV_8UC1);
        Mat lMat1 = new Mat(src.size(),CV_8UC1);
        Mat lMat2 = new Mat(src.size(),CV_8UC1);
        Mat lMat3 = new Mat(src.size(),CV_8UC1);
        Mat lMat4 = new Mat(src.size(),CV_8UC1);
        Mat lMat5 = new Mat(src.size(),CV_8UC1);
        Mat mWhite = new Mat(src.size(),CV_8UC1);   //흰색
        Mat mBlock = new Mat(src.size(), CV_8UC1);  //검은색

        mWhite.setTo(new Scalar(255,255,255));      //흰색으로 초기화
        mBlock.setTo(new Scalar(0,0,0));            //검은색으로 초기화
        gray2.setTo(new Scalar(255,255,255));       //카툰 음영마스크를 입힐꺼라 흰색으로 초기화
        screentone(gray2);                  //gray2에 카툰 음영을 입힘
        Imgproc.cvtColor(src,gray,Imgproc.COLOR_BGR2GRAY);     //원본영상을 그레이스케일로 전환
        Imgproc.GaussianBlur(gray,gray,new Size(3,3),3);    //가우시안 블로를 한다.

       // Imgproc.Canny(gray,lMat3, 160,220);
        Imgproc.Canny(gray,lMat4, 45,70);
        Imgproc.threshold(gray, lMat1, 22, 255,Imgproc.THRESH_BINARY_INV);  //검정 효과 범위
        Imgproc.threshold(gray, lMat2, 70, 255,Imgproc.THRESH_BINARY_INV);  //음영 효과 범위
        Imgproc.Canny(gray, lMat3,90,140);       //외곽선 구할 에지

       // Mat lMat6 = new Mat();
        ArrayList mList = new ArrayList(400);
        Imgproc.findContours(lMat3,mList,lMat5,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);   //외곽선을 찾는다
        //Imgproc.drawContours(gray, mList, -1,new Scalar(0,0,0),2);

        if(effectType == SKETCH)
        {

            mWhite.copyTo(dst);
            gray2.copyTo(dst,lMat2);
            mBlock.copyTo(dst,lMat1);
            mBlock.copyTo(dst,lMat4);

            Imgproc.drawContours(dst,mList,-1,new Scalar(0,0,0),2);
        }
        if(effectType == SKETCH_C)
        {

            //바이레터럴 필터가 매우 느리므로 이미지를 축소 시켜 작업하고 복원한다.
            Imgproc.cvtColor(src,src,Imgproc.COLOR_BGRA2BGR);
            Size size = src.size();
            Size smallSize= new Size();
            smallSize.width = size.width/2;
            smallSize.height = size.height/2;
            Mat smallImg = new Mat(smallSize,CV_8UC3);
            Imgproc.resize(src,smallImg,smallSize,0,0,Imgproc.INTER_LINEAR);

            Mat tmp = new Mat(smallSize,CV_8UC3);   //임시로 저장할 Mat
            int repetitions=4;      //바이레터럴 필터 반복횟수
            int ksize = 7;          //필터의 크기
            double sigmaColor =8.0;     //필터의 컬러 강도
            double sigmaSpace = 8.0;     //필터의 공간강도
            for(int i=0; i<repetitions;i++)
            {
                Imgproc.bilateralFilter(smallImg,tmp,ksize,sigmaColor,sigmaSpace);
                Imgproc.bilateralFilter(tmp,smallImg,ksize,sigmaColor,sigmaSpace);
            }
            //원래 이미지로 복귀
            Mat img=new Mat(size,CV_8UC3);
            Imgproc.resize(tmp, img,size,0,0,Imgproc.INTER_LINEAR);

            dst.setTo(new Scalar(0));       //검정색으로 초기화

            Imgproc.cvtColor(img,img,Imgproc.COLOR_BGR2HSV);        //HSV로 변경해서 채도랑 명도를 건든다.
            colorScreentone(img);
            Imgproc.cvtColor(img,img,Imgproc.COLOR_HSV2BGR);        //다시 돌려준다.

            mWhite.copyTo(gray);
            mBlock.copyTo(gray,lMat1);
            //mBlock.copyTo(gray,lMat4);
            img.copyTo(dst,gray);
            Imgproc.drawContours(dst,mList,-1,new Scalar(0,0,0),2);

        }

    }//cartoonFilter
    void colorScreentone(Mat src)
    {
        double[] data=null;
        Size size = src.size();
        //HSV형 영상만 받아야된다.
        for(int y=0; y < size.height;y++)
        {
            for(int x=0; x < size.width ;x++)
            {
                data = src.get(y,x);

                //채도 평준화
                if(data[1] <= 40)
                {
                    data[1] = 0;
                }
                else if(data[1] >40 && data[1]<=90)
                {
                    data[1]=75;
                }
                else if(data[1] >90 && data[1]<=140)
                {
                    data[1]=85;
                }
                else if(data[1] >140 && data[1]<=190)
                {
                    data[1]=160;
                }
                else if(data[1] >190 && data[1]<=240)
                {
                    data[1]=190;
                }
                else if(data[1] >240)
                {
                    data[1]=255;
                }

                //명도 평준화
                if(data[2] <= 50)
                {
                    data[2] = 0;
                }
                else if(data[2] > 50 && data[2] <= 100)
                {
                    data[2]=50;
                }
                else if(data[2] > 100 && data[2] <= 150)
                {
                    data[2]=120;
                }
                else if(data[2] > 200 && data[2] <= 250)
                {
                    data[2]=220;
                }
                else if(data[2] > 250)
                {
                    data[2]=255;
                }
                src.put(y,x,data);

            }//forx
        }//fory
    }//colorScreentone

    void screentone(Mat gray)
    {
        //그레이 스케일만 받는다.
        for(int y=0; y < gray.rows(); y++)
        {
            for(int x=0; x<gray.cols(); x++)
            {
                if((x%3==0))
                    gray.put(y,x,150);
            }
        }
    }
    public void setCaptureManga(int width, int height, byte[] data, int[] frame){
        setCaptureFrame(width,height,data,frame,mSketchMode);
    }
//NDK설정
    static{
    System.loadLibrary("cartoonfier");
    }
    private native void CartoonifyImage(long mSource, long mDst,int effect,int blurSize);
    private native void setCaptureFrame(int width, int height, byte[] data, int[] frame,boolean sketchMode );
    private native void Cartoonify(long mSource, long mDst,int effect);
}
