package com.project.aek.daytoon;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.ScriptIntrinsicLUT;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by aek on 2016-11-13.
 */

public class MangaEffect{
    public static final int
            SKETCH = 0,
            SKETCH_C=1,
            PAINT=2;

    private String[] mMangaEffects={
            "Sketch",
            "Sketch-Color",
            "Paint"
    };

    private int effectType;
    private boolean mSketchMode = false;
    private int blackBr;        //범위를 -20~50 까지로 하자 시크바는 0~70 이니 -20 배야되
    private int colorSr;        //컬러 밝기 범위 -30~30까지
    private int saturation;     //채도 범위 -30~30
    public MangaEffect(){

        blackBr = 35;
        colorSr = 50;
        saturation = 50;
    }
    public int getBlackBr(){return blackBr;}
    public void setBlackBr(int br){blackBr=br;}

    public int getColorSr(){return colorSr;}
    public void setColorSr(int sr){colorSr=sr;}

    public int getSaturation(){return saturation;}
    public void setSaturation(int s){saturation=s;}

    public int getEffect(){
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
            case "Paint":
                effectType=PAINT;
                break;

        }
    }
    public void reSetting(){
        effectType=-1;
    }

    public void setFrame(long mSource, long mDst){
        //CartoonifyImage(mSource,mDst, effectType,blursize);
        //Cartoonify(mSource,mDst,effectType);

        /*
        ImageView a;
        a.getDrawable();
        BitmapDrawable d;
        d.getBitmap();
        */
    }

    public void cartoonFilter(Mat src,Mat dst)
    {

        if(effectType == PAINT)
    {

        Imgproc.cvtColor(src,src,Imgproc.COLOR_BGRA2BGR);
        Imgproc.cvtColor(dst,dst,Imgproc.COLOR_BGRA2BGR);
        OilPaintFilter(src,dst,20,7);

            /*
            Imgproc.cvtColor(src,src,Imgproc.COLOR_BGRA2BGR);
            Imgproc.cvtColor(dst,dst,Imgproc.COLOR_BGRA2BGR);

            Mat tempSrc =new Mat(src.size(),CV_8UC3);
            src.copyTo(tempSrc);

            OilPaintFilter(src,tempSrc,20,9);

            Mat img_gray = new Mat(src.size(),CV_8UC1);
            Mat img_blur = new Mat(src.size(),CV_8UC1);
            Mat img_edge = new Mat(src.size(),CV_8UC1);

            Imgproc.cvtColor(src,img_gray,Imgproc.COLOR_BGR2GRAY);      //그레이스케일로 변환
            Imgproc.medianBlur(img_gray,img_blur,9);                //미디안블로 적용
            //세번째 인자 최대값,네번째 인자는 적용 알고리즘, 다섯번째는 임계값 유형, 여섯번째는 블록크기,
            //이러고 이진화한걸 에지로 쓴다.
            Imgproc.adaptiveThreshold(img_blur,img_edge,255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,9,2);

            Imgproc.cvtColor(img_edge,img_edge,Imgproc.COLOR_GRAY2BGR);
            Core.bitwise_and(tempSrc,img_edge,dst);
*/

    }
        else
        {


        if(effectType == SKETCH)
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
            Mat mBlock2 = new Mat(src.size(), CV_8UC1); //진한 회색
            Mat mBlock3 = new Mat(src.size(), CV_8UC1); //옅한 회색
            Mat IMat6 = new Mat(src.size(), CV_8UC1);   //진한 회색 음영
            Mat IMat7 = new Mat(src.size(), CV_8UC1);   //옅은 회색  음영

            mWhite.setTo(new Scalar(255,255,255));      //흰색으로 초기화
            mBlock.setTo(new Scalar(0,0,0));            //검은색으로 초기화
            mBlock2.setTo(new Scalar(100,100,100));     //진한 회색으로 초기화
            mBlock3.setTo(new Scalar(150,150,150));     //옅한 회색으로 초기화
            gray2.setTo(new Scalar(255,255,255));       //카툰 음영마스크를 입힐꺼라 흰색으로 초기화
            screentone(gray2);                  //gray2에 카툰 음영을 입힘
            Imgproc.cvtColor(src,gray,Imgproc.COLOR_BGR2GRAY);     //원본영상을 그레이스케일로 전환
            //Imgproc.GaussianBlur(gray,gray,new Size(3,3),3);    //가우시안 블로를 한다.
            brightnessCorrection(gray);

            Imgproc.Canny(gray,lMat4, 100,120);
            //Imgproc.Canny(gray,lMat4, 80,90);
            Imgproc.threshold(gray, lMat1, 40, 255,Imgproc.THRESH_BINARY_INV);  //검정 효과 범위
            Imgproc.threshold(gray, IMat6, 60, 255, Imgproc.THRESH_BINARY_INV);  //진한 회색 효과 범위
            Imgproc.threshold(gray, IMat7, 80, 255, Imgproc.THRESH_BINARY_INV);  //진한 회색 효과 범위
            Imgproc.threshold(gray, lMat2, 95, 255,Imgproc.THRESH_BINARY_INV);  //음영 효과 범위
            //Imgproc.Canny(gray, lMat3,120,140);       //외곽선 구할 에지
            Imgproc.Canny(gray, lMat3,140,160);


            ArrayList mList = new ArrayList(400);
            Imgproc.findContours(lMat3,mList,lMat5,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);   //외곽선을 찾는다
            //Imgproc.drawContours(gray, mList, -1,new Scalar(0,0,0),2);


            mWhite.copyTo(dst);
            gray2.copyTo(dst,lMat2);
            mBlock3.copyTo(dst, IMat7);
            mBlock2.copyTo(dst, IMat6);
            mBlock.copyTo(dst,lMat1);
            mBlock.copyTo(dst,lMat4);


           Imgproc.drawContours(dst,mList,-1,new Scalar(0,0,0),2);

        }
        else if(effectType == SKETCH_C)
        {
            Mat temp = new Mat(src.size(),CV_8UC3);
            Mat gray = new Mat(src.size(),CV_8UC1);
            Mat edge = new Mat(src.size(),CV_8UC1);
            Mat edgeBr = new Mat(src.size(),CV_8UC3);
            Mat result = new Mat(src.size(),CV_8UC3);
            Mat t = new Mat(src.size(),CV_8UC3);
            ArrayList mList = new ArrayList(400);


            Imgproc.cvtColor(src,src,Imgproc.COLOR_BGRA2BGR);

            //만화틱 흐림효과
            Imgproc.pyrMeanShiftFiltering(src,temp,20,20,3,new TermCriteria(TermCriteria.MAX_ITER+ TermCriteria.EPS,5,1));


            colorBrCorrection(temp);        //밝기 조정

            Imgproc.cvtColor(temp,gray,Imgproc.COLOR_BGR2GRAY);



            //에지 검출
            Imgproc.Canny(gray,edge,130,130);
            Imgproc.cvtColor(edge,edgeBr,Imgproc.COLOR_GRAY2BGR);

            Imgproc.findContours(edge,mList,t,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

            Log.d("임시 파일"," "+temp.channels()+" , "+temp.size());
            Log.d("엣지"," "+edgeBr.channels()+" , "+edgeBr.size());

            temp.copyTo(dst);

            Imgproc.drawContours(dst,mList,-1,new Scalar(0,0,0),1);

        }
        }


    }//cartoonFilter
    void colorBrCorrection(Mat src)
    {
        Imgproc.cvtColor(src,src,Imgproc.COLOR_BGR2HSV);
        src.convertTo(src,CvType.CV_64FC3);

        int matSize = (int)(src.total() * src.channels());
        double[] data = new double[matSize];
        src.get(0,0,data);
        double temp=0;
        int srcCols = src.cols() * 3;
        int colorBr = colorSr -50;
        int tempSt = saturation -50;

        for(int y=0; y<src.rows();++y){
            for(int x=0; x<src.cols()*3; x+=3){
                temp = data[x+2+srcCols*y];     //명도를 가져온다
                temp += colorBr;
                if(temp >127){      //밝으면
                    temp=(temp - 127)*0.8;
                    temp = data[x+2+srcCols*y] +temp;
                }//밝을때
                else if(temp <= 127){
                    temp=(127 - temp)*0.8;
                    temp = data[x+2+srcCols*y] - temp;
                }
                if(temp > 250)
                    temp = 255;
                else if(temp < 10)
                    temp = 0;
                data[x+2+srcCols*y] = temp;         //명 암 조정
                temp = data[x+1+srcCols*y];         //채도를 가져온다
                temp = data[x+1+srcCols*y] + tempSt;
                if(temp >=255)
                    temp =255;
                else if(temp <5)
                    temp =0;
                data[x+1+srcCols*y] = temp;

            }//for x
        }//for y

        src.put(0,0,data);

        src.convertTo(src,CvType.CV_8UC3);
        Imgproc.cvtColor(src,src,Imgproc.COLOR_HSV2BGR);
    }//colorScreentone

    void screentone(Mat gray)
    {
        //그레이 스케일만 받는다.
        for(int y=0; y < gray.rows(); y++)
        {
            for(int x=0; x<gray.cols(); x++)
            {
                if((x%3==0))
                    gray.put(y,x,180);
            }
        }
    }
    public void setCaptureManga(int width, int height, byte[] data, int[] frame){
        setCaptureFrame(width,height,data,frame,mSketchMode);
    }

    //밝기 조절하는거
    private void brightnessCorrection(Mat gray){
        //그레이 스케일 영상만 받는다.
        gray.convertTo(gray,CvType.CV_64FC1);
        int graySize = (int)(gray.total() + gray.channels());
        double[] data = new double[graySize];
        gray.get(0,0,data);     //여기에 1차원배열로 만들어
        double intensity=0;
        double temp=0;
        int brRate = blackBr - 20;
        for(int i=0 ; i<graySize; ++i)
        {
            data[i] += brRate;
            if(data[i] >= 127)       //허연거면
            {
                intensity = (data[i] - 127) * 0.4;
                temp = data[i] + intensity;
            }
            else if(data[i] < 127)
            {
                intensity = (127 - data[i]) * 0.4;
                temp = data[i] - intensity;
            }
            if(temp >250)
                temp = 255;
            else if(temp < 5)
                temp =0;
            data[i] = temp;
        }
        gray.put(0,0,data);
        gray.convertTo(gray,CvType.CV_8UC1);
    }

    public void OilPaintFilter(Mat src, Mat dst, int levels, int filterSize)
    {
        int[] intensityBin = new int[levels];
        int[] blueBin = new int[levels];
        int[] greenBin = new int[levels];
        int[] redBin = new int[levels];

        levels -=1;

        int filterOffset = (filterSize - 1) / 2;
        int byteOffset = 0;
        int calcOffset = 0;
        int currentIntensity = 0;
        int maxIntensity = 0;
        int maxindex = 0;

        double blue = 0;
        double green = 0;
        double red = 0;

        Size srcSize = src.size();
        float divide3 = 1/3.0f;
        float divide255 = 1/255.0f;

        src.convertTo(src, CvType.CV_64FC3);
        dst.convertTo(dst, CvType.CV_64FC3);
        int size = (int)(src.total() * src.channels());
        double[] srcData = new double[size];
        double[] dstData = new double[size];
        src.get(0,0,srcData);             //이게 src의 1차원 배열
        int srcCols = src.cols() * 3;

        for(int offsetY = filterOffset; offsetY < srcSize.height - filterOffset; offsetY++)
        {
            for(int offsetX =  filterOffset; offsetX < srcSize.width - filterOffset; offsetX++)
            {
                blue = green = red = 0;     //BGR 0으로 초기화

                currentIntensity = maxIntensity = maxindex = 0;     //intensity index 초기화

                intensityBin = new int[levels + 1];
                blueBin = new int[levels + 1];
                greenBin = new int [levels + 1];
                redBin = new int[levels + 1];

                byteOffset = offsetY * srcCols + offsetX *3;

                for(int filterY = -filterOffset; filterY <= filterOffset; filterY++)
                {
                    for(int filterX = -filterOffset; filterX <= filterOffset; filterX++)
                    {

                        calcOffset = byteOffset + (filterX *3) + (filterY * srcCols);

                        currentIntensity = (int)Math.round(((float)
                                (srcData[calcOffset] + srcData[calcOffset + 1] + srcData[calcOffset + 2])
                        *divide3 * (levels)) * divide255);


                        intensityBin[currentIntensity] += 1;
                        blueBin[currentIntensity] += srcData[calcOffset];
                        greenBin[currentIntensity] += srcData[calcOffset + 1];
                        redBin[currentIntensity] += srcData[calcOffset + 2];

                        if(intensityBin[currentIntensity] > maxIntensity)
                        {
                            maxIntensity = intensityBin[currentIntensity];
                            maxindex = currentIntensity;
                        }
                    }//filterX
                }//filterY

                blue = (blueBin[maxindex] / maxIntensity);
                green = (greenBin[maxindex] / maxIntensity);
                red = (redBin[maxindex] / maxIntensity);

                dstData[byteOffset] = blue;
                dstData[byteOffset + 1] = green;
                dstData[byteOffset + 2] = red;

            }//offsetX
        }//offsetY
        dst.put(0,0,dstData);
        src.convertTo(src,CvType.CV_8UC3);
        dst.convertTo(dst,CvType.CV_8UC3);

    }//OilPaintFilter
//NDK설정
    static{
    System.loadLibrary("cartoonfier");
    }
    private native void CartoonifyImage(long mSource, long mDst,int effect,int blurSize);
    private native void setCaptureFrame(int width, int height, byte[] data, int[] frame,boolean sketchMode );
    private native void Cartoonify(long mSource, long mDst,int effect);
}
