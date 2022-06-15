package com.virtualmarathon.Authorization.data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {


    @Id
    private String email;

    @Column(nullable = false,unique = true)
    private String password;

    @Column(nullable = false)
    private Long marathonsCompleted;
    @Column(nullable = false)
    private int currentOrganizerCount;
    @Column(nullable = false)
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
