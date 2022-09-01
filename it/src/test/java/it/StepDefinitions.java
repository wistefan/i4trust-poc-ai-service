package it;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.micronaut.http.HttpStatus;
import it.env.ParticipantEnvironment;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.awaitility.Awaitility;
import org.fiware.credentials.api.CredentialsManagementApi;
import org.fiware.credentials.model.IShareCredentialsVO;
import org.fiware.eas.ApiException;
import org.fiware.eas.api.EndpointConfigurationApi;
import org.fiware.eas.model.AuthCredentialsVO;
import org.fiware.eas.model.AuthTypeVO;
import org.fiware.eas.model.EndpointRegistrationVO;
import org.fiware.ngsi.model.EndpointVO;
import org.fiware.ngsi.model.EntityInfoVO;
import org.fiware.ngsi.model.EntityVO;
import org.fiware.ngsi.model.NotificationParamsVO;
import org.fiware.ngsi.model.PropertyVO;
import org.fiware.ngsi.model.SubscriptionVO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class StepDefinitions {

	private final UUID testId = UUID.randomUUID();

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	{
		OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	private static ParticipantEnvironment HAPPY_CATTLE;
	private static ParticipantEnvironment SMART_SHEPHERD;

	{
		HAPPY_CATTLE = new ParticipantEnvironment(
				"HappyCattle",
				getEndpointConfigurationApi("http://localhost:8070"),
				getCredentialsManagementApi("http://localhost:8060"),
				new OkHttpClient(),
				8080);
		SMART_SHEPHERD = new ParticipantEnvironment(
				"HappyCattle",
				getEndpointConfigurationApi("http://localhost:9070"),
				getCredentialsManagementApi("http://localhost:9060"),
				new OkHttpClient(),
				9080);
	}

	private static CredentialsManagementApi getCredentialsManagementApi(String address) {
		org.fiware.credentials.ApiClient cmaClient = new org.fiware.credentials.ApiClient();
		cmaClient.setBasePath(address);
		return new CredentialsManagementApi(cmaClient);
	}

	private static EndpointConfigurationApi getEndpointConfigurationApi(String address) {
		org.fiware.eas.ApiClient easClient = new org.fiware.eas.ApiClient();
		easClient.setBasePath(address);
		return new EndpointConfigurationApi(easClient);
	}

	@Given("the setup is running.")
	public void assert_setup_is_running() throws Exception {
//		Awaitility.await("Wait for happy cattle.")
//				.atMost(Duration.of(60, ChronoUnit.SECONDS))
//				.until(this::assertHappyCattleIsRunning);
//		Awaitility.await("Wait for smart shepard.")
//				.atMost(Duration.of(60, ChronoUnit.SECONDS))
//				.until(this::assertSmartShepardIsRunning);
	}

	@Given("endpoint auth is configured for happy cattle.")
	public void configure_endpoint_auth() throws Exception {
		EndpointRegistrationVO endpointRegistrationVO = new EndpointRegistrationVO()
				.authType(AuthTypeVO.ISHARE)
				.domain("release-name-kong-proxy.smartshepherd.svc.cluster.local")
				.port(9080)
				.useHttps(false)
				.authCredentials(new AuthCredentialsVO()
						.iShareClientId("EU.EORI.HAPPYCATTLE")
						.iShareIdpId("EU.EORI.SMARTSHEPHERD")
						.iShareIdpAddress("http://release-name-keyrock.smartshepherd.svc.cluster.local:9090/oauth2/token")
						.requestGrantType("urn:ietf:params:oauth:grant-type:jwt-bearer"));
		try {
			HAPPY_CATTLE.getEndpointConfigurationApi().createEndpoint(endpointRegistrationVO);
		} catch (ApiException e) {
			if (e.getCode() == HttpStatus.CONFLICT.getCode()) {
				log.info("Config already exists.");
			} else {
				// let everything else bubble.
				throw e;
			}
		}

		IShareCredentialsVO iShareCredentialsVO = new IShareCredentialsVO()
				.certificateChain(getCertificateChain("happycattle"))
				.signingKey(getSigningKey("happycattle"));
		try {
			HAPPY_CATTLE.getCredentialsManagementApi().postCredentials("EU.EORI.HAPPYCATTLE", iShareCredentialsVO);
		} catch (org.fiware.credentials.ApiException e) {
			if (e.getCode() == HttpStatus.CONFLICT.getCode()) {
				log.info("Config already exists.");
			} else {
				// let everything else bubble.
				throw e;
			}
		}
		// we need to give envoy a little time to reload
		Thread.sleep(10000);
	}

	@When("subscription to smart shepard is created.")
	public void subscription_is_created() throws Exception {
		EntityInfoVO entityInfoVO = new EntityInfoVO().id(URI.create(String.format("urn:ngsi-ld:cattle:%s", testId))).type("cattle");
		SubscriptionVO subscriptionVO = new SubscriptionVO()
				.type(SubscriptionVO.Type.SUBSCRIPTION)
				.entities(List.of(entityInfoVO))
				.geoQ(null)
				.isActive(null);
		NotificationParamsVO notificationParamsVO = new NotificationParamsVO()
				.endpoint(new EndpointVO()
						.setUri(URI.create("http://release-name-kong-proxy.smartshepherd.svc.cluster.local:9080/apollo/notification")));
		subscriptionVO.notification(notificationParamsVO);

		RequestBody requestBody = RequestBody.create(OBJECT_MAPPER.writeValueAsString(subscriptionVO), MediaType.get("application/json"));

		Request request = new Request.Builder()
				.url(String.format("http://localhost:%s/passthrough/ngsi-ld/v1/subscriptions/", HAPPY_CATTLE.getBrokerPort()))
				.method("POST", requestBody)
				.build();
		Response response = HAPPY_CATTLE.getBrokerApi().newCall(request).execute();

		assertTrue(response.code() >= 200 && response.code() < 300, "We expect any kind of successful response.");
	}

	@When("a happy cattle is created in happy-cattle.")
	public void update_at_happy_cattle() throws Exception {
		PropertyVO propertyVO = new PropertyVO()
				.type(PropertyVO.Type.PROPERTY)
				.value("happy");
		EntityVO entityVO = new EntityVO()
				.id(URI.create(String.format("urn:ngsi-ld:cattle:%s", testId)))
				.type("cattle")
				.location(null)
				.operationSpace(null)
				.observationSpace(null)
				.setAdditionalProperties(Map.of("mood", propertyVO));

		RequestBody requestBody = RequestBody.create(OBJECT_MAPPER.writeValueAsString(entityVO), MediaType.get("application/json"));
		Request request = new Request.Builder()
				.url(String.format("http://localhost:%s/passthrough/ngsi-ld/v1/entities/", HAPPY_CATTLE.getBrokerPort()))
				.method("POST", requestBody)
				.build();
		Response response = HAPPY_CATTLE.getBrokerApi().newCall(request).execute();

		assertTrue(response.code() >= 200 && response.code() < 300, "We expect any kind of successful response.");
	}

	@Then("the cattle is present in smart shepard.")
	public void assert_update_in_smart_shepard() throws Exception {
		Request request = new Request.Builder()
				.url(String.format("http://localhost:%s/passthrough/ngsi-ld/v1/entities/urn:ngsi-ld:cattle:%s", SMART_SHEPHERD.getBrokerPort(), testId))
				.build();
		Awaitility.await("Wait for the cattle").atMost(Duration.of(10, ChronoUnit.SECONDS)).until(() -> {
			Response response = SMART_SHEPHERD.getBrokerApi().newCall(request).execute();
			return response.isSuccessful();
		});

	}

//	private boolean assertHappyCattleIsRunning() {
//		return Stream.of(
//						"http://10.2.0.20:8080/version",
//						"http://10.2.0.22:1026/version",
//						"http://10.2.0.24:9090/health")
//				.allMatch(this::checkHealth);
//	}
//
//	private boolean assertSmartShepardIsRunning() {
//		return Stream.of(
//						"http://10.2.0.30:8080/version",
//						"http://10.2.0.31/api-umbrella/v1/health",
//						"http://10.2.0.32:1026/version",
//						"http://10.2.0.33:8080/health")
//				.allMatch(this::checkHealth);
//	}

	private boolean checkHealth(String url) {
		OkHttpClient healthCheckClient = new OkHttpClient();
		Request healthCheckRequest = new Request.Builder().url(url).build();
		try {
			Response healthResponse = healthCheckClient.newCall(healthCheckRequest).execute();
			if (healthResponse.isSuccessful()) {
				return true;
			}
		} catch (IOException e) {
			log.warn("Error when trying to health check {}.", url, e);
		}
		log.warn("{} is not available, yet.", url);
		return false;
	}

	private String getSigningKey(String participant) throws URISyntaxException, IOException {

		return Files.readString(
				Path.of(
						getClass().getResource(String.format("/certs/%s/key.pem", participant)).toURI()));
	}
	private String getCertificateChain(String participant) throws URISyntaxException, IOException {

		return Files.readString(
				Path.of(
						getClass().getResource(String.format("/certs/%s/cert.pem", participant)).toURI()));
	}

}
