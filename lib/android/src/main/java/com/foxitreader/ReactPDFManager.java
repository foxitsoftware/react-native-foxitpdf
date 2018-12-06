package com.foxitreader;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.common.Library;
import com.foxit.uiextensions.utils.UIToast;

public class ReactPDFManager extends ReactContextBaseJavaModule {

    private static final String REACT_CLASS = "PDFManager";

    static {
        System.loadLibrary("rdk");
    }

    private static boolean isLibraryInited = false;
    private int mErrorCode;

    public ReactPDFManager(ReactApplicationContext reactContext) {
        super(reactContext);
        if (isLibraryInited == false) {
            try {
                ApplicationInfo ai = reactContext.getPackageManager().getApplicationInfo(reactContext.getPackageName(),
                        PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                String sn = bundle.getString("foxit_sn");
                String key = bundle.getString("foxit_key");

                mErrorCode= Library.initialize(sn, key);
                isLibraryInited = true;
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
        ReactApplicationContext reactContext = this.getReactApplicationContext();
        if (mErrorCode != Constants.e_ErrSuccess) {
            String errorMsg = (mErrorCode == Constants.e_ErrInvalidLicense) ? "The license is invalid!" : "Failed to initialize the library!";
            Toast.makeText(reactContext, errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent pdf = new Intent(reactContext, PDFReaderActivity.class);
        pdf.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.i("FoxitPDF", "src:" + src);
        pdf.putExtra("src", src);
        reactContext.startActivity(pdf);
    }
}
