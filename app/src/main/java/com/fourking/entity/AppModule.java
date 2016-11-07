package com.fourking.entity;

import com.fourking.module.StringModule;
import com.fourking.presenter.StringPresenter;
import com.fourking.view.GetStringValue;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pzhihao on 2016/11/7.
 */

@Module
public class AppModule {
    private GetStringValue getStringValue;

    public AppModule(GetStringValue getStringValue) {
        this.getStringValue = getStringValue;
    }

    @Provides
    public StringModule provideStringModule(){
        return new StringModule();
    }
    @Provides
    public StringPresenter provideStringPresenter(StringModule stringModule){
        return new StringPresenter(stringModule,getStringValue);
    }
}
