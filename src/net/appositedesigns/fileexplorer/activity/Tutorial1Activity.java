package net.appositedesigns.fileexplorer.activity;

import java.io.File;
import java.io.IOException;

import net.appositedesigns.fileexplorer.R;
import net.appositedesigns.fileexplorer.util.DES;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class Tutorial1Activity extends Activity implements
		CvCameraViewListener2, OnTouchListener {
	private static final String TAG = "OCVSample::Activity";

	private CameraBridgeViewBase mOpenCvCameraView;
	private boolean mIsJavaCamera = true;
	private MenuItem mItemSwitchCamera = null;

	private Mat mRgba;
	private Mat mGray;

	private File mCascadeFile;
	private File mFeatureFile;
	private File mInputFile;
	private File cascadeDir;

	private String path;
	private String flag;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
				mCascadeFile = new File(cascadeDir,
						"traindatabase.xml");
				mFeatureFile = new File(cascadeDir, "feature_vector.xml");

				mOpenCvCameraView.enableView();
				mOpenCvCameraView.setOnTouchListener(Tutorial1Activity.this);
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public Tutorial1Activity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.tutorial1_surface_view);

		Intent intent = getIntent();
		path = intent.getStringExtra("path");
		flag = intent.getStringExtra("flag");

		if (mIsJavaCamera)
			mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
		else
			mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);

		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		mItemSwitchCamera = menu.add("Toggle Native/Java camera");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String toastMesage = new String();
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

		if (item == mItemSwitchCamera) {
			mOpenCvCameraView.setVisibility(SurfaceView.GONE);
			mIsJavaCamera = !mIsJavaCamera;

			if (mIsJavaCamera) {
				mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
				toastMesage = "Java Camera";
			} else {
				mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);
				toastMesage = "Native Camera";
			}

			mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
			mOpenCvCameraView.setCvCameraViewListener(this);
			mOpenCvCameraView.enableView();
			Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
			toast.show();
		}

		return true;
	}

	public void onCameraViewStarted(int width, int height) {
	}

	public void onCameraViewStopped() {
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();
		
		drawLine(mRgba.getNativeObjAddr());
		return mRgba;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i(TAG, "onTouch event");
		if (flag.equals("encrypt")) {
			Mat project = new Mat();

			nativeCalcFeatures(mCascadeFile.getAbsolutePath(),
					mFeatureFile.getAbsolutePath(), mGray.getNativeObjAddr(),
					project.getNativeObjAddr());

			saveImage(mGray);

			int rows = project.rows();
			int cols = project.cols();

			double[] mat = new double[rows * cols];
			project.get(0, 0, mat);

			// generate key from feature vector here .

			try {
				DES.encrypt("1111111111", path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(this, "Encryp success !!!", Toast.LENGTH_SHORT)
					.show();
			finish();
		} else if (flag.equals("decrypt")) {
			Mat project = new Mat();

			nativeCalcFeatures(mCascadeFile.getAbsolutePath(),
					mFeatureFile.getAbsolutePath(), mGray.getNativeObjAddr(),
					project.getNativeObjAddr());

			saveImage(mGray);
			int rows = project.rows();
			int cols = project.cols();

			double[] mat = new double[rows * cols];
			project.get(0, 0, mat);

			// generate key from feature vector here .

			try {
				DES.decrypt("1111111111", path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(this, "Decrypt success !!!", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
		return false;
	}

	private void saveImage(Mat m) {
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		String filename = "input_image.png";
		File file = new File(path, filename);
		saveFileImage(file.getAbsolutePath(),m.getNativeObjAddr());
		//Highgui.imwrite(file.getAbsolutePath(), m);
	}

	private String generator(double[] mat) {

		return null;
	}

	private static native void nativeCalcFeatures(
			String traindatabase_location, String feature_vector_location,
			long matAddrGr, long matAddrPr);

	private static native void saveFileImage(String filename, long matAddrGr);
	private static native void drawLine(long matAddRgb);
	public static native void getFeatureVector(String feature_location,
			long matAddrFeature);
	
}
