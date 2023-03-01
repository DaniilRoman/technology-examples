from src.beer_rater_api.config.dynamo_config import delete_table
from src.beer_rater_api.models.model import UserBeerScoreDto, UserScoreChangeDto, UserScoreDto
from src.beer_rater_api.repos.beer_type_aggregate import BeerTypeAggregate, get_top_by_count, get_beer_type_aggregate, \
    save_beer_aggregate, create_beer_type_aggregate_table
from src.beer_rater_api.repos.beer_item import BeerItem, save_beer, get_beer_by_names, search_beer_by_name, \
    create_beer_item_table
from src.beer_rater_api.repos.user_score import get_user_scores_by_user, UserScore, save_user_score, get_user_score, \
    create_user_score_table


def sign_up():
    pass


def sign_in():
    pass


########################################################

def create_beer_item(beer_item: BeerItem):
    save_beer(beer_item)


def search_beers_by_name(beer_name_query: str) -> list[BeerItem]:
    return search_beer_by_name(beer_name_query)


def get_beers_by_user(user_id: str) -> list[UserBeerScoreDto]:  # TODO fix mapping
    user_scores = get_user_scores_by_user(user_id)
    beer_names = [UserScore.from_composite_key(i.beer_name_type)[0] for i in user_scores]
    beer_items = get_beer_by_names(beer_names)
    return [UserBeerScoreDto(beer_item, user_score.score) for user_score, beer_item in zip(user_scores, beer_items)]


########################################################

def score_beer(score: UserScoreDto):
    prev_user_score = get_user_score(score.user_id, score.beer_name, score.beer_type)
    user_score = score.to_user_score()
    save_user_score(user_score)
    __update_aggregates(prev_user_score, user_score)


def __update_aggregates(prev_score: UserScore, score: UserScore):
    beer_name, _ = UserScore.from_composite_key(score.beer_name_type)
    beer_items = get_beer_by_names([beer_name])
    beer_item = beer_items[0]

    aggregate = get_beer_type_aggregate(beer_item.name, beer_item.type)
    if aggregate is not None:
        aggregate.apply_score(UserScoreChangeDto(prev_score, score))
    else:
        scores = {}
        for name, score in score.score.items():
            scores.setdefault(name, {str(score): 1})
        aggregate = BeerTypeAggregate(beer_item.name, beer_item.type, 1, scores)

    save_beer_aggregate(aggregate)


########################################################

def get_top_beer_type_aggregates() -> list[BeerTypeAggregate]:
    return get_top_by_count()


########################################################


if __name__ == '__main__':
    delete_table(UserScore.table_name)
    delete_table(BeerTypeAggregate.table_name)
    delete_table(BeerItem.table_name)

    create_user_score_table()
    create_beer_item_table()
    create_beer_type_aggregate_table()

    create_beer_item(BeerItem("beer 1", "desc", "pilsner", "", ""))
    create_beer_item(BeerItem("beer 2", "desc", "pilsner", "", ""))
    create_beer_item(BeerItem("beer 3", "desc", "wize", "", ""))

    print(search_beers_by_name("beer 1"))
    score_beer(UserScoreDto("1", "beer 1", "p", {"score 1": 3, "score 2": 2}))
    score_beer(UserScoreDto("2", "beer 1", "p", {"score 1": 1, "score 2": 3}))

    score_beer(UserScoreDto("1", "beer 2", "p", {"score 1": 3, "score 2": 2}))
    score_beer(UserScoreDto("2", "beer 2", "p", {"score 1": 1, "score 2": 3}))

    score_beer(UserScoreDto("1", "beer 3", "p", {"score 1": 10, "score 2": 20}))

    print(get_beers_by_user("1"))
    print(get_top_by_count())
