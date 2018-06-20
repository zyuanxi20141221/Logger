package com.didi365.logger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zf on 2018/5/10.
 */

public class LogObserverService extends Service implements Runnable {

    private String TAG = "LogObserverService";
    private boolean isObserverLog = false;
    private StringBuffer logContent = null;
    private Bundle mBundle = null;
    private Intent mIntent = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mIntent = new Intent();
        mBundle = new Bundle();
        logContent = new StringBuffer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLogObserver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendLogContent(String logContent) {
        mBundle.putString("log", logContent);
        mIntent.putExtras(mBundle);
        mIntent.setAction(MainActivity.LOG_ACTION);
        sendBroadcast(mIntent);
    }

    public void startLogObserver() {
        isObserverLog = true;
//        Thread mTherad = new Thread(this);
//        mTherad.start();
        OutputStream out = MyApplication.getApplication().getOutputStream();
        if(out !=null) {
            try {
                out.write(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopLogObserver() {
        isObserverLog = false;
    }

    @Override
    public void run() {
        try {
            List commandLine = new ArrayList();
            commandLine.add("logcat");
//            commandLine.add( "-d");
            commandLine.add("-v");
            commandLine.add("time");
            commandLine.add("-f");
            commandLine.add("/sdcard/log/logcat.txt");
            Process process = Runtime.getRuntime().exec((String[]) commandLine.toArray(new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
            String line = bufferedReader.readLine();
            log("line " + line);
            while (line != null) {
                logContent.append(line);
                logContent.append("\n");
            }
        } catch (IOException e) {
        }


//        ArrayList commandLine = new ArrayList();
//        commandLine.add("logcat -d -v time -f /mnt/sdcard/logcat.txt");
//        ShellUtils.execCommand(commandLine, true);
        /*
        Process pro = null;
        BufferedReader bufferedReader = null;
        try {
            String running = "logcat -s MainActivity";
//          pro = Runtime.getRuntime().exec("logcat");
            pro = Runtime.getRuntime().exec(running);
//          Runtime.getRuntime().exec("logcat -c").waitFor();
            bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = null;
        try {
            log(bufferedReader.readLine());
            while ((line = bufferedReader.readLine()) != null) {
                    logContent.delete(0, logContent.length());
                    System.out.println(line);
                    logContent.append(line);
                    logContent.append("\n");
                    sendLogContent(logContent.toString());
                    Thread.yield();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    void log(String log) {
        Log.v(TAG, log);
    }
}
