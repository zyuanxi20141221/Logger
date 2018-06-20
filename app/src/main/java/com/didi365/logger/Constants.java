package com.didi365.logger;

/**
 * Created by zf on 2017/12/26.
 * 常量定义
 */

public class Constants {

    public static String ServerAddress = "127.0.0.1"; //服务端主机地址

    public static final int serverport = 63308;   //手机sockserver侦听的端口，用于和手机端收发数据

    public static final int serverHttpPort = 22255;  //手机端http server侦听的端口号

    public static final int proxyDroidPort = 18091;  //底层网络代理程序侦听的端口

    public static final String DEVICE_ANDROID = "android_connect"; //安卓手机

    public static final String DEVICE_IOS = "iphone_connect";  //苹果手机

    public static final String USB_CONNECT = "usb_connect";

    public static final String WIFI_CONNECT = "wifi_connect";

    public static final String PHONE_APP_PACKAGENAME = "com.didi365.miudrive.app";  //安卓手机端喵驾包名称

    public static final String DEVICE_USBCONNECT_SUCCESS = "com.didi365.miudrve.usbconnect"; //USB连接成功广播

    public static String adb = "bdcl";  //车机端执行shell脚本的程序名

    public static int didiversion = 3; //安卓手机端代理网络的文件版本号

    public static byte[] proxy_on = new byte[]{0x0a, 0x01, 0x01};  //开启USB网络代理

    public static byte[] proxy_off = new byte[]{0x0a, 0x02, 0x02}; //关闭USB网络代理

    public static byte[] proxy_check = new byte[]{0x0a, 0x00, 0x00}; //查询USB代理网络开关状态

    public static byte[] proxy_enable = new byte[]{0x0a, 0x03, 0x03}; //查询USB代理网络是否可用
//    正常：0x0b 0x03 0x00
//    didiusb: 0x0b 0x03 0x01
//    diditool: 0x0b 0x03 0x02
//    didiutil: 0x0b 0x03 0x04
//    wifi: 0x0b 0x03 0x10

    public static final String TAG = "print_link"; //日志输出1

    public static final String TAG2 = "print_connect"; //日志输出2

    public static final String ANDROID_GATEWAY = "192.168.43.1"; //安卓手机开启热点的IP地址

    public static final String IOS_GATEWAY = "172.20.10.1"; //苹果手机开启热点的IP地址

    public static boolean proxyenable = false; //USB代理网络是否可用

}
