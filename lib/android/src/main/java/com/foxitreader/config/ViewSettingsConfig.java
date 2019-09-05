package com.foxitreader.config;


import java.io.Serializable;

public class ViewSettingsConfig implements Serializable {
    public static final String TAG = ViewSettingsConfig.class.getName();

    public boolean single = true;
    public boolean continuous = true;
    public boolean facingPage = true;
    public boolean coverPage = true;
    public boolean thumbnail = true;
    public boolean reflow = true;
    public boolean cropPage = true;
    public boolean screenLock = true;
    public boolean brightness = true;
    public boolean nightMode = true;
    public boolean panZoom = true;
    public boolean fitPage = true;
    public boolean fitWidth = true;
    public boolean rotate = true;
}
