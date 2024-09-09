package com.pavan.microservices.product;

import io.restassured.RestAssured;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.MongoDBContainer;

@Import(TestcontainersConfiguration.class) // Ensure TestcontainersConfiguration is correctly configured
@Testcontainers // This annotation is needed for Testcontainers to manage the lifecycle
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	@Container // Use Testcontainers JUnit 5 support
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

	@LocalServerPort
	private int port;

	@BeforeEach
	void setup(){
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void shouldCreateProduct() {
		String requestBody = """
                {
                    "name": "iPhone 15",
                    "description": "iPhone 15 is a smartphone from Apple",
                    "price": 1000
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product") // Ensure the endpoint starts with '/'
				.then()
				.statusCode(201)
				.body("id", notNullValue()) // Simplify the matchers by importing statically
				.body("name", equalTo("iPhone 15"))
				.body("description", equalTo("iPhone 15 is a smartphone from Apple"))
				.body("price", equalTo(1000));
	}

	static {
		mongoDBContainer.start(); // Consider managing container lifecycle differently for more control
	}
}
