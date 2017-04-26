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


void cartoonify(Mat src, Mat dst,int isC)
{
	Mat gray;
	Mat gray2(src.size(),CV_8UC1);
	Mat temp;
	Mat mOnes;
	Mat lMat1;
	Mat lMat2;
	Mat lMat3;
	Mat lMat5;
	Mat mWhite(src.size(),CV_8UC1);
	Mat mBlock(src.size(), CV_8UC1);
	Mat lMat6;
	Mat grayToon;
	
	mWhite.setTo(255);
	mBlock.setTo(0);
	//cvtColor(src, gray2, CV_BGR2GRAY);
	//medianBlur(gray2, gray2, 9);
	gray2.setTo(255);
	screentone(gray2);

	//mOnes.ones(5, 5, CV_8UC1);
	cvtColor(src, gray, CV_BGR2GRAY);		//gray�������� ��ȯ
	//GaussianBlur(gray, gray, Size(3, 3), 3);		//����þ� ��
	//erode(gray, gray, mOnes);
	//dilate(gray, gray, mOnes);

	brightnessCorrection(gray);


	Canny(gray, lMat3, 160, 220);
	Canny(gray, lMat6, 40, 70);
	threshold(gray, lMat1, 15, 255, THRESH_BINARY_INV);				//gray���� �Ӱ谪�� �ٸ��� �� �̹��� ����
	threshold(gray, lMat2, 80, 255, THRESH_BINARY_INV);
	Canny(gray, lMat5, 60, 80);

	/*
	mWhite.copyTo(gray);			//������� ����
	mBlock.copyTo(gray, lMat5);		//����� �������� ������ lMat5(ĳ�Ͽ���)�� ��κ��� ���� �κи� ������
	lMat5.setTo(255);
	mBlock.copyTo(lMat5, gray);
	*/
	vector<vector<Point>> contours;

	//findContours(lMat5, contours, hierarchy, CV_RETR_LIST, CV_CHAIN_APPROX_NONE,Point(0,0));
	findContours(lMat5, contours,  CV_RETR_LIST, CV_CHAIN_APPROX_NONE);		//�ܰ����� ����
							//������ �ܰ����� 4������� �׸���.
	//drawContours(gray, contours, -1, Scalar(0), 6);

	if (isC == 0)
	{
		
		//gray.copyTo(grayToon);
		//brightnessCorrection(grayToon);
		
		//findContours(lMat5, contours, CV_RETR_LIST, CV_CHAIN_APPROX_NONE);		//�ܰ����� ����
		//���
		mWhite.copyTo(dst);
		
		gray2.copyTo(dst, lMat2);
		mBlock.copyTo(dst, lMat1);
		mBlock.copyTo(dst, lMat6);
		//gray2.copyTo(gray, gray);
		mBlock.copyTo(dst, lMat5);
		drawContours(dst, contours, -1, Scalar(0), 2);
		//Mat toonMat;
		//dst.copyTo(toonMat);

		//checkeredPattern(toonMat, gray, dst);
		
	}
	////////////////////////////////////////////////////////////////////
	//		�÷�
	if (isC == 1) {

		
		Size size = src.size();
		Size smallSize;
		smallSize.width = size.width / 2;
		smallSize.height = size.height / 2;
		Mat smallImg = Mat(smallSize, CV_8UC3);
		
		resize(src, smallImg, smallSize, 0, 0, INTER_LINEAR);

		//���̷��ͷ� ���͸� �����ϴ� �װ��� �Ķ���ʹ� �÷�����, ��ġ����, ũ��, �ݺ�Ƚ����. bilateralFilter()�� �Է��� ���� ������ �����Ƿ�
		//���� Mat�� �ʿ��ϴ�.
		Mat tmp = Mat(smallSize, CV_8UC3);		//�ӽ� Mat�� �����	
		int repetitions = 4;					//������ ��ȭ ȿ���� ��� �ݺ�Ƚ��
		for (int i = 0; i < repetitions; i++) {
			int ksize = 9;			//����ũ��, �ӵ��� ū ������ ��ģ��.
			double sigmaColor = 9.0;	//�������÷�����
			double sigmaSpace = 9.0;	//��������, �ӵ��������� �ش�.

			bilateralFilter(smallImg, tmp, ksize, sigmaColor, sigmaSpace);		//
			bilateralFilter(tmp, smallImg, ksize, sigmaColor, sigmaSpace);
		}
		//���� �̹����� ����
		Mat bigImg;
		resize(tmp, bigImg, size, 0, 0, INTER_LINEAR);		//������� ���̷��ͷ� ���ͷ� �����������ϸ鼭 �̹����� �ε巴���ϴ� �������̴�.
		medianBlur(bigImg, bigImg, 9);
		
		dst.setTo(0);				//����ġ�� ��������ũ�� ������ ���� ���� ����� �����.

		//�÷�
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		cvtColor(bigImg, bigImg, CV_BGR2HSV);

		//HSV�� ��� ���� ������ [0,179], ä�� ������ [0,255], �� ������ [0,255]�Դϴ�.
		//http://docs.opencv.org/2.4/modules/imgproc/doc/miscellaneous_transformations.html#cvtcolor
		cScreentone(bigImg);
		cvtColor(bigImg, bigImg, CV_HSV2BGR);
		//�÷�
		//vector<vector<Point> > contours2;
		//findContours(lMat3, contours, CV_RETR_LIST, CV_CHAIN_APPROX_NONE);
		
		mWhite.copyTo(gray);
		//gray2.copyTo(gray, lMat2);
		mBlock.copyTo(gray, lMat1);
		mBlock.copyTo(gray, lMat5);
		mBlock.copyTo(gray, lMat6);
		//src.copyTo(dst, gray);
		bigImg.copyTo(dst, gray);
		//drawContours(dst, contours, -1, Scalar(0), 2);
		
		//mBlock.copyTo(dst, lMat1);
		//mBlock.copyTo(dst, lMat3);
	}
	//namedWindow("window1");
	namedWindow("window2");
	if (contours.empty())
		cerr << "���Ϳ� ���Ұ� ����" << endl;
	else
		cerr << "���Ϳ� ���Ұ� �ִ�" << endl;
//	contours.clear();
	
	
	//imshow("window1", grayToon);
	imshow("window2", dst);
	waitKey(0);
	

}
void cScreentone(Mat src)
{
	for (int y = 0; y < src.rows; y++)
	{
		for (int x = 0; x < src.cols * 3; x += 3)
		{
			/*
			if (src.at<unsigned char>(y, x) <= 20)
			{
				src.at<unsigned char>(y, x) = 0;
			}src.at<unsigned char>(y, x) > 20 &&
			*/
			if (src.at<unsigned char>(y, x + 1) <= 40)
			{
				src.at<unsigned char>(y, x + 1) = 0;
			}
			else if (src.at<unsigned char>(y, x + 1) > 40 && src.at<unsigned char>(y, x + 1) <= 90)
			{
				src.at<unsigned char>(y, x + 1) = 75;
			}
			else if (src.at<unsigned char>(y, x + 1) > 90 && src.at<unsigned char>(y, x + 1) <= 140)
			{
				src.at<unsigned char>(y, x + 1) = 85;
			}
			else if (src.at<unsigned char>(y, x + 1) > 140 && src.at<unsigned char>(y, x + 1) <= 190)
			{
				src.at<unsigned char>(y, x + 1) = 160;
			}
			else if (src.at<unsigned char>(y, x + 1) > 190 && src.at<unsigned char>(y, x + 1) <= 240)
			{
				src.at<unsigned char>(y, x + 1) = 190;
			}
			else if (src.at<unsigned char>(y, x + 1) > 240)
			{
				src.at<unsigned char>(y, x + 1) = 255;
			}
			/////////////////////ä��
			//////��

			if (src.at<unsigned char>(y, x + 2) <= 50)
			{
				src.at<unsigned char>(y, x + 2) = 0;
			}
			else if (src.at<unsigned char>(y, x + 2) > 50 && src.at<unsigned char>(y, x + 2) <= 100)
			{
				src.at<unsigned char>(y, x + 2) = 50;
			}
			else if (src.at<unsigned char>(y, x + 2) > 100 && src.at<unsigned char>(y, x + 2) <= 150)
			{
				src.at<unsigned char>(y, x + 2) = 120;
			}
			
			else if (src.at<unsigned char>(y, x + 2) > 200 && src.at<unsigned char>(y, x + 2) <= 250)
			{
				src.at<unsigned char>(y, x + 2) = 220;
			}
			/*
			else if (src.at<unsigned char>(y, x + 2) > 160 && src.at<unsigned char>(y, x + 2) <= 200)
			{
				src.at<unsigned char>(y, x + 2) = 200;
			}
			
			else if (src.at<unsigned char>(y, x + 2) > 200 && src.at<unsigned char>(y, x + 2) <= 240)
			{
				src.at<unsigned char>(y, x + 2) = 240;
			}
			*/
			else if (src.at<unsigned char>(y, x + 2) > 250)
			{
				src.at<unsigned char>(y, x + 2) = 255;
			}
		


		}
	
	}
}
	
void checkeredPattern(Mat toonMat, Mat src, Mat dst)
{
	int rowCount = 0;
	int colCount = 0;
	bool rowFalg = true;
	bool colFalg = true;

	for (int y = 0; y < dst.rows; y++)
	{
		for (int x = 0; x < dst.cols; x++)
		{
			colCount++;
			if (colCount >= 50)
			{
				colFalg = !colFalg;
				colCount = 0;
			}

			if (rowFalg == true)
			{
				if (colFalg == true)
				{
					dst.at<unsigned char>(y, x) = src.at<unsigned char>(y, x);
				}
				else
				{
					dst.at<unsigned char>(y, x) =toonMat.at<unsigned char>(y, x);
				}

			}
			else
			{
				if (colFalg == true)
				{
					dst.at<unsigned char>(y, x) = toonMat.at<unsigned char>(y, x);
				}
				else
				{
					dst.at<unsigned char>(y, x) = src.at<unsigned char>(y, x);
				}
			}

		}
		rowCount++;
		if (rowCount >= 50)
		{
			rowFalg = !rowFalg;
			rowCount = 0;
		}
	}
}

void screentone(Mat gray)
{
	
	for (int y = 0; y < gray.rows; y++)
	{
		for (int x = 0; x < gray.cols; x++)
		{
			if((x%3==0))
			
			gray.at<unsigned char>(y, x) = 150;
		
		}
		
	}
}
	
void brightnessCorrection(Mat gray) {
	for (int y = 0; y < gray.rows; ++y)
	{
		for (int x = 0; x < gray.cols; ++x)
		{
			int temp;
			int intensity;
			if (gray.at<unsigned char>(y, x) > 127) //���� �㿸�ٸ�
			{
				intensity = (gray.at<unsigned char>(y, x) - 127) * 0.5;
				temp = gray.at<unsigned char>(y, x) + intensity;
			}	
			else              //�Ź��Ź��ϸ�
			{
				intensity = (127 - gray.at<unsigned char>(y, x)) * 0.5;
				temp = gray.at<unsigned char>(y, x) - intensity;
			}//
			//���Ѱ� ���Ѱ� ����
			if (temp > 250)
				temp = 255;
			else if (temp < 5)
				temp = 0;
			gray.at<unsigned char>(y, x) = temp;
		}
	}
}