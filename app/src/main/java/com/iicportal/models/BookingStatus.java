package com.iicportal.models;

public class BookingStatus extends Status {
    private String bookingId;
    private String facilityImage;
    private String facilityName;
    private String state;
    private Long startTimestamp;
    private Long endTimestamp;

    public BookingStatus() {
    }

    public BookingStatus(String uid, String title, String description, String type, Long timestamp, String bookingId, String facilityImage, String facilityName, String state, Long startTimestamp, Long endTimestamp) {
        super(uid, title, description, type, timestamp);
        this.bookingId = bookingId;
        this.facilityImage = facilityImage;
        this.facilityName = facilityName;
        this.state = state;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getFacilityImage() {
        return facilityImage;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getState() {
        return state;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setFacilityImage(String facilityImage) {
        this.facilityImage = facilityImage;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }
}
