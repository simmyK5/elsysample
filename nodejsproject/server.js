const express = require("express");
const multer = require("multer");
const fs = require("fs/promises");
const path = require("path");
const cors = require("cors");
const EslyQuritySDK = require("esly-qurity-sdk").default;

const app = express();
const port = 3003;

// Enable CORS (useful for local testing)
app.use(cors());

// Configure upload directory
const upload = multer({ dest: "uploads/" });

const sdk = new EslyQuritySDK({
    apiKey: "",
    email: "",
    baseUrl: "https://api.eslyqurity.com",
});

app.post("/send-audio", upload.single("audio"), async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({ error: "No audio file uploaded" });
        }

        const audioPath = path.join(__dirname, req.file.path);

        const audioBuffer = await fs.readFile(audioPath);

        const audioBlob = new Blob([audioBuffer], {
            type: "audio/webm", // or audio/wav
        });

        console.log("Sending audio to SDK…");

        const sdkResponse = await sdk.reportIncident({
            audio: audioBlob,
        });

        await fs.unlink(audioPath);

        res.json({
            success: true,
            message: "Audio forwarded to SDK",
            sdkResponse,
        });
    } catch (error) {
        console.error("SDK upload error:", error);
        res.status(500).json({ error: "Failed to send audio to SDK" });
    }
});

app.listen(port, () =>
    console.log(`Node Audio Proxy running on http://localhost:${port}`)
);