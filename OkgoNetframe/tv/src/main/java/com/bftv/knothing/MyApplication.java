package com.bftv.knothing;

import android.app.Application;

import com.bftv.fui.net.help.NetManager;

/**
 * Created by MaZhihua on 2017/9/4.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetManager.getInstance().init(this,true);
    }
}
