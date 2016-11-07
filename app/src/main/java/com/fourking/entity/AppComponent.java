package com.fourking.entity;

import com.fourking.myapplication.MainActivity;

import dagger.Component;

/**
 * Created by pzhihao on 2016/11/7.
 */

@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
}
