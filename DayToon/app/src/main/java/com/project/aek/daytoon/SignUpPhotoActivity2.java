package com.project.aek.daytoon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Admin on 2017-01-16.
 */

public class SignUpPhotoActivity2 extends Activity {

    private int id_view;
    private int picture_num = 0; //사진 컷 번호

    private LinearLayout container;

    private int seleted_number = 0;

    final int PICK_FROM_ALBUM = 100 ;
    final int CROP_FROM_iMAGE = 200 ;
    final int EditPotoGallery = 300;

    final String CROP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/SmartWheel/" + System.currentTimeMillis() + ".jpg";


    int[] imges = {
            R.id.user_image0,
            R.id.user_image1,
            R.id.user_image2,
            R.id.user_image3,
            R.id.user_image4,
            R.id.user_image5

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupphoto);

        container = (LinearLayout) findViewById(R.id.container);

        Button capture = (Button) findViewById(R.id.capture);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                thumnail(container);
            }
        });


    }


    public void thumnail(View view) {

        view.setDrawingCacheEnabled(true);

        Bitmap scrreenshot = view.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scrreenshot.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] array = stream.toByteArray();

        final Bitmap screentshot = BitmapFactory.decodeByteArray(array, 0, array.length);


        view.setDrawingCacheEnabled(false);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.signupphoto_dialog);
        dialog.setTitle("프리뷰");

        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageBitmap(screentshot);

        final EditText text = (EditText)dialog.findViewById(R.id.text);

        Button ok = (Button) dialog.findViewById(R.id.ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    String word = text.getText().toString();

                    if ( word == null || word.equals("") ) {

                        Toast.makeText(getBaseContext(), "비어있습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {

                        //File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), word+".png");
                        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"DayToon");

                        if ( !f.isDirectory() ) {
                            f.mkdir();
                        }

                        f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"DayToon/" + word+".png");
                        f.createNewFile();

                        String path = f.getAbsolutePath();

                        OutputStream outStream = new FileOutputStream(f);

                        screentshot.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        Toast.makeText(getBaseContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();

                        outStream.close();


                    } catch (IOException e) {

                        Log.d("_test","err:" + e.getMessage());

                    }

                    dialog.dismiss();



                } catch (Exception e) {

                    Log.d("_test","err: " + e.getMessage());

                }


            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }




    //////////////////////


    /*
     * Bitmap을 저장하는 부분
     */
    private void storeCropImage(Bitmap bitmap, String filePath) {
        // SmartWheel 폴더를 생성하여 이미지를 저장하는 방식이다.
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel";
        File directory_SmartWheel = new File(dirPath);

        if (!directory_SmartWheel.exists()) // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
            directory_SmartWheel.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {

            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void call(View v) {


        String tag = v.getTag().toString();

        seleted_number = Integer.parseInt(tag);

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();

    }




    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        /*
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
        */

        Intent intent = new Intent(getBaseContext(), com.project.aek.daytoon.EditPotoGallery.class);
        //intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, EditPotoGallery);

    }




    //앨범에서 가져오기

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Log.d("_test","onActivityResult() return");
            return;
        }

        final Bundle extras = data.getExtras();

        switch (requestCode) {

            // 앨범을 선택했을때
            case PICK_FROM_ALBUM: {
                Uri mImageCaptureUri = data.getData();
                //Log.d("_test", mImageCaptureUri.getPath().toString());
                crop(mImageCaptureUri);
            }

            // 데이툰 갤러리 호출
            case EditPotoGallery: {

                if (extras != null) {

                    ImageView iv_UserPhoto = (ImageView)findViewById(imges[seleted_number]);

                    String path = extras.getString("bm");



                    Uri mImageCaptureUri = Uri.parse(path);

                    String filepath= mImageCaptureUri.getPath();


                    Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            null,
                            "_data='" + filepath + "'",
                            null,
                            null);

                    c.moveToNext();

                    int id = c.getInt( c.getColumnIndex("_id"));

                    Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                    //Log.d("_test", mImageCaptureUri.getPath().toString());
                    crop(uri);

                }

            }

            // 크롭할때
            case CROP_FROM_iMAGE: {

                if (extras != null) {

                    ImageView iv_UserPhoto = (ImageView)findViewById(imges[seleted_number]);

                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP

                    //iv_UserPhoto.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), photo);

                    iv_UserPhoto.setBackground(drawable);

                    // CROP된 이미지를 저장하기 위한 FILE 경로
                    // storeCropImage(photo, CROP_PATH);

                    break;

                }


            }



        }


    }


    void crop(Uri mImageCaptureUri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mImageCaptureUri, "image/*");

        // CROP할 이미지를 200*200 크기로 저장
        intent.putExtra("outputX", 500); // CROP한 이미지의 x축 크기
        intent.putExtra("outputY", 800); // CROP한 이미지의 y축 크기
        intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
        intent.putExtra("aspectY", 1.3); // CROP 박스의 Y축 비율
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동


    }

}
