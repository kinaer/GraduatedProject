/*
	모바일에 이식하기 전에 제대로 동작하는 데스크톱 버전먼저 구축하는게 좋다.
	디버깅할때 모바일 앱보다 훨씬 알기 쉽기 때문이다.
	데스크톱 애플리케이션은 OpenCV GUI 창을 사용하며, 카메라를 초기화하고
	각 카메라 프레임에서 1장의 코드 대부분을 포함하는 cartoonifyImage() 함수를 호출한다.
	그런다음 처리한 영상을 GUI창에 띄운다. 
	
	안드로이드 앱은 안드로이드 GUI창을 이용하며, 자바를 이용해 카메라를 초기화하고, 각 카메라 프레임에서 앞에 언급했던 확실히 똑같은 
	C++ cartoonifyImage() 함수를 호출한다.

	GUI코드가 들어간 main_desktop.cpp 로 데스크톱 프로그램을 만들어야하며,
	두 프로젝트에서 공유하는 cartoon.cpp 파일을 생성해야 한다. cartooniftImage()는 cartoon.cpp에 넣어야한다.

	웹캠이나 카메라 디바이스에 접근하기 위해 cv:VideoCapture객체의 open() 간단하게 호출한 후 기본 카메라 ID번호인 0을 넘길수 있다.
*/
#include <opencv/cv.h>
#include <opencv/highgui.h>

#include "cartoon.h"


int main(int argc, char *argv[]) 
{
	int carmerNumber = 0;		//기본 카메라 ID번호 0
	if (argc > 1)
		carmerNumber = atoi(argv[1]);	//컴파일할때 입력파라메터가 1기 이상이면 그건 카메라 번호를 바꿔주는거
	//atoi()함수는 10진수 정수를 문자열로 변환해주는 함수

	//카메라에 접근한다.
	cv::VideoCapture camera;
	camera.open(carmerNumber);
	if (!camera.isOpened())			//카메라가 이미 열려있으면 에러메시지 출력
	{
		cerr << "ERROR: Could not access the camera or video!" <<endl;
		exit(1);
	}
	//카메라에 접근했으니 해상도 설정을 시도한다.
	camera.set(CV_CAP_PROP_FRAME_WIDTH, 640);
	camera.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
	//웹캠을 초기화한 후에는 현제 카메라 영상을 cv::Mat 객체로 잡을 수 있다.
	//콘솔에서 입력을 받는 경우라면 C++스트림 추출 연산자를 사용해 cv::VideoCapture 객체에서 cv::Mat객체를 꺼내 카메라 프레임을 잡을수 있다.
	//OpenCV는 AVI나 MPG같은 비디오 파일을 쉽게 불러들인다. camera.open('my_video.avi') 이런식으로 

	/*
		OpenCV를 이용해 화면에 GUI창을 띄우고 싶다면 각영상에 대해 cv::imshow()를 호출하면 되지만 각 프레임에도 cv::waitKey()함수를 반드시 호출해야한다.
		그렇지 않으면 창을 전혀 갱신할 수 없다. cv::waitKey(0)을 호출하면 사용자가 창 내부에서 키를 입력할 때까지 무한히 기다리지만
		cv::waitKey(20)같이 양의정수를 넣으면 최소한 그 만큼의 밀리초 동안 기다리게한다.
	*/
	
	while (true) {
		//다음 카메라 프레임을 잡는다.
		cv::Mat cameraFrame;
		camera >> cameraFrame;	// 정보를 담는다
		if (cameraFrame.empty())
			cerr << "ERROR: Couldn't grab a camera frame." << endl;
		exit(1);
		//빈 결과 영상을 생성한 후 그 영상에 그린다.
		cv::Mat displayedFrame(cameraFrame.size(), CV_8UC3);

		//카메라 프레임에 만화 생성기 필터 실행한다.
		cartoonifyImage(cameraFrame, displayedFrame);
		imshow("Cartonnifier", displayedFrame);

		//최소한 20밀리초를 기다리면
		//화면에 영상을 띄울수 있다.
		//또한 GUI창에서 키를 입력했는지 조사한다.
		//리눅스를 지원하기위해서는 "char"가 존재해야한다.
		char keypress = cv::waitKey(20);
		if (keypress == 27) {
			//ESC키
			//프로그램종료 
			break;
		}
	}//while
	
}

