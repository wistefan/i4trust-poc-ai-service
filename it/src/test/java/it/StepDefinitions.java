package it;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.awaitility.Awaitility;
import org.fiware.credentials.api.CredentialsManagementApi;
import org.fiware.credentials.model.IShareCredentialsVO;
import org.fiware.eas.ApiClient;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class StepDefinitions {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	{
		OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	private static EndpointConfigurationApi endpointConfigurationApi;

	{
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath("http://10.2.0.24:9090");
		endpointConfigurationApi = new EndpointConfigurationApi(apiClient);
	}

	private static CredentialsManagementApi credentialsManagementApi;

	{
		org.fiware.credentials.ApiClient apiClient = new org.fiware.credentials.ApiClient();
		apiClient.setBasePath("http://10.2.0.25:7070");
		credentialsManagementApi = new CredentialsManagementApi(apiClient);
	}

	private static OkHttpClient entitiesApi;

	{
		entitiesApi = new OkHttpClient();
	}

	@Given("the setup is running.")
	public void assert_setup_is_running() throws Exception {
		Awaitility.await("Wait for happy cattle.")
				.atMost(Duration.of(60, ChronoUnit.SECONDS))
				.until(this::assertHappyCattleIsRunning);
		Awaitility.await("Wait for smart shepard.")
				.atMost(Duration.of(60, ChronoUnit.SECONDS))
				.until(this::assertSmartShepardIsRunning);
	}

	@Given("endpoint auth is configured for happy cattle.")
	public void setup_sidecar_in_docker() throws Exception {
		EndpointRegistrationVO endpointRegistrationVO = new EndpointRegistrationVO()
				.authType(AuthTypeVO.ISHARE)
				.domain("10.2.0.33")
				.port(8080)
				.useHttps(false)
				.authCredentials(new AuthCredentialsVO()
						.iShareClientId("EU.EORI.NLHAPPYPETS")
						.iShareIdpId("EU.EORI.NLPACKETDEL")
						.iShareIdpAddress("http://10.2.0.30:8080/oauth2/token")
						.requestGrantType("client_credentials"));
		endpointConfigurationApi.createEndpoint(endpointRegistrationVO);

		String signingKey = System.getenv("HAPPY_CATTLE_KEY");
		String certificateChain = System.getenv("HAPPY_CATTLE_CRT");
					IShareCredentialsVO iShareCredentialsVO = new IShareCredentialsVO()
				.certificateChain(certificateChain)
				.signingKey(signingKey);

		credentialsManagementApi.postCredentials("EU.EORI.NLHAPPYPETS", iShareCredentialsVO);

		// we need to give envoy a little time to reload
		Thread.sleep(10000);
	}

	@When("subscription to smart shepard is created.")
	public void subscription_is_created() throws Exception {
		EntityInfoVO entityInfoVO = new EntityInfoVO().id(URI.create("urn:ngsi-ld:test:test")).type("test");
		SubscriptionVO subscriptionVO = new SubscriptionVO()
				.type(SubscriptionVO.Type.SUBSCRIPTION)
				.entities(List.of(entityInfoVO))
				.geoQ(null)
				.isActive(null);
		NotificationParamsVO notificationParamsVO = new NotificationParamsVO()
				.endpoint(new EndpointVO()
						.setUri(URI.create("http://10.2.0.33:8080/notification")));
		subscriptionVO.notification(notificationParamsVO);

		RequestBody requestBody = RequestBody.create(OBJECT_MAPPER.writeValueAsString(subscriptionVO), MediaType.get("application/json"));

		Request request = new Request.Builder()
				.url("http://10.2.0.22:1026/ngsi-ld/v1/subscriptions/")
				.method("POST", requestBody)
				.build();
		Response response = entitiesApi.newCall(request).execute();

		assertTrue(response.code() >= 200 && response.code() < 300, "We expect any kind of successful response.");
	}

	@When("a happy cattle is created in happy-cattle.")
	public void update_at_happy_cattle() throws Exception {
		PropertyVO propertyVO = new PropertyVO()
				.type(PropertyVO.Type.PROPERTY)
				.value("happy");
		EntityVO entityVO = new EntityVO()
				.id(URI.create("urn:ngsi-ld:cattle:happy-cattle"))
				.type("cattle")
				.location(null)
				.operationSpace(null)
				.observationSpace(null)
				.setAdditionalProperties(Map.of("mood", propertyVO));

		RequestBody requestBody = RequestBody.create(OBJECT_MAPPER.writeValueAsString(entityVO), MediaType.get("application/json"));
		Request request = new Request.Builder()
				.url("http://10.2.0.22:1026/ngsi-ld/v1/entities/")
				.method("POST", requestBody)
				.build();
		Response response = entitiesApi.newCall(request).execute();

		assertTrue(response.code() >= 200 && response.code() < 300, "We expect any kind of successful response.");
	}

	@Then("the cattle is present in smart shepard.")
	public void assert_update_in_smart_shepard() throws Exception {
		Request request = new Request.Builder()
				.url("http://10.2.0.32:1026/ngsi-ld/v1/entities/urn:ngsi-ld:test:test")
				.build();
		Response response = entitiesApi.newCall(request).execute();
		assertTrue(response.isSuccessful(), "We should get something.");
	}

	private boolean assertHappyCattleIsRunning() {
		return Stream.of(
						"http://10.2.0.20:8080/version",
						"http://10.2.0.21/api-umbrella/v1/health",
						"http://10.2.0.22:1026/version",
						"http://10.2.0.24:9090/health")
				.allMatch(this::checkHealth);
	}

	private boolean assertSmartShepardIsRunning() {
		return Stream.of(
						"http://10.2.0.30:8080/version",
						"http://10.2.0.31/api-umbrella/v1/health",
						"http://10.2.0.32:1026/version",
						"http://10.2.0.33:8080/health")
				.allMatch(this::checkHealth);
	}

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
		log.warn("{} is not available, yet.");
		return false;
	}
}
