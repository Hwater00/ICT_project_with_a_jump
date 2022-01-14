package org.ict.project_with_a_jump;

public class FirebasePost {
    String date;
    String name1;
    String phonenumber;
    String home;
    String agree;
    String temperature;

    public FirebasePost() {
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String Name1) {
        this.name1 = name1;
    }

    public String getphonenumber() {
        return phonenumber;
    }

    public void setphonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String gethome() {
        return home;
    }

    public void sethome(String home) {
        this.home = home;
    }

    public String getagree() {
        return agree;
    }

    public void setagree(String agree) {
        this.agree = agree;
    }

    public void settemperature(String temperature) {
        this.temperature = temperature;
    }

    public String gettemperature() {
        return temperature;
    }

    public FirebasePost(String date, String name1, String phonenumber, String home, String agree, String temperature) {
        this.date = date;
        this.name1 = name1;
        this.phonenumber = phonenumber;
        this.home = home;
        this.agree = agree;
        this.temperature = temperature;
    }
}