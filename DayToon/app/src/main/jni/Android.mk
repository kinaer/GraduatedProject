LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
OPENCV_LIB_TYPE:=STATIC
OPENCV_INSTALL_MODULES:=on

include C:\OpenCV-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_MODULE     :=  cartoonfier
LOCAL_SRC_FILES   := jni_part.cpp
LOCAL_SRC_FILES    += C:/OpencvExample/cartoon2/Cartoon/Cartoon/cartoonFilter.cpp
LOCAL_C_INCLUDES += C:/OpencvExample/cartoon2/Cartoon/Cartoon
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
