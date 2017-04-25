package com.project.aek.daytoon;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.project.aek.daytoon.networking.FileUploadService;
import com.project.aek.daytoon.networking.FileUtils;
import com.project.aek.daytoon.networking.ServiceGenerator;
import com.project.aek.daytoon.networking.beans.UploadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareGalleryActivity extends AppCompatActivity {

    private final static int REQ_CODE_SELECT_IMAGE = 0;

    private FileUploadService service;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ShareGalleryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_gallery);

        service = ServiceGenerator.createService(FileUploadService.class);

        recyclerView = (RecyclerView)findViewById(R.id.list);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ShareGalleryAdapter(this);
        recyclerView.setAdapter(mAdapter);

        loadData();
    }

    private void loadData() {

        mAdapter.removeAll();

        Call<List<UploadFile>> call = service.list();
        call.enqueue(new Callback<List<UploadFile>>() {
            @Override
            public void onResponse(Call<List<UploadFile>> call, Response<List<UploadFile>> response) {

                if(response.isSuccessful()) {
                    List<UploadFile> list = response.body();
                    for(UploadFile file : list) {
                        mAdapter.addItem(file);
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<UploadFile>> call, Throwable t) {
                Log.v("TEST", t.getMessage());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_upload :
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE_SELECT_IMAGE) {
            if(resultCode == Activity.RESULT_OK) {
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setMessage("선택한 파일을 업로드하시겠습니까?");
                ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uploadFile(data.getData());
                    }
                });
                ab.setNegativeButton("취소", null);
                ab.show();
            }
        }
    }

    private void uploadFile(Uri fileUri) {

        File file = FileUtils.getFile(this, fileUri);

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)),
                file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<ResponseBody> call = service.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
                Toast.makeText(ShareGalleryActivity.this, "업로드하였습니다", Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("TEST", t.getMessage());
            }
        });
    }
}
