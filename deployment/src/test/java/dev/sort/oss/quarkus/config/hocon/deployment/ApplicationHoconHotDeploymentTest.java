package dev.sort.oss.quarkus.config.hocon.deployment;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.Matchers.is;

public class ApplicationHoconHotDeploymentTest {

    @RegisterExtension
    static final QuarkusDevModeTest test = new QuarkusDevModeTest()
            .withApplicationRoot((jar) -> jar
                    .addAsResource("application.conf")
                    .addAsResource("application.hocon")
                    .addClass(FooResource.class));

    @Test
    public void testConfigReload() {
        RestAssured.when().get("/foo").then()
                .statusCode(200)
                .body(is("AAAA"));

        RestAssured.when().get("/foo2").then()
                .statusCode(200)
                .body(is("CCCC"));

        test.modifyResourceFile("application.conf", s -> s.replace("AAAA", "BBBB"));

        RestAssured.when().get("/foo").then()
                .statusCode(200)
                .body(is("BBBB"));

        test.modifyResourceFile("application.hocon", s -> s.replace("CCCC", "DDDD"));

        RestAssured.when().get("/foo2").then()
                .statusCode(200)
                .body(is("DDDD"));
    }
}
