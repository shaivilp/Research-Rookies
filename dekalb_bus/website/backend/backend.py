from fastapi import FastAPI, Request, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import torch
import torch.nn as nn
import numpy as np
import json
import joblib
from datetime import datetime
from zoneinfo import ZoneInfo

# FastAPI app
app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def get_chicago_time_components():
    now = datetime.now(ZoneInfo("America/Chicago"))
    day = now.isoweekday()
    hour = now.hour
    minute = now.minute
    return day, hour, minute

#Model architecture
class ETANetwork(nn.Module):
    def __init__(self, input_size=6):
        super(ETANetwork, self).__init__()
        self.model = nn.Sequential(
            nn.Linear(input_size, 64),
            nn.ReLU(),
            nn.Linear(64, 32),
            nn.ReLU(),
            nn.Linear(32, 1),
            nn.Sigmoid()
        )
    
    def forward(self, x):
        return self.model(x)

# Load model and preprocessors
model = ETANetwork()
model.load_state_dict(torch.load("./data/eta_model.pth"))
model.eval()

label_encoders = joblib.load("./data/label_encoders.pkl")
scaler_time = joblib.load("./data/numerical_scaler.pkl")
scaler_eta = joblib.load("./data/scaler_eta.pkl")
with open("./data/route_stops.json") as f:
    route_stops = json.load(f)

# Input schema
class PredictRequest(BaseModel):
    route: str
    startingStop: int
    endingStop: int

@app.post("/predict")
def predict(req: PredictRequest):
    try:
        encoded_route = label_encoders["routeID"].transform([req.route])[0]
        encoded_last = label_encoders["lastStopID"].transform([req.startingStop])[0]
        encoded_next = label_encoders["nextStopID_actual"].transform([req.endingStop])[0]
    except KeyError as e:
        raise HTTPException(status_code=400, detail=f"Unknown label: {e}")

    day, hour, minute = get_chicago_time_components()
    
    # Normalize time
    norm_time = scaler_time.transform([[day, hour, minute]])[0]

    input_vector = np.array([encoded_route, encoded_last, encoded_next, *norm_time], dtype=np.float32)
    input_tensor = torch.tensor(input_vector).unsqueeze(0)

    with torch.no_grad():
        pred_norm = model(input_tensor).numpy()[0][0]

    eta = scaler_eta.inverse_transform([[pred_norm]])[0][0]
    return {"estimated_arrival_time": eta}

@app.get("/routes")
def get_routes():
    return [route["routeID"] for route in route_stops]

@app.get("/stops/{route_id}")
def get_stops(route_id: str):
    for route in route_stops:
        if route["routeID"] == route_id:
            return route["stops"]
    raise HTTPException(status_code=404, detail="Route not found")
