package ru.test.ViewModel;

import org.springframework.web.multipart.MultipartFile;

public class RegisterVM {
    private String userName;
    private String realName;
    private String password;
    private String passConfirm;

    public RegisterVM() {
    }

    public RegisterVM(String userName, String password, String passConfirm) {
        this.userName = userName;
        this.password = password;
        this.passConfirm = passConfirm;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassConfirm() {
        return passConfirm;
    }

    public void setPassConfirm(String passConfirm) {
        this.passConfirm = passConfirm;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
