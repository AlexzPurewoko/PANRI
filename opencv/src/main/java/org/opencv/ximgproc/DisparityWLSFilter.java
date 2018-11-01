//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ximgproc;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.ximgproc.DisparityFilter;

// C++: class DisparityWLSFilter
//javadoc: DisparityWLSFilter

public class DisparityWLSFilter extends DisparityFilter {

    protected DisparityWLSFilter(long addr) { super(addr); }

    // internal usage only
    public static DisparityWLSFilter __fromPtr__(long addr) { return new DisparityWLSFilter(addr); }

    //
    // C++:  Mat getConfidenceMap()
    //

    // C++:  Mat getConfidenceMap()
    private static native long getConfidenceMap_0(long nativeObj);


    //
    // C++:  Rect getROI()
    //

    // C++:  Rect getROI()
    private static native double[] getROI_0(long nativeObj);


    //
    // C++:  double getLambda()
    //

    // C++:  double getLambda()
    private static native double getLambda_0(long nativeObj);


    //
    // C++:  double getSigmaColor()
    //

    // C++:  double getSigmaColor()
    private static native double getSigmaColor_0(long nativeObj);


    //
    // C++:  int getDepthDiscontinuityRadius()
    //

    // C++:  int getDepthDiscontinuityRadius()
    private static native int getDepthDiscontinuityRadius_0(long nativeObj);


    //
    // C++:  int getLRCthresh()
    //

    // C++:  int getLRCthresh()
    private static native int getLRCthresh_0(long nativeObj);


    //
    // C++:  void setDepthDiscontinuityRadius(int _disc_radius)
    //

    // C++:  void setDepthDiscontinuityRadius(int _disc_radius)
    private static native void setDepthDiscontinuityRadius_0(long nativeObj, int _disc_radius);


    //
    // C++:  void setLRCthresh(int _LRC_thresh)
    //

    // C++:  void setLRCthresh(int _LRC_thresh)
    private static native void setLRCthresh_0(long nativeObj, int _LRC_thresh);


    //
    // C++:  void setLambda(double _lambda)
    //

    // C++:  void setLambda(double _lambda)
    private static native void setLambda_0(long nativeObj, double _lambda);


    //
    // C++:  void setSigmaColor(double _sigma_color)
    //

    // C++:  void setSigmaColor(double _sigma_color)
    private static native void setSigmaColor_0(long nativeObj, double _sigma_color);

    // native support for java finalize()
    private static native void delete(long nativeObj);

    //javadoc: DisparityWLSFilter::getConfidenceMap()
    public  Mat getConfidenceMap()
    {

        Mat retVal = new Mat(getConfidenceMap_0(nativeObj));

        return retVal;
    }

    //javadoc: DisparityWLSFilter::getROI()
    public  Rect getROI()
    {

        Rect retVal = new Rect(getROI_0(nativeObj));

        return retVal;
    }

    //javadoc: DisparityWLSFilter::getLambda()
    public  double getLambda()
    {

        double retVal = getLambda_0(nativeObj);

        return retVal;
    }

    //javadoc: DisparityWLSFilter::setLambda(_lambda)
    public  void setLambda(double _lambda)
    {

        setLambda_0(nativeObj, _lambda);

        return;
    }

    //javadoc: DisparityWLSFilter::getSigmaColor()
    public  double getSigmaColor()
    {

        double retVal = getSigmaColor_0(nativeObj);

        return retVal;
    }

    //javadoc: DisparityWLSFilter::setSigmaColor(_sigma_color)
    public  void setSigmaColor(double _sigma_color)
    {

        setSigmaColor_0(nativeObj, _sigma_color);

        return;
    }

    //javadoc: DisparityWLSFilter::getDepthDiscontinuityRadius()
    public  int getDepthDiscontinuityRadius()
    {

        int retVal = getDepthDiscontinuityRadius_0(nativeObj);

        return retVal;
    }

    //javadoc: DisparityWLSFilter::setDepthDiscontinuityRadius(_disc_radius)
    public  void setDepthDiscontinuityRadius(int _disc_radius)
    {

        setDepthDiscontinuityRadius_0(nativeObj, _disc_radius);

        return;
    }

    //javadoc: DisparityWLSFilter::getLRCthresh()
    public  int getLRCthresh()
    {

        int retVal = getLRCthresh_0(nativeObj);

        return retVal;
    }

    //javadoc: DisparityWLSFilter::setLRCthresh(_LRC_thresh)
    public  void setLRCthresh(int _LRC_thresh)
    {

        setLRCthresh_0(nativeObj, _LRC_thresh);

        return;
    }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
