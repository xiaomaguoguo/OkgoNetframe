package com.bftv.fui.net.help;

import android.app.Application;
import android.text.TextUtils;

import com.bftv.fui.net.OkGo;
import com.bftv.fui.net.callback.FileCallback;
import com.bftv.fui.net.callback.StringCallback;
import com.bftv.fui.net.gson.Gson;
import com.bftv.fui.net.model.HttpParams;
import com.bftv.fui.net.okhttp.okhttp3.Call;
import com.bftv.fui.net.okhttp.okhttp3.Response;
import com.bftv.fui.net.request.PostRequest;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author less
 * @version 1.0
 * @title 类的名称
 * @description 该类主要功能描述
 * @company 北京奔流网络信息技术有限公司
 * @created 2017/6/2 0002 20:35
 * @changeRecord [修改记录] <br/>
 */
public class NetManager<T> {

    private static final String BASR_URL = "http://ptbftv.gitv.tv/";

    private boolean isDownloading = false;

    private static NetManager ourInstance = new NetManager();

    public static NetManager getInstance() {
        return ourInstance;
    }

    private NetManager() {

    }

    public void init(Application application, boolean isDebug) {
        OkGo.init(application);
        OkGo okGo = OkGo.getInstance().setRetryCount(3).
                addCommonParams(new HttpParams("apptoken", "282340ce12c5e10fa84171660a2054f8")).
                addCommonParams(new HttpParams("version", "2.0")).
                addCommonParams(new HttpParams("from", "bftv_android"));

        if (isDebug) {
            okGo.debug("OkGo", Level.ALL, true);
        }
    }

    public void post(String method, final T t, final OnNetListener onNetListener) {
        post(method,null,t,onNetListener);
    }

    public void getMethod(String method,final T t,final OnNetListener onNetListener){
        get(BASR_URL+method,t,onNetListener);
    }

    public void get(String url,final T t,final OnNetListener onNetListener){
        OkGo.get(url).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                T tt = parse(s, t);
                if (onNetListener != null) {
                    onNetListener.onResult(tt);
                }
            }
        });
    }

    public void post(String method, HashMap<String, String> paramMap,final T t, final OnNetListener onNetListener) {
        post(method,paramMap,null,t,onNetListener);
    }

    public boolean isDownloading(){
        return isDownloading;
    }

    public void post(String method, HashMap<String, String> paramMap, String baseUrl,final T t, final OnNetListener onNetListener) {
        PostRequest postRequest = OkGo.post(TextUtils.isEmpty(baseUrl) ? BASR_URL : baseUrl)
                .tag(this);

        if(!TextUtils.isEmpty(method)){
            postRequest = postRequest.params(new HttpParams("method", method));
        }
        if (paramMap != null) {
            Iterator<Map.Entry<String, String>> entries = paramMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                postRequest = postRequest.params(new HttpParams(entry.getKey(),entry.getValue()));
            }
        }
        postRequest.execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                T tt = parse(s, t);
                if (onNetListener != null) {
                    onNetListener.onResult(tt);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                if (onNetListener != null) {
                    onNetListener.onError(response, e);
                }
            }
        });
    }

    public void fileTransport(String url,final OnNetFileListener onNetFileListener){
        OkGo.<File>get(url).tag(this).execute(new FileCallback() {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                isDownloading = false;
                onNetFileListener.onResult(file,call,response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                isDownloading = false;
                onNetFileListener.onError(call,response,e);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                isDownloading = true;
                onNetFileListener.downloadProgress(currentSize,totalSize,progress,networkSpeed);
            }
        });
    }

    public interface OnNetFileListener{

        void onResult(File file, Call call, Response response);

        void onError(Call call, Response response, Exception e);

        void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed);
    }

    public interface OnNetListener<T> {

        void onResult(T t);

        void onError(Response response, Exception e);
    }

    private T parse(String jsonResponse, T t) {
        if (!TextUtils.isEmpty(jsonResponse)) {
            return (T) new Gson().fromJson(jsonResponse, t.getClass());
        }
        return t;
    }

}
