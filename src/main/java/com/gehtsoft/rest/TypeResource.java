package com.gehtsoft.rest;

import com.gehtsoft.auth.IAuthChecker;
import com.gehtsoft.core.Type;
import com.gehtsoft.factory.SecurityFactory;
import com.gehtsoft.threadPool.ThreadPoolSingleton;
import com.gehtsoft.token.Token;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;

/**
 * Created by dkuzmin on 7/6/2016.
 */
@Path("type")
@Produces("application/json; charset=utf-8")
public class TypeResource {

    final static Logger logger = Logger.getLogger("typeResource");

    @Context
    private Token token = null;

    @Context
    ServletContext servletContext;

    @Context
    HttpServletResponse response;

    @Context
    HttpServletRequest request;

    @Context
    public void checkAuthenticate(HttpServletRequest httpServletRequest) throws Exception {
        IAuthChecker authChecker = SecurityFactory.getAuthChecker();
        if ((token = authChecker.validate(this.request)) == null) {
            response.sendError(401);
        }
    }

    @GET
    public List<Type> getAllTypes() throws Exception {
        if(token == null) return null;
        logger.info("Get all types started.");
        return (List) ThreadPoolSingleton.getInstance().basicThread(Type.class, "getAll", null);

    }

    @GET
    @Path("{id}")
    public Type getTypeById(@NotNull @PathParam("id") Integer id) throws Exception {
        if(token == null) return null;
        logger.info("Search started for type by parameter: " + " id " + id + ".");
        return (Type) ThreadPoolSingleton.getInstance().basicThread(Type.class, "getById", id);
    }

    @PUT
    public Type updateType(Type type) throws Exception {
        if(token == null) return null;
        logger.info("Update started for type by singer track: " + type + ".");
        return (Type) ThreadPoolSingleton.getInstance().basicThread(Type.class, "update", type);
    }

    @DELETE
    public void deleteType(@NotNull @QueryParam("id") Integer id) throws Exception {
        if(token == null) return;
        logger.info("Delete started for type by parameter: " + " id " + id + ".");
        ThreadPoolSingleton.getInstance().basicThread(Type.class, "deleteById", id);
    }

    @POST
    public Type insertType(Type type) throws Exception {
        if(token == null) return null;
        logger.info("Add new type started: " + type + ".");
        return (Type) ThreadPoolSingleton.getInstance().basicThread(Type.class, "add", type);
    }
}

