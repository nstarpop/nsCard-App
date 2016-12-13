/*
 * DownloadDB.java
 */
package com.nagrastar.businesscardfinder.businesscard;

import android.provider.BaseColumns;

/// @cond HIDE_ALWAYS

/**
 * This class define the tables of DownloadBD, includes BusinessCard, Asset and
 * MediaInfo.
 * 
 * @hide
 * 
 */
public final class BusinessCardDB implements BaseColumns {
  public static final String TAG = "NMPDownloadDB";

  /*
   * BusinessCard table contains the information of download resource and status,
   * it includes Asset as sub table
   */
  public static final String BUSINESSCARD_TABLE				= "BusinessCard";
  public static final String BUSINESSCARD_ID				= "DownloadID"; //primary key
  public static final String BUSINESSCARD_UUID				= "UUID";
  public static final String BUSINESSCARD_NAME				= "NAME";
  public static final String BUSINESSCARD_TITLE			= "jobTitle";
  public static final String BUSINESSCARD_PHONE		= "Phone";
  public static final String BUSINESSCARD_EMAIL		= "eMail";
  public static final String BUSINESSCARD_ADDRESS	= "Address";
  public static final String BUSINESSCARD_COMPANY	= "Company";
  public static final String  BUSINESSCARD_TYPE	= "Type"; // SELF,  OTHER
  public static final String  BUSINESSCARD_TAG	= "Tag"; // PER, COP



  public static final String BUSINESSCARD_CREATE = "CREATE TABLE  " + BUSINESSCARD_TABLE + " ("
      + BUSINESSCARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
      + BUSINESSCARD_UUID + " TEXT NOT NULL,"
      + BUSINESSCARD_NAME + " VARCHAR NOT NULL,"
      + BUSINESSCARD_TITLE + " VARCHAR,"
      + BUSINESSCARD_PHONE + " TEXT,"
      + BUSINESSCARD_EMAIL + " VARCHAR,"
      + BUSINESSCARD_ADDRESS + " TEXT,"
      + BUSINESSCARD_COMPANY + " VARCHAR,"
      + BUSINESSCARD_TAG + " TEXT,"
      + BUSINESSCARD_TYPE + " TEXT)";
}
/// @endcond