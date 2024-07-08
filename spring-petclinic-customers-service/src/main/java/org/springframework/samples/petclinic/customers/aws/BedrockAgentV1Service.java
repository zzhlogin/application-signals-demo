// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package org.springframework.samples.petclinic.customers.aws;

import org.springframework.samples.petclinic.customers.Util;
import org.springframework.stereotype.Component;
import com.amazonaws.regions.Regions;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.services.bedrockagent.AWSBedrockAgent;
import com.amazonaws.services.bedrockagent.AWSBedrockAgentClientBuilder;
import com.amazonaws.services.bedrockagent.model.GetKnowledgeBaseRequest;
import com.amazonaws.services.bedrockagent.model.GetKnowledgeBaseResult;
import com.amazonaws.services.bedrockagent.model.GetDataSourceRequest;
import com.amazonaws.services.bedrockagent.model.GetDataSourceResult;
import com.amazonaws.services.bedrockagent.model.GetAgentRequest;
import com.amazonaws.services.bedrockagent.model.GetAgentResult;

@Component
public class BedrockAgentV1Service {
//    private static final String knowledgeBaseId = "VTOW8CTCLP";
    private static final String knowledgeBaseId = "DXOTRRFCF8";
    private static final String datasourceId = "E7FIS6IZ4A";
    private static final String agentId = "Q08WFRPHVL";
    final AWSBedrockAgent bedrockAgentV1Client;

    public BedrockAgentV1Service() {
        // AWS web identity is set for EKS clusters, if these are not set then use default credentials
        if (System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE") == null && System.getProperty("aws.webIdentityTokenFile") == null) {
            bedrockAgentV1Client = AWSBedrockAgentClientBuilder.standard()
                .withRegion(Regions.US_EAST_1) // replace with your desired region
                .build();
        }
        else {
//            BasicAWSCredentials awsCreds = new BasicAWSCredentials("access_key_id", "secret_key_id");
            bedrockAgentV1Client = AWSBedrockAgentClientBuilder.standard()
                .withRegion(Regions.US_EAST_1) // replace with your desired region
                .withCredentials(WebIdentityTokenCredentialsProvider.create())
                .build();
        }

    }

    public String getKnowledgeBase() {
        try {
            System.out.printf("GetKnowledgeBaseRequest: " + knowledgeBaseId);
            GetKnowledgeBaseRequest request = new GetKnowledgeBaseRequest()
                    .withKnowledgeBaseId(knowledgeBaseId);
            GetKnowledgeBaseResult response = bedrockAgentV1Client.getKnowledgeBase(request);
            System.out.printf("\"KnowledgeBase ID: " + response.getKnowledgeBase().getName());
            return response.getKnowledgeBase().getName();
        } catch (Exception e) {
            System.out.printf("Failed to GetKnowledgeBaseRequest: %s. Error: %s%n", knowledgeBaseId, e.getMessage());
            throw e;
        }
    }

    public String getDatasource() {
        GetDataSourceRequest request = new GetDataSourceRequest()
                .withDataSourceId(datasourceId)
                .withKnowledgeBaseId(knowledgeBaseId);
        GetDataSourceResult response = bedrockAgentV1Client.getDataSource(request);

        return "Datasource ID: " + response.toString();
    }

  public String getAgent() {
    GetAgentRequest request = new GetAgentRequest()
            .withAgentId(agentId);
    GetAgentResult response = bedrockAgentV1Client.getAgent(request);

    return "Agent ID: " + response.getAgent();
  }


}
