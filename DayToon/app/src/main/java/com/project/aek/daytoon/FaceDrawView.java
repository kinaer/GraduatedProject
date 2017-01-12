package com.project.aek.daytoon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aek on 2016-12-28.
 */

public class FaceDrawView extends View {
    Camera.Face[] faces;
    List<Rect> rects;
    private int mWidth;
    private int mHeight;
    private int iWidth;
    private int iHeight;
    private Bitmap iStiker;
    private int x1;
    private int y1;

    private int currentId;
    private boolean stikerOn = false;

    Paint paint =new Paint();
    private boolean isLand =false;
    private boolean isFont=false;
    Context mContext;
    public FaceDrawView(Context context)
    {
        super(context);
        mContext = context;
        iStiker = BitmapFactory.decodeResource(context.getResources(),R.drawable.a26);
        if(iStiker != null)
        {
            iWidth = iStiker.getWidth();
            iHeight = iStiker.getHeight();
        }


    }
    public FaceDrawView(Context context, AttributeSet attr)
    {
        super(context,attr);
    }
    public void sizeReverse()
    {
        int temp = mWidth;
        mWidth = mHeight;
        mHeight = temp;

    }
    public void setSize(int mWidth,int mHeight)
    {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }
    public void setIsFront(boolean setFront)
    {
        isFont=setFront;
    }
    public void setIsLand(boolean setLand) {isLand = setLand;}
    public void setFaces(Camera.Face[] faces)
    {
        this.faces=faces;
    }
    public void setStikerOn(boolean on)
    {
        stikerOn = on;
    }
    public boolean getStikerOn()
    {
        return stikerOn;
    }
    public void setBitmapId(int imgId)
    {

        if(currentId == imgId)
        {
            stikerOn = false;
            currentId = 0;
        }
        else
        {
            stikerOn = true;
            currentId = imgId;
        }
        Log.d("스티커 아이디"," "+currentId);
        iStiker = BitmapFactory.decodeResource(mContext.getResources(),imgId);

    }
    public void reSizeBitmap(RectF rect)
    {

        if(isLand)
        {

            iWidth = (int)(rect.width() *1.5f);
            iHeight = (int)(rect.height() * 2f);
            iStiker = Bitmap.createScaledBitmap(iStiker,iWidth,iHeight,true);
            x1 = (int)(rect.centerX()-iWidth/2);        //왼쪽위 좌표들
            y1 = (int)(rect.centerY()-iHeight/(1.8));

        }
        else
        {
            iWidth = (int)(rect.width() *2.2f);
            iHeight = (int)(rect.height() * 1.4f);
            iStiker = Bitmap.createScaledBitmap(iStiker,iWidth,iHeight,true);
            x1 = (int)(rect.centerX()-iWidth/2);        //왼쪽위 좌표들
           y1 = (int)(rect.centerY()-iHeight/(2));

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(!stikerOn)
            return;

        Matrix matrix = new Matrix();
        matrix.setScale(isFont ? -1 : 1,1);     //앞이면 좌우 반전시켜주고 아니면 그냥
       // matrix.setScale(1,1);
        matrix.postScale(mWidth/2000f,mHeight/2000f);
        matrix.postTranslate(mWidth/2f,mHeight/2f);


        if(faces !=null) {
            if (faces.length > 0) {
                /*
                List<Rect> faceRects;
                faceRects = new ArrayList<Rect>();
                */

                List<RectF> faceRects;
                faceRects = new ArrayList<RectF>();

                ArrayList<Bitmap> stikers = new ArrayList<Bitmap>();
            //범위가 -1000,-1000 ~ 1000,1000 까지로 나옴
                for (int i = 0; i < faces.length; i++) {
                    RectF rectF =new RectF(faces[i].rect);
                    matrix.mapRect(rectF);
                    reSizeBitmap(rectF);
                    stikers.add(iStiker);
/*
                    int left = faces[i].rect.left +1000;
                    int right = faces[i].rect.right+1000;
                    int top = faces[i].rect.top+1000;
                    int bottom = faces[i].rect.bottom+1000;


                    int l = ((left)) * mWidth/2000;
                    int t = ((top)) * mHeight/2000;
                    int r = ((right)) * mWidth/2000;
                    int b = ((bottom)) * mHeight/2000;
*/

                  // Rect uRect = new Rect(l, t, r, b);
                  //  reSizeBitmap(uRect);
                   // stikers.add(iStiker);
                   // faceRects.add(uRect);
                   // faceRects.add(rectF);

                    Log.d("찾은얼굴",rectF.left+", "+rectF.top+", "+rectF.right+", "+rectF.bottom);
                    Log.d("찾은얼굴",faces[i].rect.left+", "+faces[i].rect.top+", "+faces[i].rect.right+", "+faces[i].rect.bottom);
                }
                for(Bitmap temp : stikers)
                {
                    canvas.drawBitmap(temp,x1,y1,paint);
                }
/*
                for (RectF temp : faceRects) {
                    canvas.drawRect(temp, paint);
                }
*/
            } else {
                canvas = null;
            }
        }
        super.onDraw(canvas);
    }
}
