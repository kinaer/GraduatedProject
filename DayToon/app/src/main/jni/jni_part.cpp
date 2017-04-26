//
// Created by aek on 2016-11-13.
//
#include "com_project_aek_cvcamera_MangaEffect.h"

#include <opencv2/opencv.hpp>
#include "cartoon.h"
using namespace std;
using namespace cv;

/*
JNIEXPORT void JNICALL Java_com_project_aek_cvcamera_MangaEffect_CartoonifyImage
  (JNIEnv *, jobject, jlong mSource, jlong mDst, jint effectType)
  {
      Mat& source = *(Mat*)mSource;
      Mat& dst = *(Mat*)mDst;

      Mat gray;		//grat의 Mat을 선언
      	cvtColor(source, gray, CV_BGR2GRAY);			//넘겨져온 Mat형 이미지를 그레이스케일로 컨버트해서 grat Mat에 저장
      	const int MEDIAN_BLUR_FILTER_SIZE = 7;
      	medianBlur(gray, gray, MEDIAN_BLUR_FILTER_SIZE);		//medianBlur필터는 smooth하게 해준다.
      	Mat edges;		//에지 검출 MAt
      	Mat mask;		//마스크 검출 Mat
      	Size size = source.size();	//원본 이미지의 크기
      	if (sketchMode) {
            const int LAPLACIAN_FILTER_SIZE = 5;
          Laplacian(gray, edges, CV_8U, LAPLACIAN_FILTER_SIZE);        //라플라시안 필터는 다양한 밝기가 있는 에지를 생성한다.
//스케치와 훨씬더 비슷한 에지를 만들기 위해 흰색이나 검은색 중 하나로 에지를 결정하는 이진 임계값을 적용한다.

        const int EDGES_THRESHOLD = 80;
        threshold(edges, mask, EDGES_THRESHOLD,255, THRESH_BINARY_INV);
        cvtColor(mask, dst, CV_GRAY2BGR);
        }
  }
*/
JNIEXPORT void JNICALL Java_com_project_aek_cvcamera_MangaEffect_setCaptureFrame
(JNIEnv* env, jobject object, jint width, jint height, jbyteArray data, jintArray frame, jboolean sketchMode)
{
    jbyte* _data = env->GetByteArrayElements(data,0);
    jint* _frame = env->GetIntArrayElements(frame,0);

    //input fame data 카메라에서 찍은 오리지널 프레임
    Mat mdata(height + height/2, width,CV_8UC1, (unsigned char *)_data);
    //Mat mgray(height,width,CV_8UC1, (unsigned char *)_data );

    //out 출력할 프레임
    Mat mframe4(height,width,CV_8UC4,(unsigned char*)_frame);
    Mat mframe3(height,width,CV_8UC3); //새 이미지 버퍼
    cvtColor(mdata, mframe3, CV_YUV420sp2BGR);

    Mat dst(mframe3.size(),CV_8UC3);
    dst=mframe3;
/*
    Mat gray;		//grat의 Mat을 선언
    cvtColor(mframe3, gray, CV_BGR2GRAY);			//넘겨져온 Mat형 이미지를 그레이스케일로 컨버트해서 grat Mat에 저장
    const int MEDIAN_BLUR_FILTER_SIZE = 7;
    medianBlur(gray, gray, MEDIAN_BLUR_FILTER_SIZE);		//medianBlur필터는 smooth하게 해준다.
    Mat edges;		//에지 검출 MAt
    Mat mask;		//마스크 검출 Mat
    Size size = mframe3.size();	//원본 이미지의 크기
    if (sketchMode) {
      const int LAPLACIAN_FILTER_SIZE = 5;
        Laplacian(gray, edges, CV_8U, LAPLACIAN_FILTER_SIZE);        //라플라시안 필터는 다양한 밝기가 있는 에지를 생성한다.
    //스케치와 훨씬더 비슷한 에지를 만들기 위해 흰색이나 검은색 중 하나로 에지를 결정하는 이진 임계값을 적용한다.

        const int EDGES_THRESHOLD = 80;
        threshold(edges, mask, EDGES_THRESHOLD,255, THRESH_BINARY_INV);
        cvtColor(mask, dst, CV_GRAY2BGR);
    }
    */
   cvtColor(dst, mframe4,  CV_BGR2BGRA);
 // cvtColor(mdata, mframe4, CV_YUV420p2BGRA, 4);
//__android_log_print(ANDROID_LOG_VERBOSE,"Kishore","Kishore:StillWorking");
    env->ReleaseIntArrayElements(frame, _frame, 0);
    env->ReleaseByteArrayElements(data, _data, 0);
}


//DECLARE_TIMING(CartoonifyImage);
JNIEXPORT void JNICALL Java_com_project_aek_cvcamera_MangaEffect_CartoonifyImage(JNIEnv* env, jobject object, jlong mSource, jlong mDst, jint effectType, jint blurSize)
  {
   // jlong*
    //START_TIMING(CartoonifyImage);
    Mat& source = *(Mat*)mSource;
    Mat& dst = *(Mat*)mDst;
    Mat displayed(dst.size(),CV_8UC3);
    cvtColor(source,source, COLOR_BGRA2BGR);
    cartoonifyImage(source,displayed, effectType,blurSize);
    displayed.copyTo(dst);
    //cvtColor(source,)
     //STOP_TIMING(CartoonifyImage);
        // Print the timing info.
     // SHOW_TIMING(CartoonifyImage, "CartoonifyImage");
  }

JNIEXPORT void JNICALL Java_com_project_aek_cvcamera_MangaEffect_Cartoonify(JNIEnv* env, jobject jobject, jlong mSource, jlong mDst, jint effectType)
{
    Mat& source = *(Mat*)mSource;
    Mat& dst = *(Mat*)mDst;
    Mat displayed(dst.size(),CV_8UC3);
    cartoonify(source,displayed,effectType);
    displayed.copyTo(dst);
}