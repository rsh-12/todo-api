package ru.example.todoapp.entity;
/*
 * Date: 3/25/21
 * Time: 10:47 AM
 * */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String token;

    @NotNull
    private Long userId;

    private String createdByIp;

    private LocalDateTime expiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public RefreshToken(String token, Long userId, String createdByIp) {
        this.token = token;
        this.userId = userId;
        this.createdByIp = createdByIp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RefreshToken that = (RefreshToken) o;

        if (!token.equals(that.token)) return false;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        int result = token.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "token='" + token + '\'' +
                ", userId=" + userId +
                '}';
    }

}