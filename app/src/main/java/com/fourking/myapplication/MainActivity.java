package com.fourking.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.fourking.entity.AppComponent;
import com.fourking.entity.AppModule;
import com.fourking.entity.DaggerAppComponent;
import com.fourking.presenter.StringPresenter;
import com.fourking.view.GetStringValue;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;
import java.lang.reflect.Method;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements GetStringValue {


    private static final int DOWNLOAD_OK =1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    StringPresenter stringPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle("这是标题");
        setSupportActionBar(toolbar);

//        StringPresenter s=new StringPresenter(this);
//        s.getStringOver("https://www.baidu.com/",null);


        /*AppCompoent build = DaggerAppCompoent.builder().appModule(new AppModule(this)).build();
        build.inject(this);
        stringPresenter.getStringOver("https://www.baidu.com/",null);*/

        AppComponent build = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        build.inject(this);
        stringPresenter.getStringOver("https://www.baidu.com/",null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.cart:
                Log.v("Main","点击购物车");
                break;
            case R.id.dan:

                showEditTextDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void Download(String url){
        OkGo.get(url)
                .tag(this)
                .execute(new FileCallback() {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        Log.v("Main",file.getName()+"已下载完成");
                    }
                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        double pro=(progress*100);
                        Log.v("Main","当前大小："+currentSize+"/总大小："+totalSize+"/进度："+pro+"/网速："+networkSpeed/1024+"kb/s");
                    }
                });
    }
    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)//提示用户为什么需要此权限
     void showWhy(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("权限测试")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();//再次执行请求
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)//一旦用户拒绝了
     void denied() {
        Toast.makeText(this, "真的不给权限吗", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)//用户选择的不再询问
    void notAsk() {
        Toast.makeText(this, "好的不问了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showEditTextDialog(){

        final EditText et = new EditText(this);
        et.setText("http://issuecdn.baidupcs.com/issue/netdisk/apk/BaiduNetdisk_7.14.2.apk");

        new AlertDialog.Builder(this).setTitle("搜索")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "搜索内容不能为空！" + input, Toast.LENGTH_LONG).show();
                        }
                        else {
                            //调用下载方法
                           // Download(input);
                           MainActivityPermissionsDispatcher.DownloadWithCheck(MainActivity.this,input);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        setIconEnable(menu,true);
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }
    //enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
    private void setIconEnable(Menu menu, boolean enable)
    {
        try
        {
            Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            Log.v("Main",menu.getClass().toString());
            m.setAccessible(true);
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void getBefore() {
        Log.v("Main","请求之前");
    }

    @Override
    public void getAfter() {
        Log.v("Main","请求之后");
    }

    @Override
    public void getStringSuccess(String s) {
        Log.v("Main","结果："+s);
    }

    @Override
    public void getStringFailed(String s) {
        Log.v("Main","失败："+s);
    }
}
