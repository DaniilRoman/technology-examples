# 1 - 5
# will take again (no way - yeah sure)
# will take for football (no way - yeah sure)
#
# score_settings = ["will_take_again", "will_grab_to_football"]
import logging
from typing import Optional

from src.beer_rater_api.config.dynamo_config import delete_table, dynamodb_client
from src.beer_rater_api.repos.common import GenericObject, get_list_with_error_handling, save_with_error_handling, \
    exec_with_error_handling


class UserScore(GenericObject):
    table_name = "user_score"

    def __init__(self, user_id: str, beer_name_type: str, score: dict):
        self.user_id = user_id
        self.beer_name_type = beer_name_type
        self.score = score

    @staticmethod
    def to_composite_key(beer_name: str, beer_type: str) -> str:
        return f"{beer_name}__{beer_type}"

    @staticmethod
    def from_composite_key(beer_name_type: str) -> [str, str]:
        return beer_name_type.split("__")


######################################################################
def create_user_score_table():
    try:
        dynamodb_client.create_table(
            TableName=UserScore.table_name,
            KeySchema=[
                {
                    'AttributeName': 'user_id',
                    'KeyType': 'HASH'  # Partition key
                },
                {
                    'AttributeName': 'beer_name_type',
                    'KeyType': 'RANGE'  # Sort key
                }
            ],
            AttributeDefinitions=[
                {
                    'AttributeName': 'user_id',
                    'AttributeType': 'S'
                },
                {
                    'AttributeName': 'beer_name_type',
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
        logging.info(f"Table {UserScore.table_name} already exists")


######################################################################


def search_user_score(beer_name_query) -> list[UserScore]:
    return get_list_with_error_handling(
        lambda: dynamodb_client.execute_statement(
            Statement=f'SELECT * FROM {UserScore.table_name} WHERE contains(beer_name_type, \'{beer_name_query}\')'),
        UserScore
    )


def save_user_score(user_score: UserScore):
    return save_with_error_handling(dynamodb_client, UserScore.table_name, user_score)


def delete_user_score(user_id, beer_name, beer_type) -> list[UserScore]:
    beer_name_type = UserScore.to_composite_key(beer_name, beer_type)
    return exec_with_error_handling(
        lambda: dynamodb_client.delete_item(TableName=UserScore.table_name,
                                            Key={"user_id": {'S': user_id}, "beer_name_type": {'S': beer_name_type}}))


def get_user_scores_by_user(user_id: str) -> list[UserScore]:
    return get_list_with_error_handling(
        lambda: dynamodb_client.execute_statement(
            Statement=f'SELECT * FROM {UserScore.table_name} WHERE user_id =\'{user_id}\''),
        UserScore
    )


def get_user_score(user_id: str, beer_name: str, beer_type: str) -> Optional[UserScore]:
    beer_name_type = UserScore.to_composite_key(beer_name, beer_type)
    res = get_list_with_error_handling(
        lambda: dynamodb_client.execute_statement(
            Statement=f'SELECT * FROM {UserScore.table_name} WHERE user_id =\'{user_id}\' AND beer_name_type = \'{beer_name_type}\''),
        UserScore
    )
    if res:
        return res[0]
    else:
        return None


if __name__ == '__main__':
    delete_table(UserScore.table_name)
    create_user_score_table()

    score = {"will_take_again": 5, "will_take_on_football": 1}
    berliner_kinder_pilsner = UserScore.to_composite_key("berlin kinder", "pilsner")
    berlin_kinder = UserScore("1", berliner_kinder_pilsner, score)
    save_user_score(berlin_kinder)
    berlin_kinder_2 = UserScore("2", berliner_kinder_pilsner, score)
    save_user_score(berlin_kinder_2)

    print("===========")
    print(get_user_scores_by_user("1"))
    print("===========")

    score = search_user_score("berlin")
    print(str(score))
    delete_user_score(berlin_kinder.user_id, "berlin kinder", "pilsner")
    print(search_user_score("berlin"))
