package com.yagnikfadadu.librarymanagement.ModalClass;
public class UserModal {
    public String name;
    public String password;
    public String enrollmentNumber;
    public String phoneNumber;
    public String spID;
    public String email;
    public String branch;
    public int credit;

    public UserModal(){
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setSpID(String spID) {
        this.spID = spID;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public UserModal(String name, String password, String enrollmentNumber, String phoneNumber, String spID, String email, String branch){
        this.name = name;
        this.password = password;
        this.enrollmentNumber = enrollmentNumber;
        this.phoneNumber = phoneNumber;
        this.spID = spID;
        this.email = email;
        this.branch = branch;
        this.credit = 5;
    }
}

