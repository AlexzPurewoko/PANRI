//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.face;

import org.opencv.face.Facemark;

// C++: class FacemarkLBF
//javadoc: FacemarkLBF

public class FacemarkLBF extends Facemark {

    protected FacemarkLBF(long addr) { super(addr); }

    // internal usage only
    public static FacemarkLBF __fromPtr__(long addr) { return new FacemarkLBF(addr); }

    // native support for java finalize()
    private static native void delete(long nativeObj);

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
