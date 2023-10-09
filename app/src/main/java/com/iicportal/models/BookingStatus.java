package com.iicportal.models;

public class BookingStatus extends Status {
    private String bookingId;
    private String facilityImage;
    private String facilityName;

    public BookingStatus() {
    }

    public BookingStatus(String uid, String bookingId, String title, String description, String type, Long timestamp, String facilityImage, String facilityName) {
        super(uid, title, description, type, timestamp);
        this.bookingId = bookingId;
        this.facilityImage = facilityImage;
        this.facilityName = facilityName;
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

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setFacilityImage(String facilityImage) {
        this.facilityImage = facilityImage;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
}
