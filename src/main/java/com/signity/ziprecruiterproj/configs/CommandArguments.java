package com.signity.ziprecruiterproj.configs;

public class CommandArguments {
	public static String jobSourceName = "";
	public static int startDate = 0;
	public static int endDate = -18;

	public static String printData() {
		return "CommandArguments{" + "jobSourceName='" + jobSourceName + '\'' + ", startDate=" + startDate
				+ ", endDate=" + endDate + '}';
	}
}
