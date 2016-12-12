
#pragma once

#include <stdio.h>
#include <iostream>
#include <vector>



#include "opencv2/opencv.hpp"


using namespace cv;
using namespace std;

#define SKETCH		0
#define SKETCH_C	1
#define EVIL		2
#define ALIEN		3


void cartoonifyImage(Mat srcColor, Mat dst, int effect, int blurSize=5);
;