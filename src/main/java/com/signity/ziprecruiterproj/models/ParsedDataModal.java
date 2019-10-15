package com.signity.ziprecruiterproj.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ParsedDataModal {

    @Expose
    @SerializedName("created_date")
    private Date createdDate=new Date();

    @Expose
    @SerializedName("event_date")
    private Date eventDate;

    @Expose
    @SerializedName("job_source")
    private String jobSource;

    @Expose
    @SerializedName("total_clicks")
    private String totalClicks;

    @Expose
    @SerializedName("paid_clicks")
    private String paidClicks;

    @Expose
    @SerializedName("unpaid_clicks")
    private String unpaidClicks;

    @Expose
    @SerializedName("paid_revenue")
    private String paidRevenue;


    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getJobSource() {
        return jobSource;
    }

    public void setJobSource(String jobSource) {
        this.jobSource = jobSource;
    }

    public String getTotalClicks() {
        totalClicks=String.valueOf(Integer.parseInt(paidClicks)+Integer.parseInt(unpaidClicks));
        return totalClicks;
    }

    public void setTotalClicks(String totalClicks) {
        this.totalClicks = totalClicks;
    }

    public String getPaidClicks() {
        return paidClicks;
    }

    public void setPaidClicks(String paidClicks) {
        this.paidClicks = paidClicks;
    }

    public String getUnpaidClicks() {
        return unpaidClicks;
    }

    public void setUnpaidClicks(String unpaidClicks) {
        this.unpaidClicks = unpaidClicks;
    }

    public String getPaidRevenue() {
        return paidRevenue;
    }

    public void setPaidRevenue(String paidRevenue) {
        this.paidRevenue = paidRevenue;
    }
}
