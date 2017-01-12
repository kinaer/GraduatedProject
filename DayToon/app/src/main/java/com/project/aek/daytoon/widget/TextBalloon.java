package com.project.aek.daytoon.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.project.aek.daytoon.BitmapControl;
import com.project.aek.daytoon.R;

/**
 * Created by aek on 2017-01-05.
 */

public class TextBalloon extends TextView {
    public int vWidth;
    public int vHeight;
    private Context mContext;
    public TextBalloon(Context context)
    {
        super(context);
        mContext = context;

        vWidth = this.getWidth();
        vHeight = this.getHeight();
        init();
    }
    private void init()
    {
        float scale = getResources().getDisplayMetrics().density;   //px과 dp간의 비율
        FrameLayout.LayoutParams layParam=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        layParam.gravity = Gravity.CENTER;

        this.setLayoutParams(layParam);
        this.setTextColor(Color.BLACK);
        this.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
        this.setWidth(300* BitmapControl.pixel(getContext()));
        this.setHeight(300*BitmapControl.pixel(getContext()));
        this.setBackground(mContext.getDrawable(R.drawable.balloon01));
        this.setText("");

    }

}
