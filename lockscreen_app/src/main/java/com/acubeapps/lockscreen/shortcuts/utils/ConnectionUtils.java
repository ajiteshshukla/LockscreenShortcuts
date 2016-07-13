package com.acubeapps.lockscreen.shortcuts.utils;

import com.inmobi.oem.internal.NetworkType;
import com.inmobi.oem.internal.analytics.AndroidNetworkUtils;

import android.content.Context;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;



/**
 * Created by netra.shetty on 6/30/16.
 */
public final class ConnectionUtils {

    private ConnectionUtils() {
        throw new AssertionError();
    }

    /**
     * To get device network type is 2g,3g,4g.
     */
    public static String getMobileDataType(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2g";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                /**
                 From this link https://goo.gl/R2HOjR ..NETWORK_TYPE_EVDO_0 & NETWORK_TYPE_EVDO_A
                 EV-DO is an evolution of the CDMA2000 (IS-2000) standard that supports high data rates.
                 Where CDMA2000 https://goo.gl/1y10WI .CDMA2000 is a family of 3G[1] mobile technology standards for sending voice,
                 data, and signaling data between mobile phones and cell sites.
                 */
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                //For 3g HSDPA , HSPAP(HSPA+) are main  networktype which are under 3g Network
                //But from other constants also it will 3g like HSPA,HSDPA etc which are in 3g case.
                //Some cases are added after  testing(real) in device with 3g enable data
                //and speed also matters to decide 3g network type
                //http://goo.gl/bhtVT
                return "3g";
            case TelephonyManager.NETWORK_TYPE_LTE:
                //No specification for the 4g but from wiki
                //I found(LTE (Long-Term Evolution, commonly marketed as 4G LTE))
                //https://goo.gl/9t7yrR
                return "4g";
            default:
                return "Notfound";
        }
    }

    public static String getCarrier(Context context) {
        NetworkInfo networkInfo = AndroidNetworkUtils.getActiveNetworkInfo(context);
        NetworkType networkType = AndroidNetworkUtils.getNetworkType(networkInfo);
        boolean isConnected = AndroidNetworkUtils.isConnected(context, networkType);
        if (isConnected) {
            boolean isMobileDataConnected = AndroidNetworkUtils.isSimDataOn(context);
            boolean isWifiConnected = AndroidNetworkUtils.isWifiOn(context);
            if (isWifiConnected) {
                return NetworkType.WIFI.toString();
            } else if (isMobileDataConnected) {
                return getMobileDataType(context);
            } else {
                return NetworkType.OTHERS.toString();
            }
        } else {
            return "notConnected";
        }
    }
}
