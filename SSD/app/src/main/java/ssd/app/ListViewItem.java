package ssd.app;

public class ListViewItem {

    private String titleStr ;
    private String state = "연결끊김";

    public void setTitle(String title) {
        titleStr = title ;
    }

    public String getTitle() {
        return this.titleStr ;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
