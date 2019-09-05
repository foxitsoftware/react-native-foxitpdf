package com.foxitreader.config;


import java.io.Serializable;

public class PanelConfig implements Serializable {
    public static final String TAG = PanelConfig.class.getName();

    public boolean readingBookmark = true;
    public boolean outline = true;
    public boolean annotation = true;
    public boolean attachments = true;
    public boolean signature = true;
}
