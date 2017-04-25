package com.project.aek.daytoon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkPermission();

    }



    private void checkPermission() {

        Log.d("_test","checkPermission() ");

        if (Build.VERSION.SDK_INT <= 23 ) {


            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을 경우
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 사용자가 임의로 권한을 취소시킨 경우
                    // 권한 재요청
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 119);
                } else {
                    // 권한 요청 (최초 요청)
                    ActivityCompat.requestPermissions(this, new String[]{
                            //스토리지 읽기
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            //카메라
                            Manifest.permission.CAMERA,
                            //스토리지 쓰기
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            //Manifest.permission.RECORD_AUDIO,
                            //Manifest.permission.BLUETOOTH,
                            //Manifest.permission.BLUETOOTH_ADMIN
                            // 전화걸기
                            //Manifest.permission.CALL_PHONE,
                            // 문자 보내기
                            //android.Manifest.permission.SEND_SMS,
                            // 현위치 gps
                            //android.Manifest.permission.ACCESS_FINE_LOCATION,
                            // 센서
                            //Manifest.permission.ACCESS_COARSE_LOCATION,
                            //android.Manifest.permission.ACCESS_NETWORK_STATE
                    }, 119);
                }

            } else {

                start();

            }

        } else {

            start();

        }

    }


    void start() {

        Intent intent = new Intent(getBaseContext(),MainMenuActivity.class);

        startActivity(intent);

        finish();

    }


    // checkPermission 함수의 requestPermissions 메서드의 결과값으로 콜백
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("_test","onRequestPermissionsResult() ");

        for(int k=0; k<grantResults.length; k++) {

            Log.d("_test","onRequestPermissionsResult() k: " + k + " , " + grantResults[k]);

        }
        /*

        // 카메라 퍼미션 사용이 허가된 경우
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getBaseContext(),grantResults[0] + ": 허락을 요구합니다.",Toast.LENGTH_SHORT).show();

            return;
        }

        // 카메라 퍼미션 사용이 허가된 경우
        if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getBaseContext(),grantResults[0] + ": 허락을 요구합니다.",Toast.LENGTH_SHORT).show();

            return;
        }

        // 카메라 퍼미션 사용이 허가된 경우
        if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getBaseContext(),grantResults[0] + ": 허락을 요구합니다.",Toast.LENGTH_SHORT).show();

            return;
        }
        */
        /*
        // 카메라 퍼미션 사용이 허가된 경우
        if (grantResults[3] != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getBaseContext(),grantResults[0] + ": 허락을 요구합니다.",Toast.LENGTH_SHORT).show();

            return;
        }

        // 카메라 퍼미션 사용이 허가된 경우
        if (grantResults[4] != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getBaseContext(),grantResults[0] + ": 허락을 요구합니다.",Toast.LENGTH_SHORT).show();

            return;
        }
        */

        Log.d("_test","onRequestPermissionsResult() ");


        Intent intent = new Intent(getBaseContext(),MainMenuActivity.class);

        startActivity(intent);

        finish();

    }

}
