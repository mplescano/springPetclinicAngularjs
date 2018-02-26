package org.springframework.samples.petclinic.dto.projection;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface UserForGridWeb {

    Integer getId();

    String getFirstName();

    String getLastName();

    String getUsername();

    String getRoles();

    boolean isEnabled();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date getCreatedAt();
}
