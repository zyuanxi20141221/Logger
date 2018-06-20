package com.didi365.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logstart;
    private Button logstop;
    private Button check_proxy;
    private Button start_proxy;
    private Button stop_proxy;
    private TextView logContent;
    private CheckBox checkBox_link;
    private CheckBox checkBox_system;
    private CheckBox checkBox_navi;
    private CheckBox checkBox_all;
    private Intent logObserverIntent = null;
    private BroadcastReceiver mLogBroadcastReceiver;
    private StringBuilder stringBuilder = new StringBuilder();
    private SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String LOG_ACTION = "com.example.admin.logobserver.LOG_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logstart = (Button) findViewById(R.id.btn_start);
        logstop = (Button) findViewById(R.id.btn_stop);
        check_proxy = (Button) findViewById(R.id.btn_checkproxy);
        start_proxy = (Button) findViewById(R.id.btn_startproxy);
        stop_proxy = (Button) findViewById(R.id.btn_stopproxy);
        checkBox_link = (CheckBox) findViewById(R.id.ch_connect);
        checkBox_system = (CheckBox) findViewById(R.id.ch_system);
        checkBox_navi = (CheckBox) findViewById(R.id.ch_navi);
        checkBox_all = (CheckBox) findViewById(R.id.ch_all);
        logstart.setOnClickListener(this);
        logstop.setOnClickListener(this);
        check_proxy.setOnClickListener(this);
        start_proxy.setOnClickListener(this);
        stop_proxy.setOnClickListener(this);

        registerLogBroadcastReceiver();
        stringBuilder.append("logcat -v time ");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = FileUtils.getSDCardPath() + File.separator + "didiCarLCache/" + "log/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        String sd = FileUtils.getSDCardPath();
        Log.v("MainActivity", "sd " + sd);
        saveMiuConfigIni();
        Log.v("MainActivity", "getMiuDriveState " + getMiuDriveState());

        boolean install = packageInstall(getApplicationContext(), "com.didi365.miudrive.server");
        Log.v("MainActivity", "install " + install);
    }

    public static void saveMiuConfigIni() {
        try {
            Properties properties = new Properties();
            File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "miudrive.ini");
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            properties.setProperty("miudrivework", "1");
            try {
                properties.store(fileOutputStream, "");
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean getMiuDriveState() {
        Log.d("miudrive", "getMiuDriveState");
        if(android.os.Environment.getExternalStorageState().equals("mounted")) {
            java.io.File file = new java.io.File(android.os.Environment.getExternalStorageDirectory().getPath());
            String sdPath = file.exists() ? file.getPath() : "/mnt/sdcard";
            try {
                java.io.FileInputStream fis = new java.io.FileInputStream(sdPath + java.io.File.separator + "miudrive.ini");
                java.util.Properties properties = new java.util.Properties();
                properties.load(fis);
                fis.close();
                int miudrive = java.lang.Integer.parseInt(properties.get("miudrivework").toString());
                Log.d("miudrive", "miudrive " + miudrive);
                return miudrive == 1 ? true : false;
            } catch(java.io.FileNotFoundException e) {
                e.printStackTrace();
            } catch(java.io.IOException e) {
                e.printStackTrace();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
            return false;
        } else {
            Log.d("miudrive", "ExternalStorage unmounted");
            return false;
        }
    }

    private static boolean packageInstall(Context context, String packageName){
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        return packageNames.contains(packageName);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            boolean b1 = checkBox_link.isChecked();
            boolean b2 = checkBox_system.isChecked();
            boolean b3 = checkBox_all.isChecked();
            boolean b4 = checkBox_navi.isChecked();
            if (!b1 && !b2 && !b3 && !b4) {
                Toast.makeText(getApplicationContext(), getString(R.string.noselect), Toast.LENGTH_LONG).show();
            } else {
                logstart.setEnabled(false);
                stringBuilder.append("-s ");
                if (b1) {
                    stringBuilder.append("print_link ");
                    stringBuilder.append("print_connect ");
                    stringBuilder.append("ProxyLink ");
                }
                if (b2) {
                    stringBuilder.append("DiDiSrv ");
                }
                if(b4) {
                    stringBuilder.append("LocationUtils ");
                }
                String time = myLogSdf.format(new Date());
                String fileName = "logcat-" + time.replace(" ", "").replace(":", "").replace("-", "") + "-" + System.currentTimeMillis() + ".log";
                stringBuilder.append(">");
                stringBuilder.append(FileUtils.getSDCardPath() + File.separator + "didiCarLCache/" + "log/" + fileName);
                Log.v("MainActivity", "stringBuilder " + stringBuilder.toString());
                byte[] b = stringBuilder.toString().getBytes();
                byte[] logcmd = new byte[b.length + 3];
                logcmd[0] = 0x0a;
                logcmd[1] = 0x04;
                logcmd[2] = (byte) b.length;
                System.arraycopy(b, 0, logcmd, 3, b.length);
                OutputStream out = MyApplication.getApplication().getOutputStream();
                if(out != null) {
                    try {
                        out.write(logcmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(b3) {
                    String _time = myLogSdf.format(new Date());
                    String _fileName = "logcat-" + _time.replace(" ", "").replace(":", "").replace("-", "") + "-" + System.currentTimeMillis() + ".log";
                    String _log = "logcat -v time >" + FileUtils.getSDCardPath() + File.separator + "didiCarLCache/" + "log/" + _fileName;
                    Log.v("MainActivity", "_log " + _log);
                    if(out != null) {
                        try {
                            byte[] _b = _log.getBytes();
                            byte[] _logcmd = new byte[_b.length + 3];
                            _logcmd[0] = 0x0a;
                            _logcmd[1] = 0x04;
                            _logcmd[2] = (byte) _b.length;
                            System.arraycopy(_b, 0, _logcmd, 3, _b.length);
                            out.write(_logcmd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else if (v.getId() == R.id.btn_stop) {
            logstart.setEnabled(true);
            checkBox_link.setChecked(false);
            checkBox_system.setChecked(false);
            checkBox_all.setChecked(false);
            checkBox_navi.setChecked(false);
            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append("logcat -v time ");
            OutputStream out = MyApplication.getApplication().getOutputStream();
            if(out != null) {
                try {
                    out.write(new byte[] {0x0a, 0x05, 0x05});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if(v.getId() == R.id.btn_checkproxy) {
            OutputStream out = MyApplication.getApplication().getOutputStream();
            if(out !=null) {
                try {
                    out.write(Constants.proxy_check);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if(v.getId() == R.id.btn_startproxy) {
            OutputStream out = MyApplication.getApplication().getOutputStream();
            if(out !=null) {
                try {
                    out.write(Constants.proxy_on);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if(v.getId() == R.id.btn_stopproxy) {
            OutputStream out = MyApplication.getApplication().getOutputStream();
            if(out !=null) {
                try {
                    out.write(Constants.proxy_off);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void registerLogBroadcastReceiver() {
        mLogBroadcastReceiver = new LogBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LOG_ACTION);
        registerReceiver(mLogBroadcastReceiver, filter);
    }

    private class LogBroadcastReceiver extends BroadcastReceiver {
        private String action = null;
        private Bundle mBundle = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (LOG_ACTION.equals(action)) {
                mBundle = intent.getExtras();
                logContent.setText(mBundle.getString("log"));
//                writeToSdCard(mBundle.getString("log"));
            }
        }
    }

    private String printHexString(byte[] b, int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append("0x" + hex.toLowerCase() + " ");
        }
        return sb.toString();
    }

    private byte checksum(byte[] b) {
        byte bye = 0x00;
        for(int i = 1; i < b.length - 1; i++) {
            bye += b[i];
        }
        return bye;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLogBroadcastReceiver != null) {
            unregisterReceiver(mLogBroadcastReceiver);
        }
    }
}
