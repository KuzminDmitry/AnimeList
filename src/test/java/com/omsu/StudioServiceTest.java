package com.omsu;

import com.omsu.core.Studio;
import com.omsu.factory.ServiceFactory;
import com.omsu.iDAO.IBasicService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Сева on 18.12.2016.
 */
public class StudioServiceTest {
    @Test
    public void serviceStudioTest() throws Exception{
        IBasicService studioService = ServiceFactory.getStudioService();

        List<Studio> studios = studioService.getAll();

        Integer startSize = studios.size();

        String name = "Studio name";
        String description = "Studio description";

        Studio studioForInsert = new Studio();
        studioForInsert.setName(name);
        studioForInsert.setDescription(description);

        studioForInsert = (Studio)studioService.add(studioForInsert);

        studios = studioService.getAll();
        Integer afterInsertSize = studios.size();

        Assert.assertEquals(Integer.valueOf(afterInsertSize), Integer.valueOf(startSize + 1));

        Studio studioById = (Studio)studioService.getById(studioForInsert.getId());

        Assert.assertEquals(studioForInsert.getId(), studioById.getId());
        Assert.assertEquals(studioForInsert.getName(), studioById.getName());
        Assert.assertEquals(studioForInsert.getDescription(), studioById.getDescription());

        Studio beforeUpdateStudio = new Studio();
        beforeUpdateStudio.setId(studioById.getId());
        beforeUpdateStudio.setName(studioById.getName());
        beforeUpdateStudio.setDescription(studioById.getDescription());

        beforeUpdateStudio.setName("New studio name");

        Studio afterUpdateStudio = (Studio)studioService.update(beforeUpdateStudio);

        Assert.assertEquals(studioById.getId(), beforeUpdateStudio.getId());
        Assert.assertNotEquals(studioById.getName(), beforeUpdateStudio.getName());
        Assert.assertEquals(studioById.getDescription(), beforeUpdateStudio.getDescription());

        studioService.deleteById(afterUpdateStudio.getId());

        studios = studioService.getAll();

        Integer endSize = studios.size();

        Assert.assertEquals(startSize, endSize);
    }
}
