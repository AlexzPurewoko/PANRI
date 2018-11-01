//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ximgproc;

import org.opencv.ximgproc.SelectiveSearchSegmentationStrategy;

// C++: class SelectiveSearchSegmentationStrategyMultiple
//javadoc: SelectiveSearchSegmentationStrategyMultiple

public class SelectiveSearchSegmentationStrategyMultiple extends SelectiveSearchSegmentationStrategy {

    protected SelectiveSearchSegmentationStrategyMultiple(long addr) { super(addr); }

    // internal usage only
    public static SelectiveSearchSegmentationStrategyMultiple __fromPtr__(long addr) { return new SelectiveSearchSegmentationStrategyMultiple(addr); }

    //
    // C++:  void addStrategy(Ptr_SelectiveSearchSegmentationStrategy g, float weight)
    //

    // C++:  void addStrategy(Ptr_SelectiveSearchSegmentationStrategy g, float weight)
    private static native void addStrategy_0(long nativeObj, long g_nativeObj, float weight);


    //
    // C++:  void clearStrategies()
    //

    // C++:  void clearStrategies()
    private static native void clearStrategies_0(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

    //javadoc: SelectiveSearchSegmentationStrategyMultiple::addStrategy(g, weight)
    public  void addStrategy(SelectiveSearchSegmentationStrategy g, float weight)
    {

        addStrategy_0(nativeObj, g.getNativeObjAddr(), weight);

        return;
    }

    //javadoc: SelectiveSearchSegmentationStrategyMultiple::clearStrategies()
    public  void clearStrategies()
    {

        clearStrategies_0(nativeObj);

        return;
    }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
