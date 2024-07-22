// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package org.springframework.samples.petclinic.customers.aws;

import org.springframework.samples.petclinic.customers.Util;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.SdkBytes;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

@Component
public class BedrockRuntimeV2Service {
    final BedrockRuntimeClient bedrockRuntimeV2Client;

    public BedrockRuntimeV2Service() {
        // AWS web identity is set for EKS clusters, if these are not set then use default credentials
        if (System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE") == null && System.getProperty("aws.webIdentityTokenFile") == null) {
            bedrockRuntimeV2Client = BedrockRuntimeClient.builder()
                    .region(Region.of(Util.REGION_FROM_EC2))
                    .build();
        }
        else {
            bedrockRuntimeV2Client = BedrockRuntimeClient.builder()
                    .region(Region.of(Util.REGION_FROM_EKS))
                    .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                    .build();
        }

    }

    public String invokeLlama2V2() {
        try {
            System.out.printf("invokeLlama2V2: ");
            String llama2ModelId = "meta.llama2-13b-chat-v1";
            String prompt = "Hi Amazon bedrock, how are you??";

            String payload = new JSONObject()
                    .put("prompt", prompt)
                    .put("max_gen_len", 1000)
                    .put("temperature", 0.5)
                    .put("top_p", 0.9)
                    .toString();
            System.out.printf("Invoke llama2 Model with modelId: " + llama2ModelId + " prompt: " + prompt + "max_gen_len: 1000 temperature: 0.5 top_p: 0.9");

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .body(SdkBytes.fromUtf8String(payload))
                    .modelId(llama2ModelId)
                    .contentType("application/json")
                    .accept("application/json")
                    .build();
            System.out.println("invokeLlama2 request:" + request.body().asUtf8String());

            InvokeModelResponse response = bedrockRuntimeV2Client.invokeModel(request);

            JSONObject responseBody = new JSONObject(response.body().asUtf8String());
//            System.out.println("invokeLlama2 response:" + response.body().asUtf8String());
            String generatedText = responseBody.getString("generation");
            int promptTokenCount = responseBody.getInt("prompt_token_count");
            int generationTokenCount = responseBody.getInt("generation_token_count");
            String stopReason = responseBody.getString("stop_reason");
            System.out.printf("Invoke llama2 Model response: prompt_token_count: " + promptTokenCount + " generation_token_count: " + generationTokenCount + " stop_reason: " + stopReason);
            return generatedText;
        } catch (Exception e) {
            System.out.printf("Failed to invokeLlama2: Error: %s%n",e.getMessage());
            throw e;
        }
    }
}
