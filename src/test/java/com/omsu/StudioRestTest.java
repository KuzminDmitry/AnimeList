package com.omsu;

import com.omsu.core.Role;
import com.omsu.core.Studio;
import com.omsu.core.User;
import com.omsu.factory.ServiceFactory;
import com.omsu.iDAO.IUserService;
import com.omsu.token.Token;
import com.omsu.token.TokenMemorySingleton;
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
 * Created by Сева on 18.12.2016.
 */
public class StudioRestTest extends JerseyTest {
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
    public void restStudioTest() throws Exception{
        Cookie fakeCookie = new Cookie("authdata", "fakeCookie");

        Response response = target("studio").request().cookie(fakeCookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(401, response.getStatus());

        Cookie cookie = new Cookie("authdata", token.getJwt());

        response = target("studio").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());

        List<Studio> studios = response.readEntity(new GenericType<List<Studio>>() {});

        Integer startSize = studios.size();

        String name = "Studio name";
        String description = "Studio description";

        Studio studioForInsert = new Studio();
        studioForInsert.setName(name);
        studioForInsert.setDescription(description);

        response = target("studio").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).post(Entity.json(studioForInsert), Response.class);
        Assert.assertEquals(200, response.getStatus());

        studioForInsert = response.readEntity(Studio.class);

        response = target("studio").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());
        studios = response.readEntity(new GenericType<List<Studio>>() {});

        Integer afterInsertSize = studios.size();

        Assert.assertEquals(Integer.valueOf(afterInsertSize), Integer.valueOf(startSize + 1));

        response = target("studio").path(studioForInsert.getId().toString()).request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());
        Studio studioById = response.readEntity(Studio.class);

        Assert.assertEquals(studioForInsert.getId(), studioById.getId());
        Assert.assertEquals(studioForInsert.getName(), studioById.getName());
        Assert.assertEquals(studioForInsert.getDescription(), studioById.getDescription());

        Studio beforeUpdateStudio = new Studio();
        beforeUpdateStudio.setId(studioById.getId());
        beforeUpdateStudio.setName(studioById.getName());
        beforeUpdateStudio.setDescription(studioById.getDescription());

        beforeUpdateStudio.setName("New studio name");

        response = target("studio").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).put(Entity.json(beforeUpdateStudio), Response.class);
        Assert.assertEquals(200, response.getStatus());
        Studio afterUpdateStudio = response.readEntity(Studio.class);

        Assert.assertEquals(studioById.getId(), beforeUpdateStudio.getId());
        Assert.assertNotEquals(studioById.getName(), beforeUpdateStudio.getName());
        Assert.assertEquals(studioById.getDescription(), beforeUpdateStudio.getDescription());

        response = target("studio").queryParam("id", afterUpdateStudio.getId().toString()).request().cookie(cookie).accept(MediaType.APPLICATION_JSON).delete(Response.class);
        Assert.assertEquals(204, response.getStatus());

        response = target("studio").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());
        studios = response.readEntity(new GenericType<List<Studio>>() {});

        Integer endSize = studios.size();

        Assert.assertEquals(startSize, endSize);
    }

}
