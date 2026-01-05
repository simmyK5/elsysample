using System;
using System.IO;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;

class Program
{

    const string SDK_URL = "https://api.eslyqurity.com/api/sdk/incidents";
    const string API_KEY = "";
    const string EMAIL = "";

    static async Task Main()
    {
        Console.WriteLine("Enter path to audio file (.webm, .wav, .mp3, .m4a):");
        string filePath = Console.ReadLine()?.Trim() ?? "";

        if (!File.Exists(filePath))
        {
            Console.WriteLine("File not found!");
            return;
        }

        // Determine content type based on file extension
        string contentType = filePath.EndsWith(".wav", StringComparison.OrdinalIgnoreCase) ? "audio/wav" :
                             filePath.EndsWith(".mp3", StringComparison.OrdinalIgnoreCase) ? "audio/mpeg" :
                             filePath.EndsWith(".m4a", StringComparison.OrdinalIgnoreCase) ? "audio/m4a" :
                             "audio/webm";

        try
        {
            using var client = new HttpClient();
            using var content = new MultipartFormDataContent();
            using var fileStream = File.OpenRead(filePath);
            var fileContent = new StreamContent(fileStream);
            fileContent.Headers.ContentType = new MediaTypeHeaderValue(contentType);

            // Use "audio" as the field name to match SDK conventions
            content.Add(fileContent, "audio", Path.GetFileName(filePath));

            // Set Authorization header
            client.DefaultRequestHeaders.Authorization =
                new AuthenticationHeaderValue("Bearer", $"{API_KEY}:{EMAIL}");

            Console.WriteLine("Uploading audio to SDK...");
            var response = await client.PostAsync(SDK_URL, content);
            var responseBody = await response.Content.ReadAsStringAsync();

            Console.WriteLine("Response from SDK:");
            Console.WriteLine(responseBody);
        }
        catch (Exception ex)
        {
            Console.WriteLine("Error sending audio: " + ex.Message);
        }
    }
}



/// to run this file dotnet run
/// C:\Users\simmy\Music\sensor_incident1760169932793_audio.wav
/// C:\Users\simmy\Music\sensor_incident1760190644436_audio.wav