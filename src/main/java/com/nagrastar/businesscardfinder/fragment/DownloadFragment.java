/**
 * DownloadFragment.java
 */
package com.nagrastar.businesscardfinder.fragment;

import android.Manifest;
import android.app.Activity;

import android.content.DialogInterface;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.nagrastar.businesscardfinder.R;
import com.nagrastar.businesscardfinder.SysApplication;
import com.nagrastar.businesscardfinder.businesscard.BusinessCard;
import com.nagrastar.businesscardfinder.businesscard.BusinessCardDBHandler;
import com.nagrastar.businesscardfinder.util.NMPLog;

/**
 * This class provides an example of how to use "BusinessCard to go" module.
 *
 */
public class DownloadFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback {
  private static final String TAG = "DownloadFragment";

  private String mRootPath = "NMPVideoViewSample";

  private HashMap<String, BusinessCard> mData = new HashMap<String, BusinessCard>();
  private BusinessCardHashMapAdapter mAdapter = null;

  private BusinessCard mSelectedBusinessCard = null;
  private onPermissionsResult mPermissionListener;

  private ListView mListView;
  private View mView = null;
  private Handler mHandler = new Handler();
  private BusinessCardDBHandler mBusinessCardDBHandler = null;

  public void setDBHandler(BusinessCardDBHandler businessCardDBHandler) {
    mBusinessCardDBHandler = businessCardDBHandler;
  }
  /**
   * Get the rate at which the segments of a download are downloading.
   *
   * @param xdObj
   *          The BusinessCard object to get the rate for.
   * @return The progress of the download as a factor of the total number of
   *         segments.
   */
  public static int getProgressBarRate(BusinessCard xdObj) {

    return 50;
  }


  /**
   * Instantiates the user interface view, & gets the DRM handler.
   *
   * @param xInflater
   *          The LayoutInflater object that is used to inflate views in the
   *          fragment.
   * @param xContainer
   *          Used when inflating views.
   * @param xSavedInstanceState
   *          Not used.
   */
  public View onCreateView(LayoutInflater xInflater, ViewGroup xContainer, Bundle xSavedInstanceState) {
    NMPLog.v(TAG, "Enter");

    mView = xInflater.inflate(R.layout.download_fragment_layout, xContainer, false);

    NMPLog.v(TAG, "Leave");
    return mView;
  }


  /**
   * Get shared preferences & get resources based on download mode.
   *
   * @param xSavedInstanceState
   *          If the fragment is being re-created from a previous saved state,
   *          this is the state.
   */
  @Override
  public void onActivityCreated(Bundle xSavedInstanceState) {
    super.onActivityCreated(xSavedInstanceState);
    NMPLog.v(TAG, "Enter");

    initListView(mView);

    NMPLog.v(TAG, "Leave");
  }

  public void requestStoragePermission() {
    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      Snackbar.make(mView, R.string.permission_dl_extStroage, Snackbar.LENGTH_INDEFINITE)
          .setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }
          })
          .show();
    } else {
      requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }
  }

  private interface onPermissionsResult {
    void permissionsCallback(boolean result);
  }

  public void setPermissionsListener(onPermissionsResult eventListener) {
    mPermissionListener = eventListener;
  }

  public void isWritePermissionStorage() {
    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      requestStoragePermission();
    } else {
      mPermissionListener.permissionsCallback(true);
    }
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull
  String[] permissions,@NonNull int[] grantResults) {
    if (requestCode == 0) {
      // Check if the only required permission has been granted
      if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Snackbar.make(mView,R.string.permission_stroage_granted, Snackbar.LENGTH_SHORT).show();
        mPermissionListener.permissionsCallback(true);
      } else {
        Snackbar.make(mView,R.string.permission_stroage_denied, Snackbar.LENGTH_SHORT).show();
        mPermissionListener.permissionsCallback(false);
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  /**
   * Called when a fragment is first attached to its activity.
   *
   * @param xActivity
   *          The activity attached to.
   */
  @Override
  public void onAttach(Activity xActivity) {
    super.onAttach(xActivity);
    NMPLog.v(TAG, "Enter & Leave");
  }


  /**
   * Called to do initial creation of a fragment.
   *
   * @param xSavedInstanceState
   *          If the fragment is being re-created from a previous saved state,
   *          this is the state.
   */
  @Override
  public void onCreate(Bundle xSavedInstanceState) {
    super.onCreate(xSavedInstanceState);
    NMPLog.v(TAG, "Enter & Leave");
  }


  /**
   * Called when the Fragment is visible to the user.
   */
  @Override
  public void onStart() {
    super.onStart();
    NMPLog.v(TAG, "Enter & Leave");
  }


  /**
   * Called when the fragment is visible to the user and actively running.
   */
  @Override
  public void onResume() {
    super.onResume();
    NMPLog.v(TAG, "Enter & Leave");
  }


  /**
   * Called when the Fragment is no longer resumed.
   */
  @Override
  public void onPause() {
    super.onPause();
    NMPLog.v(TAG, "Enter & Leave");
  }


  /**
   * Called when the Fragment is no longer started.
   */
  @Override
  public void onStop() {
    super.onStop();
    NMPLog.v(TAG, "Enter & Leave");
  }


  /**
   * Called when the view previously created by
   * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has been detached
   * from the fragment.
   */
  @Override
  public void onDestroyView() {
    mBusinessCardDBHandler = null;
    super.onDestroyView();
    NMPLog.v(TAG, "Enter & Leave");
  }


  /**
   * Called when the fragment is destroy. Release the download resources.
   */
  @Override
  public void onDestroy() {
    NMPLog.v(TAG, "Enter");
    releaseDownloadManger();

    super.onDestroy();
    NMPLog.v(TAG, "Leave");
  }


  /**
   * Called when the fragment is no longer attached to its activity.
   */
  @Override
  public void onDetach() {
    super.onDetach();
    NMPLog.v(TAG, "Enter & Leave");
  }

  /**
   * Release the resources used by the download manager.
   */
  private void releaseDownloadManger() {
    /*if (mDownloading != null && !mIsUsedDlService) {
      mDlManger.pauseDownload(mDownloading.getUUID());
    }
    mDlManger.unregisterDownloadStateListener(mDownloadListener);
    mDlManger = null;*/
  }


  /**
   * Get a string path for mRootPath on the external storage device.
   *
   * @return the file path of all downloaded assets on the external storage
   *         device.
   */
  private String getFilePath() {
    String dlFilePath = Environment.getExternalStorageDirectory() + java.io.File.separator + mRootPath + java.io.File.separator;
    File file = new File(dlFilePath);
    if (!file.exists()) {
      file.mkdirs();
    }
    return file.getAbsolutePath();
  }


  /**
   * Create a list of all downloads and add to the list view in <xView> with a
   * handler to create a download dialog for selected items.
   *
   * @param xView
   *          download fragment view.
   *
   */
  private void initListView(final View xView) {
    NMPLog.v(TAG, "Enter");
    if (mBusinessCardDBHandler == null) {
      NMPLog.w(TAG, "Leave with mBusinessCardDBHandler or view is invalid");
      return;
    }

    mListView = (ListView) xView.findViewById(R.id.listView);
    mData.clear();
    BusinessCard[] businessCards = mBusinessCardDBHandler.getDownloadByType("OTHERS");
    if (businessCards != null) {
      for (BusinessCard businessCard : businessCards) {
        String uuid = businessCard.getUUID();
          mData.put(uuid, businessCard);
      }
    }

    NMPLog.i(TAG, "new mAdapter");
    mAdapter = new BusinessCardHashMapAdapter(this.getActivity(), mData);
    mListView.setAdapter(mAdapter);
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> xParent, View xView, int xPosition, long xId) {
        mSelectedBusinessCard = (BusinessCard) mAdapter.getItem(xPosition);
        createDownloadDialog();
      }
    });
    NMPLog.v(TAG, "Leave");
  }


  /**
   * Depending on the current download state, create a relevant AlertDialog for
   * the state and show to the user.
   */
  private void createDownloadDialog() {
    if (mSelectedBusinessCard == null) {
      return;
    }

    final View cardView = getActivity().getLayoutInflater().inflate(R.layout.card_detail, null);
    ((TextView)cardView.findViewById(R.id.name)).setText(mSelectedBusinessCard.getName());
    ((TextView)cardView.findViewById(R.id.jobTitle)).setText(mSelectedBusinessCard.getJobTitle());
    ((TextView)cardView.findViewById(R.id.phone)).setText(mSelectedBusinessCard.getPhone());
    ((TextView)cardView.findViewById(R.id.email)).setText(mSelectedBusinessCard.getEmail());
    ((TextView)cardView.findViewById(R.id.address)).setText(mSelectedBusinessCard.getAddress());
    ((TextView)cardView.findViewById(R.id.company)).setText(mSelectedBusinessCard.getCompany());
    //TODO, ...

    SysApplication.showNagraDialog( getActivity(),
        mSelectedBusinessCard.getName(), null, cardView,
                   0, null,
                   0, null,
                   R.string.remove, mRemoveListener,
                   null, 0, null,
                   null, null, null );
  }

  /**
   * Create an onClick listener that will attempt to remove the download of a user selected(onClick)
   * item.
   */
  android.content.DialogInterface.OnClickListener mRemoveListener = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      NMPLog.d(TAG, "removeListener ");
      if (mBusinessCardDBHandler != null) {
        String uuid = mSelectedBusinessCard.getUUID();
        removeDownload(uuid);
      }
    }
  };

  public void addDownload(final BusinessCard xBusinessCard) {
    if (mBusinessCardDBHandler == null) {
      return;
    }
    boolean ret = mBusinessCardDBHandler.insertDownload(xBusinessCard);

    if (ret) {
      mHandler.post(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(DownloadFragment.this.getActivity(), R.string.download_added, Toast.LENGTH_SHORT).show();
          if (mAdapter != null) {
            mAdapter.addItem(xBusinessCard);
            mAdapter.notifyDataSetChanged();
          }
        }
      });
    }
  }

  private void removeDownload(String xUUID) {
    NMPLog.e(TAG, "removeDownload UUID --> " + xUUID);
    mBusinessCardDBHandler.deleteDownload(xUUID);

    mAdapter.removeItem(xUUID);
    mAdapter.notifyDataSetChanged();

    Toast.makeText(DownloadFragment.this.getActivity(), R.string.download_removed, Toast.LENGTH_LONG).show();
  }

}
