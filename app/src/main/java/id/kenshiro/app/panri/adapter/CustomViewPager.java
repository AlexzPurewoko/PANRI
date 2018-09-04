package id.kenshiro.app.panri.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
    private Context context;
    OnClickListener listener;
    public CustomViewPager(@NonNull Context context) {
        super(context);
        this.context=context;

    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_UP){
            if(listener != null)
                listener.onClick(this);
            return true;
        }
      return super.onTouchEvent(ev);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        listener = l;
    }
}

