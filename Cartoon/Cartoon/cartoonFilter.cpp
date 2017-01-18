#include "opencv2/opencv.hpp"
#include "cartoon.h"
using namespace std;
using namespace cv;

void cartoonifyImage(Mat srcColor, Mat dst, int effect, int blurSize)
{
	Mat gray;		//grat�� Mat�� ����
	cvtColor(srcColor, gray, CV_BGR2GRAY);			//�Ѱ����� Mat�� �̹����� �׷��̽����Ϸ� ����Ʈ�ؼ� grat Mat�� ����
	const int MEDIAN_BLUR_FILTER_SIZE = blurSize;
	medianBlur(srcColor, srcColor, MEDIAN_BLUR_FILTER_SIZE);		//medianBlur���ʹ� smooth�ϰ� ���ش�.
	medianBlur(gray, gray, MEDIAN_BLUR_FILTER_SIZE);

	Mat edges;		//���� ���� MAt
	Mat mask;		//����ũ ���� Mat

	Size size = srcColor.size();	//���� �̹����� ũ��

	//���� ���͸� �̿��� �Ǹ� ��� ����
	//������ü�� �ִ� ���� ������ ã�� �� ���� �߰� �� ���͸� �̿��� ������ �����Ѵ�.
	//������ �ణ �ִ� �׷��� ������ ���� ��������. �� ������ �׷��� �����Ϸ� ��ȯ�� �� 7x7 �߰��� ���͸� �����ϴ� �����ڵ带 �ٽ� ����Ѵ�.
	//���ö�þȰ� �����Ӱ谪�� �����ϴ� ��ſ� 3x3 ���� ���� ���͸� x�� y^2�� �����ϸ� ������ ���̰� �Ҽ� �ִ�. 
	//�� ���� �ſ� ���� ���� �̿��� �߶󳻴� ���� �Ӱ谪�� ������ �� ������ 3x3 �߰� �� ���� �Ǹ� ����ũ�� �����.
	if (effect == EVIL) {
		Mat edges2;
		Scharr(gray, edges, CV_8U, 1, 0);
		Scharr(gray, edges2, CV_8U, 0, 1);
		edges += edges2;		//x�� y������ �Բ� �����Ѵ�.
		const int EVIL_EDGE_THRESHOLD = 12;
		threshold(edges, mask, EVIL_EDGE_THRESHOLD, 255, THRESH_BINARY_INV);
		medianBlur(mask, mask, 3);
	}
	
	else {
		const int LAPLACIAN_FILTER_SIZE = 5;
		Laplacian(gray, edges, CV_8U, LAPLACIAN_FILTER_SIZE);		//���ö�þ� ���ʹ� �پ��� ��Ⱑ �ִ� ������ �����Ѵ�.
		//����ġ�� �ξ��� ����� ������ ����� ���� ����̳� ������ �� �ϳ��� ������ �����ϴ� ���� �Ӱ谪�� �����Ѵ�.
		
		const int EDGES_THRESHOLD = 81;
		threshold(edges, mask, EDGES_THRESHOLD, 255, THRESH_BINARY_INV);
		//threshold(srcColor, mask, EDGES_THRESHOLD, 255, THRESH_BINARY_INV);		//���� ����ȭ�Ѵ�. threshold���� ū���� 0 �������� 255 �� �����.
	}
	if (effect == SKETCH) {
		//srcColor.copyTo(dst, mask);
		//mask.copyTo(dst);
		cvtColor(mask, dst, CV_GRAY2BGR);
		return;
	}
		// ��������� ����ġ ����
		//ī�޶� �������� ����ġ�� ��� ���ؼ� ���� ���� ���͸� ����ϴ� �ݸ�
		//�÷� �������� �������� ������ �����ϰ� �����ϵ� ��ź�� ������ �� �ε巴�� �ϴ� ���� ���� ���͸� ����Ѵ�.

		//�÷������� : ������ ���̷��ͷ� ���ʹ� ��ī�ο� ������ �����ϸ鼭 ��ź�� ������ �ε巴�� �ϹǷ� �ſ� ������
		//�׷��� ȭ�� ���� ���δ�

	if (effect == SKETCH_C) {
		Size smallSize;
		smallSize.width = size.width / 2;
		smallSize.height = size.height / 2;
		Mat smallImg = Mat(smallSize, CV_8UC3);
		resize(srcColor, smallImg, smallSize, 0, 0, INTER_LINEAR);

		//���̷��ͷ� ���͸� �����ϴ� �װ��� �Ķ���ʹ� �÷�����, ��ġ����, ũ��, �ݺ�Ƚ����. bilateralFilter()�� �Է��� ���� ������ �����Ƿ�
		//���� Mat�� �ʿ��ϴ�.
		Mat tmp = Mat(smallSize, CV_8UC3);		//�ӽ� Mat�� �����	
		int repetitions = 10;					//������ ��ȭ ȿ���� ��� �ݺ�Ƚ��
		for (int i = 0; i < repetitions; i++) {
			int ksize = 6;			//����ũ��, �ӵ��� ū ������ ��ģ��.
			double sigmaColor = 6.0;	//�������÷�����
			double sigmaSpace = 7.0;	//��������, �ӵ��������� �ش�.
			
			bilateralFilter(smallImg, tmp, ksize, sigmaColor, sigmaSpace);		//
			bilateralFilter(tmp, smallImg, ksize, sigmaColor, sigmaSpace);
			}
		//���� �̹����� ����
		Mat bigImg;
		resize(tmp, bigImg, size, 0, 0, INTER_LINEAR);		//������� ���̷��ͷ� ���ͷ� �����������ϸ鼭 �̹����� �ε巴���ϴ� �������̴�.
		dst.setTo(0);				//����ġ�� ��������ũ�� ������ ���� ���� ����� �����.
		bigImg.copyTo(dst, mask);				//����ũ�� ��� ������ ����Ǿ� ���� ���ϴ°Ŵ�. 0�� �ƴ� ���� �����ؾߵȴ�.
		//���� ����ũ�� ����ġ ����ũ�� �����κ��� 0���� ����ȭ �Ǿ��ִ�. �׷��Ƿ� ������ �ƴ� �κи� ����ǰ� ������ �������̵ȴ�.
		//����ġ ����ũ �� ������ �ƴ� ���� ������ ȭ�Ҹ� ����
//		for (int i = 0; i < dst.rows; i++) {
	//		for (int j = 0; j < dst.cols*3; j++) {
	//	//		dst.at<unsigned char>(i, j) = 255 - dst.at<unsigned char>(i, j);
				
		//	}
	//	}
		return;
		}

	//�Ǻΰ����� �̿��� ���ϸ��� ��� ���� 
	/*
		�Ǻΰ��� �˰���
		RGB(����-�ʷ�-�Ķ�) Ȥ�� HSV(����-ä��-��)�� �̿��� �ܼ��� �÷� �Ӱ谪�̳� �÷� ������׷� ���� ���������κ��� CLELab�÷� ��������
		ī�޶� ������ ���� ǥ�� �� ��� �������� �Ʒõ��� �ʿ��� ȥ�� ���� ������ ����н� �˰��� ���� �ִ�.
		�ܼ��� HSV �Ǻ� ������ ������ ���� ������ ä���� �ſ� ���ǰ����ϰ� ���� ������, ���� �ʹ� ��Ӱų� �ʹ� ������ �ʴٸ� �Ǻη� ������
		ȭ�Ҹ� ó���� �� �ִ�. �ϸ��� LBP�����з���� �� ������ �����ϴµ� ������ ������ ���� ����� �ִ� ȭ���� �÷� ������ ���캻��.
	*/
	/*/
	if (effect == ALIEN) {
		//���� ���� �κ��� ���������� �׸���.
		Mat faceOutline = Mat::zeros(size, CV_8UC3);
		Scalar color = CV_RGB(255, 255, 0);	//�����
		int thickness = 4;
		//ȭ�� ������ 70%�� �� ���̷� ����Ѵ�.
		int sw = size.width;
		int sh = size.height;
		int faceH = sh / 2 * 0.7;	//faceH�� Ÿ���� ������
		int faceW = faceH * 0.72;	//��� ȭ�� ũ�⿡�� ����� �����ϰ� �ʺ� �����Ѵ�.
		//�� �ܰ����� �׸���.
		ellipse(faceOutline, Point(sw / 2, sh / 2), Size(faceW, faceH), 0, 0, 360, color, thickness, CV_AA);
	}
	*/
	
	srcColor.copyTo(dst,mask);


	}


	
	

	
