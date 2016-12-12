/*
	����Ͽ� �̽��ϱ� ���� ����� �����ϴ� ����ũ�� �������� �����ϴ°� ����.
	������Ҷ� ����� �ۺ��� �ξ� �˱� ���� �����̴�.
	����ũ�� ���ø����̼��� OpenCV GUI â�� ����ϸ�, ī�޶� �ʱ�ȭ�ϰ�
	�� ī�޶� �����ӿ��� 1���� �ڵ� ��κ��� �����ϴ� cartoonifyImage() �Լ��� ȣ���Ѵ�.
	�׷����� ó���� ������ GUIâ�� ����. 
	
	�ȵ���̵� ���� �ȵ���̵� GUIâ�� �̿��ϸ�, �ڹٸ� �̿��� ī�޶� �ʱ�ȭ�ϰ�, �� ī�޶� �����ӿ��� �տ� ����ߴ� Ȯ���� �Ȱ��� 
	C++ cartoonifyImage() �Լ��� ȣ���Ѵ�.

	GUI�ڵ尡 �� main_desktop.cpp �� ����ũ�� ���α׷��� �������ϸ�,
	�� ������Ʈ���� �����ϴ� cartoon.cpp ������ �����ؾ� �Ѵ�. cartooniftImage()�� cartoon.cpp�� �־���Ѵ�.

	��ķ�̳� ī�޶� ����̽��� �����ϱ� ���� cv:VideoCapture��ü�� open() �����ϰ� ȣ���� �� �⺻ ī�޶� ID��ȣ�� 0�� �ѱ�� �ִ�.
*/
#include <opencv/cv.h>
#include <opencv/highgui.h>

#include "cartoon.h"


int main(int argc, char *argv[]) 
{
	int carmerNumber = 0;		//�⺻ ī�޶� ID��ȣ 0
	if (argc > 1)
		carmerNumber = atoi(argv[1]);	//�������Ҷ� �Է��Ķ���Ͱ� 1�� �̻��̸� �װ� ī�޶� ��ȣ�� �ٲ��ִ°�
	//atoi()�Լ��� 10���� ������ ���ڿ��� ��ȯ���ִ� �Լ�

	//ī�޶� �����Ѵ�.
	cv::VideoCapture camera;
	camera.open(carmerNumber);
	if (!camera.isOpened())			//ī�޶� �̹� ���������� �����޽��� ���
	{
		cerr << "ERROR: Could not access the camera or video!" <<endl;
		exit(1);
	}
	//ī�޶� ���������� �ػ� ������ �õ��Ѵ�.
	camera.set(CV_CAP_PROP_FRAME_WIDTH, 640);
	camera.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
	//��ķ�� �ʱ�ȭ�� �Ŀ��� ���� ī�޶� ������ cv::Mat ��ü�� ���� �� �ִ�.
	//�ֿܼ��� �Է��� �޴� ����� C++��Ʈ�� ���� �����ڸ� ����� cv::VideoCapture ��ü���� cv::Mat��ü�� ���� ī�޶� �������� ������ �ִ�.
	//OpenCV�� AVI�� MPG���� ���� ������ ���� �ҷ����δ�. camera.open('my_video.avi') �̷������� 

	/*
		OpenCV�� �̿��� ȭ�鿡 GUIâ�� ���� �ʹٸ� ������ ���� cv::imshow()�� ȣ���ϸ� ������ �� �����ӿ��� cv::waitKey()�Լ��� �ݵ�� ȣ���ؾ��Ѵ�.
		�׷��� ������ â�� ���� ������ �� ����. cv::waitKey(0)�� ȣ���ϸ� ����ڰ� â ���ο��� Ű�� �Է��� ������ ������ ��ٸ�����
		cv::waitKey(20)���� ���������� ������ �ּ��� �� ��ŭ�� �и��� ���� ��ٸ����Ѵ�.
	*/
	
	while (true) {
		//���� ī�޶� �������� ��´�.
		cv::Mat cameraFrame;
		camera >> cameraFrame;	// ������ ��´�
		if (cameraFrame.empty())
			cerr << "ERROR: Couldn't grab a camera frame." << endl;
		exit(1);
		//�� ��� ������ ������ �� �� ���� �׸���.
		cv::Mat displayedFrame(cameraFrame.size(), CV_8UC3);

		//ī�޶� �����ӿ� ��ȭ ������ ���� �����Ѵ�.
		cartoonifyImage(cameraFrame, displayedFrame);
		imshow("Cartonnifier", displayedFrame);

		//�ּ��� 20�и��ʸ� ��ٸ���
		//ȭ�鿡 ������ ���� �ִ�.
		//���� GUIâ���� Ű�� �Է��ߴ��� �����Ѵ�.
		//�������� �����ϱ����ؼ��� "char"�� �����ؾ��Ѵ�.
		char keypress = cv::waitKey(20);
		if (keypress == 27) {
			//ESCŰ
			//���α׷����� 
			break;
		}
	}//while
	
}

