#include "opencv2/opencv.hpp"
#include "cartoon.h"
using namespace std;
using namespace cv;

void cartoonifyImage(Mat srcColor, Mat dst, int effect, int blurSize)
{
	Mat gray;		//grat의 Mat을 선언
	cvtColor(srcColor, gray, CV_BGR2GRAY);			//넘겨져온 Mat형 이미지를 그레이스케일로 컨버트해서 grat Mat에 저장
	const int MEDIAN_BLUR_FILTER_SIZE = blurSize;
	medianBlur(srcColor, srcColor, MEDIAN_BLUR_FILTER_SIZE);		//medianBlur필터는 smooth하게 해준다.
	medianBlur(gray, gray, MEDIAN_BLUR_FILTER_SIZE);

	Mat edges;		//에지 검출 MAt
	Mat mask;		//마스크 검출 Mat

	Size size = srcColor.size();	//원본 이미지의 크기

	//에지 필터를 이용한 악마 모드 생성
	//영상전체에 있는 많은 에지를 찾은 후 작은 중간 값 필터를 이용해 에지를 병합한다.
	//잡음이 약간 있는 그레이 스케일 영상에 수행하자. 원 영상을 그레이 스케일로 변환한 후 7x7 중간값 필터를 적용하는 이전코드를 다시 사용한다.
	//라플라시안과 이진임계값을 적용하는 대신에 3x3 샤르 기울기 필터를 x와 y^2에 적용하면 무섭게 보이게 할수 있다. 
	//그 다음 매우 낮은 값을 이용해 잘라내는 이진 임계값을 젖용한 후 끝으로 3x3 중간 값 블러로 악마 마스크를 만든다.
	if (effect == EVIL) {
		Mat edges2;
		Scharr(gray, edges, CV_8U, 1, 0);
		Scharr(gray, edges2, CV_8U, 0, 1);
		edges += edges2;		//x와 y에지를 함께 조합한다.
		const int EVIL_EDGE_THRESHOLD = 12;
		threshold(edges, mask, EVIL_EDGE_THRESHOLD, 255, THRESH_BINARY_INV);
		medianBlur(mask, mask, 3);
	}
	
	else {
		const int LAPLACIAN_FILTER_SIZE = 5;
		Laplacian(gray, edges, CV_8U, LAPLACIAN_FILTER_SIZE);		//라플라시안 필터는 다양한 밝기가 있는 에지를 생성한다.
		//스케치와 훨씬더 비슷한 에지를 만들기 위해 흰색이나 검은색 중 하나로 에지를 결정하는 이진 임계값을 적용한다.
		
		const int EDGES_THRESHOLD = 81;
		threshold(edges, mask, EDGES_THRESHOLD, 255, THRESH_BINARY_INV);
		//threshold(srcColor, mask, EDGES_THRESHOLD, 255, THRESH_BINARY_INV);		//영상 이진화한다. threshold보다 큰값은 0 작은값은 255 로 만든다.
	}
	if (effect == SKETCH) {
		//srcColor.copyTo(dst, mask);
		//mask.copyTo(dst);
		cvtColor(mask, dst, CV_GRAY2BGR);
		return;
	}
		// 여기까지가 스케치 필터
		//카메라 프레임의 스케치를 얻기 위해서 에지 추출 필터를 사용하는 반면
		//컬러 페인팅을 얻으려면 에지를 온전하게 보존하되 평탄한 영역을 더 부드럽게 하는 에지 보존 필터를 사용한다.

		//컬러페인팅 : 강력한 바이래터렐 필터는 날카로운 에지를 보존하면서 평탄한 영역을 부드럽게 하므로 매우 느리다
		//그래서 화소 수를 줄인다

	if (effect == SKETCH_C) {
		Size smallSize;
		smallSize.width = size.width / 2;
		smallSize.height = size.height / 2;
		Mat smallImg = Mat(smallSize, CV_8UC3);
		resize(srcColor, smallImg, smallSize, 0, 0, INTER_LINEAR);

		//바이래터럴 필터를 제어하는 네가지 파라미터는 컬러강도, 위치강도, 크기, 반복횟수다. bilateralFilter()는 입력을 덮어 씌우지 않으므로
		//임지 Mat이 필요하다.
		Mat tmp = Mat(smallSize, CV_8UC3);		//임시 Mat을 만든다	
		int repetitions = 10;					//강력한 만화 효과를 얻는 반복횟수
		for (int i = 0; i < repetitions; i++) {
			int ksize = 6;			//필터크기, 속도에 큰 영향을 미친다.
			double sigmaColor = 6.0;	//필터의컬러강도
			double sigmaSpace = 7.0;	//공간강도, 속도에영향을 준다.
			
			bilateralFilter(smallImg, tmp, ksize, sigmaColor, sigmaSpace);		//
			bilateralFilter(tmp, smallImg, ksize, sigmaColor, sigmaSpace);
			}
		//원래 이미지로 복구
		Mat bigImg;
		resize(tmp, bigImg, size, 0, 0, INTER_LINEAR);		//여기까지 바이래터렐 필터로 에지를보존하면서 이미지를 부드럽게하는 스무딩이다.
		dst.setTo(0);				//스케치인 에지마스크를 덮어씌우기 위해 검은 배경을 만든다.
		bigImg.copyTo(dst, mask);				//마스크는 어느 영역이 복사되야 될지 정하는거다. 0이 아닌 곳을 복사해야된다.
		//여기 마스크는 스케치 마스크로 에지부분이 0으로 이진화 되어있다. 그러므로 에지가 아닌 부분만 복사되고 에지는 검은색이된다.
		//스케치 마스크 내 에지가 아닌 곳에 페인팅 화소를 복사
//		for (int i = 0; i < dst.rows; i++) {
	//		for (int j = 0; j < dst.cols*3; j++) {
	//	//		dst.at<unsigned char>(i, j) = 255 - dst.at<unsigned char>(i, j);
				
		//	}
	//	}
		return;
		}

	//피부검출을 이용한 에일리언 모드 생성 
	/*
		피부검출 알고리즘
		RGB(빨강-초록-파랑) 혹은 HSV(색상-채도-명도)를 이용한 단순한 컬러 임계값이나 컬러 히스토그램 계산과 재투영으로부터 CLELab컬러 공간내의
		카메라 보정과 많은 표본 얼굴 기반 오프라인 훈련등이 필요한 혼합 모델인 복잡한 기계학습 알고리즘 까지 있다.
		단순한 HSV 피부 검출기는 색상이 정말 빨갛고 채도가 매우 높되과도하게 높지 않으며, 명도가 너무 어둡거나 너무 밝지만 않다면 피부로 간주해
		화소를 처리할 수 있다. 하르나 LBP계층분류기로 얼굴 검출을 수행하는데 있으며 검출한 얼굴의 가운데에 있는 화소의 컬러 범위를 살펴본다.
	*/
	/*/
	if (effect == ALIEN) {
		//얼굴이 들어가는 부분을 검은색으로 그린다.
		Mat faceOutline = Mat::zeros(size, CV_8UC3);
		Scalar color = CV_RGB(255, 255, 0);	//노란색
		int thickness = 4;
		//화면 높이의 70%를 얼굴 높이로 사용한다.
		int sw = size.width;
		int sh = size.height;
		int faceH = sh / 2 * 0.7;	//faceH는 타원의 반지름
		int faceW = faceH * 0.72;	//어떠한 화면 크기에도 모양이 동일하게 너비를 조정한다.
		//얼굴 외곽선을 그린다.
		ellipse(faceOutline, Point(sw / 2, sh / 2), Size(faceW, faceH), 0, 0, 360, color, thickness, CV_AA);
	}
	*/
	
	srcColor.copyTo(dst,mask);


	}


	
	

	
