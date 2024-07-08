// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package org.springframework.samples.petclinic.customers.aws;

import org.springframework.samples.petclinic.customers.Util;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Component
public class SecretsManagerV2Service {
    private static final String SECRET_NAME = "mySecretName1";
    final SecretsManagerClient secretsManagerClientV2;

    public SecretsManagerV2Service() {
        // AWS web identity is set for EKS clusters, if these are not set then use default credentials
        if (System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE") == null && System.getProperty("aws.webIdentityTokenFile") == null) {
            secretsManagerClientV2 = SecretsManagerClient.builder()
                    .region(Region.of(Util.REGION_FROM_EC2))
                    .build();
        }
        else {
            secretsManagerClientV2 = SecretsManagerClient.builder()
                    .region(Region.of(Util.REGION_FROM_EKS))
                    .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                    .build();
        }

    }

    public String getSecretValue() {
        try {
            System.out.printf("Getting secret value for secret: " + SECRET_NAME);
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(SECRET_NAME)
                    .build();
            GetSecretValueResponse valueResponse = secretsManagerClientV2.getSecretValue(valueRequest);
            System.out.printf("Getting secret value for secret response: " + valueResponse.toString());
            return valueResponse.toString();
        } catch (Exception e) {
            System.out.printf("Failed to get secret value for secret: %s. Error: %s%n", SECRET_NAME, e.getMessage());
            throw e;
        }
    }
}
