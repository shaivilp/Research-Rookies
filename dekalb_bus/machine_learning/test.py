import math

def euclidean_distance(lat1, lon1, lat2, lon2):

    R = 6371.0
    
    # Convert degrees to radians
    lat1_rad = math.radians(lat1)
    lon1_rad = math.radians(lon1)
    lat2_rad = math.radians(lat2)
    lon2_rad = math.radians(lon2)
    
    # Euclidean distance calculation
    x1 = R * lat1_rad
    y1 = R * lon1_rad
    x2 = R * lat2_rad
    y2 = R * lon2_rad
    
    distance = math.sqrt((x2 - x1)**2 + (y2 - y1)**2)
    
    return distance

lat1 = 41.9348030090332
lon1 = -88.76997375488281
lat2 = 41.93633270263672
lon2 = -88.7743911743164

dist = euclidean_distance(lat1, lon1, lat2, lon2)
print(f"The Euclidean distance is: {dist} km")
