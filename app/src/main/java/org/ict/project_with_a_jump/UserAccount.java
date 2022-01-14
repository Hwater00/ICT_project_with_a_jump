package org.ict.project_with_a_jump;

public class UserAccount {
    private String name; // 사용자의 이름
    private String num; // 사용자의 개인안심번호
    private String address;// 사용자의 간단한 거주지 주소
    private String idToken;

    public UserAccount() {
    }

    public UserAccount(String name, String num, String address) { // 사용자 정보 저장
        this.name = name;
        this.num = num;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}