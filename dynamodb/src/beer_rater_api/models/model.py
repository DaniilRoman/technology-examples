from typing import Optional

from src.beer_rater_api.repos.beer_item import BeerItem
from src.beer_rater_api.repos.common import GenericObject
from src.beer_rater_api.repos.user_score import UserScore


class UserScoreChangeDto(GenericObject):
    def __init__(self, prev_user_score: Optional[UserScore], new_user_score: UserScore):
        self.prev_user_score: Optional[UserScore] = prev_user_score
        self.new_user_score: UserScore = new_user_score


class UserBeerScoreDto(GenericObject):
    def __init__(self, beer_item: BeerItem, score: dict):
        self.beer_item: BeerItem = beer_item
        self.score: dict = score


class UserScoreDto(GenericObject):
    def __init__(self, user_id: str, beer_name: str, beer_type: str, score: dict):
        self.user_id = user_id
        self.beer_name = beer_name
        self.beer_type = beer_type
        self.score = score

    def to_user_score(self):
        user_name_type = UserScore.to_composite_key(self.beer_name, self.beer_type)
        return UserScore(self.user_id, user_name_type, self.score)


class User:
    def __init__(self, name: str, city: str, country: str, email: str, token: str):
        self.email: str = email
        self.token: str = token
        self.name: str = name
        self.city: str = city
        self.country: str = country


# TODO update aggregates using queue


class BeerCityAggregate:
    pass


class BeerCountryAggregate:
    pass


class BeerTypeByCityAggregate:
    pass


class BeerTypeByCountryAggregate:
    pass
