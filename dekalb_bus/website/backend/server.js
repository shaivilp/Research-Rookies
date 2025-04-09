const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");
const ort = require("onnxruntime-node");
const fs = require("fs");
const { DateTime } = require('luxon');
const routeStops = require('../../data/route_stops.json');

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
        const {route, startingStop, endingStop} = req.body;

        // Validate input data
        if (!route || !startingStop || !endingStop) {
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

        const now = DateTime.now().setZone('America/Chicago');
        const day = now.weekday;
        const hour = now.hour;
        const minute = now.minute;

        // Convert input into a Float32Array tensor
        const inputTensor = new ort.Tensor("float32", new Float32Array([routeID, startingStop, endingStop, day, hour, minute]), [1, 6]);
        
        console.log("Input Tensor:", inputTensor);

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

app.get('/routes', (req, res) => {
    const routes = routeStops.map(route => route.routeID);
    res.json(routes);
});

app.get('/stops/:routeID', (req, res) => {
    const routeID = req.params.routeID;
    const route = routeStops.find(r => r.routeID == routeID);
    if (route) {
      res.json(route.stops);
    } else {
      res.status(404).send('Route not found');
    }
});


app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
