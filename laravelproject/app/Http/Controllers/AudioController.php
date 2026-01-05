<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Log;

class AudioController extends Controller
{
   

    private $SDK_URL = 'https://api.eslyqurity.com/api/sdk/incidents';
    private $API_KEY = '$2b$10$c6Y402lEQeUn51VGKLzunuI2flbapcSWufvz4CuDl9nQtXBKS4GfK';
    private $EMAIL = 'simmykheswa@outlook.com';

    public function sendAudio(Request $request)
    {

Log::info('Audio Request Data:');

        $file = $request->file('audio');

        if (!$file || !$file->isValid()) {
            return response()->json([
                'success' => false,
                'error' => 'No valid audio file uploaded. Ensure form-data key is "audio".'
            ], 400);
        }

        try {
            $response = Http::asMultipart()
                ->withHeaders([
                    'Authorization' => "Bearer {$this->API_KEY}:{$this->EMAIL}"
                ])
                ->attach(
                    'audio',  // must match backend field name
                    fopen($file->getRealPath(), 'r'),
                    $file->getClientOriginalName()
                )
                ->timeout(120)
                ->withoutVerifying()
                ->post($this->SDK_URL);

            return response()->json([
                'success' => true,
                'backend_status' => $response->status(),
                'backend_response' => $response->json()
            ]);

        } catch (\Exception $e) {
    // This logs the error to storage/logs/laravel.log so you can read it
    \Log::error("Audio Upload Failed: " . $e->getMessage());

    return response()->json([
        'success' => false,
        'error' => 'Backend Communication Error',
        'debug' => $e->getMessage() // This will show you the real error in Postman
    ], 500);
}
    }
}

/// root folder and run php artisan serve and postman