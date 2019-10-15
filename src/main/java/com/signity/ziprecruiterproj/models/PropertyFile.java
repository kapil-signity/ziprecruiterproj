package com.signity.ziprecruiterproj.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertyFile {
    private String dataBaseName;
    private String fileName;

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "PropertyFile{" +
                "dataBaseName='" + dataBaseName + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
