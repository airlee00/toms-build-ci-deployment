package com.googlecode.jenkinsjavaapi;


import java.util.HashMap;
import java.util.Map;


public class JobStatus {

    public static enum Status {
        OK,
        WARNING,
        FAILED,
        INPROGRESS,
        PENDING,
        NOTBUILT;

        public static Status create(String colorCode) {
            Map<String, Status> codes = new HashMap<String, Status>();
            codes.put("blue", OK);
            codes.put("blue_anime", INPROGRESS);
            codes.put("yellow", WARNING);
            codes.put("yellow_anime", INPROGRESS);
            codes.put("red", FAILED);
            codes.put("red_anime", INPROGRESS);
            codes.put("grey", PENDING);
            codes.put("grey_anime", INPROGRESS);
            codes.put("notbuilt", NOTBUILT);
            codes.put("notbuilt_anime", INPROGRESS);
            return codes.get(colorCode);
        }
    }

    String name;
    Status status;
    public JobStatus(String name, Status status) {
        this.name = name;
        this.status = status;
    }
    public String getName() {
        return name;
    }
    public Status getStatus() {
        return status;
    }
}
