from flask import Flask, request, jsonify
from keras.models import load_model # type: ignore
from keras.preprocessing.image import load_img, img_to_array # type: ignore
import numpy as np
import traceback
import os
import requests
import tensorflow as tf

print(f"Running TensorFlow version: {tf.__version__}")

app = Flask(__name__)
api_key = os.getenv("USDA_API_KEY")

# Load the model
model_path = os.path.abspath("fnv_recognition_model.h5")
print(f"Loading model from: {model_path}")  #debuglog
model = load_model(model_path)

labels = {0: 'apple', 1: 'banana', 2: 'beetroot', 3: 'bell pepper', 4: 'cabbage', 5: 'capsicum', 6: 'carrot',
          7: 'cauliflower', 8: 'chilli pepper', 9: 'corn', 10: 'cucumber', 11: 'eggplant', 12: 'garlic', 13: 'ginger',
          14: 'grapes', 15: 'jalepeno', 16: 'kiwi', 17: 'lemon', 18: 'lettuce',
          19: 'mango', 20: 'onion', 21: 'orange', 22: 'paprika', 23: 'pear', 24: 'peas', 25: 'pineapple',
          26: 'pomegranate', 27: 'potato', 28: 'raddish', 29: 'soy beans', 30: 'spinach', 31: 'sweetcorn',
          32: 'sweetpotato', 33: 'tomato', 34: 'turnip', 35: 'watermelon'}

def fetch_nutritional_values(prediction):
    try:
        api_url = f"https://api.nal.usda.gov/fdc/v1/foods/search?query={prediction}&pageSize=1&dataType=Survey (FNDDS)&api_key={api_key}"
        response = requests.get(api_url)
        print(f"API Response: {response.status_code}, {response.text}")  #debugprint
        if response.status_code == 200:
            data = response.json()
            if data["foods"]:
                food = data["foods"][0]
                nutrients = food.get("foodNutrients", [])
                nutrition_info = {nutrient["nutrientName"]: nutrient["value"] for nutrient in nutrients}
                return nutrition_info
        return None
    except Exception as e:
        print(f"Error fetching nutrition info: {e}")
        return None

@app.route('/predict', methods=['POST'])
def predict():
    try:
        if 'image' not in request.files:
            print("No image found in request")  #debuglog
            return jsonify({"error": "No image uploaded"}), 400
        
        file = request.files['image']
        print(f"Received file: {file.filename}")  #debuglog

        temp_path = "temp_image.jpg"
        file.save(temp_path)

        img = load_img(temp_path, target_size=(224, 224))
        img = img_to_array(img) / 255.0
        img = np.expand_dims(img, axis=0)

        prediction = model.predict(img)
        predicted_class = np.argmax(prediction)
        predicted_label = labels[predicted_class]

        nutrition = fetch_nutritional_values(predicted_label)

        os.remove(temp_path)

        return jsonify({"name": predicted_label, "nutrition": nutrition})

    except Exception as e:
        print("Error during prediction:", traceback.format_exc())
        return jsonify({"error": "Internal Server Error", "details": str(e)}), 500

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000)