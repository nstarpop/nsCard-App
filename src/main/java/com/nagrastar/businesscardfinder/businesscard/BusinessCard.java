/*
 * BusinessCard.java
 */
package com.nagrastar.businesscardfinder.businesscard;

import com.nagrastar.businesscardfinder.util.NMPLog;

/**
 * <p>
 * This class aggregates all the information pertaining to the download of a
 * media asset.
 */
public class BusinessCard {
  public static final String TAG = "BusinessCard";

  String mUUID = null;
  String mName = null;
  String mJobTitle;
  String mPhone;
  String mEmail;
  String mAddress;
  String mCompany;
  String mType = "SELF";
  String mTag;

  public BusinessCard(final String xName) {
    mUUID = java.util.UUID.randomUUID().toString();;
    mName = xName;
    NMPLog.i(TAG, "uuid: " + mUUID);
  }

  public BusinessCard(final String xName, final String xJobTitle, final String xPhone, final String xEmail, final String xAddress,final String xCompany, final String xType,final String xTag) {
    mUUID = java.util.UUID.randomUUID().toString();;
    mName = xName;
    mJobTitle = xJobTitle;
    mPhone = xPhone;
    mEmail = xEmail;
    mAddress = xAddress;
    mCompany = xCompany;
    mType = xType;
    mTag = xTag;
    NMPLog.i(TAG, "uuid: " + mUUID);
  }
  /**
   * Create a BusinessCard object given UUID and URI.
   *
   * @param xUUID
   *          The UUID of the object to download.
   * @param xName
   *          The Name of the object to download.
   */
  public BusinessCard(final String xUUID, final String xName) {
    mUUID = xUUID;
    mName = xName;

  }

  /**
   * Create a BusinessCard object as a copy of the given BusinessCard
   *
   * @param xIn
   *          The BusinessCard object to copy.
   */
  BusinessCard(final BusinessCard xIn) {
    mUUID = xIn.mUUID;
    mName = xIn.mName;

    mJobTitle = xIn.mJobTitle;
    mPhone = xIn.mPhone;
    mEmail = xIn.mEmail;
    mAddress = xIn.mAddress;
    mCompany = xIn.mCompany;
    mTag = xIn.mTag;
    mType = xIn.mType;
  }

  /**
   * Returns the Universal Unique ID attributed to this BusinessCard. This value
   * acts as a key for all requests and operations linked to the download.
   *
   * @return the Universal Unique ID.
   */
  public final String getUUID() {
    return mUUID;
  }

  public void setName(String name) {
    mName = name;
  }

  public final String getName() {
    return mName;
  }

  public void setJobTitle(String jobTitle) {
    mJobTitle = jobTitle;
  }

  public final String getJobTitle() {
    return mJobTitle;
  }

  public void setPhone(String phone) {
    mPhone = phone;
  }

  public final String getPhone() {
    return mPhone;
  }

  public void setEmail(String email) {
    mEmail = email;
  }

  public final String getEmail() {
    return mEmail;
  }

  public void setAddress(String address) {
    mAddress = address;
  }

  public final String getAddress() {
    return mAddress;
  }

  public void setCompany(String company) {
    mCompany = company;
  }

  public final String getCompany() {
    return mCompany;
  }

  public final String getType() {
    return mType;
  }
  public final String getmTag() {
    return mTag;
  }


  /**
   * Get a string representation of this BusinessCard.
   * 
   * @return String The string representation of this BusinessCard.
   */
  @Override
  public final String toString() {
    return "NMPDownload UUID: " + mUUID + ", Name: " + mName
        + ", JobTitle: " + mJobTitle + ",Phone: " + mPhone + ", email: " + mEmail + ", Address: "
        + mAddress + ", Company: " + mCompany + ", Tag: " + mTag  + ", Type: " + mType;
  }
}