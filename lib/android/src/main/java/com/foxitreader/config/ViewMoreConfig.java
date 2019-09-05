package com.foxitreader.config;


import java.io.Serializable;

public class ViewMoreConfig implements Serializable {
    public static final String TAG = ViewMoreConfig.class.getName();


    public GroupFile groupFile;
    public GroupProtect groupProtect;
    public GroupComment groupComment;
    public GroupForm groupForm;

    public static class GroupFile implements Serializable{
        public boolean fileInfo = true;
        public boolean reduceFileSize = true;
        public boolean wirelessPrint = true;
        public boolean crop = true;
    }

    public static class GroupProtect implements Serializable{
        public boolean password = true;
    }

    public static class GroupComment implements Serializable{
        public boolean importComment = true;
        public boolean exportComment = true;
    }

    public static class GroupForm implements Serializable{
        public boolean createForm = true;
        public boolean resetForm = true;
        public boolean importForm = true;
        public boolean exportForm = true;
    }

}
