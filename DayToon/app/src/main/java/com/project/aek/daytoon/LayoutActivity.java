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
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
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

public class LayoutActivity extends Activity {

    private int id_view;
    private int picture_num = 0; //사진 컷 번호

    private LinearLayout container;

    private int selected_number = 0;

    final int PICK_FROM_ALBUM = 100 ;
    final int CROP_FROM_iMAGE = 200 ;
    final int EditPotoGallery = 300;

    final String CROP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/SmartWheel/" + System.currentTimeMillis() + ".jpg";


    int[] user_image = {
            R.id.user_image0,
            R.id.user_image1,
            R.id.user_image2,
            R.id.user_image3,
    };

    //사진이 들어있는지 체크
    int[] image_check={0,0,0,0};

    int[] balloon_check={0,0,0,0};
    int[] balloon_tr = {
            R.id.balloon0_tr,
            R.id.balloon1_tr,
            R.id.balloon2_tr,
            R.id.balloon3_tr
    };
    int[] balloon_tl = {
            R.id.balloon0_tl,
            R.id.balloon1_tl,
            R.id.balloon2_tl,
            R.id.balloon3_tl

    };
    int[] balloon_dr = {
            R.id.balloon0_dr,
            R.id.balloon1_dr,
            R.id.balloon2_dr,
            R.id.balloon3_dr

    };int[] balloon_dl = {
            R.id.balloon0_dl,
            R.id.balloon1_dl,
            R.id.balloon2_dl,
            R.id.balloon3_dl

    };



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cut4_layout);

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
                        MediaScanner scanner = MediaScanner.newInstance(getApplicationContext());
                        scanner.mediaScanning(path);

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




    public void location() {




        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.balloon_location);
        dialog.setTitle("말풍선위치");

        Button tright = (Button) dialog.findViewById(R.id.tright);
        Button tleft = (Button) dialog.findViewById(R.id.tleft);
        Button dleft = (Button) dialog.findViewById(R.id.dleft);
        Button dright = (Button) dialog.findViewById(R.id.dright);

        tright.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                setBalloon_tr();
                dialog.dismiss();
            }
        });
        tleft.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                setBalloon_tl();
                dialog.dismiss();
            }
        });
        dleft.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                setBalloon_dl();
                dialog.dismiss();
            }
        });
        dright.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                setBalloon_dr();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void setBalloon_tr(){
        ImageView iv_UserPhoto = (ImageView)findViewById(user_image[selected_number]);
        TextView iv_balloon_tr = (TextView)findViewById(balloon_tr[selected_number]);
        TextView iv_balloon_tl = (TextView)findViewById(balloon_tl[selected_number]);
        TextView iv_balloon_dr = (TextView)findViewById(balloon_dr[selected_number]);
        TextView iv_balloon_dl = (TextView)findViewById(balloon_dl[selected_number]);


        iv_balloon_tr.setVisibility(View.VISIBLE);
        iv_balloon_tl.setVisibility(View.INVISIBLE);
        iv_balloon_dr.setVisibility(View.INVISIBLE);
        iv_balloon_dl.setVisibility(View.INVISIBLE);


        balloon_check[selected_number]=1;

    }
    public void setBalloon_tl(){
        ImageView iv_UserPhoto = (ImageView)findViewById(user_image[selected_number]);
        TextView iv_balloon_tr = (TextView)findViewById(balloon_tr[selected_number]);
        TextView iv_balloon_tl = (TextView)findViewById(balloon_tl[selected_number]);
        TextView iv_balloon_dr = (TextView)findViewById(balloon_dr[selected_number]);
        TextView iv_balloon_dl = (TextView)findViewById(balloon_dl[selected_number]);


        iv_balloon_tr.setVisibility(View.INVISIBLE);
        iv_balloon_tl.setVisibility(View.VISIBLE);
        iv_balloon_dr.setVisibility(View.INVISIBLE);
        iv_balloon_dl.setVisibility(View.INVISIBLE);


        balloon_check[selected_number]=1;

    }
    public void setBalloon_dr(){
        ImageView iv_UserPhoto = (ImageView)findViewById(user_image[selected_number]);
        TextView iv_balloon_tr = (TextView)findViewById(balloon_tr[selected_number]);
        TextView iv_balloon_tl = (TextView)findViewById(balloon_tl[selected_number]);
        TextView iv_balloon_dr = (TextView)findViewById(balloon_dr[selected_number]);
        TextView iv_balloon_dl = (TextView)findViewById(balloon_dl[selected_number]);


        iv_balloon_tr.setVisibility(View.INVISIBLE);
        iv_balloon_tl.setVisibility(View.INVISIBLE);
        iv_balloon_dr.setVisibility(View.VISIBLE);
        iv_balloon_dl.setVisibility(View.INVISIBLE);


        balloon_check[selected_number]=1;

    }
    public void setBalloon_dl(){
        ImageView iv_UserPhoto = (ImageView)findViewById(user_image[selected_number]);
        TextView iv_balloon_tr = (TextView)findViewById(balloon_tr[selected_number]);
        TextView iv_balloon_tl = (TextView)findViewById(balloon_tl[selected_number]);
        TextView iv_balloon_dr = (TextView)findViewById(balloon_dr[selected_number]);
        TextView iv_balloon_dl = (TextView)findViewById(balloon_dl[selected_number]);


        iv_balloon_tr.setVisibility(View.INVISIBLE);
        iv_balloon_tl.setVisibility(View.INVISIBLE);
        iv_balloon_dr.setVisibility(View.INVISIBLE);
        iv_balloon_dl.setVisibility(View.VISIBLE);


        balloon_check[selected_number]=1;

    }


    public void balloon(View v) {

        String tag = v.getTag().toString();
        selected_number = Integer.parseInt(tag);

        v.setDrawingCacheEnabled(true);



        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.balloon_text);
        dialog.setTitle("말풍선");


        final EditText text = (EditText)dialog.findViewById(R.id.text1);

        Button ok = (Button) dialog.findViewById(R.id.ok1);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    String word = text.getText().toString();
                    if ( word == null || word.equals("") ) {

                        Toast.makeText(getBaseContext(), "비어있습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if( word.length()>20){

                        Toast.makeText(getBaseContext(), "20자 이내로 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {

                        //File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), word+".png");


                        Toast.makeText(getBaseContext(), "입력되었습니다.", Toast.LENGTH_SHORT).show();

                        TextView iv_balloon_tr = (TextView)findViewById(balloon_tr[selected_number]);
                        TextView iv_balloon_tl = (TextView)findViewById(balloon_tl[selected_number]);
                        TextView iv_balloon_dr = (TextView)findViewById(balloon_dr[selected_number]);
                        TextView iv_balloon_dl = (TextView)findViewById(balloon_dl[selected_number]);

                        iv_balloon_tr.setText(word);
                        iv_balloon_tl.setText(word);
                        iv_balloon_dr.setText(word);
                        iv_balloon_dl.setText(word);


                    } catch (Exception e) {

                        Log.d("_test","err:" + e.getMessage());

                    }

                    dialog.dismiss();



                } catch (Exception e) {

                    Log.d("_test","err: " + e.getMessage());

                }


            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.cancel1);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();



    }






    public void call(View v) {


        String tag = v.getTag().toString();

        selected_number = Integer.parseInt(tag);

        //사진이 없을경우 사진부터 입력하게끔 유도
        if(image_check[selected_number]==0&&balloon_check[selected_number]==0){


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

        //사진이 들어있을 경우 말풍선 메뉴도 같이 출력
        if(image_check[selected_number]==1&&balloon_check[selected_number]==0) {

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

            DialogInterface.OnClickListener balloonListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    location();







                }
            };


            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setNeutralButton("앨범선택", albumListener)
                    .setPositiveButton("취소", cancelListener)
                    .setNegativeButton("말풍선", balloonListener)
                    .show();
        }


        //말풍선까지 들어가 있을 경우
        if(image_check[selected_number]==1&&balloon_check[selected_number]==1){


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


            DialogInterface.OnClickListener ballondismissListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    balloondismiss();
                }
            };





            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setNeutralButton("앨범선택", albumListener)
                    .setPositiveButton("취소", cancelListener)
                    .setNegativeButton("말풍선제거",ballondismissListener)
                    .show();

        }

    }







    //말풍선 제거
    public void balloondismiss(){
        TextView iv_balloon_tr = (TextView)findViewById(balloon_tr[selected_number]);
        TextView iv_balloon_tl = (TextView)findViewById(balloon_tl[selected_number]);
        TextView iv_balloon_dr = (TextView)findViewById(balloon_dr[selected_number]);
        TextView iv_balloon_dl = (TextView)findViewById(balloon_dl[selected_number]);

        iv_balloon_tr.setVisibility(View.INVISIBLE);
        iv_balloon_tl.setVisibility(View.INVISIBLE);
        iv_balloon_dr.setVisibility(View.INVISIBLE);
        iv_balloon_dl.setVisibility(View.INVISIBLE);

        balloon_check[selected_number]=0;


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

                    ImageView iv_UserPhoto = (ImageView)findViewById(user_image[selected_number]);

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

                    ImageView iv_UserPhoto = (ImageView)findViewById(user_image[selected_number]);

                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP

                    //iv_UserPhoto.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), photo);

                    iv_UserPhoto.setBackground(drawable);
                    image_check[selected_number]=1;

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
        intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기
        intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
        intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
        intent.putExtra("aspectY", 1.3); // CROP 박스의 Y축 비율
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동


    }

}
