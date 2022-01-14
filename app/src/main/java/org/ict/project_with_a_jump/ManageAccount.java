package org.ict.project_with_a_jump;

/**
 * 사용자 계정 정보 모델 클래스
 */
public class ManageAccount {
    private String birth;
    private String companyName;
    private String daum1;
    private String daum2;
    private String daum3;
    private String daum4; // 도로명 주소의 '()' 부분
    private String gpsData; // 위치 인증을 통해 얻은 사업장 주소
    private String emailId;
    private String idToken;     //Firebase Uid(고유 토큰정보)
    private String name;
    private String password;

    public ManageAccount() {
    }

    public ManageAccount(Object object) {

    }

    public ManageAccount(String birth, String companyName, String daum1, String daum2, String daum3, String daum4, String gpsData, String emailId, String idToken, String name, String password) {
        this.birth = birth;
        this.companyName = companyName;
        this.daum1 = daum1;
        this.daum2 = daum2;
        this.daum3 = daum3;
        this.daum4 = daum4;
        this.gpsData = gpsData;
        this.idToken = idToken;
        this.name = name;
        this.emailId = emailId;
        this.password = password;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getDaum1() {
        return daum1;
    }

    public void setDaum1(String daum1) {
        this.daum1 = daum1;
    }

    public String getDaum2() {
        return daum2;
    }

    public void setDaum2(String daum2) {
        this.daum2 = daum2;
    }

    public String getDaum3() {
        return daum3;
    }

    public void setDaum3(String daum3) {
        this.daum3 = daum3;
    }

    public String getDaum4() {
        return daum4;
    }

    public void setDaum4(String daum4) {
        this.daum4 = daum4;
    }

    public String getGpsData() {
        return gpsData;
    }

    public void setGpsData(String gpsData) {
        this.gpsData = gpsData;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

}
