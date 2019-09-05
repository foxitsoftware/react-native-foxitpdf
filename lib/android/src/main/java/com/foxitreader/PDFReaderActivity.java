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
import com.foxit.uiextensions.modules.connectpdf.account.AccountModule;
import com.foxit.uiextensions.utils.AppTheme;
import com.foxit.uiextensions.utils.UIToast;
import com.foxitreader.config.BottomToolbarConfig;
import com.foxitreader.config.PanelConfig;
import com.foxitreader.config.TopToolbarConfig;
import com.foxitreader.config.ViewMoreConfig;
import com.foxitreader.config.ViewSettingsConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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
        InputStream stream;
        if (!TextUtils.isEmpty(extetnisonsConfig)) {
            try {
                stream = new ByteArrayInputStream(extetnisonsConfig.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
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
        AccountModule.getInstance().onCreate(this, savedInstanceState);

        initTopBarConfig();
        initBottomBarConfig();
        initPanelConfig();
        initViewMoreConfig();
        initViewSettingsConfig();

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

    private void initTopBarConfig() {
        boolean enableTopBar = getIntent().getBooleanExtra("enableTopToolbar", true);
        uiExtensionsManager.enableTopToolbar(enableTopBar);

        TopToolbarConfig config = (TopToolbarConfig) getIntent().getSerializableExtra(TopToolbarConfig.TAG);
        if (null != config) {
            IBarsHandler barsHandler = uiExtensionsManager.getBarManager();

            if (!config.back)
                barsHandler.setVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_LT,
                        ToolbarItemConfig.ITEM_TOPBAR_BACK, getVisibility(config.back));
            if (!config.bookmark)
                barsHandler.setVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                        ToolbarItemConfig.ITEM_TOPBAR_READINGMARK, getVisibility(config.bookmark));
            if (!config.search)
                barsHandler.setVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                        ToolbarItemConfig.ITEM_TOPBAR_SEARCH, getVisibility(config.search));
            if (!config.more)
                barsHandler.setVisibility(IBarsHandler.BarName.TOP_BAR, BaseBar.TB_Position.Position_RB,
                        ToolbarItemConfig.ITEM_TOPBAR_MORE, getVisibility(config.more));
        }
    }

    private void initBottomBarConfig() {
        boolean enableBottomBar = getIntent().getBooleanExtra("enableBottomToolbar", true);
        uiExtensionsManager.enableBottomToolbar(enableBottomBar);

        BottomToolbarConfig config = (BottomToolbarConfig) getIntent().getSerializableExtra(BottomToolbarConfig.TAG);
        if (null != config) {
            IBarsHandler barsHandler = uiExtensionsManager.getBarManager();

            if (!config.annot)
                barsHandler.setVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                        ToolbarItemConfig.ITEM_BOTTOMBAR_COMMENT, getVisibility(config.annot));
            if (!config.panel)
                barsHandler.setVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                        ToolbarItemConfig.ITEM_BOTTOMBAR_LIST, getVisibility(config.panel));
            if (!config.readmore)
                barsHandler.setVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                        ToolbarItemConfig.ITEM_BOTTOMBAR_VIEW, getVisibility(config.readmore));
            if (!config.signature)
                barsHandler.setVisibility(IBarsHandler.BarName.BOTTOM_BAR, BaseBar.TB_Position.Position_CENTER,
                        ToolbarItemConfig.ITEM_BOTTOMBAR_SIGN, getVisibility(config.signature));
        }
    }

    private void initPanelConfig() {
        PanelConfig config = (PanelConfig) getIntent().getSerializableExtra(PanelConfig.TAG);
        if (null != config) {
            if (!config.annotation)
                uiExtensionsManager.setPanelHidden(!config.annotation, PanelSpec.PanelType.Annotations);
            if (!config.attachments)
                uiExtensionsManager.setPanelHidden(!config.attachments, PanelSpec.PanelType.Attachments);
            if (!config.outline)
                uiExtensionsManager.setPanelHidden(!config.outline, PanelSpec.PanelType.Outline);
            if (!config.readingBookmark)
                uiExtensionsManager.setPanelHidden(!config.readingBookmark, PanelSpec.PanelType.ReadingBookmarks);
            if (!config.signature)
                uiExtensionsManager.setPanelHidden(!config.signature, PanelSpec.PanelType.Signatures);
        }
    }

    private void initViewSettingsConfig() {
        ViewSettingsConfig config = (ViewSettingsConfig) getIntent().getSerializableExtra(ViewSettingsConfig.TAG);
        if (null != config) {
            IMultiLineBar settingsBar = uiExtensionsManager.getSettingBar();

            if (!config.single)
                settingsBar.setVisibility(IMultiLineBar.TYPE_SINGLEPAGE, getVisibility(config.single));
            if (!config.continuous)
                settingsBar.setVisibility(IMultiLineBar.TYPE_CONTINUOUSPAGE, getVisibility(config.continuous));
            if (!config.thumbnail)
                settingsBar.setVisibility(IMultiLineBar.TYPE_THUMBNAIL, getVisibility(config.thumbnail));
            if (!config.brightness)
                settingsBar.setVisibility(IMultiLineBar.TYPE_SYSLIGHT, getVisibility(config.brightness));
            if (!config.nightMode)
                settingsBar.setVisibility(IMultiLineBar.TYPE_DAYNIGHT, getVisibility(config.nightMode));
            if (!config.reflow)
                settingsBar.setVisibility(IMultiLineBar.TYPE_REFLOW, getVisibility(config.reflow));
            if (!config.cropPage)
                settingsBar.setVisibility(IMultiLineBar.TYPE_CROP, getVisibility(config.cropPage));
            if (!config.screenLock)
                settingsBar.setVisibility(IMultiLineBar.TYPE_LOCKSCREEN, getVisibility(config.screenLock));
            if (!config.facingPage)
                settingsBar.setVisibility(IMultiLineBar.TYPE_FACINGPAGE, getVisibility(config.facingPage));
            if (!config.coverPage)
                settingsBar.setVisibility(IMultiLineBar.TYPE_COVERPAGE, getVisibility(config.coverPage));
            if (!config.panZoom)
                settingsBar.setVisibility(IMultiLineBar.TYPE_PANZOOM, getVisibility(config.panZoom));
            if (!config.fitPage)
                settingsBar.setVisibility(IMultiLineBar.TYPE_FITPAGE, getVisibility(config.fitPage));
            if (!config.fitWidth)
                settingsBar.setVisibility(IMultiLineBar.TYPE_FITWIDTH, getVisibility(config.fitWidth));
            if (!config.rotate)
                settingsBar.setVisibility(IMultiLineBar.TYPE_ROTATEVIEW, getVisibility(config.rotate));
        }
    }

    private void initViewMoreConfig() {
        ViewMoreConfig config = (ViewMoreConfig) getIntent().getSerializableExtra(ViewMoreConfig.TAG);
        if (null != config) {
            IMenuView menuView = uiExtensionsManager.getMenuView();

            ViewMoreConfig.GroupFile groupFile = config.groupFile;
            if (null != groupFile) {
                if (!groupFile.crop)
                    menuView.setItemVisibility(getVisibility(groupFile.crop), MoreMenuConfig.GROUP_FILE, MoreMenuConfig.ITEM_SNAPSHOT);
                if (!groupFile.fileInfo)
                    menuView.setItemVisibility(getVisibility(groupFile.fileInfo), MoreMenuConfig.GROUP_FILE, MoreMenuConfig.ITEM_DOCINFO);
                if (!groupFile.reduceFileSize)
                    menuView.setItemVisibility(getVisibility(groupFile.reduceFileSize), MoreMenuConfig.GROUP_FILE, MoreMenuConfig.ITEM_REDUCE_FILE_SIZE);
                if (!groupFile.wirelessPrint)
                    menuView.setItemVisibility(getVisibility(groupFile.wirelessPrint), MoreMenuConfig.GROUP_FILE, MoreMenuConfig.ITEM_PRINT_FILE);

                boolean isHideGroupFile = !groupFile.crop && !groupFile.fileInfo && !groupFile.reduceFileSize && !groupFile.wirelessPrint;
                if (isHideGroupFile)
                    menuView.setGroupVisibility(View.GONE, MoreMenuConfig.GROUP_FILE);
            }

            ViewMoreConfig.GroupProtect groupProtect = config.groupProtect;
            if (null != groupProtect) {
                if (!groupProtect.password) {
                    menuView.setItemVisibility(getVisibility(groupProtect.password), MoreMenuConfig.GROUP_PROTECT, MoreMenuConfig.ITEM_PASSWORD);
                    menuView.setItemVisibility(getVisibility(groupProtect.password), MoreMenuConfig.GROUP_PROTECT, MoreMenuConfig.ITEM_REMOVESECURITY_PASSWORD);
                    menuView.setGroupVisibility(View.GONE, MoreMenuConfig.GROUP_PROTECT);
                }
            }

            ViewMoreConfig.GroupComment groupComment = config.groupComment;
            if (null != groupComment) {
                if (!groupComment.importComment)
                    menuView.setItemVisibility(getVisibility(groupComment.importComment), MoreMenuConfig.GROUP_ANNOTATION, MoreMenuConfig.ITEM_ANNOTATION_IMPORT);
                if (!groupComment.exportComment)
                    menuView.setItemVisibility(getVisibility(groupComment.exportComment), MoreMenuConfig.GROUP_ANNOTATION, MoreMenuConfig.ITEM_ANNOTATION_EXPORT);

                boolean isHideGroupComment = !groupComment.importComment && !groupComment.exportComment;
                if (isHideGroupComment)
                    menuView.setGroupVisibility(View.GONE, MoreMenuConfig.GROUP_ANNOTATION);
            }

            ViewMoreConfig.GroupForm groupForm = config.groupForm;
            if (null != groupForm) {
                if (!groupForm.createForm)
                    menuView.setItemVisibility(getVisibility(groupForm.createForm), MoreMenuConfig.GROUP_FORM, MoreMenuConfig.ITEM_CREATE_FORM);
                if (!groupForm.resetForm)
                    menuView.setItemVisibility(getVisibility(groupForm.resetForm), MoreMenuConfig.GROUP_FORM, MoreMenuConfig.ITEM_RESET_FORM);
                if (!groupForm.importForm)
                    menuView.setItemVisibility(getVisibility(groupForm.importForm), MoreMenuConfig.GROUP_FORM, MoreMenuConfig.ITEM_IMPORT_FORM);
                if (!groupForm.exportForm)
                    menuView.setItemVisibility(getVisibility(groupForm.exportForm), MoreMenuConfig.GROUP_FORM, MoreMenuConfig.ITEM_EXPORT_FORM);

                boolean isHideGroupFrom = !groupForm.createForm && !groupForm.resetForm && !groupForm.importForm && !groupForm.exportForm;
                if (isHideGroupFrom)
                    menuView.setGroupVisibility(View.GONE, MoreMenuConfig.GROUP_FORM);
            }
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
        AccountModule.getInstance().onDestroy(this);
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
