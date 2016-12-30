package com.project.aek.daytoon;

import android.app.Application;

/**
 * Created by aek on 2016-12-20.
 */

public class GlobalValue {
    private boolean screenLand = false;      //0이면 세로 1이면 가로

    public boolean getScreenLand()
    {
        return screenLand;
    }
    public void setScreenLand(boolean value)
    {
        screenLand=value;
    }
}
