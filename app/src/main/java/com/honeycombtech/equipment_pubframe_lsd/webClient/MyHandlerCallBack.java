package com.honeycombtech.equipment_pubframe_lsd.webClient;


import android.text.TextUtils;
import android.util.Log;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;

/**
 * Created by zhoujr on 2018/3/6.
 * 自定义Handler回调
 */

public class MyHandlerCallBack implements BridgeHandler {
    private OnSendDataListener mSendDataListener;

    public MyHandlerCallBack(OnSendDataListener mSendDataListener){
        this.mSendDataListener = mSendDataListener;
    }

    @Override
    public void handler(String data, CallBackFunction function) {
        Log.e("MyHandlerCallBack","接收数据为：" + data);
        if (!TextUtils.isEmpty(data) && mSendDataListener != null) {
            mSendDataListener.sendData(data);
        }
        function.onCallBack("Native已收到消息！");
    }

    public interface OnSendDataListener {
        void sendData(String data);
    }
}
