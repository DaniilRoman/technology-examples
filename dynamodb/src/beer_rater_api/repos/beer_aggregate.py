from src.beer_rater_api.repos.common import GenericObject
from src.beer_rater_api.models.model import UserScoreChangeDto
from src.beer_rater_api.repos.user_score import UserScore


class BeerAggregate(GenericObject):
    """
        scores - { "will_take_again": {"1": 30, "4": 0}, "will_take_for_football": {"2": 3, "1": "10"} }
    """

    def __init__(self, score_count: int, scores: dict[str, dict]):
        self.score_count = -score_count
        self.scores = scores

    def apply_score(self, score_change: UserScoreChangeDto):
        if score_change.prev_user_score is None:
            for score_name, score in score_change.new_user_score.score.items():
                self.__init_or_inc_new_score(score, score_name)
            self.score_count += 1
        else:
            for score_name, score in score_change.prev_user_score.score.items():
                self.scores[score_name][str(score)] -= 1
            for score_name, score in score_change.new_user_score.score.items():
                self.__init_or_inc_new_score(score, score_name)

    def delete_score(self):
        pass # TODO

    def __init_or_inc_new_score(self, score, score_name):
        if self.scores.get(score_name) is None:
            self.scores.setdefault(score_name, {str(score): 1})
        else:
            if self.scores[score_name].get(str(score)) is None:
                self.scores[score_name].setdefault(str(score), 1)
            else:
                self.scores[score_name][str(score)] += 1


if __name__ == '__main__':
    scores = {
        "will_take_again": {"1": 30, "4": 0},
        "will_take_for_football": {"2": 3, "1": 10}
    }
    beer_aggregate = BeerAggregate(1, scores)

    user_score = UserScore("1", "berliner", {"will_take_again": 2, "wiil_take_for_cinema_evening": 3})
    user_change_dto = UserScoreChangeDto(None, user_score)

    print(beer_aggregate)
    beer_aggregate.apply_score(user_change_dto)
    print(beer_aggregate)

    beer_aggregate.apply_score(user_change_dto)
    print(beer_aggregate)

    user_score = UserScore("1", "berliner", {"will_take_again": 2, "wiil_take_for_cinema_evening": 3})
    user_score_new = UserScore("1", "berliner", {"will_take_again": 4, "wiil_take_for_cinema_evening": 1})
    user_change_dto = UserScoreChangeDto(user_score, user_score_new)

    beer_aggregate.apply_score(user_change_dto)
    print(beer_aggregate)

    user_score = UserScore("1", "berliner", {"will_take_again": 4, "wiil_take_for_cinema_evening": 1})
    user_score_new = UserScore("1", "berliner", {"will_take_again": 4, "wiil_take_for_cinema_evening": 1, "will": 3})
    user_change_dto = UserScoreChangeDto(user_score, user_score_new)

    beer_aggregate.apply_score(user_change_dto)
    print(beer_aggregate)
