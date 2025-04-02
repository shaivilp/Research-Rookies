const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");
const ort = require("onnxruntime-node");
const fs = require("fs");

const app = express();
const PORT = 3000;

app.use(cors());
app.use(bodyParser.json());

let session;

// Load ONNX Model
async function loadModel() {
    session = await ort.InferenceSession.create("eta_model.onnx");
    console.log("ONNX model loaded successfully!");
}

// Load LabelEncoder mapping
async function loadLabelEncoder() {
    const rawData = fs.readFileSync("label_encoder.json");
    labelEncoder = JSON.parse(rawData);
    console.log("LabelEncoder mapping loaded!");
}

// Convert route string to encoded number
function encodeRoute(route) {
    return labelEncoder[route] !== undefined ? labelEncoder[route] : -1;
}

// Load the model and label encoder when the server starts
loadModel()
loadLabelEncoder()

app.post("/predict", async (req, res) => {
    try {
        const { route, startingStop, endingStop, day, hour, minute } = req.body;

        // Validate input data
        if (!route || !startingStop || !endingStop || !day || !hour || !minute) {
            return res.status(400).json({ error: "Invalid input data" });
        }
        
        if (!session) {
            return res.status(500).json({ error: "Model not loaded" });
        }

        // Convert categorical inputs into numerical values
        const routeID = encodeRoute(route);

        if(routeID === -1) {
            return res.status(400).json({ error: "Invalid route"});
        }

        // Convert input into a Float32Array tensor
        const inputTensor = new ort.Tensor("float32", new Float32Array([routeID, startingStop, endingStop, day, hour, minute]), [1, 6]);

        // Run inference
        const output = await session.run({ input: inputTensor });
        console.log(output)
        const eta_prediction = output.output.data[0];

        res.json({ estimated_arrival_time: eta_prediction });
    } catch (error) {
        console.error("Prediction error:", error);
        res.status(500).json({ error: "Error processing prediction" });
    }
});

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
