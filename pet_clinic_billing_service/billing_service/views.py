from rest_framework import viewsets, status
from rest_framework.response import Response
from .models import Billing
from .serializers import BillingSerializer
import logging
import boto3
import datetime
import os
import json

logger = logging.getLogger(__name__)
# Create your views here.

class BillingViewSet(viewsets.ViewSet):
    def list(self, request):
        # sns_client
        # print("Creating SNS Topic:!!!!!!")
        # sns_client = boto3.client('sns', region_name=os.environ.get('REGION', 'us-east-1'))
        # topic_arn = "arn:aws:sns:us-east-1:007003802740:test_topic"
        # print("Getting Topic Attributes:!!!!!!")
        # topic_attributes = sns_client.get_topic_attributes(TopicArn=topic_arn)
        # print("Topic Attributes:", topic_attributes)

        # bedrock_client
        print("Getting Guardrail Information:!!!!!!")
        bedrock_client = boto3.client('bedrock', region_name=os.environ.get('REGION', 'us-east-1'))
        guardrail_arn = "arn:aws:bedrock:us-east-1:007003802740:guardrail/bt4o77i015cu"
        bedrock_client.get_guardrail(
            guardrailIdentifier=guardrail_arn
        )
        print("Guardrail information retrieved successfully.")

        # bedrock_runtime_client
        bedrock_runtime_client = boto3.client(service_name='bedrock-runtime', region_name=os.environ.get('REGION', 'us-east-1'))
        model_id = 'anthropic.claude-3-sonnet-20240229-v1:0'
        system_prompt = "Hi Amazon bedrock, how are you?"
        max_tokens = 1000

        # Prompt with user turn only.
        user_message =  {"role": "user", "content": "Hello World"}
        messages = [user_message]

        body=json.dumps(
            {
                "anthropic_version": "bedrock-2023-05-31",
                "max_tokens": max_tokens,
                "system": system_prompt,
                "messages": messages,
                "temperature": 0.1,
                "top_p": 0.9,
            }
        )

        print("invoke_model claude:!!!!!!")
        response = bedrock_runtime_client.invoke_model(body=body, modelId=model_id)
        response_body = json.loads(response.get('body').read())
        print("invoke_model claude successfully:" + str(response_body))
        queryset = Billing.objects.all()
        serializer = BillingSerializer(queryset, many=True)
        return Response(serializer.data)

    def retrieve(self, request, pk=None, owner_id=None, type=None, pet_id=None):
        try:
            billing_obj = None
            if pk is not None:
                billing_obj = Billing.objects.get(id=pk)
            else:
                billing_obj = Billing.objects.get(owner_id=owner_id, type=type, pet_id=pet_id)
            serializer = BillingSerializer(billing_obj)
            return Response(serializer.data)
        except Billing.DoesNotExist:
            return Response({'message': 'Billing object not found'}, status=404)

    def create(self, request):
        serializer = BillingSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            self.log(request.data)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def update(self, request, pk=None):
        try:
            billing_obj = Billing.objects.get(id=pk)
            serializer = BillingSerializer(billing_obj, data=request.data)
            if serializer.is_valid():
                serializer.save()
                self.log(request.data)
                return Response(serializer.data)
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        except Billing.DoesNotExist:
            return Response({'message': 'Billing object not found'}, status=status.HTTP_404_NOT_FOUND)

    def log(self, data):
        # Initialize a DynamoDB client
        client = boto3.client('dynamodb', region_name=os.environ.get('REGION', 'us-east-1'))

        # Define the table name
        table_name = 'BillingInfo'
        current_time = datetime.datetime.now()
        formatted_time = current_time.strftime("%Y-%m-%d %H:%M:%S")
        # Define the item you want to add
        item = {
            'ownerId': {'S': data['owner_id']},
            'timestamp': {'S': formatted_time},
            'billing': {'S': json.dumps(data)},
            # Add more attributes as needed
        }

        # Add the item to the table
        response = client.put_item(
            TableName=table_name,
            Item=item
        )


class HealthViewSet(viewsets.ViewSet):
    def list(self, request):
        return Response({'message':'ok'}, status=status.HTTP_200_OK)
