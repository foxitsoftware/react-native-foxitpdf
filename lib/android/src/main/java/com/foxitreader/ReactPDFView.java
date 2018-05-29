package com.foxitreader;

import android.content.Intent;
import android.util.Log;
import android.widget.RelativeLayout;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.foxit.sdk.common.Library;
import com.foxit.sdk.common.PDFError;
import com.foxit.sdk.common.PDFException;


public class ReactPDFView extends RelativeLayout {

    private String password;
    private ReadableMap extensionConfig;
    private boolean enableBottomToolbar;
    private boolean enableTopToolbar;
    private ReadableMap topToolbarConfig;
    private ReadableMap bottomToolbarConfig;
    private ReadableMap panelConfig;
    private ReadableMap viewSettingsConfig;
    private ReadableMap viewMoreConfig;

    private ThemedReactContext mThemedReactContext;

    private static String sn = "osniRGGP1XjedZLxVQzIAa6qTsiyJKVBc0TNR7cVchqtHut/HHphcw==";
    private static String key = "ezJvj98ntBh39DvoP0WUjY7NZTROhapeXws4euNAs+qWRogVCWd+2oEF87IxSi7IG1YfES7w6Y5jc8VbkBLSV4PzOyHjtvTn8DQ07cwln724y/13XHCQKeeTwclKvWccVF6o3/2+VmzWQucp54YTVjC7i9KEatRxEcNM/o3mTN1vCZiGGviNizgEYqKxIAjyN5oCrUDGQfFZ4xFIvNoTsqcm2+P4R2KNinAC5N3FepWuQ8BinuYUeBBRYrcm+rSoQ2zejZqRL3l6wsyo4spU0waryqxl0HDdD8X4ghP5FEtfzppxjFwdlfUTvLbE+jsFW3v+vSYjQo7eSRytGBjB68VfC6yioVz0NQKEwB9v6Wm5E2aMs5ucbpjlAT9DLJnzr+tckpyvRQvymKRYMvduV5KVj9PBxn8f4828/pRJCJhbJuE4lrHvEYjJpyvOccOOlBYdPvEAtfs3ZsHW0Vuda9BgGq/OfeBVlBV5Dw4OBplrEzC4Gl6/fDwdRH2qC3MvjQMDgxI10c6+JHd658RJjnufKtQA6uEIKo6xlLs0ItsxdIV8lEfpU0DFjULozw6qCcuSsOgIUEJMCZO6JbsfmulhfVKyqVU316+vM/3OyLp/2Sf+UWb5lXhx+u5CGzZknL0sIPVnJdt0O+tk7r0CO/bVJfuYlllcmooqyj9d2YbV4RQs1N9gWqSarkpzZAYFwexLXRNon1M8xfZvTBNo8qTiftulUieTGSVgZBvr8Xw4fdP4/w/fcssg0l5/WOqHHxilVqQLRW1n1MCxFir6a3+3L6eMT3/mpdfxce8MhYClZ79E8nDenRnmimfRku0KMo+Ci4CbYJRbKS/NU5qvLOQzvH4E6UoI9QOj9Hr3Vpft56oDiehurzsEttNspu+Dbznv70QJE7BddbaGaG5XW0OWcmoloL3l5sp4AUZOn1kEb8t5j8h3suqJxHQr5rmFfvtTbpzgazURp99qcvd7iV9z8pUDeiOiZ6J2af21CAQ1HCXkozqiwKgGzv19zsKccBM4gr9kJnpAkQlKQfQrBiSvcx3MnlM1ypd3/silNT22s/BmTjldZxgmhLCItC2TOmuv4LTFc7ftEnzG6AEye2aju+IrwfPxmvOT3Mluixlt6wODDI4+UJex+XWTVqLhdpHNvbiXydipcRoLWGSpByJd3ZXeBJb45j+Y/O0j5PcFvKtLhTQMEPgWtn6Ynu/kTBD1Mt0HAkyHGhExTmfBvyRt8d4=";
    private static boolean isLibraryInited = false;

    static {
        System.loadLibrary("rdk");
    }

    public ReactPDFView(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;

//        this.setLayoutParams(new ViewGroup.LayoutParams(
//                LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT));

        if (isLibraryInited == false) {
            try {
                Library.init(sn, key);
                Log.d("pdfView", "Init Success");
                isLibraryInited = true;
            } catch (PDFException e) {
                Log.d("pdfView", "Init fail");
                if (e.getLastError() == PDFError.LICENSE_INVALID.getCode()) {
                    //UIToast.getInstance(getApplicationContext()).show("The license is invalid!");
                } else {
                    //UIToast.getInstance(getApplicationContext()).show("Failed to initialize the library!");
                }
                return;
            }
        }
    }

    public void setSource(String src) {
        Intent pdf = new Intent(mThemedReactContext, PDFReaderActivity.class);
        pdf.putExtra("src", src);
        mThemedReactContext.startActivity(pdf);
    }

    public void setPassword(String param) {
        password = param;
        Log.d("password","password");
    }

    public void setExtensionConfig(ReadableMap param) {
        extensionConfig = param;

        Log.d("extensionConfig","extensionConfig");

    }

    public void setEnableBottomToolbar(boolean param) {
        enableBottomToolbar = param;
    }

    public void setEnableTopToolbar(boolean param) {
        enableTopToolbar = param;
    }

}
