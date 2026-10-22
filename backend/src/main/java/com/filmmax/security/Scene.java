package com.filmmax.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Scene {
    private Long id;
    private int sceneNumber;
    private String title;
    private String description;
    private String locationName;
    private LocalDate shootDate;
    private String timeSlot;
    private Long createdBy;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getSceneNumber() { return sceneNumber; }
    public void setSceneNumber(int sceneNumber) { this.sceneNumber = sceneNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public LocalDate getShootDate() { return shootDate; }
    public void setShootDate(LocalDate shootDate) { this.shootDate = shootDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}