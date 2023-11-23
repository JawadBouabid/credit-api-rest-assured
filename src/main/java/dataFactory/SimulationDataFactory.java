package dataFactory;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

import org.apache.http.HttpStatus;

import com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;

import net.datafaker.Faker;
import payload.Simulation;
import payload.SimulationBuilder;

import static io.restassured.RestAssured.when;

public class SimulationDataFactory {
	
	public static final int MIN_AMOUNT = 1000;
	public static final int MAX_AMOUNT = 40000;
	public static final int MIN_INSALLMENTS = 2;
	public static final int MAX_INSTALLMENTS = 48;
	public static final Faker faker = new Faker();
	
	private SimulationDataFactory() {}
	
	public static String nonExistingName() {
		String nonExistingName = faker.name().firstName();
		return nonExistingName;
	}
	
	 public static String notExistentCpf() {
	        String nonExistentCpf = faker.cpf().valid();
	        return nonExistentCpf;
	    }
	 
	 public static Simulation validSimulation() {
		 return newSimulation();
	 }
	 
	 public static Simulation oneExistingSimulation() {
		 var simulations = allSimulationFromAPI();
		 Simulation oneExistingSimulation = simulations.get(new SecureRandom().nextInt(simulations.size()));
		 return oneExistingSimulation;
		 
	 }
	 
	 public static List<Simulation> allExistingSimulations(){
		 return allSimulationFromAPI();
	 }
	 
	 public static List<Simulation> allSimulationFromAPI(){
		 var simulations = 
				 when()
				 .get("/simulations/")
				 .then()
				 .statusCode(HttpStatus.SC_OK)
				 .extract()
				 .as(Simulation[].class);
		 
		 return List.of(simulations);
	 }
	 
	 public static Simulation newSimulation() {
		 var newSimulation = new SimulationBuilder()
				 .name(faker.name().nameWithMiddle())
				 .cpf(faker.cpf().valid())
				 .email(faker.internet().emailAddress())
				 .amount(new BigDecimal(faker.number().numberBetween(MIN_AMOUNT, MAX_AMOUNT)))
				 .installments(faker.number().numberBetween(MIN_INSALLMENTS, MAX_INSTALLMENTS))
				 .insurance(faker.bool().bool())
				 .build();
		 return newSimulation;
				 
	 }

}
