package com.didi365.logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class LogProxy extends Thread {

    private final String TAG = LogProxy.class.getSimpleName();

    private int port = 22299;

    private static boolean flag;
    private Socket socket;
    private InetSocketAddress isa = null;

    public LogProxy() {
        flag = true;
    }

    @Override
    public void run() {
        super.run();
        Debug.v(TAG, "LogProxy run...");
        while (flag) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Debug.v(TAG, "LogProxy connect");
                socket = new Socket();
                isa = new InetSocketAddress("127.0.0.1", port);
                socket.connect(isa, 500);
                Debug.v(TAG, "LogProxy connect success...");
            } catch (IOException e) {
                e.printStackTrace();
                Debug.e(TAG, "LogProxy connect failed...");
                continue;
            }
            if (socket != null) {
                try {
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();
                    if (out != null) {
                        MyApplication.getApplication().setOutputStream(out);
                    }
                    if (in != null) {
                        byte[] buffer = new byte[3];
                        while (flag) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            int read = in.read(buffer, 0, buffer.length);
                            if (read == -1) {
                                break;
                            } else {
                                Debug.v(TAG, "readBuffer:" + printHexString(buffer, read));

                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public void release() {
        Debug.v(TAG, "release");
        flag = false;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket = null;
                MyApplication.getApplication().setOutputStream(null);
            }
        }
    }
}
