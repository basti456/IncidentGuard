import SwiftUI
import FirebaseCore
import FirebaseFirestore
import GoogleMaps
import Shared

class MapViewDelegate: NSObject, GMSMapViewDelegate {
    var onMapClick: ((Double, Double) -> Void)?

    func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
        onMapClick?(coordinate.latitude, coordinate.longitude)
    }
}

@main
struct iOSApp: App {
    private let mapDelegate = MapViewDelegate()

    init() {
        FirebaseApp.configure()

        // 1. Read Maps API Key from Info.plist
        if let apiKey = Bundle.main.object(forInfoDictionaryKey: "MAPS_API_KEY_IOS") as? String {
            GMSServices.provideAPIKey(apiKey)
        }

        // 2. Map View Factory
        NativeMapConfig.shared.provider = { [self] latitude, longitude, zoom, incidents, onMapClick in
            let lat = latitude.doubleValue
            let lon = longitude.doubleValue
            let camera = GMSCameraPosition.camera(withLatitude: lat, longitude: lon, zoom: zoom.floatValue)
            let mapView = GMSMapView.map(withFrame: .zero, camera: camera)

            self.mapDelegate.onMapClick = { newLat, newLon in
                onMapClick?(KotlinDouble(double: newLat), KotlinDouble(double: newLon))
            }
            mapView.delegate = self.mapDelegate

            return mapView
        }

        // 3. Map View Updater (With Color-Coded Pins by Severity)
        NativeMapConfig.shared.updater = { mapView, latitude, longitude, zoom, incidents in
            guard let gmsMapView = mapView as? GMSMapView else { return }
            let lat = latitude.doubleValue
            let lon = longitude.doubleValue

            let camera = GMSCameraPosition.camera(withLatitude: lat, longitude: lon, zoom: zoom.floatValue)
            gmsMapView.animate(to: camera)
            gmsMapView.clear()

            // Current Location Marker (Blue Pin)
            let currentMarker = GMSMarker()
            currentMarker.position = CLLocationCoordinate2D(latitude: lat, longitude: lon)
            currentMarker.title = incidents.isEmpty ? "Selected Location" : "Your Current Location"
            currentMarker.icon = GMSMarker.markerImage(with: .systemBlue)
            currentMarker.map = gmsMapView

            // Reported Incident Pins (Color-Coded by Severity)
            for incident in incidents {
                let marker = GMSMarker()
                marker.position = CLLocationCoordinate2D(latitude: incident.latitude, longitude: incident.longitude)
                marker.title = "🚨 " + incident.title
                marker.snippet = "Severity: \(incident.severity.name) | Status: \(incident.status.name)"

                switch incident.severity {
                case .critical:
                    marker.icon = GMSMarker.markerImage(with: .systemRed)
                case .high:
                    marker.icon = GMSMarker.markerImage(with: .systemOrange)
                case .medium:
                    marker.icon = GMSMarker.markerImage(with: .systemYellow)
                case .low:
                    marker.icon = GMSMarker.markerImage(with: .systemGreen)
                default:
                    marker.icon = GMSMarker.markerImage(with: .systemRed)
                }

                marker.map = gmsMapView
            }
        }

        // 4. Real Firebase Firestore Sync Handler (Using incident.description_)
        NativeSyncConfig.shared.syncHandler = { incident, completion in
            let db = Firestore.firestore()
            let data: [String: Any] = [
                "id": incident.id,
                "title": incident.title,
                "description": incident.description_, // Note: description_ avoids NSObject property collision!
                "severity": incident.severity.name,
                "status": incident.status.name,
                "latitude": incident.latitude,
                "longitude": incident.longitude,
                "temperature": incident.weather.temperatureCelsius as Any,
                "weatherCondition": incident.weather.condition as Any,
                "localImagePath": incident.localImagePath as Any,
                "remoteImageUrl": incident.remoteImageUrl as Any,
                "createdAt": incident.createdAt,
                "updatedAt": incident.updatedAt
            ]

            db.collection("incidents").document(incident.id).setData(data) { error in
                if let error = error {
                    completion(false, error.localizedDescription)
                } else {
                    completion(true, nil)
                }
            }
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}