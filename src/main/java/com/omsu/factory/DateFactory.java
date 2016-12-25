package com.omsu.factory;

import com.omsu.configProperties.ConfigProperties;
import com.omsu.date.GMTService;
import com.omsu.date.IDateService;
import com.omsu.date.UTCService;

/**
 * Created by dkuzmin on 8/24/2016.
 */
public class DateFactory {

    private static String datetime = ConfigProperties.getProperties().getProperty("factory.datetime");

    public static IDateService getDateService() {
        if (datetime.equals("GMT")) {
            return new GMTService();
        } else if (datetime.equals("UTC")) {
            return new UTCService();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for factory.datetime property " + datetime + "!");
        }
    }
}
