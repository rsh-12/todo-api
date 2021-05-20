package ru.example.todo.entity;
/*
 * Date: 3/25/21
 * Time: 9:47 AM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import ru.example.todo.enums.Role;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotEmpty
    @Pattern(regexp = "^[a-z_-]{2,}[0-9a-z_-]*@[a-z]{2,5}\\.(ru|com)",
            flags = Pattern.Flag.CASE_INSENSITIVE, message = "Not a valid email address")
    @Size(min = 4, max = 127, message = "Email is required: minimum 4 characters")
    @Column(nullable = false, unique = true)
    private String username;

    // todo set min=8
    @NotNull
    @NotBlank
    @NotEmpty
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
        this.roles = Collections.singleton(Role.ROLE_USER);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Role> getRoles() {
        return roles;
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

        if (!Objects.equals(id, user.id)) return false;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
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
