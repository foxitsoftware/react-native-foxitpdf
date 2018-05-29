package com.foxitreader;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.foxit.sdk.PDFViewCtrl;
import com.foxit.sdk.common.PDFException;
import com.foxit.sdk.pdf.PDFDoc;
import com.foxit.uiextensions.UIExtensionsManager;
import com.foxit.uiextensions.pdfreader.impl.PDFReader;
import com.foxit.uiextensions.utils.AppTheme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

public class PDFReaderActivity extends FragmentActivity {
    public PDFReader mPDFReader;
    public PDFViewCtrl viewCtrl;

    private byte[] downloadUrl(URL toDownload) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = toDownload.openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outputStream.toByteArray();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String src = intent.getExtras().getString("src");

        PDFDoc document;
        viewCtrl = new PDFViewCtrl(this.getApplicationContext());
        viewCtrl.setVerticalScrollBarEnabled(true);
        viewCtrl.setHorizontalScrollBarEnabled(true);

//        String path = "/mnt/sdcard/input_files/Sample.pdf";

        String UIExtensionsConfig = "{\n" +
                " \"modules\": {\n" +
                " \"readingbookmark\": true,\n" +
                " \"outline\": true,\n" +
                " \"annotations\": true,\n" +
                " \"thumbnail\" : true,\n" +
                " \"attachment\": true,\n" +
                " \"signature\": true,\n" +
                " \"search\": true,\n" +
                " \"pageNavigation\": true,\n" +
                " \"form\": true,\n" +
                " \"selection\": true,\n" +
                " \"encryption\" : true\n" +
                " }\n"+ "}\n";
        InputStream stream = new ByteArrayInputStream(UIExtensionsConfig.getBytes(Charset.forName("UTF-8")));
        UIExtensionsManager.Config config = new UIExtensionsManager.Config(stream);

        UIExtensionsManager uiExtensionsManager = new com.foxit.uiextensions.UIExtensionsManager(this.getApplicationContext(), null, viewCtrl, config);
        viewCtrl.setUIExtensionsManager(uiExtensionsManager);
        uiExtensionsManager.setAttachedActivity(this);

        try {
            byte[] bytes = downloadUrl(new URL(src));
            document = new PDFDoc(bytes);
            document.load(null);
            viewCtrl.setDoc(document);

        } catch (PDFException e) {
            Log.d("error", e.getMessage());
            e.printStackTrace();
        } catch (java.net.MalformedURLException e) {
            Log.d("error", e.getMessage());
            e.printStackTrace();
        }

        mPDFReader = (PDFReader) uiExtensionsManager.getPDFReader();
        mPDFReader.onCreate(this, viewCtrl, null);
        AppTheme.setThemeFullScreen(this);
        AppTheme.setThemeNeedMenuKey(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_pdfreader);

        addContentView(mPDFReader.getContentView(), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
