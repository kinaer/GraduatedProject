package com.project.aek.daytoon.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import com.project.aek.daytoon.BitmapControl;

/**
 * Created by aek on 2017-01-24.
 */

public class Sticker {
    private int id;
    public Bitmap img;
    private Context mContext;
    private int iWidth;
    private int iHeight;
    public int x1;
    public int y1;


    public Sticker(Context context, int mId)
    {
        mContext = context;
        id = mId;
        //img = BitmapFactory.decodeResource(mContext.getResources(), id);
    }

    public void reSizeBitmap(RectF rect, boolean isLand, int degree)
    {
        if(degree == 90)
        {

            iWidth = (int)(rect.width() * 1.7f);
            iHeight = (int)(rect.height() * 1.5f);

            img = BitmapFactory.decodeResource(mContext.getResources(),id);
            img = BitmapControl.bmpRotate(img,90);

            img = Bitmap.createScaledBitmap(img,iWidth,iHeight,true);
            // x1 = (int)(rect.centerY()-iHeight/(1.6f));
            // y1 = (int)(rect.centerX()-iWidth/2.2f);         //왼쪽위 좌표들
            x1 = (int)(rect.centerX()-iWidth/3f);        //왼쪽위 좌표들
            y1 = (int)(rect.centerY()-iHeight/(2.2f));

        }
        else if(degree == 270)
        {
            iWidth = (int)(rect.width() * 1.7f);
            iHeight = (int)(rect.height() * 1.5f);

            img = BitmapFactory.decodeResource(mContext.getResources(),id);
            img = BitmapControl.bmpRotate(img,270);

            img = Bitmap.createScaledBitmap(img,iWidth,iHeight,true);
            // x1 = (int)(rect.centerY()-iHeight/(1.6f));
            // y1 = (int)(rect.centerX()-iWidth/2.2f);         //왼쪽위 좌표들
            x1 = (int)(rect.centerX()-iWidth/1.5f);        //왼쪽위 좌표들
            y1 = (int)(rect.centerY()-iHeight/(2.1f));
        }
        else if(degree == 180)
        {
            iWidth = (int)(rect.width() *1.5f);
            iHeight = (int)(rect.height() * 1.7f);
            img = BitmapFactory.decodeResource(mContext.getResources(),id);
            img = BitmapControl.bmpRotate(img,180);

            img = Bitmap.createScaledBitmap(img,iWidth,iHeight,true);

            x1 = (int)(rect.centerX()-iWidth/2.1);        //왼쪽위 좌표들
            y1 = (int)(rect.centerY()-iHeight/(3.2));
        }
        else
        {

            iWidth = (int)(rect.width() *1.5f);
            iHeight = (int)(rect.height() * 1.7f);
            img = BitmapFactory.decodeResource(mContext.getResources(),id);

            img = Bitmap.createScaledBitmap(img,iWidth,iHeight,true);

            x1 = (int)(rect.centerX()-iWidth/2.1);        //왼쪽위 좌표들
            y1 = (int)(rect.centerY()-iHeight/(1.5));

        }

    }

}
