package ru.example.todo.entity;
/*
 * Date: 5/18/21
 * Time: 4:03 PM
 * */

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "otp")
@DynamicInsert
@DynamicUpdate
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotBlank
    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 6, max = 6)
    private String code;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    private Date expiresAt;

    public Otp() {
        this.setExpiresAt();
    }

    public Otp(String username, String code) {
        this.username = username;
        this.code = code;
        this.setExpiresAt();
    }

    public void updateExpiryDate() {
        this.setExpiresAt();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getCode() {
        return code;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private void setExpiresAt() {
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(10);
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        this.expiresAt = Date.from(instant);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Otp otp = (Otp) o;

        return Objects.equals(username, otp.username);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Otp{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
