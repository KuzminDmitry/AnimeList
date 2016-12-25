package com.omsu.factory;

import com.omsu.token.Token;
import com.omsu.core.*;
import com.omsu.iDAO.ITokenService;
import com.omsu.iDAO.IBasicService;
import com.omsu.iDAO.IUserService;

/**`
 * Created by dkuzmin on 8/15/2016.
 */
public class EntityFactory {

    public static IBasicService getBasicService(Class c) {
        if (c.equals(Genre.class)) {
            return ServiceFactory.getGenreService();
        } else if (c.equals(Type.class)) {
            return ServiceFactory.getTypeService();
        } else if (c.equals(Rate.class)) {
            return ServiceFactory.getRateService();
        } else if (c.equals(Studio.class)) {
            return ServiceFactory.getStudioService();
        } else if (c.equals(Producer.class)) {
            return ServiceFactory.getProducerService();
        } else {
            throw new ExceptionInInitializerError("There are no method in factory for " + c + "!");
        }
    }


    public static IUserService getUserService(Class c) {
        if (c.equals(User.class)) {
            return ServiceFactory.getUserService();
        } else {
            throw new ExceptionInInitializerError("There are no method in factory for " + c + "!");
        }
    }

    public static ITokenService getTokenService(Class c) {
        if (c.equals(Token.class)) {
            return ServiceFactory.getAuthService();
        } else {
            throw new ExceptionInInitializerError("There are no method in factory for " + c + "!");
        }
    }
}
