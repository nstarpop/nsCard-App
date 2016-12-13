/**
 * DeviceIP.java
 *
 * Created by agou on 12/10/2015.
 *
 * Copyright(c) 2014 Nagravision S.A, All Rights Reserved.
 * This software is the proprietary information of Nagravision S.A.
 */

package com.nagrastar.businesscardfinder.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class DeviceIP {
  private static final String TAG = "DeviceIP";

  /**
   * Get IPv4 address of the first non-localhost device interface
   * @return ip address or empty string
   */
  public static String getDeviceIPAddress() {
    try {
      List<NetworkInterface> deviceInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface intf : deviceInterfaces) {
        List<InetAddress> addressess = Collections.list(intf.getInetAddresses());
        for (InetAddress address : addressess) {
          if (!address.isLoopbackAddress()) {
            String stringAddr = address.getHostAddress();
            boolean isIPv4 = stringAddr.indexOf(':') < 0;
            if (isIPv4) {
              NMPLog.i(TAG, "stringAddr : " + stringAddr);
              return stringAddr;
            }
          }
        }
      }
    } catch (Exception ex) {
      NMPLog.e(TAG, "getDeviceIpAddress : " + ex.toString());
    }
    return "";
  }

}
