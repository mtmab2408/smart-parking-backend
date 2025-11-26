package com.smartpark.parking_backend.dto;

public record ManualUpdateRequest(String status, String operator, String note) {}