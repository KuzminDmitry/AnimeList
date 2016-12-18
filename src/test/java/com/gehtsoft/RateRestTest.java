package com.gehtsoft;

import com.gehtsoft.core.Rate;
import com.gehtsoft.core.Role;
import com.gehtsoft.core.User;
import com.gehtsoft.factory.ServiceFactory;
import com.gehtsoft.iDAO.IUserService;
import com.gehtsoft.token.Token;
import com.gehtsoft.token.TokenMemorySingleton;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dkuzmin on 7/22/2016.
 */
public class RateRestTest extends JerseyTest {

    @Override
    protected DeploymentContext configureDeployment() {
        return ServletDeploymentContext.builder(new ResourceConfig())
                .initParam(ServerProperties.PROVIDER_PACKAGES, this.getClass().getPackage().getName())
                .build();
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }

    private Token token;

    private User user = new User();

    IUserService userService = ServiceFactory.getUserService();

    @Before
    public void beforeMethod() throws Exception{
        user.setUserName("newuser");
        user.setPassword("hispassword");
        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(Role.USER);
        user.setRoleIds(roleIds);
        List<String> roleNames = new ArrayList<>();
        roleNames.add("User");
        user.setRoleNames(roleNames);

        user = (User)userService.add(user);

        token = TokenMemorySingleton.getInstance().addToken(user);
    }

    @After
    public void afterMethod() throws Exception {
        if(user.getId()!=null){
            userService.deleteById(user.getId());
        }
        TokenMemorySingleton.getInstance().deleteToken(token);
    }

    @Test
    public void restRateTest() throws Exception {

        Cookie fakeCookie = new Cookie("authdata", "fakeCookie");

        Response response = target("rate").request().cookie(fakeCookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(401, response.getStatus());

        Cookie cookie = new Cookie("authdata", token.getJwt());

        response = target("rate").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());

        List<Rate> rates = response.readEntity(new GenericType<List<Rate>>() {});

        Integer startSize = rates.size();

        String name = "Наименование жанра";
        String description = "Описание жанра";

        Rate rateForInsert = new Rate();
        rateForInsert.setName(name);
        rateForInsert.setDescription(description);

        response = target("rate").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).post(Entity.json(rateForInsert), Response.class);
        Assert.assertEquals(200, response.getStatus());

        rateForInsert = response.readEntity(Rate.class);

        response = target("rate").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());
        rates = response.readEntity(new GenericType<List<Rate>>() {});

        Integer afterInsertSize = rates.size();

        Assert.assertEquals(Integer.valueOf(afterInsertSize), Integer.valueOf(startSize + 1));

        response = target("rate").path(rateForInsert.getId().toString()).request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());
        Rate rateById = response.readEntity(Rate.class);

        Assert.assertEquals(rateForInsert.getId(), rateById.getId());
        Assert.assertEquals(rateForInsert.getName(), rateById.getName());
        Assert.assertEquals(rateForInsert.getDescription(), rateById.getDescription());

        Rate beforeUpdateRate = new Rate();
        beforeUpdateRate.setId(rateById.getId());
        beforeUpdateRate.setName(rateById.getName());
        beforeUpdateRate.setDescription(rateById.getDescription());

        beforeUpdateRate.setName("Новое наименование жанра");

        response = target("rate").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).put(Entity.json(beforeUpdateRate), Response.class);
        Assert.assertEquals(200, response.getStatus());
        Rate afterUpdateRate = response.readEntity(Rate.class);

        Assert.assertEquals(rateById.getId(), beforeUpdateRate.getId());
        Assert.assertNotEquals(rateById.getName(), beforeUpdateRate.getName());
        Assert.assertEquals(rateById.getDescription(), beforeUpdateRate.getDescription());

        response = target("rate").queryParam("id", afterUpdateRate.getId().toString()).request().cookie(cookie).accept(MediaType.APPLICATION_JSON).delete(Response.class);
        Assert.assertEquals(204, response.getStatus());

        response = target("rate").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());
        rates = response.readEntity(new GenericType<List<Rate>>() {});

        Integer endSize = rates.size();

        Assert.assertEquals(startSize, endSize);

    }

}
