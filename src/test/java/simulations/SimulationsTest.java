package simulations;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.*;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.hamcrest.core.Is;
import org.testng.annotations.Test;

import baseAPI.BaseAPI;

import dataFactory.SimulationDataFactory;
import io.restassured.http.ContentType;
import payload.Simulation;





public class SimulationsTest extends BaseAPI{
	
	
	
	//Should validate one Existing simulation
	@Test
	 void getOneExistingSimulation() {
        var existingSimulation = SimulationDataFactory.oneExistingSimulation();
       
        given().
            pathParam("cpf", existingSimulation.getCpf()).
        when().
            get("/simulations/{cpf}").
        then().
            statusCode(SC_OK).
            body(
                "name", equalTo(existingSimulation.getName()),
                "cpf", equalTo(existingSimulation.getCpf()),
                "email", equalTo(existingSimulation.getEmail()),
                "amount", equalTo(existingSimulation.getAmount()),
                "installments", equalTo(existingSimulation.getInstallments()),
                "insurance", equalTo(existingSimulation.getInsurance())
            );
    }
	
	//Should validate all existing simulations
	@Test
	void getAllExistingSimulations() {
		var existingSimulations = SimulationDataFactory.allExistingSimulations();

	System.out.println("size of simulations"+existingSimulations.size());
	System.out.println("size of simulations"+existingSimulations.size());
	System.out.println("size of simulations"+existingSimulations.size());
	System.out.println("size of simulations"+existingSimulations.size());
		
		var requestedSimulations = when().
				get("/simulations/").
				then().
				statusCode(SC_OK).
				
				extract().as(Simulation[].class);
		
		assertEquals(List.of(requestedSimulations), existingSimulations);
	}
	
	//Should filter by name a non existing simulation
	@Test
	void simulationByNameNotFound() {
		given().
			queryParam("name", SimulationDataFactory.nonExistingName()).
		when().
			get("/simulations/").
		then().statusCode(SC_NOT_FOUND);
	}
	
	//Should find a simulation filtered by name
	@Test
	void returnSimulationByName() {
		
		var oneExistingSimulation = SimulationDataFactory.oneExistingSimulation();
		
		given().
			queryParam("name", oneExistingSimulation.getName()).
		when().
			get("/simulations/").
		then().
			body("[0].name", equalTo(oneExistingSimulation.getName()),
					 "[0].cpf", equalTo(oneExistingSimulation.getCpf()),
		                "[0].email", equalTo(oneExistingSimulation.getEmail()),
		                "[0].amount", equalTo(oneExistingSimulation.getAmount()),
		                "[0].installments", equalTo(oneExistingSimulation.getInstallments()),
		                "[0].insurance", equalTo(oneExistingSimulation.getInsurance()));
			
	}
	
	//Should create a new simulation
	@Test
	void createNewSimulatiopnSuccessfully() {
		var simulation = SimulationDataFactory.validSimulation();
		
		given().
			contentType(ContentType.JSON).
			body(simulation).
		when().
			post("/simulations/").
		then().
			statusCode(SC_CREATED).
			assertThat().header("Location", containsString("http://localhost:8088/api/v1/simulations/"));
			
		
	}
	
	//Should validate a CPF duplication
	@Test
	void simulationWithDuplicatedCPF() {
		var existingSimulation = SimulationDataFactory.oneExistingSimulation();
		
		given().
			contentType(ContentType.JSON).
			body(existingSimulation).
		when().
			post("/simulaions/").
		then().
		statusCode(SC_CONFLICT).// returns 404 (SC_NOT_FOUND)instead of 409(SC_CONFLICT)------>api defect
		body("message", is("CPF already exists"));//return " no message Available instead of "CPF already Exists"-------->api defect
		
	}
	
	//should delete an existing simulation
	@Test
	void deleteExistingSimulation() {
		var existingSimulation = SimulationDataFactory.oneExistingSimulation();
		System.out.println(existingSimulation.getCpf());
		
		given().
			pathParam("cpf", existingSimulation.getCpf()).
		when().
			delete("/simulations/{cpf}").
		then().
			statusCode(SC_NO_CONTENT);
		
	}
	
	//should update an existing simulation
	@Test
	void updateExisttingSimulation() {
		var existingSimulation = SimulationDataFactory.oneExistingSimulation();
		System.out.println(existingSimulation.getCpf());
		
		var simulation = SimulationDataFactory.validSimulation();
		simulation.setCpf(existingSimulation.getCpf());
		simulation.setInsurance(existingSimulation.getInsurance());
		
		var requestedSimulation = 
				given().
					contentType(ContentType.JSON).
					pathParam("cpf", existingSimulation.getCpf()).
					body(simulation).
				when().
					put("simulations/{cpf}").
				then().
					extract().
					as(SimulationDataFactory.class);
		
		assertEquals(simulation, requestedSimulation);
	}
	
	
	//should return  status code of 404 (NOT FOUND) when trying to update a simulation with a non existing CPF
	@Test
	void updateSimulationWithNonExistingCPF() {
		var simulation = SimulationDataFactory.validSimulation();
		
		given().
			contentType(ContentType.JSON).
			pathParam("cpf", SimulationDataFactory.notExistentCpf()).
			body(simulation).
		when().
			put("/simulation/{cpf}").
		then().
		assertThat().statusCode(SC_NOT_FOUND);
	}
	
	

}
