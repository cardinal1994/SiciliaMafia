package com.example.siciliamafia.Model;

public class Rule {

    private String heading;
    private String subtext;
    private String id;

    public Rule(String heading, String subtext, String id) {
        this.heading = heading;
        this.subtext = subtext;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext;
    }


    public Rule() {
    }
}
