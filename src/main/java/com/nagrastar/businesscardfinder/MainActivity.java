/**
 * MainActivity.java
 */
package com.nagrastar.businesscardfinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import com.nagrastar.businesscardfinder.businesscard.BusinessCard;
import com.nagrastar.businesscardfinder.businesscard.BusinessCardDBHandler;
import com.nagrastar.businesscardfinder.fragment.DownloadFragment;
import com.nagrastar.businesscardfinder.fragment.NetInfo;
import com.nagrastar.businesscardfinder.fragment.listener.NetworkListener;
import com.nagrastar.businesscardfinder.fragment.PersonalFragment;
import com.nagrastar.businesscardfinder.util.DeviceIP;
import com.nagrastar.businesscardfinder.util.NMPLog;
import com.nagrastar.businesscardfinder.msgHandler.*;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.content.Context;
/**
 * Main screen activity, show streams list and download list
 * 
 */
public class MainActivity extends AppCompatActivity {
  static final String TAG = "MainActivity";

  private RelativeLayout mStreamsRelativeLayout = null;
  private RelativeLayout mDownloadRelativeLayout = null;
  private TextView mStreamsTextView = null;
  private TextView mDownloadTextView = null;
  private ViewPager mViewPager = null;
  private TextView[] mTextViewList = null;
  private PersonalFragment mPersonalFragment = null;
  private DownloadFragment mDownloadFragment = null;
  private NetInfo mNetInfo = null;

  private int mSelectID = 0;
  private int[] mSelectList;
  private multicastReceiver mulReceiver = null;
  private multicastSender mulSender = null;
  private MulticastLock multicastLock;

  private BusinessCardDBHandler mBusinessCardDBHandler = null;
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
  protected void onCreate(Bundle xSavedInstanceState) {
    NMPLog.v(TAG, "Enter");

    super.onCreate(xSavedInstanceState);

    mBusinessCardDBHandler = new BusinessCardDBHandler(this);
    mBusinessCardDBHandler.open();

    mPersonalFragment = new PersonalFragment();
    mPersonalFragment.setDBHandler(mBusinessCardDBHandler);
    mDownloadFragment = new DownloadFragment();
    mDownloadFragment.setDBHandler(mBusinessCardDBHandler);

    setContentView(R.layout.main);
    initLayout();
    initData();

    SysApplication.getInstance().addActivity(this);

    mNetInfo = new NetInfo();
    mNetInfo.setNetworkListener(mNetworkListener);

    // unify the actionBar when some device include hardware menu
    try {
      ViewConfiguration mconfig = ViewConfiguration.get(this);
      Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
      if (menuKeyField != null) {
        menuKeyField.setAccessible(true);
        menuKeyField.setBoolean(mconfig, false);
      }
    } catch (Exception e) {
      e.printStackTrace();
      NMPLog.e(TAG, "Leave with modify the sHasPermanentMenuKey failed ");
    }

    if(SysApplication.getInstance().isPermissionRevoked()){
      Intent it = new Intent(MainActivity.this, SplashActivity.class);
      MainActivity.this.startActivity(it);
      MainActivity.this.finish();
    }

    WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
    multicastLock = wifi.createMulticastLock("multicast.test");
    multicastLock.acquire();
    mulSender = new multicastSender(mBusinessCardDBHandler);
    mulSender.start();
    mulReceiver = new multicastReceiver(this, mOnReceviedCard);
    mulReceiver.start();
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

    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(mNetInfo, filter);

    NMPLog.v(TAG, "Leave");
  }

  /**
   * Called when you are no longer visible to the user. You will next receive
   * either {@link #onRestart}, {@link #onDestroy}, or nothing, depending on
   * later user activity.
   */
  @Override
  protected void onStop() {
    NMPLog.v(TAG, "Enter");

    super.onStop();
    unregisterReceiver(mNetInfo);
    multicastLock.release();
    NMPLog.v(TAG, "Leave");
  }

  /**
   * Perform any final cleanup before an activity is destroyed.
   */
  @Override
  protected void onDestroy() {
    NMPLog.v(TAG, "Enter");
    super.onDestroy();
    mulReceiver.quit();
    mulSender.quit();
    mulReceiver.interrupt();
    mulSender.interrupt();

    mulReceiver = null;
    mulSender = null;

    mBusinessCardDBHandler.close();
    NMPLog.v(TAG, "Leave");
  }

  /**
   * Handles the pressing of the back button, by telling the active activity
   * tracker to exit.
   * 
   * @param xKeyCode
   *          key code
   * @param xEvent
   *          key event
   * @return Return {@code true} to prevent this event from being propagated
   *         further, or {@code false} to indicate that you have not handled
   *         this event and it should continue to be propagated.
   */
  @Override
  public boolean onKeyDown(int xKeyCode, KeyEvent xEvent) {
    if (xKeyCode == KeyEvent.KEYCODE_BACK) {
      NMPLog.i(TAG, "KEYCODE_BACK ");
      SysApplication.showNagraDialog(this,
              "exit", "do you want to exit?",
              null,
              R.string.cancel,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface xDialog, int xWhichButton) {
                }
              },
              0, null,
              R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface xDialog, int xWhichButton) {
                  SysApplication.getInstance().exit();
                }
              },
              null, 0, null, null,null,null);

      return true;
    }

    return super.onKeyDown(xKeyCode, xEvent);
  }

  /**
   * Initialise the contents of the Activity's standard options menu. You should
   * place your menu items in to <var>xMenu</var>.
   * 
   * @param xMenu
   *          The options menu in which you place your items.
   * 
   * @return You must return {@code true} for the menu to be displayed; if you
   *         return {@code false} it will not be shown.
   */
  @Override
  public boolean onCreateOptionsMenu(Menu xMenu) {
    super.onCreateOptionsMenu(xMenu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.settings_menu, xMenu);
    return true;
  }

  /**
   * This hook is called whenever an item in your options menu is selected.
   * 
   * @param xItem
   *          The menu item that was selected.
   * 
   * @return boolean Return false to allow normal menu processing to proceed,
   *         true to consume it here.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem xItem)
  {
    int id = xItem.getItemId();
    switch(id)
    {
    case R.id.menu_set_remove_pak_db:
      break;

    case R.id.menu_set_about:
    {
      View aboutView = getLayoutInflater().inflate(R.layout.about_view, null);
      try
      {
        ((TextView)aboutView.findViewById(R.id.about_app)).setText("App Version - " + getPackageManager()
        .getPackageInfo(getPackageName(), 0).versionName);
      }
      catch (NameNotFoundException nnfe)
      {
        //do nothing on purpose for now
      }
      ((TextView)aboutView.findViewById(R.id.about_ipaddress)).setText("IP Address - " + DeviceIP.getDeviceIPAddress());

      SysApplication.showNagraDialog(this,
          "Business Cards Finder Application",
          null,
          aboutView,
          0,
          null,
          0,
          null,
          0,
          null,
          null, 0, null, null, null, null);
      return true;
    }
    case R.id.menu_set_analytics:
      break;

    default:
     break;
    }

    return super.onOptionsItemSelected(xItem);
  }

  private String parseDeviceIdentifiers(String deviceIdentifiers) {
    String ret = "";
    try {
      JSONObject parsed = new JSONObject(deviceIdentifiers);
      JSONObject jsonIdent = parsed.getJSONObject("DeviceIdentifiers");
      ret += "android.os.Build.MANUFACTURER: " + jsonIdent.getString("Manufacturer") + "\n";
      ret += "android.os.Build.MODEL: " + jsonIdent.getString("Model") + "\n";
      ret += "android.os.Build.PRODUCT: " + jsonIdent.getString("Product") + "\n";
      ret += "android.os.Build.DEVICE: " + jsonIdent.getString("Device") + "\n";
      ret += "android.os.Build.BOARD: " + jsonIdent.getString("Board") + "\n";
      ret += "android.os.Build.HARDWARE: " + jsonIdent.getString("Hardware");
    } catch (JSONException e) {
      NMPLog.e(TAG, e.getMessage());
      NMPLog.e(TAG, e.getStackTrace().toString());
    }
    return ret;
  }

  /**
   * Initialise activity UI layout.
   */
  private void initLayout() {
    mStreamsRelativeLayout = (RelativeLayout) findViewById(R.id.main_RelativeLayout1);
    mDownloadRelativeLayout = (RelativeLayout) findViewById(R.id.main_RelativeLayout2);
    mStreamsTextView = (TextView) findViewById(R.id.main_textView_streams);
    mDownloadTextView = (TextView) findViewById(R.id.main_textView_download);
    mViewPager = (ViewPager) findViewById(R.id.main_viewPager);
  }

  /**
   * Initialise data.
   */
  private void initData() {
    mSelectList = new int[] { 0, 1 };
    mTextViewList = new TextView[] { mStreamsTextView, mDownloadTextView };

    mStreamsRelativeLayout.setOnClickListener(mClickListener);
    mDownloadRelativeLayout.setOnClickListener(mClickListener);

    mViewPager.setAdapter(mAdapter);
    mViewPager.setOnPageChangeListener(mPageChangeListener);
  }

  /**
   * Create a fragment pager adapter to handle the fragment pager.
   */
  private FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

    public int getCount() {
      return mSelectList.length;
    }

    public Fragment getItem(int xPosition) {
      NMPLog.v(TAG, "Enter");
      Fragment fragment = null;
      switch (xPosition) {
        case 0:
          fragment = mPersonalFragment;
          break;
        case 1:
          fragment = mDownloadFragment;
          break;
      }
      NMPLog.v(TAG, "Leave");
      return fragment;
    }
  };

  /**
   * Create the page change listener.
   */
  private SimpleOnPageChangeListener mPageChangeListener = new SimpleOnPageChangeListener() {
    public void onPageSelected(int xPosition) {
      setSelectedTitle(xPosition);
    }
  };

  /**
   * Create onClick listener.
   */
  private OnClickListener mClickListener = new OnClickListener() {
    public void onClick(View xV) {
      NMPLog.v(TAG, "Enter");
      switch (xV.getId()) {
        case R.id.main_RelativeLayout1:
          if (mSelectID == 0) {
            return;
          } else {
            setSelectedTitle(0);
            mViewPager.setCurrentItem(0);
          }
          break;
        case R.id.main_RelativeLayout2:
          if (mSelectID == 1) {
            return;
          } else {
            setSelectedTitle(1);
            mViewPager.setCurrentItem(1);
          }
          break;
      }
      NMPLog.v(TAG, "Leave");
    }
  };

  /**
   * Set fragment page title to highlighted selected fragment.
   *
   * @param xPosition
   *          Position of the item in select list.
   */

  private void setSelectedTitle(int xPosition) {
    for (int i = 0; i < mSelectList.length; i++) {
      if (mSelectList[i] == 0) {
        mSelectList[i] = 1;
        mTextViewList[i].setVisibility(View.INVISIBLE);
      }
    }
    mSelectList[xPosition] = 0;
    mTextViewList[xPosition].setVisibility(View.VISIBLE);
    mSelectID = xPosition;
  }

  /**
   * Create a network listener.
   */
  private NetworkListener mNetworkListener = new NetworkListener() {

    @Override
    public void unconnect() {
      if (mSelectID == 1) {
        Resources mRsrcs = mDownloadFragment.getActivity().getResources();
        SysApplication.showNagraDialog(mDownloadFragment.getActivity(),
            mRsrcs.getString(R.string.warning),
            mRsrcs.getString(R.string.netUnconnectedTxt),
            null,
            0,
            null,
            0,
            null,
            R.string.ok,
            null,
            null,
            0,
            null,
            null,
            null,
            null);
      }
    }

    @Override
    public void recoverConnection() {
      if (mSelectID == 1) {
        Resources mRsrcs = mDownloadFragment.getActivity().getResources();
        SysApplication.showNagraDialog(mDownloadFragment.getActivity(),
            mRsrcs.getString(R.string.warning),
            mRsrcs.getString(R.string.networkRecoverTxt),
            null,
            0,
            null,
            0,
            null,
            R.string.ok,
            null,
            null,
            0,
            null,
            null,
            null,
            null);
      }
    }
  };

  public interface IOnReceviedBussinessCard {
    public void onOnReceviedBusinessCard(BusinessCard card);
  }

  private IOnReceviedBussinessCard  mOnReceviedCard = new IOnReceviedBussinessCard() {

    @Override
    public void onOnReceviedBusinessCard(BusinessCard card) {
      mDownloadFragment.addDownload(card);
    }
  };
}