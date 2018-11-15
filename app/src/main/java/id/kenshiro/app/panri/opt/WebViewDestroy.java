package id.kenshiro.app.panri.opt;

import android.view.ViewGroup;
import android.webkit.WebView;

public class WebViewDestroy {
    public static void destroyWebView(ViewGroup parentOfWebVIew, WebView content) {
        // Make sure you remove the WebView from its parent view before doing anything.
        if (parentOfWebVIew != null) parentOfWebVIew.removeView(content);

        content.clearHistory();

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        content.clearCache(true);

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        content.loadUrl("about:blank");

        content.onPause();
        content.removeAllViews();
        content.destroyDrawingCache();

        // NOTE: This pauses JavaScript execution for ALL WebViews,
        // do not use if you have other WebViews still alive.
        // If you create another WebView after calling this,
        // make sure to call mWebView.resumeTimers().
        content.pauseTimers();

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        content.destroy();

        // Null out the reference so that you don't end up re-using it.
        content = null;
    }
}
