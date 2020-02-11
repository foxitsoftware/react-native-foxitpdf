package com.foxitreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.foxit.sdk.PDFViewCtrl;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.config.Config;
import com.foxit.uiextensions.controls.menu.IMenuView;
import com.foxit.uiextensions.controls.menu.MoreMenuConfig;
import com.foxit.uiextensions.controls.panel.PanelSpec;
import com.foxit.uiextensions.controls.propertybar.IMultiLineBar;
import com.foxit.uiextensions.controls.toolbar.BaseBar;
import com.foxit.uiextensions.controls.toolbar.IBarsHandler;
import com.foxit.uiextensions.controls.toolbar.ToolbarItemConfig;
import com.foxit.uiextensions.utils.AppTheme;
import com.foxit.uiextensions.utils.JsonUtil;
import com.foxit.uiextensions.utils.UIToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class PDFReaderActivity extends FragmentActivity {

    private PDFViewCtrl pdfViewCtrl = null;
    private UIExtensionsManager uiExtensionsManager = null;

    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppTheme.setThemeFullScreen(this);
        AppTheme.setThemeNeedMenuKey(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // extensionsConfig
        String extetnisonsConfig = getIntent().getStringExtra(Config.class.getName());
        String ui_config = null;
        InputStream stream;
        if (!TextUtils.isEmpty(extetnisonsConfig)) {
            try {
                JSONObject jsonObject = new JSONObject(extetnisonsConfig);
                if (jsonObject.has("NativeMap") && jsonObject.get("NativeMap") instanceof JSONObject) {
                    ui_config = jsonObject.getJSONObject("NativeMap").toString();
                    stream = new ByteArrayInputStream(ui_config.getBytes("UTF-8"));
                } else {
                    stream = getApplicationContext().getResources().openRawResource(R.raw.uiextensions_config);
                }
            } catch (Exception e) {
                stream = getApplicationContext().getResources().openRawResource(R.raw.uiextensions_config);
            }
        } else {
            stream = getApplicationContext().getResources().openRawResource(R.raw.uiextensions_config);
        }
        pdfViewCtrl = new PDFViewCtrl(this);
        Config config = new Config(stream);
        uiExtensionsManager = new UIExtensionsManager(this.getApplicationContext(), pdfViewCtrl, config);
        uiExtensionsManager.setAttachedActivity(this);
        uiExtensionsManager.onCreate(this, pdfViewCtrl, savedInstanceState);
        pdfViewCtrl.setUIExtensionsManager(uiExtensionsManager);
        pdfViewCtrl.setAttachedActivity(this);

        if (!TextUtils.isEmpty(ui_config)) {
            initConfig(ui_config);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int permission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                openDocument();
            }
        } else {
            openDocument();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        setContentView(uiExtensionsManager.getContentView());
    }

    private void openDocument() {
        Intent intent = getIntent();
        String filePath = intent.getExtras().getString("path");
        String password = intent.getExtras().getString("password");
        byte[] bytes = null;
        if (!TextUtils.isEmpty(password)) {
            bytes = password.getBytes();
        }
        uiExtensionsManager.openDocument(filePath, bytes);
    }

    private void initConfig(String config) {
        try {
            JSONObject object = new JSONObject(config);
            //toolbars
            if (object.has("toolBars") && object.get("toolBars") instanceof JSONObject) {
                initToolBars(object.getJSONObject("toolBars"));
            }
            //panels
            if (object.has("panels") && object.get("panels") instanceof JSONObject) {
                initPanelConfig(object.getJSONObject("panels"));
            }
            //menus
            if (object.has("menus") && object.get("menus") instanceof JSONObject) {
                //moreMenus
                JSONObject menus = object.getJSONObject("menus");
                if (menus.has("moreMenus") && menus.get("moreMenus") instanceof JSONObject) {
                    initViewMoreConfig(menus.getJSONObject("moreMenus"));
                }
                //viewMenus
                if (menus.has("viewMenus") && menus.get("viewMenus") instanceof JSONObject) {
                    initViewSettingsConfig(menus.getJSONObject("viewMenus"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initToolBars(JSONObject object) {
        try {
            boolean enableTopBar = JsonUtil.getBoolean(object, "enableTopToolbar", true);
            uiExtensionsManager.enableTopToolbar(enableTopBar);
            boolean enableBottomBar = JsonUtil.getBoolean(object, "enableBottomToolbar", true);
            uiExtensionsManager.enableBottomToolbar(enableBottomBar);

            if (object.has("topBar")) {
                JSONObject topBarObject = object.getJSONObject("topBar");
                IBarsHandler barsHandler = uiExtensionsManager.getBarManager();

                boolean back = JsonUtil.getBoolean(topBarObject, "back", true);
                boolean more = JsonUtil.getBoolean(topBarObject, "more", true);
                boolean bookmark = JsonUtil.getBoolean(topBarObject, "bookmark", true);
                boolean search = JsonUtil.getBoolean(topBarObject, "search", true);

                if (!back)
                    barsHandler.setVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_LT,
                            ToolbarItemConfig.ITEM_TOPBAR_BACK, getVisibility(back));
                if (!bookmark)
                    barsHandler.setVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                            ToolbarItemConfig.ITEM_TOPBAR_READINGMARK, getVisibility(bookmark));
                if (!search)
                    barsHandler.setVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                            ToolbarItemConfig.ITEM_TOPBAR_SEARCH, getVisibility(search));
                if (!more)
                    barsHandler.setVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                            ToolbarItemConfig.ITEM_TOPBAR_MORE, getVisibility(more));
            }

            if (object.has("bottomBar")) {
                JSONObject bottomBarObject = object.getJSONObject("bottomBar");
                IBarsHandler barsHandler = uiExtensionsManager.getBarManager();

                boolean annot = JsonUtil.getBoolean(bottomBarObject, "annot", true);
                boolean panel = JsonUtil.getBoolean(bottomBarObject, "panel", true);
                boolean readmore = JsonUtil.getBoolean(bottomBarObject, "readmore", true);
                boolean signature = JsonUtil.getBoolean(bottomBarObject, "signature", true);

                if (!annot)
                    barsHandler.setVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                            ToolbarItemConfig.ITEM_BOTTOMBAR_COMMENT, getVisibility(annot));
                if (!panel)
                    barsHandler.setVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                            ToolbarItemConfig.ITEM_BOTTOMBAR_LIST, getVisibility(panel));
                if (!readmore)
                    barsHandler.setVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                            ToolbarItemConfig.ITEM_BOTTOMBAR_VIEW, getVisibility(readmore));
                if (!signature)
                    barsHandler.setVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                            ToolbarItemConfig.ITEM_BOTTOMBAR_SIGN, getVisibility(signature));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initPanelConfig(JSONObject object) {
        boolean annotation = JsonUtil.getBoolean(object, "annotation", true);
        boolean attachments = JsonUtil.getBoolean(object, "attachments", true);
        boolean outline = JsonUtil.getBoolean(object, "outline", true);
        boolean readingBookmark = JsonUtil.getBoolean(object, "readingBookmark", true);
        boolean signature = JsonUtil.getBoolean(object, "signature", true);

        if (!annotation)
            uiExtensionsManager.setPanelHidden(!annotation, PanelSpec.PanelType.Annotations);
        if (!attachments)
            uiExtensionsManager.setPanelHidden(!attachments, PanelSpec.PanelType.Attachments);
        if (!outline)
            uiExtensionsManager.setPanelHidden(!outline, PanelSpec.PanelType.Outline);
        if (!readingBookmark)
            uiExtensionsManager.setPanelHidden(!readingBookmark, PanelSpec.PanelType.ReadingBookmarks);
        if (!signature)
            uiExtensionsManager.setPanelHidden(!signature, PanelSpec.PanelType.Signatures);
    }

    private void initViewSettingsConfig(JSONObject object) {
        boolean single = JsonUtil.getBoolean(object, "single", true);
        boolean continuous = JsonUtil.getBoolean(object, "continuous", true);
        boolean thumbnail = JsonUtil.getBoolean(object, "thumbnail", true);
        boolean brightness = JsonUtil.getBoolean(object, "brightness", true);
        boolean nightMode = JsonUtil.getBoolean(object, "nightMode", true);
        boolean reflow = JsonUtil.getBoolean(object, "reflow", true);
        boolean cropPage = JsonUtil.getBoolean(object, "cropPage", true);
        boolean screenLock = JsonUtil.getBoolean(object, "screenLock", true);
        boolean facingPage = JsonUtil.getBoolean(object, "facingPage", true);
        boolean coverPage = JsonUtil.getBoolean(object, "coverPage", true);
        boolean panZoom = JsonUtil.getBoolean(object, "panZoom", true);
        boolean fitPage = JsonUtil.getBoolean(object, "fitPage", true);
        boolean fitWidth = JsonUtil.getBoolean(object, "fitWidth", true);
        boolean rotate = JsonUtil.getBoolean(object, "rotate", true);

        IMultiLineBar settingsBar = uiExtensionsManager.getSettingBar();
        if (!single)
            settingsBar.setVisibility(IMultiLineBar.TYPE_SINGLEPAGE, getVisibility(single));
        if (!continuous)
            settingsBar.setVisibility(IMultiLineBar.TYPE_CONTINUOUSPAGE, getVisibility(continuous));
        if (!thumbnail)
            settingsBar.setVisibility(IMultiLineBar.TYPE_THUMBNAIL, getVisibility(thumbnail));
        if (!brightness)
            settingsBar.setVisibility(IMultiLineBar.TYPE_SYSLIGHT, getVisibility(brightness));
        if (!nightMode)
            settingsBar.setVisibility(IMultiLineBar.TYPE_DAYNIGHT, getVisibility(nightMode));
        if (!reflow)
            settingsBar.setVisibility(IMultiLineBar.TYPE_REFLOW, getVisibility(reflow));
        if (!cropPage)
            settingsBar.setVisibility(IMultiLineBar.TYPE_CROP, getVisibility(cropPage));
        if (!screenLock)
            settingsBar.setVisibility(IMultiLineBar.TYPE_LOCKSCREEN, getVisibility(screenLock));
        if (!facingPage)
            settingsBar.setVisibility(IMultiLineBar.TYPE_FACINGPAGE, getVisibility(facingPage));
        if (!coverPage)
            settingsBar.setVisibility(IMultiLineBar.TYPE_COVERPAGE, getVisibility(coverPage));
        if (!panZoom)
            settingsBar.setVisibility(IMultiLineBar.TYPE_PANZOOM, getVisibility(panZoom));
        if (!fitPage)
            settingsBar.setVisibility(IMultiLineBar.TYPE_FITPAGE, getVisibility(fitPage));
        if (!fitWidth)
            settingsBar.setVisibility(IMultiLineBar.TYPE_FITWIDTH, getVisibility(fitWidth));
        if (!rotate)
            settingsBar.setVisibility(IMultiLineBar.TYPE_ROTATEVIEW, getVisibility(rotate));
    }

    private void initViewMoreConfig(JSONObject object) {
        IMenuView menuView = uiExtensionsManager.getMenuView();

        try {
            if (object.has("groupFile") && object.get("groupFile") instanceof JSONObject) {
                JSONObject groupFile = object.getJSONObject("groupFile");
                boolean crop = JsonUtil.getBoolean(groupFile, "crop", true);
                boolean fileInfo = JsonUtil.getBoolean(groupFile, "fileInfo", true);
                boolean reduceFileSize = JsonUtil.getBoolean(groupFile, "reduceFileSize", true);
                boolean wirelessPrint = JsonUtil.getBoolean(groupFile, "wirelessPrint", true);

                if (!crop)
                    menuView.setItemVisibility(getVisibility(crop), MoreMenuConfig.GROUP_FILE, MoreMenuConfig.ITEM_SNAPSHOT);
                if (!fileInfo)
                    menuView.setItemVisibility(getVisibility(fileInfo), MoreMenuConfig.GROUP_FILE, MoreMenuConfig.ITEM_DOCINFO);
                if (!reduceFileSize)
                    menuView.setItemVisibility(getVisibility(reduceFileSize), MoreMenuConfig.GROUP_FILE, MoreMenuConfig.ITEM_REDUCE_FILE_SIZE);
                if (!wirelessPrint)
                    menuView.setItemVisibility(getVisibility(wirelessPrint), MoreMenuConfig.GROUP_FILE, MoreMenuConfig.ITEM_PRINT_FILE);

                boolean isHideGroupFile = !crop && !fileInfo && !reduceFileSize && !wirelessPrint;
                if (isHideGroupFile)
                    menuView.setGroupVisibility(View.GONE, MoreMenuConfig.GROUP_FILE);
            }

            if (object.has("groupProtect") && object.get("groupProtect") instanceof JSONObject) {
                JSONObject groupProtect = object.getJSONObject("groupProtect");
                boolean password = JsonUtil.getBoolean(groupProtect, "password", true);
                if (!password) {
                    menuView.setItemVisibility(getVisibility(password), MoreMenuConfig.GROUP_PROTECT, MoreMenuConfig.ITEM_PASSWORD);
                    menuView.setItemVisibility(getVisibility(password), MoreMenuConfig.GROUP_PROTECT, MoreMenuConfig.ITEM_REMOVESECURITY_PASSWORD);
                    menuView.setGroupVisibility(View.GONE, MoreMenuConfig.GROUP_PROTECT);
                }
            }

            if (object.has("groupComment") && object.get("groupComment") instanceof JSONObject) {
                JSONObject groupComment = object.getJSONObject("groupComment");
                boolean importComment = JsonUtil.getBoolean(groupComment, "importComment", true);
                boolean exportComment = JsonUtil.getBoolean(groupComment, "exportComment", true);

                if (!importComment)
                    menuView.setItemVisibility(getVisibility(importComment), MoreMenuConfig.GROUP_ANNOTATION, MoreMenuConfig.ITEM_ANNOTATION_IMPORT);
                if (!exportComment)
                    menuView.setItemVisibility(getVisibility(exportComment), MoreMenuConfig.GROUP_ANNOTATION, MoreMenuConfig.ITEM_ANNOTATION_EXPORT);

                boolean isHideGroupComment = !importComment && !exportComment;
                if (isHideGroupComment)
                    menuView.setGroupVisibility(View.GONE, MoreMenuConfig.GROUP_ANNOTATION);
            }

            if (object.has("groupForm") && object.get("groupForm") instanceof JSONObject) {
                JSONObject groupForm = object.getJSONObject("groupForm");
                boolean createForm = JsonUtil.getBoolean(groupForm, "createForm", true);
                boolean resetForm = JsonUtil.getBoolean(groupForm, "resetForm", true);
                boolean importForm = JsonUtil.getBoolean(groupForm, "importForm", true);
                boolean exportForm = JsonUtil.getBoolean(groupForm, "exportForm", true);

                if (!createForm)
                    menuView.setItemVisibility(getVisibility(createForm), MoreMenuConfig.GROUP_FORM, MoreMenuConfig.ITEM_CREATE_FORM);
                if (!resetForm)
                    menuView.setItemVisibility(getVisibility(resetForm), MoreMenuConfig.GROUP_FORM, MoreMenuConfig.ITEM_RESET_FORM);
                if (!importForm)
                    menuView.setItemVisibility(getVisibility(importForm), MoreMenuConfig.GROUP_FORM, MoreMenuConfig.ITEM_IMPORT_FORM);
                if (!exportForm)
                    menuView.setItemVisibility(getVisibility(exportForm), MoreMenuConfig.GROUP_FORM, MoreMenuConfig.ITEM_EXPORT_FORM);

                boolean isHideGroupFrom = !createForm && !resetForm && !importForm && !exportForm;
                if (isHideGroupFrom)
                    menuView.setGroupVisibility(View.GONE, MoreMenuConfig.GROUP_FORM);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getVisibility(boolean isVisible) {
        return isVisible ? View.VISIBLE : View.GONE;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openDocument();
            } else {
                UIToast.getInstance(getApplicationContext()).show(getString(R.string.fx_permission_denied));
                finish();
            }
        }
    }

    @Override
    public void onStart() {
        if (uiExtensionsManager != null) {
            uiExtensionsManager.onStart(this);
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if (uiExtensionsManager != null) {
            uiExtensionsManager.onStop(this);
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if (uiExtensionsManager != null) {
            uiExtensionsManager.onPause(this);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (uiExtensionsManager != null) {
            uiExtensionsManager.onResume(this);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (uiExtensionsManager != null) {
            uiExtensionsManager.onDestroy(this);
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (uiExtensionsManager != null) {
            uiExtensionsManager.onConfigurationChanged(this, newConfig);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (uiExtensionsManager != null && uiExtensionsManager.onKeyDown(this, keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiExtensionsManager.handleActivityResult(this, requestCode, resultCode, data);
    }

}
