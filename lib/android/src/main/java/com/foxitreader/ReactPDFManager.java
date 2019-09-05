package com.foxitreader;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.foxit.sdk.common.Constants;
import com.foxit.sdk.common.Library;
import com.foxit.uiextensions.config.Config;
import com.foxitreader.config.BottomToolbarConfig;
import com.foxitreader.config.PanelConfig;
import com.foxitreader.config.TopToolbarConfig;
import com.foxitreader.config.ViewMoreConfig;
import com.foxitreader.config.ViewSettingsConfig;

public class ReactPDFManager extends ReactContextBaseJavaModule {

    private static final String REACT_CLASS = "PDFManager";

    private static boolean isLibraryInited = false;
    private static int mErrorCode = Constants.e_ErrInvalidLicense;
    private static String mLastSn;
    private static String mLastKey;

    public ReactPDFManager(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void initialize(String foxit_sn, String foxit_key) {
        try {
            if (isLibraryInited == false) {
                mErrorCode = Library.initialize(foxit_sn, foxit_key);
                isLibraryInited = true;
            } else if (!mLastSn.equals(foxit_sn) || !mLastKey.equals(foxit_key)) {
                Library.release();
                mErrorCode = Library.initialize(foxit_sn, foxit_key);
            }
            mLastSn = foxit_sn;
            mLastKey = foxit_key;
        } catch (Exception e) {
            Log.e("FoxitPDF", "Initialization library failed： " + e.getMessage());
        }
    }

    @ReactMethod
    public void openPDF(String src, String password, ReadableMap extensionConfig, Boolean enableTopToolbar,
                        Boolean enableBottomToolbar, ReadableMap topToolbarConfig, ReadableMap bottomToolbarConfig,
                        ReadableMap panelConfig, ReadableMap viewSettingsConfig, ReadableMap viewMoreConfig) {
        openDoc(src, password, extensionConfig, enableTopToolbar, enableBottomToolbar, topToolbarConfig,
                bottomToolbarConfig, panelConfig, viewSettingsConfig, viewMoreConfig);
    }

    @ReactMethod
    public void openDocument(String path, String password, ReadableMap extensionConfig, Boolean enableTopToolbar,
                             Boolean enableBottomToolbar, ReadableMap topToolbarConfig, ReadableMap bottomToolbarConfig,
                             ReadableMap panelConfig, ReadableMap viewSettingsConfig, ReadableMap viewMoreConfig) {
        openDoc(path, password, extensionConfig, enableTopToolbar, enableBottomToolbar, topToolbarConfig,
                bottomToolbarConfig, panelConfig, viewSettingsConfig, viewMoreConfig);
    }

    private void openDoc(String path, String password, ReadableMap extensionConfig, Boolean enableTopToolbar,
                         Boolean enableBottomToolbar, ReadableMap topToolbarConfig, ReadableMap bottomToolbarConfig,
                         ReadableMap panelConfig, ReadableMap viewSettingsConfig, ReadableMap viewMoreConfig) {
        ReactApplicationContext reactContext = this.getReactApplicationContext();
        if (mErrorCode != Constants.e_ErrSuccess) {
            String errorMsg = (mErrorCode == Constants.e_ErrInvalidLicense) ? "The license is invalid!" : "Failed to initialize the library!";
            Toast.makeText(reactContext, errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(path)) {
            String errorMsg = "File path cannot be empty!";
            Toast.makeText(reactContext, errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(reactContext, PDFReaderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("path", path);
        intent.putExtra("password", password);
        if (enableTopToolbar != null)
            intent.putExtra("enableTopToolbar", enableTopToolbar.booleanValue());
        if (enableTopToolbar != null)
            intent.putExtra("enableBottomToolbar", enableBottomToolbar.booleanValue());

        if (null != extensionConfig) {
            intent.putExtra(Config.class.getName(), extensionConfig.toString());
        }
        intent.putExtra(TopToolbarConfig.TAG, getTopBarConfig(topToolbarConfig));
        intent.putExtra(BottomToolbarConfig.TAG, getBottomBarConfig(bottomToolbarConfig));
        intent.putExtra(PanelConfig.TAG, getPanelConfig(panelConfig));
        intent.putExtra(ViewSettingsConfig.TAG, getViewSettingsConfig(viewSettingsConfig));
        intent.putExtra(ViewMoreConfig.TAG, getViewMoreConfig(viewMoreConfig));
        reactContext.startActivity(intent);
    }

    private TopToolbarConfig getTopBarConfig(ReadableMap topToolbarConfig) {
        TopToolbarConfig config = null;
        if (null != topToolbarConfig) {
            config = new TopToolbarConfig();
            config.more = getBoolean(topToolbarConfig, "more", true);
            config.back = getBoolean(topToolbarConfig, "back", true);
            config.bookmark = getBoolean(topToolbarConfig, "bookmark", true);
            config.search = getBoolean(topToolbarConfig, "search", true);
        }
        return config;
    }

    private BottomToolbarConfig getBottomBarConfig(ReadableMap bottomToolbarConfig) {
        BottomToolbarConfig config = null;
        if (null != bottomToolbarConfig) {
            config = new BottomToolbarConfig();
            config.annot = getBoolean(bottomToolbarConfig, "annot", true);
            config.panel = getBoolean(bottomToolbarConfig, "panel", true);
            config.readmore = getBoolean(bottomToolbarConfig, "readmore", true);
            config.signature = getBoolean(bottomToolbarConfig, "signature", true);
        }
        return config;
    }

    private PanelConfig getPanelConfig(ReadableMap panelConfig) {
        PanelConfig config = null;
        if (null != panelConfig) {
            config = new PanelConfig();
            config.readingBookmark = getBoolean(panelConfig, "readingBookmark", true);
            config.outline = getBoolean(panelConfig, "outline", true);
            config.annotation = getBoolean(panelConfig, "annotation", true);
            config.attachments = getBoolean(panelConfig, "attachments", true);
            config.signature = getBoolean(panelConfig, "signature", true);
        }
        return config;
    }

    private ViewSettingsConfig getViewSettingsConfig(ReadableMap viewSettingsConfig) {
        ViewSettingsConfig config = null;
        if (null != viewSettingsConfig) {
            config = new ViewSettingsConfig();
            config.single = getBoolean(viewSettingsConfig, "single", true);
            config.continuous = getBoolean(viewSettingsConfig, "continuous", true);
            config.facingPage = getBoolean(viewSettingsConfig, "facingPage", true);
            config.coverPage = getBoolean(viewSettingsConfig, "coverPage", true);
            config.thumbnail = getBoolean(viewSettingsConfig, "thumbnail", true);
            config.reflow = getBoolean(viewSettingsConfig, "reflow", true);
            config.cropPage = getBoolean(viewSettingsConfig, "cropPage", true);
            config.screenLock = getBoolean(viewSettingsConfig, "screenLock", true);
            config.brightness = getBoolean(viewSettingsConfig, "brightness", true);
            config.nightMode = getBoolean(viewSettingsConfig, "nightMode", true);
            config.panZoom = getBoolean(viewSettingsConfig, "panZoom", true);
            config.fitPage = getBoolean(viewSettingsConfig, "fitPage", true);
            config.fitWidth = getBoolean(viewSettingsConfig, "fitWidth", true);
            config.rotate = getBoolean(viewSettingsConfig, "rotate", true);
        }
        return config;
    }

    private ViewMoreConfig getViewMoreConfig(ReadableMap viewMoreConfig) {
        ViewMoreConfig config = null;
        if (null != viewMoreConfig) {
            config = new ViewMoreConfig();

            //file
            ViewMoreConfig.GroupFile groupFile = null;
            if (viewMoreConfig.hasKey("groupFile") && viewMoreConfig.getType("groupFile") == ReadableType.Map) {
                ReadableMap fileMap = viewMoreConfig.getMap("groupFile");
                groupFile = new ViewMoreConfig.GroupFile();
                groupFile.crop = getBoolean(fileMap, "crop", true);
                groupFile.fileInfo = getBoolean(fileMap, "fileInfo", true);
                groupFile.reduceFileSize = getBoolean(fileMap, "reduceFileSize", true);
                groupFile.wirelessPrint = getBoolean(fileMap, "wirelessPrint", true);

                config.groupFile = groupFile;
            }

            //protect
            ViewMoreConfig.GroupProtect groupProtect = null;
            if (viewMoreConfig.hasKey("groupProtect") && viewMoreConfig.getType("groupProtect") == ReadableType.Map) {
                ReadableMap projectMap = viewMoreConfig.getMap("groupProtect");
                groupProtect = new ViewMoreConfig.GroupProtect();
                groupProtect.password = getBoolean(projectMap, "password", true);

                config.groupProtect = groupProtect;
            }

            // comment
            ViewMoreConfig.GroupComment groupComment = null;
            if (viewMoreConfig.hasKey("groupComment") && viewMoreConfig.getType("groupComment") == ReadableType.Map) {
                ReadableMap commentMap = viewMoreConfig.getMap("groupComment");
                groupComment = new ViewMoreConfig.GroupComment();
                groupComment.importComment = getBoolean(commentMap, "importComment", true);
                groupComment.exportComment = getBoolean(commentMap, "exportComment", true);

                config.groupComment = groupComment;
            }

            //form
            ViewMoreConfig.GroupForm groupForm = null;
            if (viewMoreConfig.hasKey("groupForm") && viewMoreConfig.getType("groupForm") == ReadableType.Map) {
                ReadableMap formMap = viewMoreConfig.getMap("groupForm");
                groupForm = new ViewMoreConfig.GroupForm();
                groupForm.createForm = getBoolean(formMap, "createForm", true);
                groupForm.resetForm = getBoolean(formMap, "resetForm", true);
                groupForm.importForm = getBoolean(formMap, "importForm", true);
                groupForm.exportForm = getBoolean(formMap, "exportForm", true);

                config.groupForm = groupForm;
            }
        }
        return config;
    }

    private boolean getBoolean(ReadableMap readableMap, String name, boolean defaultValue) {
        if (readableMap.hasKey(name) && readableMap.getType(name) == ReadableType.Boolean) {
            return readableMap.getBoolean(name);
        }
        return defaultValue;
    }

}
