package com.project.aek.daytoon;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.project.aek.daytoon.widget.TextBalloon;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by aek on 2017-04-19.
 */

public class ConversionActivity extends AppCompatActivity {

    MangaEffect mangaEffect;
    File tmpFile;
    Thread mTread = null;
    String tmpFilePath;
    ImageView testimage;
    Bitmap tmpFileBm;
    Bitmap effectedBm;
    Mat srcMat;
    Mat dstMat;
    MenuDrawer mMenuDrawer;
    BitmapControl bmpCnt;

    SeekBar blackBrSeekbar;
    SeekBar colorSrSeekbar;
    SeekBar colorStSeekbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        setActionBar();
        init();
        createImage();

    }//onCreate

    void init(){
        mMenuDrawer = MenuDrawer.attach(this, Position.RIGHT);
        mMenuDrawer.setContentView(R.layout.activity_conversion);
        mMenuDrawer.setMenuView(R.layout.effect_setting_menu);

        testimage = (ImageView)findViewById(R.id.testImage);
        mangaEffect = new MangaEffect();
        bmpCnt = new BitmapControl();
        Intent intent = getIntent();
        int i=(int)intent.getExtras().get("effectType");
        mangaEffect.setEffect(i);       //필터설정

        blackBrSeekbar = (SeekBar)findViewById(R.id.blackBrSeekbar);
        blackBrSeekbar.setProgress(mangaEffect.getBlackBr());
        blackBrSeekbar.setOnSeekBarChangeListener(blackSeekListener);

        colorSrSeekbar= (SeekBar)findViewById(R.id.colorSr);
        colorSrSeekbar.setProgress(mangaEffect.getColorSr());
        colorSrSeekbar.setOnSeekBarChangeListener(colorSeekListener);

        colorStSeekbar = (SeekBar)findViewById(R.id.colorSaturation);
        colorStSeekbar.setProgress(mangaEffect.getSaturation());
        colorStSeekbar.setOnSeekBarChangeListener(colorSeekListener);

        tmpFilePath = intent.getStringExtra("tempFile");    //임시저장 파일 경로

        tmpFileBm = BitmapFactory.decodeFile(tmpFilePath);  //임시 파일 비트맵
        effectedBm = Bitmap.createBitmap(tmpFileBm.getWidth(), tmpFileBm.getHeight(),Bitmap.Config.ARGB_8888);
        //효과 적용 비트맵
        Size imgSize = new Size(tmpFileBm.getWidth(), tmpFileBm.getHeight());   //이미지 크기 받아와
        srcMat = new Mat(imgSize,CV_8UC3);              //소스Mat 초기화
        dstMat = new Mat(imgSize,CV_8UC3);              //목적MAt 초기화
        Utils.bitmapToMat(tmpFileBm, srcMat);           //비트맵을 Mat으로 바꾼다.


    }//init

    private SeekBar.OnSeekBarChangeListener blackSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mangaEffect.setBlackBr(blackBrSeekbar.getProgress());
            createImage();
        }
    };

    private SeekBar.OnSeekBarChangeListener colorSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mangaEffect.setColorSr(colorSrSeekbar.getProgress());
            mangaEffect.setSaturation(colorStSeekbar.getProgress());
            createImage();
        }
    };
    private DialogInterface.OnDismissListener dismissProgress = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            mTread = null;
            testimage.setImageBitmap(effectedBm);
        }
    };  //프로그레스다이얼로그 지워질때

    void createImage(){
        if(mTread == null){
        final ProgressDialog dialog = new ProgressDialog(ConversionActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setOnDismissListener(dismissProgress);
        dialog.setMessage("잠시만 기다려주세요...");
        dialog.show();

            mTread = new Thread(new Runnable(){
                public void run(){
                    mangaEffect.cartoonFilter(srcMat,dstMat);
                    Utils.matToBitmap(dstMat,effectedBm);
                   dialog.dismiss();
                }
            });
            mTread.start();
        }

    }//createImage

    private void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();      //제공되는 액션바를 가져온다.

        //CustomEnabled를 true로 하고 필요없는건 false
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        //LayoutInflater 는 XML에 정의된 리소스들을 뷰의 형태로 변환한다.
        View mCustomView = LayoutInflater.from(this).inflate(R.layout.conversion_actionbar,null);       //만들어둔 xml을 뷰로 변경
        actionBar.setCustomView(mCustomView);       //뷰를 적용

        Toolbar parent = (Toolbar)mCustomView.getParent();
        parent.setContentInsetsAbsolute(0,0);       //패딩 제거
    }//setCameraActionBar
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.homeBtm:
                finish();
                break;
            case R.id.settingBtn:
                mMenuDrawer.toggleMenu();
                break;
            case R.id.saveBtm:
                String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File mfile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"DayToon");
                if(!mfile.exists()){
                    if(!mfile.mkdir()){
                        Log.d("메인", "사진파일 디렉터리ㅣ 생성실패");
                    }
                }

                String filename=File.separator+"IMG_"+name+".jpg";
                String path =(mfile.getPath()+filename);
                try {
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
                    effectedBm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    Log.d("메인", "스캐치 사진 찍기");
                    out.flush();
                    out.close();
                    MediaScanner scanner = MediaScanner.newInstance(this);
                    scanner.mediaScanning(path);
                } catch (IOException e) {
                    Log.e("메인", "사진 저장 실패");
                    e.printStackTrace();
                }
                Intent intent = new Intent(this, MainMenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //새로운 액티비티 스택을 지우면서 새로운 액티비티를 생성하려면 인텐트에 플래그에 입력한다.
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                break;
        }
    }
}
