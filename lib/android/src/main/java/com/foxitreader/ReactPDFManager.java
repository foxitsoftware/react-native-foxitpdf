package com.foxitreader;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.foxit.sdk.common.Library;
import com.foxit.sdk.common.PDFError;
import com.foxit.sdk.common.PDFException;

public class ReactPDFManager extends ReactContextBaseJavaModule {

    private static final String REACT_CLASS = "PDFManager";

    static {
        System.loadLibrary("rdk");
    }

    private static boolean isLibraryInited = false;

    public ReactPDFManager(ReactApplicationContext reactContext) {
        super(reactContext);
        if (isLibraryInited == false) {
            try {
                ApplicationInfo ai = reactContext.getPackageManager().getApplicationInfo(reactContext.getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                String sn = bundle.getString("foxit_sn");
                String key = bundle.getString("foxit_key");
                try {
                    Library.init(sn, key);
                    Log.d("FoxitPDF", "Init Success");
                    isLibraryInited = true;
                } catch (PDFException e) {
                    Log.d("FoxitPDF", "Init fail");
                    if (e.getLastError() == PDFError.LICENSE_INVALID.getCode()) {
                        //UIToast.getInstance(getApplicationContext()).show("The license is invalid!");
                    } else {
                        //UIToast.getInstance(getApplicationContext()).show("Failed to initialize the library!");
                    }
                    return;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("FoxitPDF", "Failed to load meta-data, NameNotFound: " + e.getMessage());
            } catch (NullPointerException e) {
                Log.e("FoxitPDF", "Failed to load meta-data, NullPointer: " + e.getMessage());
            }
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void openPDF(String src, String password, ReadableMap extensionConfig, Boolean enableTopToolbar,
            Boolean enableBottomToolbar, ReadableMap topToolbarConfig, ReadableMap bottomToolbarConfig,
            ReadableMap panelConfig, ReadableMap viewSettingsConfig, ReadableMap viewMoreConfig) {
        ReactApplicationContext mThemedReactContext = this.getReactApplicationContext();
        Intent pdf = new Intent(mThemedReactContext, PDFReaderActivity.class);
        pdf.putExtra("src", src);
        mThemedReactContext.startActivity(pdf);
    }
}
