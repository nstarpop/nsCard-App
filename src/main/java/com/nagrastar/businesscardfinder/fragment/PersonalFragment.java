/**
 * PersonalFragment.java
 */
package com.nagrastar.businesscardfinder.fragment;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nagrastar.businesscardfinder.R;
import com.nagrastar.businesscardfinder.SysApplication;
import com.nagrastar.businesscardfinder.businesscard.BusinessCard;
import com.nagrastar.businesscardfinder.businesscard.BusinessCardDBHandler;
import com.nagrastar.businesscardfinder.util.NMPLog;

/**
 * The PersonalFragment class implement GUI to:
 * <p>
 * <li>list all available streams</li>
 * <li>trigger a stream playback</li>
 * <li>trigger a stream download</li>
 * </p>
 * Extends
 * <a href="http://developer.android.com/reference/android/support/v4/app/ListFragment.html">ListFragment</a>
 */
public class PersonalFragment extends Fragment {

  private static final String TAG = "PersonalFragment";
  private Button mCreadCard = null;

  private ArrayList<String> mStreamsStringList = null;

  private HashMap<String, BusinessCard> mData = new HashMap<String, BusinessCard>();
  private BusinessCardHashMapAdapter mAdapter = null;
  private BusinessCard mSelectedBusinessCard = null;

  private ListView mListView;
  private View mView = null;
  private BusinessCardDBHandler mBusinessCardDBHandler = null;

  public void setDBHandler(BusinessCardDBHandler businessCardDBHandler) {
    mBusinessCardDBHandler = businessCardDBHandler;
  }

  /**
   * Provides a default View implementation for this Fragment.
   * 
   * @param xInflater
   *          The LayoutInflater object that can be used to inflate any views in
   *          the fragment.
   * @param xContainer
   *          If non-null, this is the parent view that the fragment's UI should
   *          be attached to. The fragment should not add the view itself, but
   *          this can be used to generate the LayoutParams of the view.
   * @param xSavedInstanceState
   *          If non-null, this fragment is being re-constructed from a previous
   *          saved state as given here.
   */
  public View onCreateView(LayoutInflater xInflater, ViewGroup xContainer,  Bundle xSavedInstanceState) {
    NMPLog.v(TAG, "Enter");

    mView = xInflater.inflate(R.layout.stream_fragment_layout, xContainer, false);

    NMPLog.v(TAG, "Leave");
    return mView;
  }


  /**
   * <p>
   * Setup a list of available streams from the signed-on server and apply to
   * this objects
   * <a href="http://developer.android.com/reference/android/widget/ListFragment.html">ListAdapter</a>
   * </p>
   * <p>
   * Get a
   * <a href="http://developer.android.com/reference/android/os/Handler.html">Handler</a>
   * to show toast messages.
   * </p>
   * 
   * @param xSavedInstanceState
   *          If the fragment is being re-created from a previous saved state,
   *          this is the state.
   */
  @Override
  public void onActivityCreated(Bundle xSavedInstanceState) {
    super.onActivityCreated(xSavedInstanceState);
    NMPLog.v(TAG, "Enter");

    mCreadCard = (Button) getActivity().findViewById(R.id.create_card);
    mCreadCard.setOnClickListener(mCreateCardListener);

    initListView(mView);

    NMPLog.v(TAG, "Leave");
  }


  /**
   * Create filter and goto, views and listeners for this Fragment.
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

  private void initListView(final View xView) {
    NMPLog.v(TAG, "Enter");
    if (mBusinessCardDBHandler == null) {
      NMPLog.w(TAG, "Leave with mBusinessCardDBHandler or view is invalid");
      return;
    }

    mListView = (ListView) xView.findViewById(R.id.personalListView);
    mData.clear();
    BusinessCard[] businessCards = mBusinessCardDBHandler.getDownloadByType("SELF");
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
          R.string.update_card, mEditListener,
          R.string.remove, mRemoveListener,
          null, 0, null,
          null, null, null );
  }


  View.OnClickListener mCreateCardListener = new OnClickListener() {
    @Override
    public void onClick(View xView) {
      final View cardView = getActivity().getLayoutInflater().inflate(R.layout.personal_card, null);
      Resources mRsrcs = getActivity().getResources();
      SysApplication.showNagraDialog(getActivity(),
              mRsrcs.getString(R.string.create_card), null, cardView,
              R.string.cancel, null,
              0, null,
              R.string.save, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    String name = ((EditText)cardView.findViewById(R.id.editName)).getText().toString();
                    String title = ((EditText)cardView.findViewById(R.id.editJobTitle)).getText().toString();
                    String phone = ((EditText)cardView.findViewById(R.id.editPhone)).getText().toString();
                    String email = ((EditText)cardView.findViewById(R.id.editEmail)).getText().toString();
                    String address = ((EditText)cardView.findViewById(R.id.editAddress)).getText().toString();
                    String company = ((EditText)cardView.findViewById(R.id.editCompany)).getText().toString();
					String type = "SELF";
                    String tag = "COP";

                    NMPLog.v(TAG, "name " + name);
                    BusinessCard card = new BusinessCard(name, title, phone,email,address,company, type, tag);
                    addDownload(card);
                    NMPLog.v(TAG, "mAdapter size " + mAdapter.getCount());
                  }
              },
              null, 0, null,
              null, null, null);

    }
  };

  /**
   * Create an onClick listener that creates a list of download bitrates for a user
   * selected(onClick) item to download.
   */
  android.content.DialogInterface.OnClickListener mEditListener = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      NMPLog.d(TAG, "mEditListener ");
      if (mSelectedBusinessCard == null) {
        return;
      }

      final View cardView = getActivity().getLayoutInflater().inflate(R.layout.personal_card, null);
      ((TextView)cardView.findViewById(R.id.editName)).setText(mSelectedBusinessCard.getName());
      ((TextView)cardView.findViewById(R.id.editJobTitle)).setText(mSelectedBusinessCard.getJobTitle());
      ((TextView)cardView.findViewById(R.id.editPhone)).setText(mSelectedBusinessCard.getPhone());
      ((TextView)cardView.findViewById(R.id.editEmail)).setText(mSelectedBusinessCard.getEmail());
      ((TextView)cardView.findViewById(R.id.editAddress)).setText(mSelectedBusinessCard.getAddress());
      ((TextView)cardView.findViewById(R.id.editCompany)).setText(mSelectedBusinessCard.getCompany());

      Resources mRsrcs = getActivity().getResources();
      SysApplication.showNagraDialog(getActivity(),
        mRsrcs.getString(R.string.update_card), null, cardView,
        R.string.save, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String name = ((EditText)cardView.findViewById(R.id.editName)).getText().toString();
            String title = ((EditText)cardView.findViewById(R.id.editJobTitle)).getText().toString();
            String phone = ((EditText)cardView.findViewById(R.id.editPhone)).getText().toString();
            String email = ((EditText)cardView.findViewById(R.id.editEmail)).getText().toString();
            String address = ((EditText)cardView.findViewById(R.id.editAddress)).getText().toString();
            String company = ((EditText)cardView.findViewById(R.id.editCompany)).getText().toString();
            String tag = "ns";
            //TODO, ...

            mSelectedBusinessCard.setName(name);
            mSelectedBusinessCard.setJobTitle(title);
            mSelectedBusinessCard.setPhone(phone);
            mSelectedBusinessCard.setEmail(email);
            mSelectedBusinessCard.setAddress(address);
            mSelectedBusinessCard.setCompany(company);

            updateDownload(mSelectedBusinessCard);
          }
        },
        0, null,
        R.string.cancel, null,
        null, 0, null,
        null, null, null);

    }
  };

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

  private void addDownload(BusinessCard xBusinessCard) {
    if (mAdapter != null) {
      mBusinessCardDBHandler.insertDownload(xBusinessCard);
      mAdapter.addItem(xBusinessCard);
      mAdapter.notifyDataSetChanged();

      Toast.makeText(PersonalFragment.this.getActivity(), R.string.personal_added, Toast.LENGTH_LONG).show();
    }
  }

  private void updateDownload(BusinessCard xBusinessCard) {
  mBusinessCardDBHandler.updateDownload(xBusinessCard);
  mAdapter.notifyDataSetChanged();

  Toast.makeText(PersonalFragment.this.getActivity(), R.string.personal_updated, Toast.LENGTH_LONG).show();
  }

  private void removeDownload(String xUUID) {
    NMPLog.e(TAG, "removeDownload UUID --> " + xUUID);
    mBusinessCardDBHandler.deleteDownload(xUUID);

    mAdapter.removeItem(xUUID);
    mAdapter.notifyDataSetChanged();

    Toast.makeText(PersonalFragment.this.getActivity(), R.string.download_removed, Toast.LENGTH_LONG).show();
  }

}
