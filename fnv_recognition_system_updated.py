import os
import streamlit as st
from PIL import Image
from keras_preprocessing.image import load_img,img_to_array
import numpy as np
from keras.models import load_model # type: ignore
import requests

st.set_page_config(page_title="Fruits-Vegetable Recognition System", page_icon="🍍", layout="wide")

api_key = os.getenv("USDA_API_KEY")

if not api_key:
    st.error("API key not found. Please set the 'USDA_API_KEY' environment variable.")
    
model = load_model(r'fruit_veg_model_updated.h5')
labels = {0: 'apple', 1: 'banana', 2: 'beetroot', 3: 'bell pepper', 4: 'cabbage', 5: 'capsicum', 6: 'carrot',
          7: 'cauliflower', 8: 'chilli pepper', 9: 'corn', 10: 'cucumber', 11: 'eggplant', 12: 'garlic', 13: 'ginger',
          14: 'grapes', 15: 'jalepeno', 16: 'kiwi', 17: 'lemon', 18: 'lettuce',
          19: 'mango', 20: 'onion', 21: 'orange', 22: 'paprika', 23: 'pear', 24: 'peas', 25: 'pineapple',
          26: 'pomegranate', 27: 'potato', 28: 'raddish', 29: 'soy beans', 30: 'spinach', 31: 'sweetcorn',
          32: 'sweetpotato', 33: 'tomato', 34: 'turnip', 35: 'watermelon'}

fruits = ['Apple', 'Banana', 'Bello Pepper', 'Chilli Pepper', 'Grapes', 'Jalepeno', 'Kiwi', 'Lemon', 'Mango', 'Orange',
          'Paprika', 'Pear', 'Pineapple', 'Pomegranate', 'Watermelon']
vegetables = ['Beetroot', 'Cabbage', 'Capsicum', 'Carrot', 'Cauliflower', 'Corn', 'Cucumber', 'Eggplant', 'Ginger',
              'Lettuce', 'Onion', 'Peas', 'Potato', 'Raddish', 'Soy Beans', 'Spinach', 'Sweetcorn', 'Sweetpotato',
              'Tomato', 'Turnip']


def fetch_nutritional_values(prediction):
    try:
        api_url = f"https://api.nal.usda.gov/fdc/v1/foods/search?query={prediction}&pageSize=1&dataType=Survey (FNDDS)&api_key={api_key}"
        response = requests.get(api_url)
        if response.status_code == 200:
            data = response.json()
            if data["foods"]:
                food = data["foods"][0]
                nutrients = food.get("foodNutrients", [])
                nutrition_info = {nutrient["nutrientName"]: nutrient["value"] for nutrient in nutrients}
                return nutrition_info
            else:
                st.error("No nutritional information found.")
                return None
        else:
            st.error("Failed to fetch nutritional information. Check your API key or network connection.")
            return None
    except Exception as e:
        st.error("An error occurred while fetching nutritional information.")
        print(e)
        return None


def processed_img(img_path):
    class_labels = list(labels.values())
    img = load_img(img_path, target_size=(224, 224, 3))
    img = img_to_array(img)
    img = img / 255.0
    img = np.expand_dims(img, axis=0)
    
    predictions = model.predict(img)
    confidence = np.max(predictions)
    predicted_class = np.argmax(predictions)
    
    if confidence < 0.6:
        return "Unknown", confidence
    else:
        result_label = labels[predicted_class].capitalize()
        return result_label, confidence


def run():
    st.title("Fruits🍍-Vegetable🍅 Recognition System")
    img_file = st.file_uploader("Choose an Image", type=["jpg", "png"])
    if img_file is not None:
        img = Image.open(img_file).resize((250, 250))
        st.image(img)
        save_image_path = "test_" + img_file.name
        with open(save_image_path, "wb") as f:
            f.write(img_file.getbuffer())

        if img_file is not None:
            result, confidence = processed_img(save_image_path)
            if result == "Unknown":
                st.error('**This image is not recognized as a fruit or vegetable.**')
            elif result in vegetables:
                st.info('**Category : Vegetables**')
            else:
                st.info('**Category : Fruit**')
            st.success(f"**Predicted : {result} ({confidence*100:.2f}% confidence)**")
            if result != "Unknown":
                cal = fetch_nutritional_values(result)
                if cal:
                    filtered_nutrition = {key: value for key, value in cal.items() if value != 0}
                    st.warning(f"**Nutritional Information (per 100 grams):**")
                    for key, value in filtered_nutrition.items():
                        st.write(f"- {key}: {value}")


run()