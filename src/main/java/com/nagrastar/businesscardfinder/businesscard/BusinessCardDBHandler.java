/*
 * DownloadDBHandler.java
 */
package com.nagrastar.businesscardfinder.businesscard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.nagrastar.businesscardfinder.util.NMPLog;

/// @cond HIDE_ALWAYS

/**
 * This class provide normal DB CRUD API.
 * 
 * @hide
 */
public class BusinessCardDBHandler {

  private static final String TAG = "BusinessCardDBHandler";
  private static final String DATABASE_NAME = "nmpdownload.db";
  private static final int DATABASE_VERSION = 1;

  private static SQLiteDatabase sDownloadDatabase = null;

  private DownloadDBHelper mDBHelper = null;
  private Context mContext = null;

  private class DownloadDBHelper extends SQLiteOpenHelper {
    public DownloadDBHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    public DownloadDBHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      NMPLog.d(TAG, "Enter");
      db.execSQL(BusinessCardDB.BUSINESSCARD_CREATE);
      NMPLog.d(TAG, "Leave");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      NMPLog.d(TAG, "Enter & Leave with oldVersion: " + oldVersion + " and  newVersion " + newVersion);
    }
  }

  public BusinessCardDBHandler(Context xContext) {
    NMPLog.d(TAG, "Enter");
    mContext = xContext;
    NMPLog.d(TAG, "Leave");
  }

  public BusinessCardDBHandler open() throws SQLException {
    NMPLog.d(TAG, "Enter");
    mDBHelper = new DownloadDBHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
    sDownloadDatabase = mDBHelper.getWritableDatabase();

    NMPLog.d(TAG, "Leave");
    return this;
  }

  public void close() {
    NMPLog.d(TAG, "Enter");
    sDownloadDatabase.close();
    NMPLog.d(TAG, "Leave");
  }

  public boolean isIOUsable(String xID) {
    NMPLog.d(TAG, "Enter with ID " + xID + " & Leave with true");
    return true;
  }

  private ContentValues fillDownloadValue(BusinessCard xBusinessCard, boolean xWithPrivateData) {

    ContentValues downloadValues = new ContentValues();

    downloadValues.put(BusinessCardDB.BUSINESSCARD_UUID, xBusinessCard.mUUID);
    downloadValues.put(BusinessCardDB.BUSINESSCARD_NAME, xBusinessCard.mName);
    downloadValues.put(BusinessCardDB.BUSINESSCARD_TITLE, xBusinessCard.mJobTitle);
    downloadValues.put(BusinessCardDB.BUSINESSCARD_PHONE, xBusinessCard.mPhone);
    downloadValues.put(BusinessCardDB.BUSINESSCARD_EMAIL, xBusinessCard.mEmail);
    downloadValues.put(BusinessCardDB.BUSINESSCARD_ADDRESS, xBusinessCard.mAddress);
    downloadValues.put(BusinessCardDB.BUSINESSCARD_COMPANY, xBusinessCard.mCompany);
    downloadValues.put(BusinessCardDB.BUSINESSCARD_TYPE, xBusinessCard.mType);
    downloadValues.put(BusinessCardDB.BUSINESSCARD_TAG, xBusinessCard.mTag);

    return downloadValues;
  }

  public boolean insertDownload(BusinessCard xBusinessCard)
  {
    NMPLog.d(TAG, "Enter with NAME " + xBusinessCard.mName);
    boolean ret = true;
    if(checkDownloadNameAndPhoneExist(xBusinessCard.getName(),xBusinessCard.getPhone()))
    {
      NMPLog.e(TAG, "BusinessCard already exist in DB!");
      return false;
    }
    ContentValues downloadValues = fillDownloadValue(xBusinessCard, true);

    sDownloadDatabase.beginTransaction();
    try {
      long ID = sDownloadDatabase.insert(BusinessCardDB.BUSINESSCARD_TABLE, null, downloadValues);
      if (ID <= 0) {
        NMPLog.e(TAG, "insert BusinessCard to DB failed!");
        ret = false;
      }
      sDownloadDatabase.setTransactionSuccessful();
    } finally {
      sDownloadDatabase.endTransaction();
    }

    NMPLog.d(TAG, "Leave");
    return ret;
  }

  private int queryDownloadID(String xUUID) {
    int downloadID = -1;

    downloadID = queryColumnID(BusinessCardDB.BUSINESSCARD_TABLE, new String[] {BusinessCardDB.BUSINESSCARD_ID}, BusinessCardDB.BUSINESSCARD_UUID + "=?",
                               new String[] {xUUID});

    return downloadID;
  }
  private boolean checkDownloadNameAndPhoneExist(String xName, String xPhone) {
    boolean ret = false;
    Cursor cursor = sDownloadDatabase.query(BusinessCardDB.BUSINESSCARD_TABLE, new String[] {BusinessCardDB.BUSINESSCARD_NAME},  BusinessCardDB.BUSINESSCARD_NAME + "=?",  new String[] {xName}, null, null, null, null);
    if (cursor != null && cursor.getCount() != 0)
    {
       ret = true;
    }
    cursor.close();
    return ret;
  }

  public boolean updateDownload(BusinessCard xBusinessCard) {
    NMPLog.d(TAG, "Enter");

    int downloadID = queryDownloadID(xBusinessCard.mUUID);
    if (downloadID == -1) {
      NMPLog.e(TAG, "Leave with downloadID is -1");
      return false;
    }

    sDownloadDatabase.beginTransaction();
    try {
	  ContentValues downloadValues = fillDownloadValue(xBusinessCard, false);
      sDownloadDatabase.update(BusinessCardDB.BUSINESSCARD_TABLE, downloadValues, BusinessCardDB.BUSINESSCARD_ID + "=" + downloadID, null);
      NMPLog.i(TAG, "update donwload done");

      sDownloadDatabase.setTransactionSuccessful();
      NMPLog.d(TAG, "setTransactionSuccessful");
    } finally {
      sDownloadDatabase.endTransaction();
    }

    NMPLog.d(TAG, "Leave with true");
    return true;
  }

  private Cursor getDownloadCursor(String xUUID) {
    Cursor cursor = sDownloadDatabase.query(BusinessCardDB.BUSINESSCARD_TABLE, null, BusinessCardDB.BUSINESSCARD_UUID + "=?", new String[] {xUUID}, null, null, null, null);
    return cursor;
  }
  private Cursor getDownloadCursorByType(String xType) {
    Cursor cursor = sDownloadDatabase.query(BusinessCardDB.BUSINESSCARD_TABLE, null, BusinessCardDB.BUSINESSCARD_TYPE + "=?", new String[]{xType}, null, null, null, null);

    return cursor;
  }

  private int getCursorInt(Cursor xCursor, String xQueryIndex) {
    int result = xCursor.getInt(xCursor.getColumnIndex(xQueryIndex));

    return result;
  }

  private long getCursorLong(Cursor xCursor, String xQueryIndex) {
    long result = xCursor.getLong(xCursor.getColumnIndex(xQueryIndex));

    return result;
  }

  private String getCursorString(Cursor xCursor, String xQueryIndex) {
    String result = xCursor.getString(xCursor.getColumnIndex(xQueryIndex));

    return result;
  }

  public boolean isUUIDUsable(String xUUID) {
    Cursor cursor = getDownloadCursor(xUUID);
    if(cursor != null && cursor.getCount() != 0) {
      return false;
    }
    return true;
  }

  public BusinessCard getDownloadByUUID(String xUUID) {
    NMPLog.d(TAG,"Enter with UUID " + xUUID);
    BusinessCard tempBusinessCard = null;

    Cursor cursor = getDownloadCursor(xUUID);
    if(cursor != null && cursor.getCount() != 0) {
      cursor.moveToFirst();

      int downloadID = getCursorInt(cursor, BusinessCardDB.BUSINESSCARD_ID);
      tempBusinessCard = new BusinessCard(getCursorString(cursor, BusinessCardDB.BUSINESSCARD_UUID), getCursorString(cursor, BusinessCardDB.BUSINESSCARD_NAME));

      tempBusinessCard.mJobTitle = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_TITLE);
      tempBusinessCard.mPhone = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_PHONE);
      tempBusinessCard.mEmail = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_EMAIL);
      tempBusinessCard.mAddress = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_ADDRESS);
      tempBusinessCard.mCompany = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_COMPANY);
      tempBusinessCard.mTag = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_TAG);
      tempBusinessCard.mType = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_TYPE);
    }

    cursor.close();
    NMPLog.d(TAG,"Leave");
    return tempBusinessCard;
  }
  public BusinessCard[] getDownloadByType(String xType) {
    NMPLog.d(TAG, "Enter");
    Cursor cursor = sDownloadDatabase.query(BusinessCardDB.BUSINESSCARD_TABLE, null,BusinessCardDB.BUSINESSCARD_TYPE + "=?", new String[] {xType}, null, null, null, null);
    if (cursor == null) {
      return null;
    }
    int rowNum = cursor.getCount();
    if (rowNum <= 0) {
      return null;
    }
    BusinessCard[] cardsWithSameType = new BusinessCard[rowNum];
    if (cursor.moveToFirst()) {
      int i = 0;
      do {
        String name = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_NAME);
        cardsWithSameType[i] = new BusinessCard(name);
        cardsWithSameType[i].mUUID = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_UUID);
        cardsWithSameType[i].mJobTitle = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_TITLE);
        cardsWithSameType[i].mPhone = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_PHONE);
        cardsWithSameType[i].mEmail = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_EMAIL);
        cardsWithSameType[i].mAddress = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_ADDRESS);
        cardsWithSameType[i].mCompany = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_COMPANY);
        cardsWithSameType[i].mTag = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_TAG);
        cardsWithSameType[i].mType = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_TYPE);
        i++;
      } while (cursor.moveToNext());
    }
    cursor.close();

    NMPLog.d(TAG, "Leave with count is " + rowNum);
    return cardsWithSameType;
  }

  public BusinessCard[] getDownloads() {
    NMPLog.d(TAG, "Enter");
    Cursor cursor = sDownloadDatabase.query(BusinessCardDB.BUSINESSCARD_TABLE, new String[] {BusinessCardDB.BUSINESSCARD_ID }, null, null, null, null, null);
    if (cursor == null ||  cursor.getCount() == 0) {
      NMPLog.w(TAG, "Leave with no download found.");
      return null;
    }

    int rowNum = cursor.getCount();
    if (rowNum <= 0) {
      return null;
    }
    BusinessCard[] retBusinessCard = new BusinessCard[rowNum];
    if (cursor.moveToFirst()) {
      int i = 0;
      do {
        retBusinessCard[i] = getDownloadByID(getCursorInt(cursor, BusinessCardDB.BUSINESSCARD_ID));
        i++;
      } while (cursor.moveToNext());
    }
    NMPLog.d(TAG, "Leave with count is " + rowNum);
    return retBusinessCard;
  }

  private BusinessCard getDownloadByID(int xID) {
    NMPLog.d(TAG, "Enter with ID " + xID);
    BusinessCard tempBusinessCard = null;

    Cursor cursor = sDownloadDatabase.query(BusinessCardDB.BUSINESSCARD_TABLE, null, BusinessCardDB.BUSINESSCARD_ID + "=?", new String[] {String.valueOf(xID)}, null, null, null, null);
    if (cursor != null && cursor.getCount() != 0) {
      cursor.moveToFirst();

      int downloadID = xID;
      tempBusinessCard = new BusinessCard(getCursorString(cursor, BusinessCardDB.BUSINESSCARD_UUID), getCursorString(cursor, BusinessCardDB.BUSINESSCARD_NAME));

      tempBusinessCard.mJobTitle = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_TITLE);
      tempBusinessCard.mPhone = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_PHONE);
      tempBusinessCard.mEmail = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_EMAIL);
      tempBusinessCard.mAddress = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_ADDRESS);
      tempBusinessCard.mCompany = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_COMPANY);
      tempBusinessCard.mTag = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_TAG);
      tempBusinessCard.mType = getCursorString(cursor, BusinessCardDB.BUSINESSCARD_TYPE);
    }
    cursor.close();
    NMPLog.d(TAG,"Leave");
    return tempBusinessCard;
  }

  public boolean deleteDownload(String xUUID) {
    NMPLog.d(TAG,"Enter with UUID " + xUUID);
    if (xUUID == null) {
      return false;
    }

    sDownloadDatabase.delete(BusinessCardDB.BUSINESSCARD_TABLE, BusinessCardDB.BUSINESSCARD_UUID + "=?", new String[] {xUUID});

    NMPLog.d(TAG,"Leave");
    return true;
  }

  private int queryColumnID(String table, String[] columns, String selection, String[] selectionArgs) {
    int ID = -1;
    if (columns == null) {
      return ID;
    }

    Cursor cursor = sDownloadDatabase.query(table, columns, selection, selectionArgs, null, null, null, null);
    if (cursor != null && cursor.getCount() != 0) {
      cursor.moveToFirst();
      ID = getCursorInt(cursor, columns[0]);
    }
    cursor.close();

    return ID;
  }
}
/// @endcond