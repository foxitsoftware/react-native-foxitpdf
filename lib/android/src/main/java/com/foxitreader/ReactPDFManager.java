package com.foxitreader;

import android.support.annotation.Nullable;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

public class ReactPDFManager extends SimpleViewManager<ReactPDFView> {

    public static final String REACT_CLASS = "RNTPDF";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ReactPDFView createViewInstance(ThemedReactContext reactContext) {
        return new ReactPDFView(reactContext);
    }

    @ReactProp(name = "src")
    public void setSource(final ReactPDFView view,@Nullable String src) {
        view.setSource(src);
    }

    @ReactProp(name = "password")
    public void setPassword(final ReactPDFView view,@Nullable String param) {
        view.setPassword(param);
    }

    @ReactProp(name = "extensionConfig")
    public void setExtensionConfig(final ReactPDFView view,@Nullable ReadableMap param) {
        view.setExtensionConfig(param);
    }
}
