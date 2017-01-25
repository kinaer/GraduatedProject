package com.project.aek.daytoon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.project.aek.daytoon.widget.Sticker;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

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

    ArrayList<Sticker> stikers = new ArrayList<Sticker>();

    private int rotateDegree = 0;

    org.opencv.core.Rect[] mCascadeFaces;
    Bitmap mrgb;

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
    public boolean getFront(){return isFont;}
    public void setIsLand(boolean setLand) {isLand = setLand;}
    public void setFaces(Camera.Face[] faces)
    {
        this.faces=faces;
    }
    public void setCascadeFaces(org.opencv.core.Rect[] faces,Bitmap rgb)
    {
        mrgb=rgb;
        mCascadeFaces = faces;
    }
    public void setRotateDegree(int degree)
    {
        rotateDegree = degree;
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
        //iStiker = BitmapFactory.decodeResource(mContext.getResources(),imgId);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        /*
        if(mrgb !=null)
        {
           // Bitmap rgbBmp = Bitmap.createBitmap(mrgb.cols(),mrgb.rows(), Bitmap.Config.ARGB_8888);
           // Utils.matToBitmap(mrgb, rgbBmp);
            canvas.drawBitmap(mrgb,0,0,paint);
        }

        if(mCascadeFaces != null){
            List<RectF> facesRect = new ArrayList<RectF>();
            for(org.opencv.core.Rect r : mCascadeFaces)
            {


                Point center = new Point();
                center.x = (int)(r.x + r.width * 0.5);
                center.y = (int)(r.y + r.height * 0.5);
                Log.e("얼굴 X " ," "+r.x+" , "+r.width);
                Log.e("얼굴 Y "," "+r.y+" , "+r.height);
                float ml = (float)(center.x - (r.width * 0.5));
                float mr = (float)(center.x + (r.width * 0.5));
                float mt = (float)(center.y - (r.height * 0.5));
                float mb = (float)(center.y + (r.height * 0.5));

                RectF uRect = new RectF(ml,mt,mr,mb);
                facesRect.add(uRect);

            }

            for (RectF temp : facesRect) {
                canvas.drawRect(temp, paint);
            }

        }
        else {
            canvas = null;
        }
*/


        if(!stikerOn)
            return;

        Matrix matrix = new Matrix();
        if(!isFont && isLand)
        {
            matrix.setScale(1,1);

        }
        else if(isFont && isLand)
        {
            matrix.setScale(-1,1);
        }
        else if(isFont && !isLand)
        {
            matrix.setScale(-1,1);
        }

        else
        {
            matrix.setScale(1,1);
        }
        matrix.postRotate(90);                          //화면을 90도 회전했으므로 회전해준다.
        matrix.postScale(mWidth/2000f,mHeight/2000f);
        matrix.postTranslate(mWidth/2f,mHeight/2f);

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        if(faces !=null) {
            if (faces.length > 0) {


                List<RectF> faceRects;
                faceRects = new ArrayList<RectF>();

                //ArrayList<Bitmap> stikers = new ArrayList<Bitmap>();
                Bitmap[] mSticker = new Bitmap[faces.length];
                for (int i = 0; i < faces.length; i++)
                {
                    stikers.add(new Sticker(mContext,currentId));
                }
            //범위가 -1000,-1000 ~ 1000,1000 까지로 나옴
                for (int i = 0; i < faces.length; i++) {
                    RectF rectF =new RectF(faces[i].rect);
                    matrix.mapRect(rectF);
                    stikers.get(i).reSizeBitmap(rectF, isLand, rotateDegree);
                   // reSizeBitmap(rectF);
                    faceRects.add(rectF);
                   // stikers.add(iStiker);


                  // Rect uRect = new Rect(l, t, r, b);
                  //  reSizeBitmap(uRect);
                   // stikers.add(iStiker);
                   // faceRects.add(uRect);
                   // faceRects.add(rectF);

                   // Log.d("찾은얼굴",rectF.left+", "+rectF.top+", "+rectF.right+", "+rectF.bottom);
                   Log.d("찾은얼굴WH",rectF.centerX()+", "+rectF.centerY());
                   // Log.d("찾은얼굴",faces[i].rect.left+", "+faces[i].rect.top+", "+faces[i].rect.right+", "+faces[i].rect.bottom);
                }
                /*
                for(Bitmap temp : stikers)
                {
                    canvas.drawBitmap(temp,x1,y1,paint);
                }
                */
                for(Sticker temp : stikers)
                {
                    canvas.drawBitmap(temp.img, temp.x1, temp.y1, null);
                }
                /*
                for (RectF temp : faceRects) {
                    canvas.drawRect(temp, paint);
                }
                */
                for(int i=0; i<stikers.size(); i++) {
                    stikers.remove(i);
                }
                stikers.clear();
            } else {
                canvas = null;
            }
        }

        super.onDraw(canvas);
    }
}
