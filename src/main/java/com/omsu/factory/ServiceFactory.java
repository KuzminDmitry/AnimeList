package com.omsu.factory;

import com.omsu.configProperties.ConfigProperties;
import com.omsu.iDAO.*;
import com.omsu.mySQL.DAO.*;

/**
 * Created by dkuzmin on 7/11/2016.
 */
public class ServiceFactory {

    private static String database = ConfigProperties.getProperties().getProperty("factory.database");

    public static IBasicService getRoleService() {
        if (database.equals("mysql")) {
            return new RoleService();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for factory.database " + database + "!");
        }
    }

    public static IUserService getUserService() {
        if (database.equals("mysql")) {
            return new UserService();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for factory.database " + database + "!");
        }
    }

    public static ITokenService getAuthService() {
        if (database.equals("mysql")) {
            return new TokenService();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for factory.database " + database + "!");
        }
    }

    public static IBasicService getGenreService() {
        if (database.equals("mysql")) {
            return new GenreService();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for factory.database " + database + "!");
        }
    }
    public static IBasicService getStudioService(){
        if(database.equals("mysql")){
            return new StudioService();
        }else{
            throw new ExceptionInInitializerError("There are no interface in factory for factory.database " + database + "!");
        }
    }

    public static IBasicService getTypeService() {
        if (database.equals("mysql")) {
            return new TypeService();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for factory.database " + database + "!");
        }
    }

    public static IBasicService getRateService() {
        if (database.equals("mysql")) {
            return new RateService();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for factory.database " + database + "!");
        }
    }

    public static IBasicService getProducerService() {
        if (database.equals("mysql")) {
            return new ProducerService();
        } else {
            throw new ExceptionInInitializerError("There are no interface in factory for factory.database " + database + "!");
        }
    }
}
