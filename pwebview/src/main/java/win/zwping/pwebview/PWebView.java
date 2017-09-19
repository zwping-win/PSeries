package win.zwping.pwebview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * <p>describe：
 * <p>    note：<uses-permission android:name="android.permission.INTERNET" />
 * <p>  author：zwp on 2017/9/13 0013 mail：1101558280@qq.com web: http://www.zwping.win </p>
 */
/*
webSettings
webViewClient  （处理通知&请求事件）
WebChromeClient  （处理jsDialog、webSiteIcon、webSiteTitle、loading）
js互调、addJavaScriptInterface
web stackOverflow
**/
//http://www.jianshu.com/p/3c94ae673e2a
public class PWebView extends WebView {

    public PWebView(Context context) {
        super(context);
    }

    public PWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    /**
     * 加载默认webSetting
     */
    protected void loadDefaultWebSetting() {
        WebSettings webSettings = this.getSettings();
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        //webSettings.setJavaScriptEnabled(true);
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可

        //支持插件
        //webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
    }

    /**
     * //销毁WebView
     * //在关闭了Activity时，如果WebView的音乐或视频，还在播放。就必须销毁WebView
     * //但是注意：webView调用desTory时,webView仍绑定在Activity上
     * //这是由于自定义webView构建时传入了该Activity的context对象
     * //因此需要先从父容器中移除webView,然后再销毁webView:
     *
     * @param parentLayout
     * @param webView
     */
    public static void removeWebView(ViewGroup parentLayout, WebView webView) {
        if (null != parentLayout && null != webView) {
            parentLayout.removeView(webView);
            webView.destroy();
        }
    }

    /**
     * @param webView
     * @param clearCache//清除网页访问留下的缓存          //由于内核缓存是全局的因此这个方法不仅仅针对webView而是针对整个应用程序.
     * @param clearHistory//清除当前webView访问的历史记录 //只会清除webView访问历史记录里的所有记录除了当前访问记录
     * @param clearFormData                    //这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
     */
    public static void clear(WebView webView, boolean clearCache, boolean clearHistory, boolean clearFormData) {
        if (clearCache) {
            if (null != webView) webView.clearCache(true);
            return;
        }
        if (clearHistory) if (null != webView) webView.clearHistory();
        if (clearFormData) if (null != webView) webView.clearFormData();
    }
}
