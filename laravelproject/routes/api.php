use App\Http\Controllers\AudioController;

Route::post('/send-audio', [AudioController::class, 'sendAudio']);
