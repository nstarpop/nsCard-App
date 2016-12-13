/**
 * BusinessCardHashMapAdapter.java
 * 
 * @brief This class implements the Hash map Adapter for a ListView.
 */
package com.nagrastar.businesscardfinder.fragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.nagrastar.businesscardfinder.R;
import com.nagrastar.businesscardfinder.businesscard.BusinessCard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * This class implements the Hash map Adapter for a ListView.
 *
 */
public class BusinessCardHashMapAdapter extends BaseAdapter {
  // Simple class to make it so that we don't have to call findViewById frequently
  private static class ViewHolder {
    TextView  mTagView;

    TextView  mNameView;
    TextView  mCompanyView;

    //TextView  mJobTitleView;
    //TextView  mPhoneView;
    //TextView  mEmailView;
    //TextView  mAddressView;
    ProgressBar mProgressBar;
    TextView    mStatusView;
  }

  Context mContext;
  private HashMap<String, BusinessCard> mData;
  List<String> mKeys = new LinkedList<String>();
  int count = 0;

  /**
   * Constructor of BusinessCardHashMapAdapter.
   * 
   * @param xContext
   *          <a href="http://developer.android.com/reference/android/content/Context.html">Context</a>
   *          used to access the systems services.
   *
   * @param xData
   *          HashMap of {@link BusinessCard} to be set into adapter.
   */
  public BusinessCardHashMapAdapter(Context xContext, HashMap<String, BusinessCard> xData) {
    mContext = xContext;
    mData    = xData;
    mKeys.addAll(mData.keySet());
  }


  /**
   * Add one item to the download listview
   * 
   * @param xItem
   *          {@link BusinessCard} object to add.
   */
  public void addItem(BusinessCard xItem) {
    mData.put(xItem.getUUID(), xItem);
    mKeys.add(xItem.getUUID());
  }


  /**
   * Remove one item from the download listview
   * 
   * @param xUuid
   *          {@link BusinessCard} uuid to remove.
   */
  public void removeItem(String xUuid) {
    mData.remove(xUuid);
    mKeys.remove(xUuid);
  }


  /**
   * Set view content of list view.
   * 
   * @param xPosition
   *          Index of the item whose view we want.
   * @param xConvertView
   *          View of every item.
   * @param xParent
   *          The parent that this view will eventually be attached to.
   */
  @Override
  public View getView(int xPosition, View xConvertView, ViewGroup xParent) {
    View row = xConvertView;
    String uuid = mKeys.get(xPosition);
    final BusinessCard info = mData.get(uuid);

    ViewHolder holder = null;
    if(null == row) {
      LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      holder = new ViewHolder();

      if (info.getType().equals("SELF")) {
        row = inflater.inflate(R.layout.personal_item, xParent, false);

        holder.mTagView = (TextView) row.findViewById(R.id.itemTag);
        holder.mNameView = (TextView) row.findViewById(R.id.itemName);
        holder.mCompanyView = (TextView)row.findViewById(R.id.itemComany);

      /*holder.mJobTitleView = (TextView) row.findViewById(R.id.textView2);
        holder.mPhoneView = (TextView)row.findViewById(R.id.textView3);
        holder.mEmailView = (TextView)row.findViewById(R.id.textView4);
        holder.mAddressView = (TextView)row.findViewById(R.id.textView5);*/

      } else {
        row = inflater.inflate(R.layout.download_item, xParent, false);

        holder.mNameView = (TextView) row.findViewById(R.id.nameTxt);
        holder.mProgressBar = (ProgressBar) row.findViewById(R.id.downloadProgressbar);
        holder.mStatusView = (TextView)row.findViewById(R.id.prgressTxt);

      }

      row.setTag(holder);
    } else {
      holder = (ViewHolder) row.getTag();
    }
    if (holder.mTagView != null) {
      holder.mTagView.setText(info.getmTag());
    }

    if (holder.mNameView != null) {
      holder.mNameView.setText(info.getName());
    }

    if (holder.mCompanyView != null) {
      holder.mCompanyView.setText(info.getCompany());
    }

//    holder.mJobTitleView.setText(info.getJobTitle());
//    holder.mPhoneView.setText(info.getPhone());
//    holder.mEmailView.setText(info.getEmail());
//    holder.mAddressView.setText(info.getAddress());

    return row;
  }


  /**
   * Get list view items count.
   * 
   */
  @Override
  public int getCount() {
    return mData.size();
  }


  /**
   * Get item by position.
   * 
   * @param xPosition
   *          Index of the item whose view we want.
   */
  @Override
  public Object getItem(int xPosition) {
    String uuid = mKeys.get(xPosition);
    final BusinessCard info = mData.get(uuid);
    return info;
  }


  /**
   * Get item id by position.
   * 
   * @param xPosition
   *          Index of the item whose view we want.
   */
  @Override
  public long getItemId(int xPosition) {
    return xPosition;
  }
}
