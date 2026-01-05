<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AudioController;

Route::get('/', function () {
    return view('welcome');
});
Route::post("/send-audio", [AudioController::class, 'sendAudio']);