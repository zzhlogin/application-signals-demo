package org.springframework.samples.petclinic.customers.aws;

import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.bedrock.AmazonBedrock;
import com.amazonaws.services.bedrock.AmazonBedrockClientBuilder;
import com.amazonaws.services.bedrock.model.GetGuardrailRequest;
import com.amazonaws.services.bedrock.model.GetGuardrailResult;

import org.springframework.stereotype.Component;

@Component
public class BedrockV1Service {
    private static final String guardrailId = "bt4o77i015cu";
    final AmazonBedrock bedrockV1Client;

    public BedrockV1Service() {
        // AWS web identity is set for EKS clusters, if these are not set then use default credentials
        if (System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE") == null && System.getProperty("aws.webIdentityTokenFile") == null) {
            bedrockV1Client = AmazonBedrockClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1) // replace with your desired region
                    .build();
        }
        else {
            //            BasicAWSCredentials awsCreds = new BasicAWSCredentials("access_key_id", "secret_key_id");
            bedrockV1Client = AmazonBedrockClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1) // replace with your desired region
                    .withCredentials(WebIdentityTokenCredentialsProvider.create())
                    .build();
        }

    }

    public String getGuardrail() {
        GetGuardrailRequest request = new GetGuardrailRequest()
                .withGuardrailIdentifier(guardrailId);
        GetGuardrailResult response = bedrockV1Client.getGuardrail(request);

        return "Guardrail ID: " + response.toString();
    }
}
