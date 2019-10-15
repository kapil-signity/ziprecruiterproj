package com.signity.ziprecruiterproj.configs;

public class CommandArguments {
    public static String jobSourceName="";
    public static Integer startDate=0;
    public static Integer endDate=16;


    public static String printData() {
        return "CommandArguments{" +
                "jobSourceName='" + jobSourceName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
