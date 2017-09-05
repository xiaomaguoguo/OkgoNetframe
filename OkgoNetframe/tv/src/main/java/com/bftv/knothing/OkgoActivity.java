package com.bftv.knothing;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bftv.fui.net.help.NetManager;
import com.bftv.fui.net.okhttp.okhttp3.Response;

import java.util.HashMap;

/**
 * Created by MaZhihua on 2017/9/4.
 */

public class OkgoActivity extends Activity {

    public static final String TAG = OkgoActivity.class.getSimpleName();

    private Button button = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //不带参数
//                NetManager.getInstance().post(Methods.WELCOME, new StudentClass(), onNetListener);

                //带参数
                HashMap<String,String> params = new HashMap<>();
                NetManager.getInstance().post(Methods.WELCOME,params,new StudentClass(),onNetListener);
            }
        });
    }

    private NetManager.OnNetListener onNetListener = new NetManager.OnNetListener() {
        @Override
        public void onResult(Object o) {
            StudentClass result = (StudentClass) o;
            Log.d(TAG,"返回的结果 result = " + "XXXXXXXX");
        }

        @Override
        public void onError(Response response, Exception e) {
            Log.d(TAG,"onError msg = " + e.getMessage());
        }
    };

    //返回的实体类
    public class StudentClass{

    }
}
