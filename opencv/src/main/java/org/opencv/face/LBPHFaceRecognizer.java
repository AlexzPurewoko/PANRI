//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.face;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.utils.Converters;

// C++: class LBPHFaceRecognizer
//javadoc: LBPHFaceRecognizer

public class LBPHFaceRecognizer extends FaceRecognizer {

    protected LBPHFaceRecognizer(long addr) { super(addr); }

    // internal usage only
    public static LBPHFaceRecognizer __fromPtr__(long addr) { return new LBPHFaceRecognizer(addr); }

    //
    // C++:  Mat getLabels()
    //

    //javadoc: LBPHFaceRecognizer::create(radius, neighbors, grid_x, grid_y, threshold)
    public static LBPHFaceRecognizer create(int radius, int neighbors, int grid_x, int grid_y, double threshold)
    {

        LBPHFaceRecognizer retVal = LBPHFaceRecognizer.__fromPtr__(create_0(radius, neighbors, grid_x, grid_y, threshold));

        return retVal;
    }


    //
    // C++: static Ptr_LBPHFaceRecognizer create(int radius = 1, int neighbors = 8, int grid_x = 8, int grid_y = 8, double threshold = DBL_MAX)
    //

    //javadoc: LBPHFaceRecognizer::create()
    public static LBPHFaceRecognizer create()
    {

        LBPHFaceRecognizer retVal = LBPHFaceRecognizer.__fromPtr__(create_1());

        return retVal;
    }

    // C++:  Mat getLabels()
    private static native long getLabels_0(long nativeObj);


    //
    // C++:  double getThreshold()
    //

    // C++: static Ptr_LBPHFaceRecognizer create(int radius = 1, int neighbors = 8, int grid_x = 8, int grid_y = 8, double threshold = DBL_MAX)
    private static native long create_0(int radius, int neighbors, int grid_x, int grid_y, double threshold);


    //
    // C++:  int getGridX()
    //

    private static native long create_1();


    //
    // C++:  int getGridY()
    //

    // C++:  double getThreshold()
    private static native double getThreshold_0(long nativeObj);


    //
    // C++:  int getNeighbors()
    //

    // C++:  int getGridX()
    private static native int getGridX_0(long nativeObj);


    //
    // C++:  int getRadius()
    //

    // C++:  int getGridY()
    private static native int getGridY_0(long nativeObj);


    //
    // C++:  vector_Mat getHistograms()
    //

    // C++:  int getNeighbors()
    private static native int getNeighbors_0(long nativeObj);


    //
    // C++:  void setGridX(int val)
    //

    // C++:  int getRadius()
    private static native int getRadius_0(long nativeObj);


    //
    // C++:  void setGridY(int val)
    //

    // C++:  vector_Mat getHistograms()
    private static native long getHistograms_0(long nativeObj);


    //
    // C++:  void setNeighbors(int val)
    //

    // C++:  void setGridX(int val)
    private static native void setGridX_0(long nativeObj, int val);


    //
    // C++:  void setRadius(int val)
    //

    // C++:  void setGridY(int val)
    private static native void setGridY_0(long nativeObj, int val);


    //
    // C++:  void setThreshold(double val)
    //

    // C++:  void setNeighbors(int val)
    private static native void setNeighbors_0(long nativeObj, int val);

    // C++:  void setRadius(int val)
    private static native void setRadius_0(long nativeObj, int val);

    // C++:  void setThreshold(double val)
    private static native void setThreshold_0(long nativeObj, double val);

    // native support for java finalize()
    private static native void delete(long nativeObj);

    //javadoc: LBPHFaceRecognizer::getLabels()
    public  Mat getLabels()
    {

        Mat retVal = new Mat(getLabels_0(nativeObj));

        return retVal;
    }

    //javadoc: LBPHFaceRecognizer::getThreshold()
    public  double getThreshold()
    {

        double retVal = getThreshold_0(nativeObj);

        return retVal;
    }

    //javadoc: LBPHFaceRecognizer::setThreshold(val)
    public  void setThreshold(double val)
    {

        setThreshold_0(nativeObj, val);

        return;
    }

    //javadoc: LBPHFaceRecognizer::getGridX()
    public  int getGridX()
    {

        int retVal = getGridX_0(nativeObj);

        return retVal;
    }

    //javadoc: LBPHFaceRecognizer::setGridX(val)
    public  void setGridX(int val)
    {

        setGridX_0(nativeObj, val);

        return;
    }

    //javadoc: LBPHFaceRecognizer::getGridY()
    public  int getGridY()
    {

        int retVal = getGridY_0(nativeObj);

        return retVal;
    }

    //javadoc: LBPHFaceRecognizer::setGridY(val)
    public  void setGridY(int val)
    {

        setGridY_0(nativeObj, val);

        return;
    }

    //javadoc: LBPHFaceRecognizer::getNeighbors()
    public  int getNeighbors()
    {

        int retVal = getNeighbors_0(nativeObj);

        return retVal;
    }

    //javadoc: LBPHFaceRecognizer::setNeighbors(val)
    public  void setNeighbors(int val)
    {

        setNeighbors_0(nativeObj, val);

        return;
    }

    //javadoc: LBPHFaceRecognizer::getRadius()
    public  int getRadius()
    {

        int retVal = getRadius_0(nativeObj);

        return retVal;
    }

    //javadoc: LBPHFaceRecognizer::setRadius(val)
    public  void setRadius(int val)
    {

        setRadius_0(nativeObj, val);

        return;
    }

    //javadoc: LBPHFaceRecognizer::getHistograms()
    public  List<Mat> getHistograms()
    {
        List<Mat> retVal = new ArrayList<Mat>();
        Mat retValMat = new Mat(getHistograms_0(nativeObj));
        Converters.Mat_to_vector_Mat(retValMat, retVal);
        return retVal;
    }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
