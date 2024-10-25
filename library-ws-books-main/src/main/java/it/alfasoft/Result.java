package it.alfasoft;

import java.io.Serializable;

public class Result implements Serializable {

    private String message;

    public Result() {}

    public Result(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
