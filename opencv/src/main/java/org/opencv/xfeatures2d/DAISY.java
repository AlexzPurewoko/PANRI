//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.xfeatures2d;

import org.opencv.core.Mat;
import org.opencv.features2d.Feature2D;
import org.opencv.xfeatures2d.DAISY;

// C++: class DAISY
//javadoc: DAISY

public class DAISY extends Feature2D {

    public static final int
            NRM_NONE = 100,
            NRM_PARTIAL = 101,
            NRM_FULL = 102,
            NRM_SIFT = 103;

    protected DAISY(long addr) { super(addr); }

    // internal usage only
    public static DAISY __fromPtr__(long addr) { return new DAISY(addr); }


    //
    // C++: static Ptr_DAISY create(float radius = 15, int q_radius = 3, int q_theta = 8, int q_hist = 8, int norm = DAISY::NRM_NONE, Mat H = Mat(), bool interpolation = true, bool use_orientation = false)
    //

    //javadoc: DAISY::create(radius, q_radius, q_theta, q_hist, norm, H, interpolation, use_orientation)
    public static DAISY create(float radius, int q_radius, int q_theta, int q_hist, int norm, Mat H, boolean interpolation, boolean use_orientation)
    {
        
        DAISY retVal = DAISY.__fromPtr__(create_0(radius, q_radius, q_theta, q_hist, norm, H.nativeObj, interpolation, use_orientation));
        
        return retVal;
    }

    //javadoc: DAISY::create()
    public static DAISY create()
    {
        
        DAISY retVal = DAISY.__fromPtr__(create_1());
        
        return retVal;
    }

    // C++: static Ptr_DAISY create(float radius = 15, int q_radius = 3, int q_theta = 8, int q_hist = 8, int norm = DAISY::NRM_NONE, Mat H = Mat(), bool interpolation = true, bool use_orientation = false)
    private static native long create_0(float radius, int q_radius, int q_theta, int q_hist, int norm, long H_nativeObj, boolean interpolation, boolean use_orientation);

    private static native long create_1();

    // native support for java finalize()
    private static native void delete(long nativeObj);

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
