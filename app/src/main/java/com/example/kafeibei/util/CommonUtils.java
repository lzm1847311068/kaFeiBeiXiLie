package com.example.kafeibei.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * 通用工具类 .
 */
public class CommonUtils {

    public static final String LOG_TAG = "CommonUtils";

    /**
     * 检查网络是否可用 .
     *
     * @param paramContext
     * @return
     */
    public static boolean checkNetworkEnable(Context paramContext) {
        boolean i = false;
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
            return true;
        return false;
    }

    /**
     * 获取客户端ip .
     *
     * @param context
     * @return
     */
    public static String getClientIp(Context context) {
        try {
            if ("wifi".equals(isWifiOrGPRS(context))) {
                return getWIFILocalIpAddress(context);
            }
            if ("mobile".equals(isWifiOrGPRS(context))) {
                return getGPRSLocalIpAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "127.0.0.1";
    }


    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    //获得wifi状态state
    public static String isWifiOrGPRS(Context context) {
        boolean isConnected = false;
        //1.得到网络连接信息
        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        //2.判断网络是否连接
        if (connectManager.getActiveNetworkInfo() != null) {
            isConnected = connectManager.getActiveNetworkInfo().isConnected();
        }
        if (!isConnected) {
            return "none";
        }
        NetworkInfo.State wifi_state = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        //获得gprs状态
        NetworkInfo.State gprs_state = connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (wifi_state == NetworkInfo.State.CONNECTED || wifi_state == NetworkInfo.State.CONNECTING) {
            return "wifi";
        } else if (gprs_state == NetworkInfo.State.CONNECTED || gprs_state == NetworkInfo.State.CONNECTING) {
            return "mobile";
        }
        return "none";
    }

    /**
     * 获取当前ip地址   wifi  eq对比后获取wifi
     *  wifiInfo.getSSID().replace("\"", "").replace("<", "").replace(">", "")
     * @param context
     * @return
     */
    public static String getWIFILocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
    }




    public static String getWIFIName(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            //前提加上定位权限
            return wifiInfo.getSSID().replace("\"", "").replace("<", "").replace(">", "");
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
    }


    //GPRS连接下的ip
    public static String getGPRSLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(LOG_TAG, ex.toString());
        }
        return "127.0.0.1";
    }


}
