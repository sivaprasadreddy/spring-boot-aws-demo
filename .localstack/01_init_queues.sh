#!/bin/bash

#awslocal --endpoint-url=http://localhost:4576 sqs create-queue --queue-name test_queue --region us-east-1
#awslocal --endpoint-url=http://localhost:4576 sqs list-queues --region us-east-1

awslocal sqs create-queue --queue-name test_queue
awslocal sqs list-queues

awslocal sns create-topic --name test_topic
awslocal sns list-topics
