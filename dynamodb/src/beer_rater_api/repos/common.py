from src.beer_rater_api.config.dynamo_config import from_dict, to_dict


class GenericObject(object):
    def __repr__(self):
        return str(self.__dict__)


def exec_with_error_handling(specific_operation_func):
    res = specific_operation_func()
    if res["ResponseMetadata"]["HTTPStatusCode"] != 200:
        raise RuntimeError("Cannot delete item")
    return res


def get_list_with_error_handling(get_func, class_type):
    res = exec_with_error_handling(get_func)
    return [class_type(**from_dict(i)) for i in res["Items"]]


def save_with_error_handling(client, table_name, item):
    return exec_with_error_handling(lambda: client.put_item(
        TableName=table_name,
        Item=to_dict(item)
    ))
