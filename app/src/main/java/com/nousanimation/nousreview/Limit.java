package com.nousanimation.nousreview;

public class Limit {
    public String fileType;
    public int minFaceCount;
    public int maxFaceCount;
    public int minVertCount;
    public int maxVertCount;

    public Limit(String fileType, int minFace, int maxFace, int minVert, int maxVert) {
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setMinFaceCount(int minFaceCount) {
        this.minFaceCount = minFaceCount;
    }

    public void setMaxFaceCount(int maxFaceCount) {
        this.maxFaceCount = maxFaceCount;
    }

    public void setMinVertCount(int minVertCount) {
        this.minVertCount = minVertCount;
    }

    public void setMaxVertCount(int maxVertCount) {
        this.maxVertCount = maxVertCount;
    }

}
