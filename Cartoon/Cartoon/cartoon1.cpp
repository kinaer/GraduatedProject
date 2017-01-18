#include "opencv2/opencv.hpp"
#include "cartoon.h"
using namespace std;
using namespace cv;

bool sketchMode = true;
bool evilMode = true;

int main()
{
	VideoCapture cam;
	cam.open(0);			//����Ʈ �⺻ ī�޶� ����
	//ī�޶� ���� ���ϸ�
	if (!cam.isOpened())
	{
		cerr << "ERROR : Could not access ths cam of video" << endl;
		exit(1);
	}

	namedWindow("cam");
	while (1) {
		Mat frame;
		cam >> frame;
		if (frame.empty())
		{
			cerr << "ERROR : Could not grab a cam frame" << endl;
			exit(1);
		}
		Mat displayedFrame(frame.size(),CV_8UC3);
		//frame.at<uchar>(3, 9);
		cartoonifyImage(frame, displayedFrame, SKETCH,3);
		IplImage temp =  displayedFrame;
		cvShowImage("cam", &temp);
		//displayedFrame.release();
		//frame.release();
		//imshow("cam", displayedFrame);
		
		int c = waitKey(20);
		if (c == 27)
			break;
	}
	cam.release();
	
	
}