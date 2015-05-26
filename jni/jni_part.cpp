#include <jni.h>
#include <opencv2/core/core.hpp>
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
		JNIEnv *, jclass, jstring, jstring, jlong);
JNIEXPORT void JNICALL Java_net_appositedesigns_fileexplorer_activity_Tutorial1Activity_nativeCalcFeatures(
		JNIEnv *env, jclass, jstring train_file, jstring feature_file,
		jlong addrGray) {
	LOGD("Entering nativeCalcFeatures !!!");
	const char *nativeStringTrain = env->GetStringUTFChars(train_file, NULL);
	const char *nativeStringFeature = env->GetStringUTFChars(feature_file,
			NULL);

	Mat mean, eigenvalues, eigenvectors;

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

		LOGD("READ TRAINDATABASE SUCCESSFUL !");
	}

	Mat& mGr = *(Mat*) addrGray;

	cv::resize(mGr, mGr, Size(200, 180), 0, 0, INTER_NEAREST);

	/**
	 * 1. project test image to facespaces here.
	 */
	Mat project = subspaceProject(eigenvectors, mean, mGr.reshape(1, 1));

	LOGI("%d , %d\n", mGr.rows, mGr.cols);
	{
		FileStorage fs;
		fs.open(filepathfeature, FileStorage::WRITE);
		fs << "project" << project;
	}
	LOGD("SAVE FEATURE VECTOR SUCCESSFUL !");

}
}
