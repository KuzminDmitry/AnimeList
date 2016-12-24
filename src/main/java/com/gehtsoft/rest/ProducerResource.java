package com.gehtsoft.rest;

import com.gehtsoft.auth.IAuthChecker;
import com.gehtsoft.core.Producer;
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
@Path("producer")
@Produces("application/json; charset=utf-8")
public class ProducerResource {

    final static Logger logger = Logger.getLogger("producerResource");

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
    public List<Producer> getAllProducers() throws Exception {
        logger.info("Get all producers started.");
        return (List) ThreadPoolSingleton.getInstance().basicThread(Producer.class, "getAll", null);

    }

    @GET
    @Path("{id}")
    public Producer getProducerById(@NotNull @PathParam("id") Integer id) throws Exception {
        if(token == null) return null;
        logger.info("Search started for producer by parameter: " + " id " + id + ".");
        return (Producer) ThreadPoolSingleton.getInstance().basicThread(Producer.class, "getById", id);
    }

    @PUT
    public Producer updateProducer(Producer producer) throws Exception {
        if(token == null) return null;
        logger.info("Update started for producer by singer track: " + producer + ".");
        return (Producer) ThreadPoolSingleton.getInstance().basicThread(Producer.class, "update", producer);
    }

    @DELETE
    public void deleteProducer(@NotNull @QueryParam("id") Integer id) throws Exception {
        if(token == null) return;
        logger.info("Delete started for producer by parameter: " + " id " + id + ".");
        ThreadPoolSingleton.getInstance().basicThread(Producer.class, "deleteById", id);
    }

    @POST
    public Producer insertProducer(Producer producer) throws Exception {
        if(token == null) return null;
        logger.info("Add new producer started: " + producer + ".");
        return (Producer) ThreadPoolSingleton.getInstance().basicThread(Producer.class, "add", producer);
    }
}

