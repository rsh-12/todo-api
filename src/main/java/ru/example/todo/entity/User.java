package ru.example.todo.entity;
/*
 * Date: 3/25/21
 * Time: 9:47 AM
 * */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Size(min = 4, max = 127, message = "Email is required: minimum 4 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "Password is required")
    private String password;

    @JsonFormat(timezone = "Asia/Yekaterinburg")
    @CreationTimestamp
    private Date createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<TodoSection> todoSections;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TodoTask> todoTasks;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public List<TodoTask> getTodoTasks() {
        return todoTasks;
    }

    public void setTodoTasks(List<TodoTask> todoTasks) {
        this.todoTasks = todoTasks;
    }

    public Set<TodoSection> getTodoSections() {
        return todoSections;
    }

    public void setTodoSections(Set<TodoSection> todoSections) {
        this.todoSections = todoSections;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}