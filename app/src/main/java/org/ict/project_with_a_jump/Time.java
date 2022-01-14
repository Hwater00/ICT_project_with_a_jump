package org.ict.project_with_a_jump;

public class Time {
    private String open;
    private String closed;

    public Time() {
    }

    /*
    public Time(String open, String closed){
        this.open=open;
        this.closed=closed;
    }
    */

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

}
