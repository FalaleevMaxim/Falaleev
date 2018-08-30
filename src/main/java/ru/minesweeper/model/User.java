package ru.minesweeper.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    private int id;
    private String userName;
    private String password;
    private String realName;
    private String currentGameId;

    public User() {
    }

    public User(int id, String userName, String password, String realName) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.realName = realName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "username")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "real_name")
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Column(name = "current_game")
    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
    }
}
