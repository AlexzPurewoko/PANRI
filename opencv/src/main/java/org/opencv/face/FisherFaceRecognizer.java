//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.face;

import org.opencv.face.BasicFaceRecognizer;
import org.opencv.face.FisherFaceRecognizer;

// C++: class FisherFaceRecognizer
//javadoc: FisherFaceRecognizer

public class FisherFaceRecognizer extends BasicFaceRecognizer {

    protected FisherFaceRecognizer(long addr) { super(addr); }

    // internal usage only
    public static FisherFaceRecognizer __fromPtr__(long addr) { return new FisherFaceRecognizer(addr); }

    //
    // C++: static Ptr_FisherFaceRecognizer create(int num_components = 0, double threshold = DBL_MAX)
    //

    //javadoc: FisherFaceRecognizer::create(num_components, threshold)
    public static FisherFaceRecognizer create(int num_components, double threshold)
    {
        
        FisherFaceRecognizer retVal = FisherFaceRecognizer.__fromPtr__(create_0(num_components, threshold));
        
        return retVal;
    }

    //javadoc: FisherFaceRecognizer::create()
    public static FisherFaceRecognizer create()
    {
        
        FisherFaceRecognizer retVal = FisherFaceRecognizer.__fromPtr__(create_1());
        
        return retVal;
    }

    // C++: static Ptr_FisherFaceRecognizer create(int num_components = 0, double threshold = DBL_MAX)
    private static native long create_0(int num_components, double threshold);

    private static native long create_1();

    // native support for java finalize()
    private static native void delete(long nativeObj);

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
