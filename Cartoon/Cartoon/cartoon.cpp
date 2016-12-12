#include "cartoon.h"


void cartoonifyImage(Mat srcColor, Mat dst) {
	Mat gray;
	//이미지컬러 컨버전
	cvtColor(srcColor, gray, CV_BGR2GRAY);
	const int MEDIAN_BLUR_FiLTER_SIZE = 7;
	medianBlur(gray, gray, MEDIAN_BLUR_FiLTER_SIZE);

	//라플라시안 필터는 다양한 밝기가 있는 에지를 생성
	Mat edges;
	const int LAPLACIAN_FILTER_SIZE = 5;
	Laplacian(gray, edges, CV_8U, LAPLACIAN_FILTER_SIZE);

	Mat mask;
	const int EDGES_THRESHOLD = 80;


}