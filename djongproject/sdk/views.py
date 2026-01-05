import os
import requests
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt

# Constants
SDK_URL = "https://api.eslyqurity.com/api/sdk/incidents" # Ensure NO trailing slash here
API_KEY = "$2b$10$c6Y402lEQeUn51VGKLzunuI2flbapcSWufvz4CuDl9nQtXBKS4GfK"
EMAIL = "simmykheswa@outlook.com"

# Create upload folder like you did in Flask
UPLOAD_FOLDER = "uploads"
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

@csrf_exempt
def send_audio(request):
    if request.method != "POST":
        return JsonResponse({"error": "Only POST allowed"}, status=405)

    if "audio" not in request.FILES:
        return JsonResponse({"error": "No audio file uploaded"}, status=400)

    audio_file = request.FILES["audio"]
    
    # Save file to disk exactly like Flask
    temp_path = os.path.join(UPLOAD_FOLDER, audio_file.name)
    with open(temp_path, 'wb+') as destination:
        for chunk in audio_file.chunks():
            destination.write(chunk)

    try:
        # Prepare multipart form data
        with open(temp_path, "rb") as f:
            files = {"audio": (audio_file.name, f, "audio/webm")}
            headers = {"Authorization": f"Bearer {API_KEY}:{EMAIL}"}

            print("📡 Sending audio to SDK…")
            # Using the exact same request call as your Flask app
            response = requests.post(SDK_URL, files=files, headers=headers)
            
            # Check success
            if response.status_code == 200:
                return JsonResponse({
                    "success": True,
                    "message": "Audio forwarded to SDK",
                    "sdkResponse": response.json()
                })
            else:
                return JsonResponse({
                    "success": False,
                    "status_code": response.status_code,
                    "sdk_error": response.text
                })

    except Exception as e:
        print(f"❌ Error: {e}")
        return JsonResponse({"error": str(e)}, status=500)
    finally:
        # Cleanup
        if os.path.exists(temp_path):
            os.remove(temp_path)