//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.bgsegm;

import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractor;

// C++: class BackgroundSubtractorCNT
//javadoc: BackgroundSubtractorCNT

public class BackgroundSubtractorCNT extends BackgroundSubtractor {

    protected BackgroundSubtractorCNT(long addr) { super(addr); }

    // internal usage only
    public static BackgroundSubtractorCNT __fromPtr__(long addr) { return new BackgroundSubtractorCNT(addr); }

    //
    // C++:  bool getIsParallel()
    //

    // C++:  bool getIsParallel()
    private static native boolean getIsParallel_0(long nativeObj);


    //
    // C++:  bool getUseHistory()
    //

    // C++:  bool getUseHistory()
    private static native boolean getUseHistory_0(long nativeObj);


    //
    // C++:  int getMaxPixelStability()
    //

    // C++:  int getMaxPixelStability()
    private static native int getMaxPixelStability_0(long nativeObj);


    //
    // C++:  int getMinPixelStability()
    //

    // C++:  int getMinPixelStability()
    private static native int getMinPixelStability_0(long nativeObj);


    //
    // C++:  void apply(Mat image, Mat& fgmask, double learningRate = -1)
    //

    // C++:  void apply(Mat image, Mat& fgmask, double learningRate = -1)
    private static native void apply_0(long nativeObj, long image_nativeObj, long fgmask_nativeObj, double learningRate);

    private static native void apply_1(long nativeObj, long image_nativeObj, long fgmask_nativeObj);


    //
    // C++:  void getBackgroundImage(Mat& backgroundImage)
    //

    // C++:  void getBackgroundImage(Mat& backgroundImage)
    private static native void getBackgroundImage_0(long nativeObj, long backgroundImage_nativeObj);


    //
    // C++:  void setIsParallel(bool value)
    //

    // C++:  void setIsParallel(bool value)
    private static native void setIsParallel_0(long nativeObj, boolean value);


    //
    // C++:  void setMaxPixelStability(int value)
    //

    // C++:  void setMaxPixelStability(int value)
    private static native void setMaxPixelStability_0(long nativeObj, int value);


    //
    // C++:  void setMinPixelStability(int value)
    //

    // C++:  void setMinPixelStability(int value)
    private static native void setMinPixelStability_0(long nativeObj, int value);


    //
    // C++:  void setUseHistory(bool value)
    //

    // C++:  void setUseHistory(bool value)
    private static native void setUseHistory_0(long nativeObj, boolean value);

    // native support for java finalize()
    private static native void delete(long nativeObj);

    //javadoc: BackgroundSubtractorCNT::getIsParallel()
    public  boolean getIsParallel()
    {

        boolean retVal = getIsParallel_0(nativeObj);

        return retVal;
    }

    //javadoc: BackgroundSubtractorCNT::setIsParallel(value)
    public  void setIsParallel(boolean value)
    {

        setIsParallel_0(nativeObj, value);

        return;
    }

    //javadoc: BackgroundSubtractorCNT::getUseHistory()
    public  boolean getUseHistory()
    {

        boolean retVal = getUseHistory_0(nativeObj);

        return retVal;
    }

    //javadoc: BackgroundSubtractorCNT::setUseHistory(value)
    public  void setUseHistory(boolean value)
    {

        setUseHistory_0(nativeObj, value);

        return;
    }

    //javadoc: BackgroundSubtractorCNT::getMaxPixelStability()
    public  int getMaxPixelStability()
    {

        int retVal = getMaxPixelStability_0(nativeObj);

        return retVal;
    }

    //javadoc: BackgroundSubtractorCNT::setMaxPixelStability(value)
    public  void setMaxPixelStability(int value)
    {

        setMaxPixelStability_0(nativeObj, value);

        return;
    }

    //javadoc: BackgroundSubtractorCNT::getMinPixelStability()
    public  int getMinPixelStability()
    {

        int retVal = getMinPixelStability_0(nativeObj);

        return retVal;
    }

    //javadoc: BackgroundSubtractorCNT::setMinPixelStability(value)
    public  void setMinPixelStability(int value)
    {

        setMinPixelStability_0(nativeObj, value);

        return;
    }

    //javadoc: BackgroundSubtractorCNT::apply(image, fgmask, learningRate)
    public  void apply(Mat image, Mat fgmask, double learningRate)
    {

        apply_0(nativeObj, image.nativeObj, fgmask.nativeObj, learningRate);

        return;
    }

    //javadoc: BackgroundSubtractorCNT::apply(image, fgmask)
    public  void apply(Mat image, Mat fgmask)
    {

        apply_1(nativeObj, image.nativeObj, fgmask.nativeObj);

        return;
    }

    //javadoc: BackgroundSubtractorCNT::getBackgroundImage(backgroundImage)
    public  void getBackgroundImage(Mat backgroundImage)
    {

        getBackgroundImage_0(nativeObj, backgroundImage.nativeObj);

        return;
    }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
