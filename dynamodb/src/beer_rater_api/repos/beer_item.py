import logging

from src.beer_rater_api.config.dynamo_config import dynamodb_client, delete_table
from src.beer_rater_api.repos.common import GenericObject, exec_with_error_handling, get_list_with_error_handling, \
    save_with_error_handling


class BeerItem(GenericObject):
    table_name = "beer_item"

    def __init__(self, name: str, description: str, type: str, sub_type: str, picture_url: str):
        self.name = name
        self.description = description
        self.type = type
        self.sub_type = sub_type
        self.picture_url = picture_url


######################################################################
def create_beer_item_table():
    try:
        dynamodb_client.create_table(
            TableName=BeerItem.table_name,
            KeySchema=[
                {
                    'AttributeName': 'name',
                    'KeyType': 'HASH'  # Partition key
                },
                {
                    'AttributeName': 'type',
                    'KeyType': 'RANGE'  # Sort key
                }
            ],
            AttributeDefinitions=[
                {
                    'AttributeName': 'name',
                    'AttributeType': 'S'
                },
                {
                    'AttributeName': 'type',
                    'AttributeType': 'S'
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
        logging.info(f"Table {BeerItem.table_name} already exists")


######################################################################


def search_beer_by_name(beer_name_query: str) -> list[BeerItem]:
    return get_list_with_error_handling(
        lambda: dynamodb_client.execute_statement(
            Statement=f'SELECT * FROM {BeerItem.table_name} WHERE contains(name, \'{beer_name_query}\')'),
        BeerItem
    )


def get_beer_by_names(names: list[str]) -> list[BeerItem]:
    return get_list_with_error_handling(
        lambda: dynamodb_client.execute_statement(
            Statement=f'SELECT * FROM {BeerItem.table_name} WHERE name IN {names}'),
        BeerItem
    )


def save_beer(beer):
    return save_with_error_handling(dynamodb_client, BeerItem.table_name, beer)


def delete_beer_by_name(beer_name, beer_type) -> list[BeerItem]:
    return exec_with_error_handling(
        lambda: dynamodb_client.delete_item(TableName=BeerItem.table_name,
                                            Key={"name": {'S': beer_name}, "type": {'S': beer_type}}))


if __name__ == '__main__':
    delete_table(BeerItem.table_name)
    create_beer_item_table()

    berlin_kinder = BeerItem("berlin kinder", "suck beer", "pilsner", "berlin pilsner", "url")
    save_beer(berlin_kinder)
    beer = get_beer_by_names(["berlin kinder"])[0]
    print(str(beer))
    delete_beer_by_name(beer.name, beer.type)
    print(search_beer_by_name("berlin"))
