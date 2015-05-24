#include <FileStorage-jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/contrib/detection_based_tracker.hpp>

#include <string>
#include <vector>
#include <android/log.h>

#define LOG_TAG "FileStorage/FileStorage-jni.cpp"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

JNIEXPORT void JNICALL Java_com_example_testreadxmlfile_FileStorage_nativeCalcFeatures(
		JNIEnv *env, jclass, jstring location) {
	LOGD("Entering nativeCalcFeatures !!!");
	const char *nativeString = env->GetStringUTFChars(location, NULL);
	string filepath = string(nativeString);
	{
		FileStorage fs;
		fs.open(filepath, FileStorage::READ);

		if (!fs.isOpened()) {
			LOGI("Can't open file: %s ", filepath.c_str());
		}

		Mat mean;

		LOGD("READ BEGING !!!");
		fs["mean"] >> mean;

		LOGI("%d , %d\n", mean.rows, mean.cols);

		LOGD("READ ENDED !!!");
	}

}
JNIEXPORT void JNICALL Java_com_example_testreadxmlfile_FileStorage_nativeSayHello(
		JNIEnv *env, jclass) {
	LOGD("Entering nativeSayHello !!!");
}
