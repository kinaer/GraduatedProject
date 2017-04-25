package com.project.aek.daytoon.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.project.aek.daytoon.R;

/**
 * Created by aek on 2017-01-11.
 */

public class StikerAdapter extends BaseAdapter {
    private Context mContext;           //액티비티 context
    private int vWidth;                 //화면 넓이
    private int vHeight;                //화면 높이
    private int imWidth;                //이미지 넓이
    private int imHeight;               //이미지 높이

    //스티커 배열
    Integer[] mStikers =
            {R.drawable.icon_a01, R.drawable.icon_a02, R.drawable.icon_a03, R.drawable.icon_a04, R.drawable.icon_a05,
             R.drawable.icon_a06, R.drawable.icon_a07, R.drawable.icon_a08, R.drawable.icon_a09, R.drawable.icon_a10,
             R.drawable.icon_a11, R.drawable.icon_a12, R.drawable.icon_a13, R.drawable.icon_a14, R.drawable.icon_a15,
             R.drawable.icon_a16, R.drawable.a25, R.drawable.a26};
    //스티커 그리드뷰 배열
    Integer[] mViewImg =
            {R.drawable.a01, R.drawable.a02, R.drawable.a03, R.drawable.a04, R.drawable.a05,
             R.drawable.a06, R.drawable.a07, R.drawable.a08, R.drawable.a09, R.drawable.a10,
             R.drawable.a11, R.drawable.a12, R.drawable.a13, R.drawable.a14, R.drawable.a15,
             R.drawable.a16, R.drawable.a25, R.drawable.a25 };


    public StikerAdapter(Context context,int Width, int Height)
    {
        mContext = context;

        Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        vWidth = display.getWidth();
        vHeight = display.getHeight()-50;
       // vWidth = Width;
        //vHeight = Height;
        Log.d("스티커 그리드뷰","크기"+Width+"  "+Height);

        if(vWidth < vHeight)        //세로일때
        {
            imWidth = Width / 5 - 30;
            imHeight = Height / 3 - 30;
        }
        else        //가로일때
        {
            imWidth = Width / 10 - 30;
            imHeight = Height  - 6;
        }

       // imWidth = 40;
       // imHeight = 40;
        Log.d("스티커 그리드뷰","크기2 "+imWidth+"  "+imHeight);
    }//생성자
    @Override
    public int getCount() {
        return mStikers.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null)
        {
            imageView = new ImageView(mContext);            //들어온 액티비티 화면에 만든다
            imageView.setLayoutParams(new GridView.LayoutParams(imWidth, imHeight));        //뷰 하나하나의 크기 지정
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);        //가운데 중심으로 나머지는 잘라
            imageView.setPadding(3, 3, 3, 3);
        }
        else
        {
            imageView = (ImageView)convertView;
        }
        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(),mStikers[position]);
        imageView.setImageBitmap(bm);

        /*
        if(bm != null && !bm.isRecycled())
        {
            bm.recycle();
        }
        */
        return imageView;
    }

    @Override
    public Object getItem(int position) {
        return mViewImg[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
