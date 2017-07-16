package ai.elimu.appstore.util;

import ai.elimu.model.enums.Environment;

public class EnvironmentSettings {

//      private static final Environment ENVIRONMENT = Environment.DEV;
//      public static final Environment ENVIRONMENT = Environment.TEST;
    public static final Environment ENVIRONMENT = Environment.PROD;

    public static final String PROD_DOMAIN = "elimu.ai";

    public static String getDomain() {
        if (ENVIRONMENT == Environment.DEV) {
            return "192.168.0.103"; // Replace with the IP address of your WIFI router
        } else if (ENVIRONMENT == Environment.TEST) {
            return "test." + PROD_DOMAIN;
        } else {
            return PROD_DOMAIN;
        }
    }

    public static String getBaseRestUrl() {
        return getBaseUrl() + "/rest/v1";
    }

    public static String getBaseUrl() {
        if (ENVIRONMENT == Environment.DEV) {
            return "http://" + getDomain() + ":8080/webapp";
        } else {
            return "http://" + getDomain();
        }
    }
}
