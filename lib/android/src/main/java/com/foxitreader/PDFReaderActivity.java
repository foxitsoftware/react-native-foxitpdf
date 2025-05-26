/**
 * Copyright (C) 2003-2025, Foxit Software Inc..
 * All Rights Reserved.
 * <p>
 * http://www.foxitsoftware.com
 * <p>
 * The following code is copyrighted and is the proprietary of Foxit Software Inc.. It is not allowed to
 * distribute any parts of Foxit PDF SDK to third party or public without permission unless an agreement
 * is signed between Foxit Software Inc. and customers to explicitly grant customers permissions.
 * Review legal.txt for additional license and legal information.
 */
package com.foxitreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.foxit.sdk.PDFViewCtrl;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.config.Config;
import com.foxit.uiextensions.controls.menu.IMenuGroup;
import com.foxit.uiextensions.controls.menu.IMenuView;
import com.foxit.uiextensions.controls.menu.SubgroupMenuItemImpl;
import com.foxit.uiextensions.controls.panel.PanelSpec;
import com.foxit.uiextensions.controls.propertybar.IViewSettingsWindow;
import com.foxit.uiextensions.controls.toolbar.BaseBar;
import com.foxit.uiextensions.controls.toolbar.IBarsHandler;
import com.foxit.uiextensions.controls.toolbar.ToolbarItemConfig;
import com.foxit.uiextensions.modules.more.MoreMenuConstants;
import com.foxit.uiextensions.theme.ThemeConfig;
import com.foxit.uiextensions.utils.ActManager;
import com.foxit.uiextensions.utils.AppDisplay;
import com.foxit.uiextensions.utils.AppTheme;
import com.foxit.uiextensions.utils.JsonUtil;
import com.foxit.uiextensions.utils.SystemUiHelper;
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
    private static final int REQUEST_ALL_FILES_ACCESS_PERMISSION = 111;
    private static final int REQUEST_EXTERNAL_STORAGE = 222;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PDFViewCtrl pdfViewCtrl = null;
    private UIExtensionsManager uiExtensionsManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppTheme.setThemeNeedMenuKey(this);
        SystemUiHelper.getInstance().setStatusBarColor(getWindow(), ThemeConfig.getInstance(this).getPrimaryColor());
        ActManager.getInstance().setCurrentActivity(this);
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

        if (Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        setContentView(uiExtensionsManager.getContentView());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent, REQUEST_ALL_FILES_ACCESS_PERMISSION);
                return;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                return;
            }
        }
        openDocument();
    }

    private void openDocument() {
        String path = getIntent().getExtras().getString(ReactConstants.KEY_PATH);
        String password = getIntent().getExtras().getString(ReactConstants.KEY_PASSWORD);
        int open_type = getIntent().getIntExtra(ReactConstants.KEY_OPEN_TYPE, ReactConstants.OPEN_FROM_LOCAL);
        byte[] bytes = null;
        if (!TextUtils.isEmpty(password)) {
            bytes = password.getBytes();
        }
        if (open_type == ReactConstants.OPEN_FROM_LOCAL) {
            uiExtensionsManager.openDocument(path, bytes);
        } else {
            pdfViewCtrl.openDocFromUrl(path, bytes, null, null);
        }
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

            if (object.has("toolItems")) {
                JSONObject topBarObject = object.getJSONObject("toolItems");
                IBarsHandler barsHandler = uiExtensionsManager.getBarManager();

                boolean back = JsonUtil.getBoolean(topBarObject, "back", true);
                boolean more = JsonUtil.getBoolean(topBarObject, "more", true);
                boolean bookmark = JsonUtil.getBoolean(topBarObject, "bookmark", true);
                boolean search = JsonUtil.getBoolean(topBarObject, "search", true);
                boolean panel = JsonUtil.getBoolean(topBarObject, "panel", true);
                boolean thumbnail = JsonUtil.getBoolean(topBarObject, "thumbnail", true);
                if (!back)
                    barsHandler.setItemVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_LT,
                            ToolbarItemConfig.ITEM_TOPBAR_BACK, getVisibility(back));
                if (!search)
                    barsHandler.setItemVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                            ToolbarItemConfig.ITEM_TOPBAR_SEARCH, getVisibility(search));
                if (!more)
                    barsHandler.setItemVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                            ToolbarItemConfig.ITEM_TOPBAR_MORE, getVisibility(more));
                if (!bookmark) {
                    if (AppDisplay.isPad())
                        barsHandler.setItemVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                                ToolbarItemConfig.ITEM_TOPBAR_BOOKMARK, getVisibility(bookmark));
                    else
                        barsHandler.setItemVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                                ToolbarItemConfig.ITEM_BOTTOMBAR_BOOKMARK, getVisibility(bookmark));
                }
                if (!panel) {
                    if (AppDisplay.isPad())
                        barsHandler.setItemVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_LT,
                                ToolbarItemConfig.ITEM_TOPBAR_PANEL, getVisibility(panel));
                    else
                        barsHandler.setItemVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                                ToolbarItemConfig.ITEM_BOTTOMBAR_LIST, getVisibility(panel));
                }
                if (!thumbnail) {
                    if (AppDisplay.isPad())
                        barsHandler.setItemVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                                ToolbarItemConfig.ITEM_TOPBAR_THUMBNAIL, getVisibility(panel));
                    else
                        barsHandler.setItemVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                                ToolbarItemConfig.ITEM_BOTTOMBAR_THUMBNAIL, getVisibility(panel));
                }

                boolean home = JsonUtil.getBoolean(topBarObject, "home", true);
                boolean edit = JsonUtil.getBoolean(topBarObject, "edit", true);
                boolean comment = JsonUtil.getBoolean(topBarObject, "comment", true);
                boolean drawing = JsonUtil.getBoolean(topBarObject, "drawing", true);
                boolean view = JsonUtil.getBoolean(topBarObject, "view", true);
                boolean form = JsonUtil.getBoolean(topBarObject, "form", true);
                boolean fillSign = JsonUtil.getBoolean(topBarObject, "fillSign", true);
                boolean protect = JsonUtil.getBoolean(topBarObject, "protect", true);
                if (!home)
                    uiExtensionsManager.getMainFrame().removeTab(ToolbarItemConfig.ITEM_HOME_TAB);
                if (!edit)
                    uiExtensionsManager.getMainFrame().removeTab(ToolbarItemConfig.ITEM_EDIT_TAB);
                if (!comment)
                    uiExtensionsManager.getMainFrame().removeTab(ToolbarItemConfig.ITEM_COMMENT_TAB);
                if (!drawing)
                    uiExtensionsManager.getMainFrame().removeTab(ToolbarItemConfig.ITEM_DRAWING_TAB);
                if (!form)
                    uiExtensionsManager.getMainFrame().removeTab(ToolbarItemConfig.ITEM_FORM_TAB);
                if (!fillSign)
                    uiExtensionsManager.getMainFrame().removeTab(ToolbarItemConfig.ITEM_FILLSIGN_TAB);
                if (!view) {
                    if (AppDisplay.isPad())
                        uiExtensionsManager.getMainFrame().removeTab(ToolbarItemConfig.ITEM_VIEW_TAB);
                    else
                        barsHandler.setItemVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                                ToolbarItemConfig.ITEM_BOTTOMBAR_VIEW, getVisibility(panel));
                }
                if (!protect) {
                    if (AppDisplay.isPad())
                        uiExtensionsManager.getMainFrame().removeTab(ToolbarItemConfig.ITEM_PROTECT_TAB);
                }
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
            uiExtensionsManager.getPanelManager().removePanel(PanelSpec.ANNOTATIONS);
        if (!attachments)
            uiExtensionsManager.getPanelManager().removePanel(PanelSpec.ATTACHMENTS);
        if (!outline)
            uiExtensionsManager.getPanelManager().removePanel(PanelSpec.OUTLINE);
        if (!readingBookmark)
            uiExtensionsManager.getPanelManager().removePanel(PanelSpec.BOOKMARKS);
        if (!signature)
            uiExtensionsManager.getPanelManager().removePanel(PanelSpec.SIGNATURES);
    }

    private void initViewSettingsConfig(JSONObject object) {
        boolean single = JsonUtil.getBoolean(object, "single", true);
        boolean facingPage = JsonUtil.getBoolean(object, "facingPage", true);
        boolean coverPage = JsonUtil.getBoolean(object, "coverPage", true);
        boolean continuous = JsonUtil.getBoolean(object, "continuous", true);
        boolean day = JsonUtil.getBoolean(object, "day", true);
        boolean night = JsonUtil.getBoolean(object, "night", true);
        boolean pageColor = JsonUtil.getBoolean(object, "pageColor", true);
        boolean fitPage = JsonUtil.getBoolean(object, "fitPage", true);
        boolean fitWidth = JsonUtil.getBoolean(object, "fitWidth", true);
        boolean reflow = JsonUtil.getBoolean(object, "reflow", true);
        boolean cropPage = JsonUtil.getBoolean(object, "cropPage", true);
        boolean speak = JsonUtil.getBoolean(object, "speak", true);
        boolean autoFilp = JsonUtil.getBoolean(object, "autoFilp", true);
        boolean rotate = JsonUtil.getBoolean(object, "rotate", true);
        boolean panZoom = JsonUtil.getBoolean(object, "panZoom", true);

        IViewSettingsWindow settingsBar = uiExtensionsManager.getSettingWindow();
        if (!single)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_SINGLE_PAGE, single);
        if (!facingPage)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_FACING_PAGE, facingPage);
        if (!coverPage)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_COVER_PAGE, coverPage);
        if (!continuous)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_CONTINUOUS_PAGE, continuous);
        if (!day)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_DAY, day);
        if (!night)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_NIGHT, night);
        if (!pageColor)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_PAGE_COLOR, pageColor);
        if (!fitPage)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_FIT_PAGE, fitPage);
        if (!fitWidth)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_FIT_WIDTH, fitWidth);
        if (!reflow)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_REFLOW, reflow);
        if (!cropPage)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_CROP, cropPage);
        if (!speak)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_TTS, speak);
        if (!autoFilp)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_AUTO_FLIP, autoFilp);
        if (!rotate)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_ROTATE_VIEW, rotate);
        if (!panZoom)
            settingsBar.setVisible(IViewSettingsWindow.TYPE_PAN_ZOOM, panZoom);
    }

    private void initViewMoreConfig(JSONObject object) {
        IMenuView menuView = uiExtensionsManager.getMenuView();
        try {
            boolean fileInfo = JsonUtil.getBoolean(object, "fileInfo", true);
            if (!fileInfo)
                menuView.setTitleBarVisible(fileInfo);

            if (object.has("protect")) {
                JSONObject protect = object.getJSONObject("protect");
                boolean redaction = JsonUtil.getBoolean(protect, "redaction", true);
                boolean encryption = JsonUtil.getBoolean(protect, "encryption", true);
                boolean trustedCertificates = JsonUtil.getBoolean(protect, "trustedCertificates", true);

                IMenuGroup group = menuView.getGroup(MoreMenuConstants.GROUP_ACTION_MENU_PRIMARY);
                SubgroupMenuItemImpl subGroup = (SubgroupMenuItemImpl) group.getItem(MoreMenuConstants.ITEM_PRIMARY_PROTECT);
                if (!redaction)
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_PROTECT_REDACTION);
                if (!encryption) {
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_PROTECT_REMOVE_PASSWORD);
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_PROTECT_FILE_ENCRYPTION);
                }
                if (!trustedCertificates)
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_PROTECT_TRUSTED_CERTIFICATES);
            } else {
                IMenuGroup group = menuView.getGroup(MoreMenuConstants.GROUP_ACTION_MENU_PRIMARY);
                group.getItem(MoreMenuConstants.ITEM_PRIMARY_PROTECT).setVisible(false);
            }

            if (object.has("commentsAndFields")) {
                JSONObject comments = object.getJSONObject("commentsAndFields");
                boolean importComment = JsonUtil.getBoolean(comments, "importComment", true);
                boolean exportComment = JsonUtil.getBoolean(comments, "exportComment", true);
                boolean summarizeComment = JsonUtil.getBoolean(comments, "summarizeComment", true);
                boolean resetForm = JsonUtil.getBoolean(comments, "resetForm", true);
                boolean importForm = JsonUtil.getBoolean(comments, "importForm", true);
                boolean exportForm = JsonUtil.getBoolean(comments, "exportForm", true);
                IMenuGroup group = menuView.getGroup(MoreMenuConstants.GROUP_ACTION_MENU_PRIMARY);
                SubgroupMenuItemImpl subGroup = (SubgroupMenuItemImpl) group.getItem(MoreMenuConstants.ITEM_PRIMARY_COMMENT_FIELDS);
                if (!importComment)
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_COMMENTS_FIELDS_IMPORT_COMMENTS);
                if (!exportComment)
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_COMMENTS_FIELDS_EXPORT_COMMENTS);
                if (!summarizeComment)
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_COMMENTS_FIELDS_SUMMARIZE_COMMENTS);
                if (!resetForm)
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_COMMENTS_FIELDS_RESET_FORM_FIELDS);
                if (!importForm)
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_COMMENTS_FIELDS_IMPORT_FORM_DATA);
                if (!exportForm)
                    subGroup.removeSubItem(MoreMenuConstants.ITEM_COMMENTS_FIELDS_EXPORT_FORM_DATA);
            } else {
                IMenuGroup group = menuView.getGroup(MoreMenuConstants.GROUP_ACTION_MENU_PRIMARY);
                group.getItem(MoreMenuConstants.ITEM_PRIMARY_COMMENT_FIELDS).setVisible(false);
            }

            boolean saveAs = JsonUtil.getBoolean(object, "saveAs", true);
            boolean reduceFileSize = JsonUtil.getBoolean(object, "reduceFileSize", true);
            boolean print = JsonUtil.getBoolean(object, "print", true);
            boolean flatten = JsonUtil.getBoolean(object, "flatten", true);
            boolean crop = JsonUtil.getBoolean(object, "crop", true);
            IMenuGroup group = menuView.getGroup(MoreMenuConstants.GROUP_ACTION_MENU_SECONDARY);
            if (!saveAs)
                group.removeItem(MoreMenuConstants.ITEM_SECONDARY_SAVE_AS);
            if (!reduceFileSize)
                group.removeItem(MoreMenuConstants.ITEM_SECONDARY_REDUCE_FILE_SIZE);
            if (!print)
                group.removeItem(MoreMenuConstants.ITEM_SECONDARY_PRINT);
            if (!flatten)
                group.removeItem(MoreMenuConstants.ITEM_SECONDARY_FLATTEN);
            if (!crop)
                group.removeItem(MoreMenuConstants.ITEM_SECONDARY_SCREEN);
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
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//                UIToast.getInstance(getApplicationContext()).show(getString(R.string.fx_permission_denied));
                finish();
            }
        } else {
            if (uiExtensionsManager != null) {
                uiExtensionsManager.handleRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALL_FILES_ACCESS_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    openDocument();
                }
            }
        } else {
            uiExtensionsManager.handleActivityResult(this, requestCode, resultCode, data);
        }
    }

}
