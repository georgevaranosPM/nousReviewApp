package com.nousanimation.nousreview;

public class Work {
    public String name;
    public String production;
    public String upload_date;
    public int path;
    public String description;

    public Work(String u, String s, String d, int i, String s2) {
    }

    public void setName(String name) {this.name = name; }

    public void setProduction(String production) {
        this.production = production;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public void setPath(int path) {
        this.path = path;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public String getProduction() {
        return production;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public int getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

}


