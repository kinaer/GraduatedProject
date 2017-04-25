package com.project.aek.daytoon.networking;

import com.project.aek.daytoon.networking.beans.UploadFile;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadService {

    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(@Part MultipartBody.Part file);

    @GET("list")
    Call<List<UploadFile>> list();

}
