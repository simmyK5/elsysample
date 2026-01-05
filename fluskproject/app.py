from flask import Flask, request, jsonify
import requests
import os

app = Flask(__name__)

# SDK configuration
SDK_URL = "https://api.eslyqurity.com"
API_KEY = "$2b$10$c6Y402lEQeUn51VGKLzunuI2flbapcSWufvz4CuDl9nQtXBKS4GfK"
EMAIL = "simmykheswa@outlook.com"

UPLOAD_FOLDER = "uploads"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)


@app.route("/send-audio", methods=["POST"])
def send_audio():
    # Check if file exists
    if "audio" not in request.files:
        return jsonify({"error": "No audio file uploaded"}), 400

    audio_file = request.files["audio"]

    try:
        # Save uploaded file to temporary folder
        temp_path = os.path.join(UPLOAD_FOLDER, audio_file.filename)
        audio_file.save(temp_path)

        # Prepare multipart form data
        with open(temp_path, "rb") as f:
            files = {"audio": (audio_file.filename, f, "audio/webm")}
            headers = {"Authorization": f"Bearer {API_KEY}:{EMAIL}"}

            print("📡 Sending audio to SDK…")
            response = requests.post(SDK_URL, files=files, headers=headers)
            response.raise_for_status()

        # Cleanup temporary file
        os.remove(temp_path)

        return jsonify({
            "success": True,
            "message": "Audio forwarded to SDK",
            "sdkResponse": response.json()
        })

    except requests.exceptions.RequestException as e:
        print("❌ SDK upload error:", e)
        return jsonify({"error": "Failed to send audio to SDK"}), 500
    except Exception as e:
        print("❌ Unexpected error:", e)
        return jsonify({"error": "An unexpected error occurred"}), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
    
#py app.py
