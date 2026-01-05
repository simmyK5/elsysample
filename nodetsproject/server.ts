import express, { Request, Response } from "express";
import multer from "multer";
import path from "path";
import EslyQuritySDK from "esly-qurity-sdk";
import fs from "node:fs/promises";
import { fileURLToPath } from "url";

// Fix for __dirname when using ES Modules in TS
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const port = 3004;

const upload = multer({ dest: "uploads/" });

app.use(express.static("public"));

const sdk = new EslyQuritySDK({
  apiKey: "$2b$10$c6Y402lEQeUn51VGKLzunuI2flbapcSWufvz4CuDl9nQtXBKS4GfK",
  email: "simmykheswa@outlook.com",
  baseUrl: "https://api.eslyqurity.com",
});

app.post("/send-audio", upload.single("audio"), async (req: Request, res: Response) => {
  const file = req.file;
  if (!file) {
    return res.status(400).json({ error: "No file uploaded" });
  }

  const audioPath = path.resolve(file.path);

  try {
    const audioBuffer = await fs.readFile(audioPath);

    // Convert Buffer to Blob for the SDK
    // Node.js 18+ has global Blob support
    const audioBlob = new Blob([audioBuffer], {
      type: file.mimetype || "audio/webm",
    });

    console.log("📡 Sending audio to SDK…");

    const sdkResponse = await sdk.reportIncident({
      audio: audioBlob as any, // Cast to any if SDK types are strictly browser-blobs
    });

    // Clean up the uploaded file
    await fs.unlink(audioPath);
    
    res.json({ success: true, sdkResponse: sdkResponse });
  } catch (err) {
    console.error("❌ Error sending to SDK:", err);
    
    // Try to clean up even if SDK fails
    try { await fs.unlink(audioPath); } catch (e) {}
    
    res.status(500).json({ error: "Failed to send audio" });
  }
});

app.listen(port, () => console.log(`🚀 Server running at http://localhost:${port}`));