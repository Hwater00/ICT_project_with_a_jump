package org.ict.project_with_a_jump;

public class EntryList {
    String place; //장소
    String time; //장소에 간 시간
    String num; //개인안심번호

    public EntryList() {
    }

    public EntryList(String place, String time) {
        this.place = place;
        this.time = time;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String name) {
        this.num = num;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}