package org.literacyapp.appstore.util;

import org.literacyapp.model.enums.Environment;

public class EnvironmentSettings {

    public static final String DOMAIN = "literacyapp.org";

//      public static final Environment ENVIRONMENT = Environment.DEV;
      public static final Environment ENVIRONMENT = Environment.TEST;
//    public static final Environment ENVIRONMENT = Environment.PROD;

    public static String getBaseUrl() {
        if (ENVIRONMENT == Environment.DEV) {
            return "http://192.168.1.129:8080/literacyapp-webapp"; // Replace with the IP address of your WIFI router
        } else if (ENVIRONMENT == Environment.TEST) {
            return "http://test." + DOMAIN;
        } else {
            return "http://" + DOMAIN;
        }
    }
}
