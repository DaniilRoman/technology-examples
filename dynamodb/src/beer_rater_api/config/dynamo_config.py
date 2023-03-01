import boto3
import logging

from boto3.dynamodb.conditions import Key


dynamodb_client = boto3.client(
    'dynamodb',
    aws_access_key_id='',
    aws_secret_access_key='',
    region_name='',
    endpoint_url='http://localhost:8000'
)

dynamodb = boto3.resource(
    'dynamodb',
    aws_access_key_id='',
    aws_secret_access_key='',
    region_name='',
    endpoint_url='http://localhost:8000'
)

serializer = boto3.dynamodb.types.TypeSerializer()
deserializer = boto3.dynamodb.types.TypeDeserializer()


def to_dict(data: object):
    return {k: serializer.serialize(v) for k, v in data.__dict__.items()}


def from_dict(data: dict):
    return {k: deserializer.deserialize(v) for k, v in data.items()}


def delete_table(table_name):
    try:
        dynamodb_client.delete_table(TableName=table_name)
    except dynamodb_client.exceptions.ResourceNotFoundException:
        logging.error(f"Cannot delete {table_name}")
