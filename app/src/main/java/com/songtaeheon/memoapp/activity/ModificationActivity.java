package com.songtaeheon.memoapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.songtaeheon.memoapp.db.DBHandler;
import com.songtaeheon.memoapp.adapter.DetailAdapter;
import com.songtaeheon.memoapp.model.Memo;
import com.songtaeheon.memoapp.model.Picture;
import com.songtaeheon.memoapp.interfaces.OnPicDeleteListener;
import com.songtaeheon.memoapp.R;
import com.songtaeheon.memoapp.utils.BroadcastUtil;
import com.songtaeheon.memoapp.utils.PermissionUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModificationActivity extends AppCompatActivity implements OnPicDeleteListener {

    public static final String TAG = "ModificationActivity";

    private static final int PICK_FROM_GALLARY = 0;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_URL = 2;

    private static final int REQUEST_GALLERY = 1000;
    private static final int REQUEST_CAMERA = 1001;

    private EditText mTitleEditText;
    private EditText mDetailEditText;
    private ViewPager mViewPager;

    private Memo mMemo;
    private ArrayList<Picture> mPictureList;
    private ArrayList<Picture> mDeletedList;

    private DetailAdapter pagerAdapter;
    private DBHandler dbHandler = null;

    private boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification);

        mViewPager = findViewById(R.id.full_viewPager);
        mTitleEditText = findViewById(R.id.title_edittext);
        mDetailEditText = findViewById(R.id.detail_edittext);

        dbHandler = new DBHandler(this);
        mDeletedList = new ArrayList<>();

        Intent intent = getIntent();
        mMemo = (Memo) intent.getSerializableExtra(getString(R.string.memo));

        if(mMemo == null) {//새로 추가된 경우
            isNew = true;
            mMemo = new Memo();
            mPictureList = new ArrayList<>();
        }else {//수정하는 경우
            isNew = false;
            mPictureList = dbHandler.getAllPictures(mMemo.getId());

            mTitleEditText.setText(mMemo.getTitle());
            mDetailEditText.setText(mMemo.getDetail());
            if(mMemo.getTitle().equals("")) mTitleEditText.setHint(getString(R.string.noTitle));
            if(mMemo.getDetail().equals("")) mDetailEditText.setHint(getString(R.string.noDetail));
        }

        setUpViewPager();
    }

    //액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id == R.id.action_complete) {
            setMemoFromUI();

            if(isNew) {
                addNewMemoInDb();
                BroadcastUtil.sendMemo(mMemo, this, getString(R.string.actionAdd));
            }else{
                modifyMemoInDb();
                //update되면, 원래 있던 걸 지우고, 새로 추가한다.
                BroadcastUtil.sendMemo(mMemo, this, getString(R.string.actionDelete));
                BroadcastUtil.sendMemo(mMemo, this, getString(R.string.actionAdd));
            }
            Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (id == R.id.action_image) {
            if(mPictureList.size() >= 10){
                Toast.makeText(this, "이미지는 10개까지 가능합니다.", Toast.LENGTH_SHORT).show();
            }else {
                showImageActionDialog();
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.REQUEST_PERMISSION_READ) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectGallery();
            } else {
                Toast.makeText(this, "읽기 권한이 거부되었습니다.",Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == PermissionUtil.REQUEST_PERMISSION_CAMERA){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, "카메라 접근 권한이 거부되었습니다.",Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_GALLERY){
            if(resultCode == RESULT_OK && data != null){
                String imgPath = getPathFromURI(data.getData());

                if(imgPath != null)
                    addAndShowImg(imgPath, false);
            }else{
                Log.w(TAG, "request gallert result is not ok");
            }
        }else if(requestCode == REQUEST_CAMERA){
            if(resultCode == RESULT_OK){
                String imgPath = mPictureList.get(mPictureList.size()-1).getUri();//사진찍고 왔으므로 마지막 index가 찍은 결과!

                galleryAddPic(imgPath);//갤러리에 추가한다.
                addAndShowImg(imgPath, true);
            }else{
                Log.w(TAG, "request CAMERA CAPTURE result is not ok");
            }
        }else if(requestCode == DetailAdapter.REQUEST_FOR_PICTURE_ACTIVITY && resultCode == RESULT_OK && data != null){
            int position = data.getIntExtra(getString(R.string.position), 0);
            mViewPager.setCurrentItem(position);
        }
    }

    private void selectGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_GALLERY);
        }
    }

    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "create Image File Exception. message : " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.songtaeheon.memoapp.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }


    private String getPathFromURI(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        String path = null;
        if(cursor == null){
            Log.e(TAG, "cursor in getPathFromURI is null");
        }else if(cursor.getCount() < 1){
            Log.w(TAG, "the search was unsuccessful");
            cursor.close();
        }else{
            cursor.moveToFirst();
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(idx);
            cursor.close();
        }
        return path;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // 빈 파일 생성
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir      /* directory */
        );

        mPictureList.add(new Picture(image.getAbsolutePath()));
        return image;
    }

    //갤러리에 찍은 사진 저장
    private void galleryAddPic(String uriStr) {
        File f = new File(uriStr);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void addAndShowImg(String imgPath, boolean fromCamera){
        if(!fromCamera)//사진 찍으면, 이미 들어가 있음.
            mPictureList.add(new Picture(imgPath));
        if(mPictureList.size() >= 1) mViewPager.setVisibility(View.VISIBLE);
        pagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mPictureList.size()-1);
    }
    private void setMemoFromUI(){
        mMemo.setTitle(mTitleEditText.getText().toString());
        mMemo.setDetail(mDetailEditText.getText().toString());
        if(mPictureList.size() > 0) {
            String thumbnailUri = mPictureList.get(0).getUri();
            mMemo.setThumbnailUri(thumbnailUri);
        }
    }
    private void showUrlInputDialog(){
        final LinearLayout linear = (LinearLayout) View.inflate(ModificationActivity.this, R.layout.dialog_url, null);

        new AlertDialog.Builder(ModificationActivity.this)
                .setView(linear)
                .setPositiveButton(getString(R.string.buttonOk), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText url = linear.findViewById(R.id.url_edittext);
                        String imagePath = url.getText().toString();
                        addAndShowImg(imagePath, false);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.buttonCancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void setUpViewPager(){
        int dpValue = 54;
        float d = getResources().getDisplayMetrics().density;
        int margin = (int) (dpValue * d);
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(margin, 0, margin, 0);
        mViewPager.setPageMargin(margin/2);

        if(mPictureList.size() == 0) mViewPager.setVisibility(View.GONE);

        pagerAdapter = new DetailAdapter(this, mPictureList, true, this) ;
        mViewPager.setAdapter(pagerAdapter) ;
    }

    void showImageActionDialog(){
        final CharSequence[] items = { getString(R.string.pickFromGallery), getString(R.string.pickFromCamera), getString(R.string.pickFromUrl) };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this);
        // 제목셋팅
        alertDialogBuilder.setTitle(getString(R.string.dialogImageTitle));
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switch(id){
                    case PICK_FROM_GALLARY:
                        if(PermissionUtil.checkReadPermission(ModificationActivity.this))
                            selectGallery();
                        break;
                    case PICK_FROM_CAMERA:
                        if(PermissionUtil.checkCameraPermission(ModificationActivity.this))
                            takePicture();
                        break;
                    case PICK_FROM_URL:
                        showUrlInputDialog();
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void addNewMemoInDb(){
        String timeStr = Calendar.getInstance().getTime().toString();
        long memo_id = dbHandler.insertMemoAndGetRowId(mMemo, timeStr);
        dbHandler.insertAllPictures(mPictureList, memo_id);
        mMemo.setId(memo_id);
        dbHandler.close();
    }

    void modifyMemoInDb(){
        String timeStr = Calendar.getInstance().getTime().toString();
        dbHandler.deletePictureList(mDeletedList);
        dbHandler.insertPictureList(mPictureList, mMemo.getId());

        //thumbnail변경
        if(mPictureList.size() >= 1)
            mMemo.setThumbnailUri(mPictureList.get(0).getUri());
        else
            mMemo.setThumbnailUri(null);

        dbHandler.updateMemo(mMemo, timeStr);
        dbHandler.close();
    }
    @Override
    public void deleteOnePicture(Picture pic) {
        mDeletedList.add(pic);
    }
}
