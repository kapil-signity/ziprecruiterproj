package com.signity.ziprecruiterproj.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertyFile {
    private String dataBaseName;

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }
}
