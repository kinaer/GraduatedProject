package com.project.aek.daytoon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by aek on 2017-01-07.
 */

public class BitmapControl {
    public static Bitmap bmpSideInversion(Bitmap bitmap)
    {
        //좌우 반전
        Matrix m = new Matrix();
        m.setScale(-1,1);

        try
        {
            Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            if(bitmap != converted)
            {
                bitmap.recycle();
                bitmap = converted;
            }
        }
        catch(OutOfMemoryError ex)
        {
            ex.printStackTrace();
        }

        return bitmap;

    }

    //비트맵 회전
    public static Bitmap bmpRotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            //각도만큼 비트맵을 회전한다 크기의 절반씩 건내줘야 회전 잘됨

            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }
    public static Bitmap resizeBitmap(Bitmap src)
    {
        int mWidth = src.getWidth();
        int mHeight = src.getHeight();
        int newWidth = mWidth;
        int newHeight = mHeight;
        float rate =0.0f;

        //비율 조절
        if(mWidth>mHeight)  //가로가 더큰경우
        {
            //if(640 <= mWidth)            //최대 기준치보다 크면
           // {
                rate = 640/(float)mWidth;
                // newHeight = (int)(mHeight*rate);
                newHeight = 360;
                newWidth = 640;
           // }
        }
        else        //세로가 더 큰경우
        {
           // if(640<=mHeight)
           // {
                rate = 640 /(float)mHeight;
                // newWidth = (int)(mWidth*rate);
                newWidth=360;
                newHeight = 640;
           // }
        }
        return Bitmap.createScaledBitmap(src,newWidth,newHeight,true);
    }

    public static Bitmap combineBitmap(Bitmap pic, Bitmap face)
    {
        Bitmap result =null;
        int witdh, height = 0;

        witdh = pic.getWidth();
        height = pic.getHeight();

        result = Bitmap.createBitmap(witdh,height, Bitmap.Config.ARGB_8888);

        Canvas combine = new Canvas(result);
        combine.drawBitmap(pic,0,0,null);
        combine.drawBitmap(face,0,0,null);
        return result;
    }

    public static int dp(Context context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int dp = (int)(160/density);

        return dp;
    }

    public static int pixel(Context context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int pixel = (int)(density/160);          //dp->픽셀 변환

        return pixel;
    }
}
