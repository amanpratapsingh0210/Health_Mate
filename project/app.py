from flask import Flask, request, jsonify, render_template
from ultralytics import YOLO
from PIL import Image
import os, uuid, json, cv2
import numpy as np
import google.generativeai as genai
import re

# ========== CONFIG ==========
UPLOAD_FOLDER = "static"
INPUT_DIR = "input_images"
OUTPUT_DIR = "output_items"
MODEL_PATH = r"weights/best.pt"
GEMINI_API_KEY = "AIzaSyBkLQnhC1jFpjE3GMVzWVgFudWchFcTCvw"

# ========== INIT ==========
app = Flask(__name__)
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(INPUT_DIR, exist_ok=True)
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Load YOLOv8 model
seg_model = YOLO(MODEL_PATH)

# Configure Gemini
genai.configure(api_key=GEMINI_API_KEY)
g_model = genai.GenerativeModel("gemini-2.0-flash-lite")

# ========== GEMINI HELPERS ==========

def recognize_food_item(img_path):
    prompt = (
        "Identify the food item in this image. "
        "Be specific. Just return the name of the item (e.g., 'paneer butter masala', 'mix weg', 'rice', 'chapati', 'green salad')."
    )
    img = Image.open(img_path).convert("RGB")
    res = g_model.generate_content([prompt, img], stream=False)
    return res.text.strip()

def get_nutritional_info(food_name):
    prompt = (
        f"Give me the nutritional value of {food_name} per 100 grams. "
        "Respond ONLY with valid JSON: {\"calories\": num, \"protein\": num, \"carbs\": num, \"fat\": num}"
    )
    try:
        res = g_model.generate_content(prompt, stream=False)
        match = re.search(r'\{.*?\}', res.text, re.DOTALL)
        return json.loads(match.group(0)) if match else None
    except:
        return None

# ========== SEGMENTATION ==========

def compute_iou(mask1, mask2):
    inter = np.logical_and(mask1, mask2).sum()
    union = np.logical_or(mask1, mask2).sum()
    return inter / union if union else 0

def segment_and_save_items(img_path, output_dir, iou_thresh=0.5):
    results = seg_model(img_path)[0]
    image = cv2.imread(img_path)
    masks = results.masks.data.cpu().numpy() if results.masks else []
    boxes = results.boxes.xyxy.cpu().numpy() if results.boxes else []
    kept_masks, kept_boxes = [], []

    for i, mask in enumerate(masks):
        if any(compute_iou(mask, kmask) > iou_thresh for kmask in kept_masks):
            continue
        kept_masks.append(mask)
        kept_boxes.append(boxes[i])

    filenames = []
    for i, (mask, box) in enumerate(zip(kept_masks, kept_boxes)):
        x1, y1, x2, y2 = map(int, box)
        resized_mask = cv2.resize(mask.astype(np.uint8), (image.shape[1], image.shape[0]), interpolation=cv2.INTER_NEAREST)
        item = cv2.bitwise_and(image, image, mask=resized_mask)
        crop = item[y1:y2, x1:x2]
        if crop.size == 0 or crop.shape[0] < 20 or crop.shape[1] < 20:
            continue
        filename = f"{uuid.uuid4().hex[:8]}.jpg"
        save_path = os.path.join(output_dir, filename)
        cv2.imwrite(save_path, crop)
        filenames.append(save_path)
    return filenames

# ========== FLASK ROUTE ==========

@app.route("/", methods=["GET"])
def home():
    return render_template("upload.html")

@app.route("/analyze", methods=["POST"])
def analyze_image():
    file = request.files.get("image")
    if not file:
        return jsonify({"error": "No image uploaded"}), 400

    # Save uploaded image
    image_path = os.path.join(UPLOAD_FOLDER, "uploaded_image.jpg")
    file.save(image_path)

    # Clear previous outputs
    for f in os.listdir(OUTPUT_DIR): os.remove(os.path.join(OUTPUT_DIR, f))

    # Step 1: Segment the image
    item_images = segment_and_save_items(image_path, OUTPUT_DIR)
    
    # Step 2 & 3: Classify and get nutrition
    results = []
    for img in item_images:
        label = recognize_food_item(img)
        nutrition = get_nutritional_info(label)
        results.append({
            "filename": os.path.basename(img),
            "label": label,
            "nutrition": nutrition or "Not found"
        })

    return jsonify(results)

# ========== MAIN ==========
if __name__ == "__main__":
    app.run(debug=True)