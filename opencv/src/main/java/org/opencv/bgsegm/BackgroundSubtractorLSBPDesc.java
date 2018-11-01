//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.bgsegm;



// C++: class BackgroundSubtractorLSBPDesc
//javadoc: BackgroundSubtractorLSBPDesc

public class BackgroundSubtractorLSBPDesc {

    protected final long nativeObj;
    protected BackgroundSubtractorLSBPDesc(long addr) { nativeObj = addr; }

    // internal usage only
    public static BackgroundSubtractorLSBPDesc __fromPtr__(long addr) { return new BackgroundSubtractorLSBPDesc(addr); }

    // native support for java finalize()
    private static native void delete(long nativeObj);

    public long getNativeObjAddr() { return nativeObj; }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
