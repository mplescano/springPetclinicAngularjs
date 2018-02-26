package org.springframework.samples.petclinic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

public class HashUtil {

    private static final Logger logger = LoggerFactory.getLogger(HashUtil.class);

    public static String hashString(String str){
        String result = null;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(str.getBytes());
            result = DatatypeConverter.printHexBinary(digest).toLowerCase();
        }catch (Exception ex){
            logger.error("Error al hashear: ", ex);
        }
        return result;
    }

}
