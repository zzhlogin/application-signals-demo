package org.springframework.samples.petclinic.customers.aws;

import org.springframework.samples.petclinic.customers.Util;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.GetGuardrailRequest;
import software.amazon.awssdk.services.bedrock.model.GetGuardrailResponse;

@Component
public class BedrockV2Service {
    private static final String guardrailId = "fri0qcmxe5tr";
    final BedrockClient bedrockV2Client;

    public BedrockV2Service() {
        // AWS web identity is set for EKS clusters, if these are not set then use default credentials
        if (System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE") == null && System.getProperty("aws.webIdentityTokenFile") == null) {
            bedrockV2Client = BedrockClient.builder()
                    .region(Region.of(Util.REGION_FROM_EC2))
                    .build();
        }
        else {
            bedrockV2Client = BedrockClient.builder()
                    .region(Region.of(Util.REGION_FROM_EKS))
                    .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                    .build();
        }

    }

    public String getGuardrail() {
        try {
            GetGuardrailRequest request = GetGuardrailRequest.builder()
                    .guardrailIdentifier(guardrailId)
                    .build();
            GetGuardrailResponse response = bedrockV2Client.getGuardrail(request);
            return response.toString();
        } catch (Exception e) {
            System.out.printf("Failed to GetGuardrailRequest: %s. Error: %s%n", guardrailId, e.getMessage());
            throw e;
        }
    }
}
