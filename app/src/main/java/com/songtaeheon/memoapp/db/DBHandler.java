package com.songtaeheon.memoapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.songtaeheon.memoapp.model.Memo;
import com.songtaeheon.memoapp.model.Picture;

import java.util.ArrayList;

public class DBHandler {

    private final String TAG = "DBHandler";

    private SQLiteOpenHelper mHelper;
    private SQLiteDatabase mDB = null;

    public DBHandler(Context context, String name) {
        mHelper = new DBHelper(context, name, null, 1);
    }

    public DBHandler(Context context) {
        mHelper = new DBHelper(context, DBConstants.DB_NAME, null, 1);
    }

    private Cursor selectAllMemo(){
        mDB = mHelper.getReadableDatabase();
        Cursor c = mDB.rawQuery("SELECT * FROM " + DBConstants.MEMO_TABLE_NAME + " ORDER BY " + DBConstants.TIME + " DESC", null);
        return c;
    }

    public ArrayList<Memo> getAllMemo(){
        Log.d(TAG, "getAllMemo");
        ArrayList<Memo> list = new ArrayList<>();

        Cursor cursor = selectAllMemo();
        if(cursor != null){
            cursor.moveToFirst();
            if(cursor.getCount() != 0) {
                do {
                    Memo memo = new Memo();
                    memo.setId(cursor.getLong(cursor.getColumnIndex(DBConstants.ID)));
                    memo.setTitle(cursor.getString(cursor.getColumnIndex(DBConstants.TITLE)));
                    memo.setDetail(cursor.getString(cursor.getColumnIndex(DBConstants.DETAIL)));
                    memo.setThumbnailUri(cursor.getString(cursor.getColumnIndex(DBConstants.THUMBNAIL)));//null일 수 있음.
                    Log.d(TAG, "memo list : " + memo.getTitle() + " uri : "+ memo.getThumbnailUri());
                    list.add(memo);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return list;
    }


    private Cursor selectPictures(long memo_id){
        mDB = mHelper.getReadableDatabase();
        Cursor c = mDB.rawQuery("SELECT "+DBConstants.IMAGE_URI +", " + DBConstants.ID +" FROM " + DBConstants.PICTURE_TABLE_NAME + " WHERE " + DBConstants.MEMO_ID + "=? ORDER BY " + DBConstants.ID, new String[] {Long.toString(memo_id)});
        return c;
    }

    private void insertPicture(Picture pic, long memo_id){
        mDB = mHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(DBConstants.IMAGE_URI, pic.getUri());
        value.put(DBConstants.MEMO_ID, memo_id);

        mDB.insert(DBConstants.PICTURE_TABLE_NAME, null, value);
    }

    public ArrayList<Picture> getAllPictures(long memo_id){
        Log.d(TAG, "getAllPictures + " + memo_id);
        ArrayList<Picture> list = new ArrayList<>();

        Cursor cursor = selectPictures(memo_id);
        if(cursor != null){
            cursor.moveToFirst();
            if(cursor.getCount() != 0) {
                do {
                    Picture pic = new Picture(
                            cursor.getInt(cursor.getColumnIndex(DBConstants.ID)),
                            cursor.getString(cursor.getColumnIndex(DBConstants.IMAGE_URI)));

                    Log.d(TAG, "pic list : " + pic.getId() + " uri : "+ pic.getUri());
                    list.add(pic);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return list;
    }

    public long insertMemoAndGetRowId(Memo memo, String time){
        mDB = mHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(DBConstants.TITLE, memo.getTitle());
        value.put(DBConstants.DETAIL, memo.getDetail());
        value.put(DBConstants.THUMBNAIL, memo.getThumbnailUri());
        value.put(DBConstants.TIME, time);

        long rowId = mDB.insert(DBConstants.MEMO_TABLE_NAME, null, value);
        return rowId;
    }


    public void deleteMemo(long id)
    {
        Log.d(TAG, "delete");
        mDB = mHelper.getWritableDatabase();
        mDB.delete(DBConstants.MEMO_TABLE_NAME, "_ID=?", new String[]{Long.toString(id)});
    }

    public void updateMemo(Memo memo, String time)
    {
        Log.d(TAG, "delete");
        mDB = mHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(DBConstants.TITLE, memo.getTitle());
        value.put(DBConstants.DETAIL, memo.getDetail());
        value.put(DBConstants.THUMBNAIL, memo.getThumbnailUri());
        value.put(DBConstants.TIME, time);
        mDB.update(DBConstants.MEMO_TABLE_NAME, value, DBConstants.ID + "=" + memo.getId(), null);
    }

    public void insertAllPictures(ArrayList<Picture> list, long memo_id){
        for(int i = 0; i < list.size(); i++){
            Picture pic = list.get(i);
            if(pic.getId() == -1){//-1이 아니면 이미 들어있는 것
                insertPicture(pic, memo_id);
            }
        }
    }

    private void deletePicture(long id){//picture id!!
        Log.d(TAG, "delete");
        mDB = mHelper.getWritableDatabase();
        mDB.delete(DBConstants.PICTURE_TABLE_NAME, "_ID=?", new String[]{Long.toString(id)});
    }

    public void deleteAllPicture(long memoId){
        Log.d(TAG, "delete all picture");
        mDB = mHelper.getWritableDatabase();
        mDB.delete(DBConstants.PICTURE_TABLE_NAME, DBConstants.MEMO_ID+"=?", new String[]{Long.toString(memoId)});
    }

    public void deletePictureList(ArrayList<Picture> deletedList){
        for(int i = 0; i < deletedList.size(); i++){
            long deletedDbId = deletedList.get(i).getId();
            if( deletedDbId != -1){
                deletePicture(deletedDbId);
            }
        }
    }

    public void insertPictureList(ArrayList<Picture> pictureList, long memoId){
        for(int i = 0; i < pictureList.size(); i++){
            if( pictureList.get(i).getId() == -1){
                insertPicture(pictureList.get(i), memoId);
            }
        }
    }



    public void close() {
        mHelper.close();
    }



}