package com.virtualmarathon.Authorization.shared;

import java.io.Serializable;

public class UserDto implements Serializable {

    private String email;
    private String password;
    private Long marathonsCompleted;
    private int currentOrganizerCount;
    private Long totalPoints;


    public Long getMarathonsCompleted() {
        return marathonsCompleted;
    }

    public void setMarathonsCompleted(Long marathonsCompleted) {
        this.marathonsCompleted = marathonsCompleted;
    }

    public int getCurrentOrganizerCount() {
        return currentOrganizerCount;
    }

    public void setCurrentOrganizerCount(int currentOrganizerCount) {
        this.currentOrganizerCount = currentOrganizerCount;
    }

    public Long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




}
