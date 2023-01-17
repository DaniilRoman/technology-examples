from flask import Flask
import os

app = Flask(__name__)


@app.route('/')
def hello():
    return 'Hello, World!\n' + os.environ['CUSTOM_RESPONSE']


if __name__ == '__main__':
    app.run(debug=True, port=8080)