package baseAPI;



import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import config.Configuration;
import config.ConfigurationManager;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.port;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.config.RestAssuredConfig.newConfig;

public abstract class BaseAPI {

	protected static Configuration configuration;

	@BeforeTest
	public static void beforeAllTests() {
		configuration = ConfigurationManager.getConfiguration();

		baseURI = configuration.baseURI();
		basePath = configuration.basePath();
		port = configuration.port();
		
		  // solve the problem with big decimal assertions
        config = newConfig().
            jsonConfig(jsonConfig().numberReturnType(NumberReturnType.BIG_DECIMAL)).
            sslConfig(new SSLConfig().allowAllHostnames());

        RestAssured.useRelaxedHTTPSValidation();

       

	}

}
