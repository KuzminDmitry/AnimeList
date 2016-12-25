package com.omsu.rest;

import com.omsu.auth.IAuthChecker;
import com.omsu.core.Rate;
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

/**
 * Created by dkuzmin on 7/6/2016.
 */
@Path("rate")
@Produces("application/json; charset=utf-8")
public class RateResource {

    final static Logger logger = Logger.getLogger("rateResource");

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
    public List<Rate> getAllRates() throws Exception {
        logger.info("Get all rates started.");
        return (List) ThreadPoolSingleton.getInstance().basicThread(Rate.class, "getAll", null);

    }

    @GET
    @Path("{id}")
    public Rate getRateById(@NotNull @PathParam("id") Integer id) throws Exception {
        if(token == null) return null;
        logger.info("Search started for rate by parameter: " + " id " + id + ".");
        return (Rate) ThreadPoolSingleton.getInstance().basicThread(Rate.class, "getById", id);
    }

    @PUT
    public Rate updateRate(Rate rate) throws Exception {
        if(token == null) return null;
        logger.info("Update started for rate by singer track: " + rate + ".");
        return (Rate) ThreadPoolSingleton.getInstance().basicThread(Rate.class, "update", rate);
    }

    @DELETE
    public void deleteRate(@NotNull @QueryParam("id") Integer id) throws Exception {
        if(token == null) return;
        logger.info("Delete started for rate by parameter: " + " id " + id + ".");
        ThreadPoolSingleton.getInstance().basicThread(Rate.class, "deleteById", id);
    }

    @POST
    public Rate insertRate(Rate rate) throws Exception {
        if(token == null) return null;
        logger.info("Add new rate started: " + rate + ".");
        return (Rate) ThreadPoolSingleton.getInstance().basicThread(Rate.class, "add", rate);
    }
}

