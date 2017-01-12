package com.project.aek.daytoon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;

/**
 * Created by aek on 2016-12-30.
 */

public class EditPotoGallery extends AppCompatActivity {
    private GridView mGalleryView;
    private String[] mFileList;           //경로안에 FileList를 받을 변수
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpotogallery);
        setCameraActionBar();
        intent = getIntent();
        Button homeBtm = (Button)findViewById(R.id.homeBtm);
        homeBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });
        mGalleryView = (GridView) findViewById(R.id.galleryView);
        File mfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DayToon");
        final String mPotoPath = mfile.getPath();


        mGalleryView.setAdapter(new CustomGalleryAdapter(this, mPotoPath));
        mGalleryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // parent.getAdapter().getItem(position);

                //비트맵으로 넘기고 싶었지만 인텐트로 넘기는건 40byte가 한계라고 한다
                //그래서 경로를 넘겨 비트맵을 직접 생성한다.

                String mFilePath = mPotoPath+File.separator+mFileList[position];

                intent.putExtra("bm",mFilePath);
                setResult(RESULT_OK,intent);
                finish();

            }
        });
    }

    private void setCameraActionBar()
    {
        ActionBar actionBar = getSupportActionBar();      //제공되는 액션바를 가져온다.

        //CustomEnabled를 true로 하고 필요없는건 false
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        //LayoutInflater 는 XML에 정의된 리소스들을 뷰의 형태로 변환한다.
        View mCustomView = LayoutInflater.from(this).inflate(R.layout.back_actionbar,null);       //만들어둔 xml을 뷰로 변경
        actionBar.setCustomView(mCustomView);       //뷰를 적용

        Toolbar parent = (Toolbar)mCustomView.getParent();
        parent.setContentInsetsAbsolute(0,0);       //패딩 제거
    }//setCameraActionBar


    public class CustomGalleryAdapter extends BaseAdapter {
        private String mFilePath;           //지정경로를 받아오기 위한 변수
        private Context mContext;           //액티비티 context
       // private String[] mFileList;           //경로안에 FileList를 받을 변수
        Bitmap mBitmap;                     //지정 경로의 사진을 Bitmap으로 받아오기위한 변수
        private int vWidth;                 //화면 넓이
        private int vHeight;                //화면 높이
        private int imWidth;                //이미지 넓이
        private int imHeight;               //이미지 높이

        /*/////////////////////////////////////
                어답터 생성자
         ////////////////////////////////////*/

        public CustomGalleryAdapter(Context context, String filepath) {
            mContext = context;         //액티비티 컨텍스트
            mFilePath = filepath;         //파일 경로

            File mfile = new File(mFilePath);       //파일 변수를 만들어
            if (!mfile.exists()) {
                if (!mfile.mkdir()) {
                    Log.d("갤러리어답터", "사진파일 디렉터리ㅣ 생성실패");
                }
            }

            mFileList = mfile.list();   //디렉터리내에 file명들을 저장해
            //화면의 크기를 얻어온다.
            Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            vWidth = display.getWidth();
            vHeight = display.getHeight()-50;

            imWidth = vWidth/3 - 30;
            imHeight = vHeight/4 -30;

       } //어답터 생성자


        ///////////////////////////////////////////////////////////////////
        //                getCount    -   getView 갯수를 지정하는 함수
        //////////////////////////////////////////////////////////////////

        @Override
        public int getCount() {
            return mFileList.length;            //파일리스트 반환
        }

        ///////////////////////////////////////////////////////////////////
        //                  getItemId
        //////////////////////////////////////////////////////////////////

        @Override
        public long getItemId(int position) {
            return position;
        }

        ///////////////////////////////////////////////////////////////////
        //                  getItem
        //////////////////////////////////////////////////////////////////
        @Override
        public Object getItem(int position) {
            return position;
        }

        /*/////////////////////////////////////////////////////////////
         경로내의 사진을 보여주는 메서드

        //////////////////////////////////////////////////////////////// */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*
             실제 화면에 그려지는 아이템을 ConvertView 라는 배열로 관리하는데,
              화면에 보여지는 만큼 Convert View를 생성하고 스크롤시 View를 재활용
              새로운 아이템을 표시하라 때마다 Adapter의 getView( )를 호출하게 된다.
              getView 메소드는 각 View를 보여줄 때마다 호출되기 때문에 5개의 View를
              보여줄 때 무조건 5번의 호출이 이루어지게 된다.
              재활용할때는 null이 아닌값이 들어온다. 그러니 convertView가 null인경우는 처음뿐이다

             */

            ImageView imageView;        //갤러리에 들어갈 이미지뷰 생성
            if (convertView == null)         //처음에만 초기화하고 그 이후로는 재활용을 한다.
            {
                imageView = new ImageView(mContext);            //들어온 액티비티 화면에 만든다
                imageView.setLayoutParams(new GridView.LayoutParams(imWidth, imHeight));        //뷰 하나하나의 크기 지정
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);        //가운데 중심으로 나머지는 잘라
                imageView.setPadding(5, 5, 5, 5);
            } else            //처음 불린게 아니라 재활용이면
            {
                imageView = (ImageView) convertView;
            }
            Bitmap bm = BitmapFactory.decodeFile(mFilePath + File.separator + mFileList[position]);
            imageView.setImageBitmap(bm);

            /*
            if(bm != null && !bm.isRecycled())
            {
                bm.recycle();
            }
            */
            Log.d("갤러리뷰","포지션"+position);
            return imageView;
        }
    }
}
