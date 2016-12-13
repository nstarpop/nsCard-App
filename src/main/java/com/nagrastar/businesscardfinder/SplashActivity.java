/**
 * SplashActivity.java
 *
 */
package com.nagrastar.businesscardfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nagrastar.businesscardfinder.util.NMPLog;

import com.nagrastar.businesscardfinder.MainActivity;

/**
 * The startup
 * <a href="http://developer.android.com/reference/android/app/Activity.html">Activity</a>
 * for the native reference application.
 *    Implement Splash Screen, loading nmpsdk library, init pak, jump to sign on screen or main activity
 * 
 */
public class SplashActivity extends Activity {
  private static final String TAG = "SplashActivity";

  /**
   * Called when the activity is starting.
   * 
   * @param xSavedInstanceState
   *          If the activity is being re-initialized after previously being
   *          shut down then this Bundle contains the data it most recently
   *          supplied in {@link #onSaveInstanceState(Bundle)}. <strong>Note:
   *          Otherwise it is null.</strong>
   */
  @Override
  public void onCreate(Bundle xSavedInstanceState) {
    NMPLog.v(TAG, "Enter");
    super.onCreate(xSavedInstanceState);

    setContentView(R.layout.splash);
    SysApplication.getInstance().addActivity(this);

    NMPLog.v(TAG, "Leave");
  }

  /**
   * Called after {@link #onCreate(Bundle)} - or after {@link #onRestart()} when
   * the activity had been stopped, but is now again being displayed to the
   * user. It will be followed by {@link #onResume()}
   */
  @Override
  protected void onStart() {
    NMPLog.v(TAG, "Enter");
    super.onStart();

    // Post code to be run 1s after startup to allow the splash screen to be
    // seen.
    new Handler().postDelayed(new Runnable() {
      public void run() {
        NMPLog.i(TAG, "Jump to SignonActivity");
        Intent it = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(it);
      }
  }, 1000);

    NMPLog.v(TAG, "Leave");
  }

  /**
   * Perform any final cleanup before an activity is destroyed.
   */
  @Override
  protected void onDestroy() {
    NMPLog.v(TAG, "Enter");
    super.onDestroy();
    NMPLog.v(TAG, "Leave");
  }
}
