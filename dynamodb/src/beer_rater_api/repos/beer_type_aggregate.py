import logging
from typing import Optional

from src.beer_rater_api.config.dynamo_config import dynamodb_client, delete_table
from src.beer_rater_api.repos.beer_aggregate import BeerAggregate
from src.beer_rater_api.repos.common import get_list_with_error_handling, save_with_error_handling, exec_with_error_handling


class BeerTypeAggregate(BeerAggregate):
    table_name = "beer_type_aggregate"

    def __init__(self, beer_name: str, beer_type: str, score_count: int, scores: dict[str, dict],
                 global_partition_key: str = "beer_type_aggregate"):
        super().__init__(score_count, scores) # negative to make sorting bu desc # TODO inconsistent
        self.beer_name = beer_name
        self.beer_type = beer_type
        self.global_partition_key = global_partition_key


######################################################################
def create_beer_type_aggregate_table():
    try:
        dynamodb_client.create_table(
            TableName=BeerTypeAggregate.table_name,
            KeySchema=[
                {
                    'AttributeName': 'beer_type',
                    'KeyType': 'HASH'  # Partition key
                },
                {
                    'AttributeName': 'beer_name',
                    'KeyType': 'RANGE'  # Sort key
                }
            ],
            AttributeDefinitions=[
                {
                    'AttributeName': 'beer_name',
                    'AttributeType': 'S'
                },
                {
                    'AttributeName': 'beer_type',
                    'AttributeType': 'S'
                },
                {
                    'AttributeName': 'score_count',
                    'AttributeType': 'N'
                },
                {
                    'AttributeName': 'global_partition_key',
                    'AttributeType': 'S'
                }
            ],
            GlobalSecondaryIndexes=[
                {
                    'IndexName': 'count_global_idx',
                    'KeySchema': [
                        {
                            'AttributeName': 'global_partition_key',
                            'KeyType': 'HASH'
                        },
                        {
                            'AttributeName': 'score_count',
                            'KeyType': 'RANGE'
                        },
                    ],
                    'Projection': {
                        'ProjectionType': 'ALL'
                    },
                    'ProvisionedThroughput': {
                        'ReadCapacityUnits': 123,
                        'WriteCapacityUnits': 123
                    }
                },
            ],
            ProvisionedThroughput={
                # ReadCapacityUnits set to 10 strongly consistent reads per second
                'ReadCapacityUnits': 10,
                'WriteCapacityUnits': 10  # WriteCapacityUnits set to 10 writes per second
            }
            #     'PayPerRequest':
            # }
        )
    except dynamodb_client.exceptions.ResourceInUseException:
        logging.info(f"Table {BeerTypeAggregate.table_name} already exists")

######################################################################


# def get_beer_type_aggregates(beer_type_query) -> list[BeerTypeAggregate]:
#     return get_with_error_handling(
#         lambda: dynamodb_client.execute_statement(
#             Statement=f'SELECT * FROM {BeerTypeAggregate.table_name} WHERE contains(beer_name, \'{beer_type_query}\')'),
#         BeerTypeAggregate
#     )


def get_beer_type_aggregate(beer_name, beer_type) -> Optional[BeerTypeAggregate]:
    res = get_list_with_error_handling(
        lambda: dynamodb_client.execute_statement(
            Statement=f'SELECT * FROM {BeerTypeAggregate.table_name} WHERE beer_name =\'{beer_name}\' AND beer_type = \'{beer_type}\''),
        BeerTypeAggregate
    )
    if res:
        return res[0]
    else:
        return None


def get_top_by_count(limit: int = 10) -> list[BeerTypeAggregate]:
    return get_list_with_error_handling(
        lambda: dynamodb_client.execute_statement(
            Statement=f'SELECT * FROM {BeerTypeAggregate.table_name}.count_global_idx', Limit=limit),
        BeerTypeAggregate
    )


def save_beer_aggregate(beer_aggregate: BeerTypeAggregate):
    return save_with_error_handling(dynamodb_client, BeerTypeAggregate.table_name, beer_aggregate)


def delete_beer_aggregate(user_id, beer_name) -> list[BeerTypeAggregate]:
    return exec_with_error_handling(
        lambda: dynamodb_client.delete_item(TableName=BeerTypeAggregate.table_name,
                                            Key={"beer_name": {'S': user_id}, "beer_type": {'S': beer_name}}))


if __name__ == '__main__':
    delete_table(BeerTypeAggregate.table_name)
    create_beer_type_aggregate_table()

    save_beer_aggregate(BeerTypeAggregate("2", "2", 4, {"qwe": {}}))
    save_beer_aggregate(BeerTypeAggregate("1", "1", 2, {"qwe": {}}))
    save_beer_aggregate(BeerTypeAggregate("11", "1", 6, {"qwe": {}}))
    print(get_top_by_count())

    # score = {"will_take_again": 5, "will_take_on_football": 1}
    # berlin_kinder = UserScore("1", "berlin kinder", score)
    # save_user_score(berlin_kinder)
    # berlin_kinder_2 = UserScore("2", "berlin kinder", score)
    # save_user_score(berlin_kinder_2)
    #
    # score = get_user_score("berlin")
    # print(str(score))
    # delete_beer_aggregate(berlin_kinder.user_id, berlin_kinder.beer_name)
    # print(get_user_score("berlin"))