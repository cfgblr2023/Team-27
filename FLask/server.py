from flask import Flask, redirect, url_for, render_template, request, session
import requests
from util.config import app, cred
from PIL import Image
import tensorflow as tf
from firebase_admin import storage

flaskApp = Flask(__name__)
flaskApp.secret_key = "#kua2164@#$nlksa"

@flaskApp.route("/")
def home():
    return "<h1> Hello </h1>" 

@flaskApp.route("/user")
def user():
    if "userEmail" in session:
        usr = session["userEmail"]
        return f"logged in user: {usr}"
    else:
        return "Invalid credinantels"

@flaskApp.route("/upload", methods = ["POST", "GET"])
def login():
    if request.method == "POST":
        return redirect(url_for("user"))
    else:
        
        return render_template("login.html")


@flaskApp.route("/predict", methods = ["GET"])
def predict():
    bucket = storage.bucket(app=app)
    blob = list(bucket.list_blobs(prefix="file/"))[0]
    blob.download_to_filename("images/new.jpg")
    file_path = r"images\new.jpg"
    model = tf.keras.models.load_model("runorwalk.h5")
    image = tf.keras.utils.load_img(file_path)
    input_arr = tf.keras.utils.img_to_array(image)
    input_arr.resize(1, 36)
    predictions = model.predict(input_arr)
    d  = {
        "0": "Not walkable footpath",
        "1": "Walkable footpath"
    }
    pred_str = str(int(predictions[0]))
    return render_template("predict.html", pred=d[pred_str]);


if __name__ == "__main__":
    flaskApp.run(debug=True)