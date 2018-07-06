package com.ktv.tools;
//这里是引用过来的类就不需要命名规范了
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 黄守维
 * 2015-4-25
 * 这是外部引用的类拷贝过来使用的
 * 判断当前的网络是否可用
 * 
 * @author Administrator
 * 
 */
public class ConnectionState {

		// 判断网络连接状态
		public static boolean isNetworkConnected(Context context) {
			if (context != null) {
				ConnectivityManager mConnectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mNetworkInfo = mConnectivityManager
						.getActiveNetworkInfo();
				if (mNetworkInfo != null) {
					return mNetworkInfo.isAvailable();
				}
			}
			return false;
		}

		// 判断wifi状态
		public static boolean isWifiConnected(Context context) {
			if (context != null) {
				ConnectivityManager mConnectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mWiFiNetworkInfo = mConnectivityManager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (mWiFiNetworkInfo != null) {
					return mWiFiNetworkInfo.isAvailable();
				}
			}
			return false;
		}

		// 判断移动网络
		public static boolean isMobileConnected(Context context) {
			if (context != null) {
				ConnectivityManager mConnectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mMobileNetworkInfo = mConnectivityManager
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if (mMobileNetworkInfo != null) {
					return mMobileNetworkInfo.isAvailable();
				}
			}
			return false;
		}

		// 获取连接类型
		public static int getConnectedType(Context context) {
			if (context != null) {
				ConnectivityManager mConnectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mNetworkInfo = mConnectivityManager
						.getActiveNetworkInfo();
				if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
					return mNetworkInfo.getType();
				}
			}
			return -1;
		}
}