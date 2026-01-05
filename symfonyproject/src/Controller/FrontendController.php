<?php
namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Attribute\Route;

class FrontendController extends AbstractController
{
    public function __construct(private HttpClientInterface $client) {}

    #[Route('/send-audio', name: 'send-audio', methods: ['POST'])]
public function sendAudio(Request $request): JsonResponse
{
    // Receive raw audio from request
    $audioData = $request->getContent();
    file_put_contents('audio.log', "Received bytes: " . strlen($audioData));

    if (empty($audioData)) {
        return new JsonResponse(['error' => 'No audio received'], 400);
    }


    $url = 'https://api.eslyqurity.com/api/sdk/incidents';
    $apiKey = '';
    $email = '';

    $response = $this->client->request('POST', $url, [
        'headers' => [
            'Authorization' => "Bearer $apiKey:$email",
            'Content-Type'  => 'audio/wav',
        ],
        'body' => $audioData,
        'timeout' => 120
    ]);

    $statusCode = $response->getStatusCode();
    
    // Check if the response is actually JSON before calling toArray
    $contentType = $response->getHeaders()['content-type'][0] ?? '';
    
    if (str_contains($contentType, 'application/json')) {
        // If it's a boolean like 'true', toArray(false) might still fail 
        // if it's not a structured array. Let's get the raw content first.
        $content = $response->getContent(false);
        $data = json_decode($content, true);
    } else {
        $data = $response->getContent(false);
    }

    return new JsonResponse([
        'sent_to_backend' => true,
        'backend_status' => $statusCode,
        'backend_response' => $data
    ]);
}

   

}

/// root folder and run php -S localhost:9000 -t public and postman