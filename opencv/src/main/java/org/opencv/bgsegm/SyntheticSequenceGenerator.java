//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.bgsegm;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;

// C++: class SyntheticSequenceGenerator
//javadoc: SyntheticSequenceGenerator

public class SyntheticSequenceGenerator extends Algorithm {

    protected SyntheticSequenceGenerator(long addr) { super(addr); }

    //javadoc: SyntheticSequenceGenerator::SyntheticSequenceGenerator(background, object, amplitude, wavelength, wavespeed, objspeed)
    public   SyntheticSequenceGenerator(Mat background, Mat object, double amplitude, double wavelength, double wavespeed, double objspeed)
    {

        super( SyntheticSequenceGenerator_0(background.nativeObj, object.nativeObj, amplitude, wavelength, wavespeed, objspeed) );

        return;
    }

    //
    // C++:   SyntheticSequenceGenerator(Mat background, Mat object, double amplitude, double wavelength, double wavespeed, double objspeed)
    //

    // internal usage only
    public static SyntheticSequenceGenerator __fromPtr__(long addr) { return new SyntheticSequenceGenerator(addr); }


    //
    // C++:  void getNextFrame(Mat& frame, Mat& gtMask)
    //

    // C++:   SyntheticSequenceGenerator(Mat background, Mat object, double amplitude, double wavelength, double wavespeed, double objspeed)
    private static native long SyntheticSequenceGenerator_0(long background_nativeObj, long object_nativeObj, double amplitude, double wavelength, double wavespeed, double objspeed);

    // C++:  void getNextFrame(Mat& frame, Mat& gtMask)
    private static native void getNextFrame_0(long nativeObj, long frame_nativeObj, long gtMask_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

    //javadoc: SyntheticSequenceGenerator::getNextFrame(frame, gtMask)
    public  void getNextFrame(Mat frame, Mat gtMask)
    {

        getNextFrame_0(nativeObj, frame.nativeObj, gtMask.nativeObj);

        return;
    }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
