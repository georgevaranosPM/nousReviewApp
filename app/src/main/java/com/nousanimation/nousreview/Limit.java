package com.nousanimation.nousreview;

//H klasi "Limit" i opoia periexei 5 metavlites gia tous periorismous twn arxeiwn
public class Limit {
    public String fileType;
    public int minFaceCount;
    public int maxFaceCount;
    public int minVertCount;
    public int maxVertCount;

    public Limit(String fileType, int minFace, int maxFace, int minVert, int maxVert) {
    }

    //Setters (Getters de xreiazontai)
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
