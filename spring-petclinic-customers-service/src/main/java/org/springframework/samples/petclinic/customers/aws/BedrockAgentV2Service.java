// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package org.springframework.samples.petclinic.customers.aws;

import org.springframework.samples.petclinic.customers.Util;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.GetAgentRequest;
import software.amazon.awssdk.services.bedrockagent.model.GetAgentResponse;
import software.amazon.awssdk.services.bedrockagent.model.GetDataSourceRequest;
import software.amazon.awssdk.services.bedrockagent.model.GetDataSourceResponse;
import software.amazon.awssdk.services.bedrockagent.model.GetKnowledgeBaseRequest;
import software.amazon.awssdk.services.bedrockagent.model.GetKnowledgeBaseResponse;

@Component
public class BedrockAgentV2Service {
    private static final String agentId = "M8WPRK0AIA";
    private static final String datasourceId = "SF2CAIKK32";
    private static final String knowledgeBaseId = "FIFFBKOOWC";
    final BedrockAgentClient bedrockAgentV2Client;

    public BedrockAgentV2Service() {
        // AWS web identity is set for EKS clusters, if these are not set then use default credentials
        if (System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE") == null && System.getProperty("aws.webIdentityTokenFile") == null) {
            bedrockAgentV2Client = BedrockAgentClient.builder()
                    .region(Region.of(Util.REGION_FROM_EC2))
                    .build();
        }
        else {
            bedrockAgentV2Client = BedrockAgentClient.builder()
                    .region(Region.of(Util.REGION_FROM_EKS))
                    .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                    .build();
        }

    }

    public String bedrockAgentGetAgentV2() {
        try {
            System.out.printf("GetAgentRequest: " + agentId);
            GetAgentRequest request = GetAgentRequest.builder()
                    .agentId(agentId)
                    .build();
            GetAgentResponse response = bedrockAgentV2Client.getAgent(request);
            System.out.printf("GetAgentResponse: " + response.toString());
            return response.toString();
        } catch (Exception e) {
            System.out.printf("Failed to GetAgentRequest: %s. Error: %s%n", agentId, e.getMessage());
            throw e;
        }
    }

    public String bedrockAgentGetDatasourceV2() {
        try {
            System.out.printf("GetDataSourceRequest: " + datasourceId);
            GetDataSourceRequest request = GetDataSourceRequest.builder()
                    .dataSourceId(datasourceId)
                    .knowledgeBaseId(knowledgeBaseId)
                    .build();
            GetDataSourceResponse response = bedrockAgentV2Client.getDataSource(request);
            System.out.printf("GetDataSourceResponse: " + response.toString());
            return response.toString();
        } catch (Exception e) {
            System.out.printf("Failed to GetDataSourceRequest: %s. Error: %s%n", datasourceId, e.getMessage());
            throw e;
        }
    }

    public String bedrockAgentGetKnowledgeBaseV2() {
        try {
            System.out.printf("GetKnowledgeBaseRequest: " + knowledgeBaseId);
            GetKnowledgeBaseRequest request = GetKnowledgeBaseRequest.builder()
                    .knowledgeBaseId(knowledgeBaseId)
                    .build();
            GetKnowledgeBaseResponse response = bedrockAgentV2Client.getKnowledgeBase(request);
            System.out.printf("GetKnowledgeBaseResponse: " + response.toString());
            return response.toString();
        } catch (Exception e) {
            System.out.printf("Failed to GetKnowledgeBaseRequest: %s. Error: %s%n", knowledgeBaseId, e.getMessage());
            throw e;
        }
    }
}
