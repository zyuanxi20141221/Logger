package com.didi365.logger;

import android.app.Application;
import java.io.OutputStream;

/**
 * Created by zf on 2018/5/11.
 */

public class MyApplication extends Application {

    private static MyApplication application;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    private OutputStream outputStream;

    public static MyApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        LogProxy proxyLink = new LogProxy();
        proxyLink.start();
    }
}
