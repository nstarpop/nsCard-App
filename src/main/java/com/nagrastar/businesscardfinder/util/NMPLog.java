/// @cond HIDE_ALWAYS
/*
 * NMPLog.java
 *
 * Created on 04/08/2015
 *
 * Copyright(c) 2015 Nagravision S.A, All Rights Reserved.
 * This software is the proprietary information of Nagravision S.A.
 */
package com.nagrastar.businesscardfinder.util;

import android.util.Log;


/**
* @hide
* 
*/
public final class NMPLog {
  public static void v(String tag, String msg) {
    String message = generateMsg(msg);
    if (message != null) {
      Log.v(tag, message);
    }
  }

  public static void v(String tag, String msg, Throwable tr) {
    String message = generateMsg(msg + '\n' + Log.getStackTraceString(tr));
    if (message != null) {
      Log.v(tag, message);
    }
  }

  public static  void d(String tag, String msg) {
    String message = generateMsg(msg);
    if (message != null) {
      Log.d(tag, message);
    }
  }
   
  public static void d(String tag, String msg, Throwable tr) {
    String message = generateMsg(msg + '\n' + Log.getStackTraceString(tr));
    if (message != null) {
      Log.d(tag, message);
    }
  }
  
  public static void i(String tag, String msg) {
    String message = generateMsg(msg);
    if (message != null) {
      Log.i(tag, message);
    }
  }
   
  public static void i(String tag, String msg, Throwable tr) {
    String message = generateMsg(msg + '\n' + Log.getStackTraceString(tr));
    if (message != null) {
      Log.i(tag, message);
    }
  }
  
  public static void w(String tag, String msg) {
    String message = generateMsg(msg);
    if (message != null) {
      Log.w(tag, message);
    }
  }
 
  public static void w(String tag, String msg, Throwable tr) {
    String message = generateMsg(msg + '\n' + Log.getStackTraceString(tr));
    if (message != null) {
      Log.w(tag, message);
    }
  }
  
  public static void e(String tag, String msg) {
    String message = generateMsg(msg);
    if (message != null) {
      Log.e(tag, message);
    }
  }

  public static void e(String tag, String msg, Throwable tr) {
    String message = generateMsg(msg + '\n' + Log.getStackTraceString(tr));
    if (message != null) {
      Log.e(tag, message);
    }
  }
  
  private static String generateMsg(String msg) {
    StackTraceElement[] stacktrace = (new Exception()).getStackTrace();
    if (stacktrace == null || stacktrace.length < 3)
      return null;
    
    StackTraceElement traceElement = stacktrace[2];
    
    StringBuffer toStringBuffer = new StringBuffer(
        traceElement.getMethodName()).append(": ").append(
        traceElement.getLineNumber()).append(" ").append(msg);
    
    String message = toStringBuffer.toString();
    
    return message;
  }

}
/// @endcond