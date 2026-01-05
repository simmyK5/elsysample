from django.urls import path
from .views import send_audio

urlpatterns = [
    path('send-audio', send_audio),
]
