// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package org.springframework.samples.petclinic.customers.aws;

import org.springframework.samples.petclinic.customers.Util;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.regions.Regions;
//import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.DescribeStateMachineRequest;
import com.amazonaws.services.stepfunctions.model.DescribeStateMachineResult;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskRequest;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;

@Component
public class StepFunctionV1Service {
    private static final String stateMachineArn = "arn:aws:iam::007003802740:role/service-role/StepFunctions-InvalidRole";
    private static final String activityArn = "arn:aws:states:us-east-1:007003802740:activity:testActivity";
    final AWSStepFunctions stepFunctionsV1Client;

    public StepFunctionV1Service() {
        // AWS web identity is set for EKS clusters, if these are not set then use default credentials
        if (System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE") == null && System.getProperty("aws.webIdentityTokenFile") == null) {
            stepFunctionsV1Client = AWSStepFunctionsClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1) // replace with your desired region
                    .build();
        }
        else {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials("access_key_id", "secret_key_id");
            stepFunctionsV1Client = AWSStepFunctionsClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1) // replace with your desired region
                    .withCredentials(WebIdentityTokenCredentialsProvider.create())
                    .build();
        }

    }

    public String describeStateMachine() {
        try {
            System.out.printf("DescribeStateMachineRequest: " + stateMachineArn);
            DescribeStateMachineRequest describeStateMachineRequest = new DescribeStateMachineRequest()
                    .withStateMachineArn(stateMachineArn);
            DescribeStateMachineResult result = stepFunctionsV1Client.describeStateMachine(describeStateMachineRequest);
            System.out.printf("\"DescribeStateMachineResult: " + result.toString());
            return result.toString();
        } catch (Exception e) {
            System.out.printf("Failed to DescribeStateMachineRequest: %s. Error: %s%n", stateMachineArn, e.getMessage());
            throw e;
        }
    }

    public String getActivityTask() {
        try {
            System.out.printf("GetActivityTaskRequest: " + activityArn);
            GetActivityTaskRequest getActivityTaskRequest = new GetActivityTaskRequest()
                    .withActivityArn(activityArn);
            GetActivityTaskResult result = stepFunctionsV1Client.getActivityTask(getActivityTaskRequest);
            System.out.printf("\"GetActivityTaskResult: " + result.toString());
            return result.toString();
        } catch (Exception e) {
            System.out.printf("Failed to GetActivityTaskRequest: %s. Error: %s%n", activityArn, e.getMessage());
            throw e;
        }
    }
}
