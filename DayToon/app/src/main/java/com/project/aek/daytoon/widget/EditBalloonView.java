package com.project.aek.daytoon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by aek on 2017-01-05.
 */
//말풍선을 추가할 수 있는 이미지뷰
public class EditBalloonView extends ImageView
{
    public int vWidth;             //이미지뷰 크기
    public int vHeight;

    public EditBalloonView(Context context)
    {
        super(context);
        init();

    }
    public EditBalloonView(Context context, AttributeSet attr)
    {
        super(context,attr);
        init();

    }
    private void init()
    {
        this.setAdjustViewBounds(true);
        vWidth = this.getWidth();
        vHeight = this.getHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}