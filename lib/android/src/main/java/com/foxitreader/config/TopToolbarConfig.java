package com.foxitreader.config;


import java.io.Serializable;

public class TopToolbarConfig implements Serializable {
    public static final String TAG = TopToolbarConfig.class.getName();

    public boolean more;
    public boolean back = true;
    public boolean bookmark = true;
    public boolean search = true;
}
