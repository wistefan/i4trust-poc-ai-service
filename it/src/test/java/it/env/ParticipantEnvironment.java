package it.env;

import lombok.Data;
import okhttp3.OkHttpClient;
import org.fiware.credentials.api.CredentialsManagementApi;
import org.fiware.eas.api.EndpointConfigurationApi;

@Data
public class ParticipantEnvironment {

	private final String name;

	private final EndpointConfigurationApi endpointConfigurationApi;
	private final CredentialsManagementApi credentialsManagementApi;
	private final OkHttpClient brokerApi;
	private final int brokerPort;

}
