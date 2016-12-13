/**
 * SysApplication.java
 */
package com.nagrastar.businesscardfinder;

import java.util.EmptyStackException;
import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.nagrastar.businesscardfinder.util.NMPLog;

/**
 * Android
 * <a href="http://developer.android.com/reference/android/app/Application.html">Application</a>
 * that keeps track of active activities, and performs low memory tasks.
 * 
 * Implemented as a singleton object
 * 
 */
public class SysApplication extends Application {
  public static final String TAG = "SysApplication";

  public static long sSystemClock = 0;

  private Stack<Activity>		mList = new Stack<Activity>();
  private static SysApplication sInstance = null;

  private SysApplication() {
  }


  /**
   * Get an instance of this object.
   * 
   * @return SysApplication An instance of this object
   */
  public synchronized static SysApplication getInstance() {
    if (null == sInstance) {
      sInstance = new SysApplication();
    }

    return sInstance;
  }


  /**
   * Add an active activity to this applications tacking list
   * 
   * @param xActivity
   *          The activity to add
   */
  public void addActivity(Activity xActivity) {
    mList.push(xActivity);
  }


  /**
   * Remove an active activity from this applications tacking list
   * 
   * @param xActivity
   *          The activity to remove
   */
  public void removeActivity(Activity xActivity) {
    mList.remove(xActivity);
  }


  /**
   * Exit the application by finishing all known active activities
   */
  public void exit() {
    try {
      Activity activity;
      while ((activity = mList.pop()) != null) {
        NMPLog.d(TAG, "finish activity: " + activity);
        activity.finish();
      }

    } catch (EmptyStackException e) {

    }
  }


  /**
   * This is called by the Android system when the overall system is running low
   * on memory, and actively running processes should trim their memory usage.
   */
  @Override
  public void onLowMemory() {
    NMPLog.v(TAG, "onLowMemory");

    super.onLowMemory();
    System.gc();
  }

  
  /**
   * Creates and shows an <a href="http://developer.android.com/reference/android/app/AlertDialog.html">AlertDialog</a>
   *
   * 
   * @param xA
   *        Activity asking for an AlertDialog
   * @param xTitle
   *        The tile of the AlertDialog
   * @param xMessage
   *        The message of the AlertDialog
   * @param xView
   *        An optional View to add to the Dialog
   * @param xNegTextId
   *        The resource ID for the text to use for the Negative button. 0 = No button
   * @param xNegListener
   *        The Listener used when the button is touched.
   * @param xNeuTextId
   *        The resource ID for the text to use for the Neutral button. 0 = No button
   * @param xNeuListener
   *        The Listener used when the button is touched.
   * @param xPosTextId
   *        The resource ID for the text to use for the Positive button. 0 = No button
   * @param xPosListener
   *        The Listener used when the button is touched.
   * @return {@link <a href="http://developer.android.com/reference/android/app/AlertDialog.html">AlertDialog</a>}
   *         that has been shown.
   */
  public final static AlertDialog showNagraDialog( Activity xA, String xTitle, String xMessage,
                            View xView,
                            int xNegTextId, DialogInterface.OnClickListener xNegListener, 
                            int xNeuTextId, DialogInterface.OnClickListener xNeuListener, 
                            int xPosTextId, DialogInterface.OnClickListener xPosListener,
                            CharSequence[] xSCItems, int xSCCheckedItem, DialogInterface.OnClickListener xSCListener,
                            CharSequence[] xMCItems, boolean[] xMCCheckedItems, DialogInterface.OnMultiChoiceClickListener xMCListener
                            )
  {
    AlertDialog result = null;
    NMPLog.v(TAG, "Enter");

    if( xA != null ) {
      Builder builder = new AlertDialog.Builder(xA);
      Resources rsrcs = xA.getResources();
      // Allow us to change the colours of the title and message text independently

      // Apply title text
      int tColour = rsrcs.getColor( R.color.nagra_menu_text );
      TextView title = new TextView(xA);
      title.setTextColor(tColour);
      title.setText(xTitle);
      title.setPadding(10, 10, 10, 10);
      title.setGravity(Gravity.CENTER);
      title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
      builder.setCustomTitle(title);

      // Apply message text
      if( xMessage != null ) {
        int mColour = Color.WHITE;
        Spanned messageHTML = Html.fromHtml("<font color='" + mColour + "'>" + xMessage + "</font>");
        builder.setMessage(messageHTML);
      }
      else
      {
    	if( (xSCItems == null) && (xMCItems == null) && (xView == null) ) {
          // Set a blank message when no other items to display,
          // otherwise androids' AlertDialog shifts the titleDivider and doesn't apply the colour.
          builder.setMessage("");
        }
      }
      
      // Add buttons with listeners
      if( xNegTextId != 0 ) {
        builder.setNegativeButton(xNegTextId, xNegListener);
      }
      if( xNeuTextId != 0 ) {
        builder.setNeutralButton(xNeuTextId, xNeuListener);
      }
      if( xPosTextId != 0 ) {
        builder.setPositiveButton(xPosTextId, xPosListener);
      }
      
      // Set any single choice items
      if( xSCItems != null ) {
        NMPLog.d(TAG, "Setting single choice items");
        builder.setSingleChoiceItems( xSCItems, xSCCheckedItem, xSCListener );
      }
      
      // Set any multi choice items
      if( xMCItems != null ) {
          NMPLog.d(TAG, "Setting multi choice items");
        builder.setMultiChoiceItems( xMCItems, xMCCheckedItems, xMCListener );
      }
      
      if( xView != null ) {
        builder.setView( xView );
      }

      // Create the dialog and change the titleDivider colour
      result = builder.create();
      result.show();
      
      // Can only update the titleDivider after dialog has been shown
      Resources resources = result.getContext().getResources();
      int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
      View titleDivider = result.getWindow().getDecorView().findViewById(titleDividerId);
      if (titleDivider != null)
      titleDivider.setBackgroundColor( tColour );

      // Change the button colours here
      if( xNegTextId != 0 ) {
        result.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
      }
      if( xNeuTextId != 0 ) {
        result.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.WHITE);
      }
      if( xPosTextId != 0 ) {
        result.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
      }
    }
    
    return result;
  }

  /**
   * When storage permission has been revoked on android M devices, the android os
   * will kill the application immediately, when resume the application, the android
   * will resume directly to the activity where it was without go through SplashActivity.
   *
   * @return true if the application has been killed due to permission revocation.
   */
  public boolean isPermissionRevoked() {
    if(mList.size() >0){
      for(Activity activity:mList){
        if(activity instanceof SplashActivity) {
          return false;
        }
      }
      return true;
    }
    return true;
  }
}
