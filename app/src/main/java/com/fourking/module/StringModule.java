package com.fourking.module;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx.RxAdapter;

import java.util.Map;

import rx.Observable;

/**
 * Created by pzhihao on 2016/11/6.
 */
public class StringModule {
    public Observable<String> getString(String url, Map<String,String> parammap){
        return OkGo.get(url)
                .params(parammap)
                .getCall(StringConvert.create(), RxAdapter.<String>create());
    }
}
