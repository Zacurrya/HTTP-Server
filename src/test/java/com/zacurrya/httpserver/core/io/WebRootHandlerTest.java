package com.zacurrya.httpserver;

import com.zacurrya.httpserver.core.io.WebRootHandler;
import com.zacurrya.httpserver.core.io.WebRootNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance( TestInstance.Lifecycle.PER_CLASS)
public class WebRootHandlerTest {

    @BeforeAll
    public void beforeClass() {

    }

    @Test
    void constructorGoodPath() {
        try {
            WebRootHandler handler = new WebRootHandler("src/main/resources/webroot");
        } catch (WebRootNotFoundException e) {
            fail(e);
        }
    }

    @Test
    void constructorBadPath() {
        try {
            WebRootHandler handler = new WebRootHandler("src/main/resources/webroot");
            fail();
        } catch (WebRootNotFoundException e) {

        }
    }

    // --
}
