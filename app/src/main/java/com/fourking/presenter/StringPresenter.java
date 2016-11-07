package com.fourking.presenter;

import android.util.Log;

import com.fourking.module.StringModule;
import com.fourking.view.GetStringValue;

import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by pzhihao on 2016/11/6.
 */
public class StringPresenter {
    private StringModule stringModule;
    private GetStringValue getStringValue;

//    public StringPresenter(GetStringValue getStringValue) {
//        stringModule =new StringModule();
//        this.getStringValue = getStringValue;
//    }

    //使用Gagger2注入实例
    public StringPresenter(StringModule stringModule, GetStringValue getStringValue) {
        this.stringModule = stringModule;
        this.getStringValue = getStringValue;
    }

    public void getStringOver(String url, Map<String,String> parammap){
        stringModule.getString( url,parammap)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.v("Main","线程在："+Thread.currentThread().getName());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        getStringValue.getAfter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getStringValue.getStringFailed(e.getMessage());
                    }
                    @Override
                    public void onNext(String s) {
                        getStringValue.getStringSuccess(s);
                    }
                });
    }
}
