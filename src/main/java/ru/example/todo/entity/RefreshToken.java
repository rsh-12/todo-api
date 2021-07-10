package ru.example.todo.entity;
/*
 * Date: 3/25/21
 * Time: 10:47 AM
 * */

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String value;

    @NotNull
    private Long userId;

    private String createdByIp;

    private Date expiresAt;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    public RefreshToken() {
    }

    public RefreshToken(String value, Long userId, Date expiresAt) {
        this.value = value;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCreatedByIp() {
        return createdByIp;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCreatedByIp(String createdByIp) {
        this.createdByIp = createdByIp;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RefreshToken that = (RefreshToken) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(value, that.value)) return false;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", userId=" + userId +
                ", createdByIp='" + createdByIp + '\'' +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
