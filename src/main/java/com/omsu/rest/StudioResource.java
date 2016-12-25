package com.omsu.rest;

import com.omsu.auth.IAuthChecker;
import com.omsu.core.Studio;
import com.omsu.factory.SecurityFactory;
import com.omsu.threadPool.ThreadPoolSingleton;
import com.omsu.token.Token;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;


@Path("studio")
@Produces("application/json; charset=utf-8")
public class StudioResource {
    final static Logger logger = Logger.getLogger("studioResource");

    @Context
    private Token token = null;

    @Context
    ServletContext servletContext;

    @Context
    HttpServletRequest request;

    @Context
    HttpServletResponse response;

    @Context
    public void checkAuthenticate(HttpServletRequest httpServletRequest) throws Exception {
        IAuthChecker authChecker = SecurityFactory.getAuthChecker();
        if ((token = authChecker.validate(this.request)) == null) {
            response.sendError(401);
        }
    }
    @GET
    public List<Studio> getAllStudios() throws Exception {
        logger.info("Get all studios started.");
        return (List) ThreadPoolSingleton.getInstance().basicThread(Studio.class, "getAll", null);
    }

    @GET
    @Path("{id}")
    public Studio getStudioByID(@NotNull @PathParam("id") Integer id) throws Exception{
        if(token == null) return null;
        logger.info("Search started for studio by parameter: " + " id " + id + ".");
        return (Studio)ThreadPoolSingleton.getInstance().basicThread(Studio.class,"getById",id);
    }

    @PUT
    public Studio updateStudio(Studio studio) throws Exception {
        if(token == null) return null;
        logger.info("Update started for studio: " + studio + ".");
        return (Studio) ThreadPoolSingleton.getInstance().basicThread(Studio.class, "update", studio);
    }

    @DELETE
    public void deleteStudio(@NotNull @QueryParam("id") Integer id) throws Exception {
        if(token == null) return;
        logger.info("Delete started for studio by parameter: " + " id " + id + ".");
        ThreadPoolSingleton.getInstance().basicThread(Studio.class, "deleteById", id);
    }

    @POST
    public Studio insertStudio(Studio studio) throws Exception {
        if(token == null) return null;
        logger.info("Add new studio started: " + studio + ".");
        return (Studio) ThreadPoolSingleton.getInstance().basicThread(Studio.class, "add", studio);
    }
}
