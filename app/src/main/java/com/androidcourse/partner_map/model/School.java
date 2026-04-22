package com.androidcourse.partner_map.model;

public class School {
    private String schoolId;
    private String name;
    private double lat;
    private double lng;

    public School() {}

    public School(String schoolId, String name, double lat, double lng) {
        this.schoolId = schoolId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
}
