#include "cartoon.h"


void cartoonifyImage(Mat srcColor, Mat dst) {
	Mat gray;
	//�̹����÷� ������
	cvtColor(srcColor, gray, CV_BGR2GRAY);
	const int MEDIAN_BLUR_FiLTER_SIZE = 7;
	medianBlur(gray, gray, MEDIAN_BLUR_FiLTER_SIZE);

	//���ö�þ� ���ʹ� �پ��� ��Ⱑ �ִ� ������ ����
	Mat edges;
	const int LAPLACIAN_FILTER_SIZE = 5;
	Laplacian(gray, edges, CV_8U, LAPLACIAN_FILTER_SIZE);

	Mat mask;
	const int EDGES_THRESHOLD = 80;


}