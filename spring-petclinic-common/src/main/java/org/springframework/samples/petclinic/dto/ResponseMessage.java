package org.springframework.samples.petclinic.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private boolean success;

    private String message;

    private Object data;

    public ResponseMessage(boolean status, String message) {
        this(status, message, null);
    }

    public ResponseMessage(boolean status, String message, Object data) {
        this.success = status;
        this.message = message;
        if (data != null) {
            this.data = data;
        } else {
            this.data = new ArrayList<>();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
