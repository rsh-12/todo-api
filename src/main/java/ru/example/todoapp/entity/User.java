package ru.example.todoapp.entity;
/*
 * Date: 3/25/21
 * Time: 9:47 AM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import ru.example.todoapp.enums.Role;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-z_-]{2,}[0-9a-z_-]*@[a-z]{2,5}\\.(ru|com)",
            flags = Pattern.Flag.CASE_INSENSITIVE, message = "Not a valid email address")
    @Size(min = 4, max = 127, message = "Email is required: minimum 4 characters")
    @Column(nullable = false, unique = true)
    private String username;

    // todo set min=8
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 4, message = "Password is required")
    private String password;

    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @CreationTimestamp
    private Date createdAt;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",
            cascade = {CascadeType.REFRESH, CascadeType.DETACH,
                    CascadeType.REMOVE, CascadeType.MERGE})
    private Set<TodoSection> todoSections;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",
            cascade = {CascadeType.REFRESH, CascadeType.DETACH,
                    CascadeType.REMOVE, CascadeType.MERGE})
    private List<TodoTask> todoTasks;

    public User() {
        this.roles = Collections.singleton(Role.USER);
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username == null ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return createdAt == null ? new Date() : createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Role> getRoles() {
        return roles == null ? Collections.emptySet() : roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void clearRoles() {
        this.roles = Collections.emptySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }
}
