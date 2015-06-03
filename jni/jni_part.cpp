#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/contrib/contrib.hpp>
#include <vector>
#include <android/log.h>

#define LOG_TAG "jni/jni_part.cpp"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

extern "C" {

JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_nativeCalcFeatures(
		JNIEnv *, jclass, jstring, jstring, jlong, jlong);
JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_nativeCalcFeatures(
		JNIEnv *env, jclass, jstring train_file, jstring feature_file,
		jlong addrGray, jlong addrProject) {
	LOGD("Entering nativeCalcFeatures !!!");

	const char *nativeStringTrain = env->GetStringUTFChars(train_file, NULL);
	const char *nativeStringFeature = env->GetStringUTFChars(feature_file,
			NULL);

	Mat mean, eigenvalues, eigenvectors;
	jboolean savefile;

	string filepathtrain = string(nativeStringTrain);
	string filepathfeature = string(nativeStringFeature);

	{
		FileStorage fs;
		fs.open(filepathtrain, FileStorage::READ);

		if (!fs.isOpened()) {
			LOGI("Can't open file: %s ", filepathtrain.c_str());
		}
		fs["mean"] >> mean;
		fs["eigenvectors"] >> eigenvectors;
	}

	Mat& mGr = *(Mat*) addrGray;
	Mat& mPr = *(Mat*) addrProject;

	//cv::resize(mGr, mGr, Size(200, 180), 0, 0, INTER_NEAREST);
	int w = mGr.cols;
	int h = mGr.rows;
	Mat subimage = mGr(Rect(w / 2 - 180, h / 2 - 200, 360, 400));
	cv::resize(subimage, subimage, Size(), 0.5, 0.5);
	LOGI("Size of input image: %d %d ", subimage.rows, subimage.cols);

	//project test image to facespaces here.
	mPr = subspaceProject(eigenvectors, mean, subimage.reshape(1, 1));

	{
		FileStorage fs;
		fs.open(filepathfeature, FileStorage::WRITE);
		fs << "project" << mPr;
	}
}

JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_saveFileImage(
		JNIEnv *, jclass, jstring, jlong);
JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_saveFileImage(
		JNIEnv *env, jclass, jstring location, jlong addrGray) {
	LOGD("Entering saveFileImage !!!");

	const char *jlocation = env->GetStringUTFChars(location, NULL);
	string jnilocation = string(jlocation);

	Mat& mGr = *(Mat*) addrGray;

	//cv::resize(mGr, mGr, Size(200, 180), 0, 0, INTER_NEAREST);
	int w = mGr.cols;
	int h = mGr.rows;
	Mat subimage = mGr(Rect(w / 2 - 180, h / 2 - 200, 360, 400));
	cv::resize(subimage, subimage, Size(), 0.5, 0.5);

	LOGI("Save image %d %d as %s", mGr.rows, mGr.cols, jnilocation.c_str());
	imwrite(jnilocation, subimage);

	LOGI("Save image success !!!");
}

JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_drawLine(
		JNIEnv *, jclass, jlong);
JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_drawLine(
		JNIEnv *env, jclass, jlong addrRgb) {
	Mat& mRgba = *(Mat*) addrRgb;
	int w = mRgba.cols;
	int h = mRgba.rows;

	line(mRgba, Point(w / 2 - 180, h / 2 - 200),
			Point(w / 2 + 180, h / 2 - 200), Scalar(0, 0, 0), 2, 8);
	line(mRgba, Point(w / 2 - 180, h / 2 - 200),
			Point(w / 2 - 180, h / 2 + 200), Scalar(0, 0, 0), 2, 8);
	line(mRgba, Point(w / 2 - 180, h / 2 + 200),
			Point(w / 2 + 180, h / 2 + 200), Scalar(0, 0, 0), 2, 8);
	line(mRgba, Point(w / 2 + 180, h / 2 - 200),
			Point(w / 2 + 180, h / 2 + 200), Scalar(0, 0, 0), 2, 8);
}

JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_getFeatureVector(
		JNIEnv *, jclass, jstring, jlong);
JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_getFeatureVector(
		JNIEnv *env, jclass, jstring feature_file, jlong addrFeature) {
	const char *nativeStringFeature = env->GetStringUTFChars(feature_file,
			NULL);
	string filepathfeature = string(nativeStringFeature);
	Mat& mFea = *(Mat*) addrFeature;
	{
		FileStorage fs;
		fs.open(filepathfeature, FileStorage::READ);

		if (!fs.isOpened()) {
			LOGI("Can't open file: %s ", filepathfeature.c_str());
		}
		fs["project"] >> mFea;
	}
}

}
