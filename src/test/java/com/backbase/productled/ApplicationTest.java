package com.backbase.productled;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("it")
@DirtiesContext
public class ApplicationTest {

    static {
        System.setProperty("SIG_SECRET_KEY", "JWTSecretKeyDontUseInProduction!");
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void contextLoads() {
        Assert.assertNotNull(applicationContext);
    }

    @Test
    public void shouldLoadContext() {
        Application
            .main(new String[]{ "--spring.profiles.active=it"});

        Assert.assertNotNull(applicationContext);
    }
}
