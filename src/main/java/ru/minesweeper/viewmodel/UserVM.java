package ru.minesweeper.viewmodel;

public class UserVM {
    public UserVM(int id, String userName, String realName) {
        this.setId(id);
        this.setUserName(userName);
        this.setRealName(realName);
    }
    private int id;
    private String userName;
    private String realName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
