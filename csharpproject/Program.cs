using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Hosting;
using System.Net.Http.Headers;

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

// ------------------------------
// Configuration
// ------------------------------
const string SDK_URL = "https://api.eslyqurity.com/api/sdk/incidents";
const string API_KEY = "$2b$10$c6Y402lEQeUn51VGKLzunuI2flbapcSWufvz4CuDl9nQtXBKS4GfK";
const string EMAIL = "simmykheswa@outlook.com";

// ------------------------------
// POST /send-audio
// ------------------------------
app.MapPost("/send-audio", async (HttpRequest request) =>
{
    if (!request.HasFormContentType || request.Form.Files.Count == 0)
        return Results.BadRequest(new { error = "No audio file uploaded" });

    var audioFile = request.Form.Files[0];

    try
    {
        using var client = new HttpClient();
        using var content = new MultipartFormDataContent();
        using var stream = audioFile.OpenReadStream();
        var fileContent = new StreamContent(stream);
        fileContent.Headers.ContentType = new MediaTypeHeaderValue("audio/webm");

        // Important: field name "audio" to match other SDK examples
        content.Add(fileContent, "audio", audioFile.FileName);

        // Authorization header
        client.DefaultRequestHeaders.Authorization =
            new AuthenticationHeaderValue("Bearer", $"{API_KEY}:{EMAIL}");

        var response = await client.PostAsync(SDK_URL, content);
        var responseBody = await response.Content.ReadAsStringAsync();

        return Results.Json(new { success = true, sdkResponse = responseBody });
    }
    catch (Exception ex)
    {
        Console.WriteLine("❌ Error sending audio: " + ex.Message);
        return Results.Json(new { error = "Failed to send audio" }, statusCode: 500);
    }
});

// ------------------------------
// Run server
// ------------------------------
app.Run("http://localhost:5000");
///// to run this dotnet run 