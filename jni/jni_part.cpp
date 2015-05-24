#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include <android/log.h>

#define LOG_TAG "jni/jni_part.cpp"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

extern "C" {

JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_DrawRectangle(
		JNIEnv*, jobject, jlong addrGray, jlong addrRgba);
JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_DrawRectangle(
		JNIEnv*, jobject, jlong addrGray, jlong addrRgba) {
	Mat& mGr = *(Mat*) addrGray;
	Mat& mRgb = *(Mat*) addrRgba;
	int w = mRgb.rows;
	int h = mRgb.cols;
	rectangle(mRgb, Point(w / 4, h / 4), Point(3 * w / 4, 3 * w / 4),
			Scalar(0, 255, 255), -1, 8);
}

JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_FindFeatures(
		JNIEnv*, jobject, jlong addrGray, jlong addrRgba);
JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_FindFeatures(
		JNIEnv*, jobject, jlong addrGray, jlong addrRgba) {
	LOGD("Entering FindFeatures !!!");

	Mat& mGr = *(Mat*) addrGray;
	Mat& mRgb = *(Mat*) addrRgba;
	vector<KeyPoint> v;

	FastFeatureDetector detector(50);
	detector.detect(mGr, v);
	for (unsigned int i = 0; i < v.size(); i++) {
		const KeyPoint& kp = v[i];
		circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255, 0, 0, 255));
	}
}

JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_nativeCalcFeatures(
		JNIEnv *, jclass, jstring, jlong);
JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_nativeCalcFeatures(
		JNIEnv *env, jclass, jstring location, jlong addrGray) {
	LOGD("Entering nativeCalcFeatures !!!");
	const char *nativeString = env->GetStringUTFChars(location, NULL);
	Mat mean, eigenvalues, eigenvectors;

	string filepath = string(nativeString);
	{
		FileStorage fs;
		fs.open(filepath, FileStorage::READ);

		if (!fs.isOpened()) {
			LOGI("Can't open file: %s ", filepath.c_str());
		}

		LOGD("READ BEGING !!!");


		fs["mean"] >> mean;
		LOGI("%d , %d\n", mean.rows, mean.cols);

		fs["eigenvectors"] >> eigenvectors;
		LOGI("%d , %d\n", eigenvectors.rows, eigenvectors.cols);

		LOGD("READ ENDED !!!");
	}

	Mat& mGr = *(Mat*) addrGray;
	LOGI("%d , %d\n", mGr.rows, mGr.cols);

	Mat face = mGr(cv::Rect(mGr.cols / 2, mGr.rows / 2, 200, 180));
	LOGI("%d , %d\n", face.rows, face.cols);
}
}
