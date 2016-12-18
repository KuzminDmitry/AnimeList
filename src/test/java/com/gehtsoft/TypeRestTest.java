package com.gehtsoft;

import com.gehtsoft.core.Type;
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
public class TypeRestTest extends JerseyTest {

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
    public void restTypeTest() throws Exception {

        Cookie fakeCookie = new Cookie("authdata", "fakeCookie");

        Response response = target("type").request().cookie(fakeCookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(401, response.getStatus());

        Cookie cookie = new Cookie("authdata", token.getJwt());

        response = target("type").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());

        List<Type> types = response.readEntity(new GenericType<List<Type>>() {});

        Integer startSize = types.size();

        String name = "Наименование жанра";
        String description = "Описание жанра";

        Type typeForInsert = new Type();
        typeForInsert.setName(name);
        typeForInsert.setDescription(description);

        response = target("type").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).post(Entity.json(typeForInsert), Response.class);
        Assert.assertEquals(200, response.getStatus());

        typeForInsert = response.readEntity(Type.class);

        response = target("type").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());
        types = response.readEntity(new GenericType<List<Type>>() {});

        Integer afterInsertSize = types.size();

        Assert.assertEquals(Integer.valueOf(afterInsertSize), Integer.valueOf(startSize + 1));

        response = target("type").path(typeForInsert.getId().toString()).request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());
        Type typeById = response.readEntity(Type.class);

        Assert.assertEquals(typeForInsert.getId(), typeById.getId());
        Assert.assertEquals(typeForInsert.getName(), typeById.getName());
        Assert.assertEquals(typeForInsert.getDescription(), typeById.getDescription());

        Type beforeUpdateType = new Type();
        beforeUpdateType.setId(typeById.getId());
        beforeUpdateType.setName(typeById.getName());
        beforeUpdateType.setDescription(typeById.getDescription());

        beforeUpdateType.setName("Новое наименование жанра");

        response = target("type").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).put(Entity.json(beforeUpdateType), Response.class);
        Assert.assertEquals(200, response.getStatus());
        Type afterUpdateType = response.readEntity(Type.class);

        Assert.assertEquals(typeById.getId(), beforeUpdateType.getId());
        Assert.assertNotEquals(typeById.getName(), beforeUpdateType.getName());
        Assert.assertEquals(typeById.getDescription(), beforeUpdateType.getDescription());

        response = target("type").queryParam("id", afterUpdateType.getId().toString()).request().cookie(cookie).accept(MediaType.APPLICATION_JSON).delete(Response.class);
        Assert.assertEquals(204, response.getStatus());

        response = target("type").request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get(Response.class);
        Assert.assertEquals(200, response.getStatus());
        types = response.readEntity(new GenericType<List<Type>>() {});

        Integer endSize = types.size();

        Assert.assertEquals(startSize, endSize);

    }

}
