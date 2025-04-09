import React, { useEffect, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from '@/components/ui/select';

export default function ETAPredictor() {
  const [routes, setRoutes] = useState([]);
  const [stops, setStops] = useState([]);
  const [selectedRoute, setSelectedRoute] = useState('');
  const [startStop, setStartStop] = useState('');
  const [endStop, setEndStop] = useState('');
  const [eta, setEta] = useState(null);

  useEffect(() => {
    fetch('http://localhost:3000/routes')
      .then(res => res.json())
      .then(data => setRoutes(data));
  }, []);

  useEffect(() => {
    if (selectedRoute) {
      fetch(`http://localhost:3000/stops/${selectedRoute}`)
        .then(res => res.json())
        .then(data => {
          const seen = new Set();
          const uniqueStops = data.filter(stop => {
            const id = stop.stopIDs;
            if (seen.has(id)) return false;
            seen.add(id);
            return true;
          });
          setStops(uniqueStops);
        });
    }
  }, [selectedRoute]);

  const handleSubmit = () => {
    if (!selectedRoute || !startStop || !endStop) return;
    const payload = {
      route: selectedRoute,
      startingStop: startStop,
      endingStop: endStop
    };

    fetch('http://localhost:3000/predict', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    })
    .then(res => res.json())
    .then(data => {
      setEta(data.estimated_arrival_time);
    });
  };

  const formatEta = (seconds) => {
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = Math.floor(seconds % 60);
    return [
      hrs > 0 ? `${hrs}h` : null,
      mins > 0 ? `${mins}m` : null,
      `${secs}s`
    ].filter(Boolean).join(' ');
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
      <div className="w-full max-w-xs bg-white rounded-xl shadow-lg p-6">
        <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">ETA Predictor</h1>

        <div className="space-y-6 flex flex-col items-center">
          <div className="w-full flex flex-col items-center">
            <label className="block text-sm font-medium text-gray-700 mb-2">Select Route</label>
            <Select onValueChange={setSelectedRoute}>
              <SelectTrigger className="w-64 text-sm h-9">
                <SelectValue placeholder="Select Route" />
              </SelectTrigger>
              <SelectContent>
                {routes.map(route => (
                  <SelectItem key={route} value={route}>
                    {route}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="w-full flex flex-col items-center">
            <label className="block text-sm font-medium text-gray-700 mb-2">Starting Stop</label>
            <Select onValueChange={setStartStop} disabled={!stops.length}>
              <SelectTrigger className="w-64 text-sm h-9">
                <SelectValue placeholder="Start Stop" />
              </SelectTrigger>
              <SelectContent>
                {stops.map(stop => (
                  <SelectItem key={stop.stopIDs} value={stop.stopIDs.toString()}>
                    {stop.stopName}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="w-full flex flex-col items-center">
            <label className="block text-sm font-medium text-gray-700 mb-2">Ending Stop</label>
            <Select onValueChange={setEndStop} disabled={!stops.length}>
              <SelectTrigger className="w-64 text-sm h-9">
                <SelectValue placeholder="End Stop" />
              </SelectTrigger>
              <SelectContent>
                {stops.map(stop => (
                  <SelectItem key={stop.stopIDs} value={stop.stopIDs.toString()}>
                    {stop.stopName}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="mt-6">
            <Button onClick={handleSubmit} disabled={!startStop || !endStop || !selectedRoute} className="w-64 h-9 text-sm">
              Predict ETA
            </Button>
          </div>

          {eta !== null && (
            <div className="text-center text-xl font-semibold text-indigo-800 mt-6">
              Predicted ETA: <span className="font-bold text-indigo-900">{formatEta(eta)}</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}