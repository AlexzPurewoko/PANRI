//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.text;



// C++: class Callback
//javadoc: Callback

public class Callback {

    protected final long nativeObj;
    protected Callback(long addr) { nativeObj = addr; }

    // internal usage only
    public static Callback __fromPtr__(long addr) { return new Callback(addr); }

    // native support for java finalize()
    private static native void delete(long nativeObj);

    public long getNativeObjAddr() { return nativeObj; }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
