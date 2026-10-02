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

func severityColor(_ severity: Severity) -> UIColor {
    switch severity {
    case .critical:
        return .systemRed
    case .high:
        return .systemOrange
    case .medium:
        return .systemYellow
    case .low:
        return .systemGreen
    default:
        return .systemRed
    }
}

func parseSeverity(_ name: String) -> Severity {
    switch name.uppercased() {
    case "CRITICAL":
        return .critical
    case "HIGH":
        return .high
    case "MEDIUM":
        return .medium
    case "LOW":
        return .low
    default:
        return .low
    }
}

func parseStatus(_ name: String) -> Status {
    switch name.uppercased() {
    case "OPEN":
        return .open
    case "IN_PROGRESS":
        return .inProgress
    case "RESOLVED":
        return .resolved
    default:
        return .open
    }
}

class GoogleMapsProvider: NSObject, NativeMapProvider {

    private var mapDelegate = MapViewDelegate()

    func createMapView(
        latitude: Double,
        longitude: Double,
        zoom: Float,
        incidents: [Incident],
        onMapClick: @escaping (KotlinDouble, KotlinDouble) -> Void
    ) -> Any {
        let camera = GMSCameraPosition.camera(withLatitude: latitude, longitude: longitude, zoom: zoom)
        let mapView = GMSMapView.map(withFrame: .zero, camera: camera)

        mapDelegate.onMapClick = { newLat, newLon in
            onMapClick(KotlinDouble(double: newLat), KotlinDouble(double: newLon))
        }
        mapView.delegate = mapDelegate

        return mapView
    }

    func updateMapView(
        mapView: Any,
        latitude: Double,
        longitude: Double,
        zoom: Float,
        incidents: [Incident]
    ) {
        guard let gmsMapView = mapView as? GMSMapView else { return }

        let camera = GMSCameraPosition.camera(withLatitude: latitude, longitude: longitude, zoom: zoom)
        gmsMapView.animate(to: camera)
        gmsMapView.clear()

        let currentMarker = GMSMarker()
        currentMarker.position = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        currentMarker.title = incidents.isEmpty ? "Selected Location" : "Your Current Location"
        currentMarker.icon = GMSMarker.markerImage(with: .systemBlue)
        currentMarker.map = gmsMapView

        for incident in incidents {
            let marker = GMSMarker()
            marker.position = CLLocationCoordinate2D(latitude: incident.latitude, longitude: incident.longitude)
            marker.title = "🚨 " + incident.title
            marker.snippet = "Severity: \(incident.severity.name) | Status: \(incident.status.name)"
            marker.icon = GMSMarker.markerImage(with: severityColor(incident.severity))
            marker.map = gmsMapView
        }
    }
}

class FirebaseSyncProvider: NSObject, NativeSyncProvider {

    func syncIncidentToFirestore(incident: Incident, completionHandler: @escaping (KotlinBoolean?, Error?) -> Void) {
        let db = Firestore.firestore()
        let data: [String: Any] = [
            "id": incident.id,
            "title": incident.title,
            "description": incident.description_,
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
                completionHandler(nil, error)
            } else {
                completionHandler(KotlinBoolean(bool: true), nil)
            }
        }
    }

    func fetchRemoteIncidents(completionHandler: @escaping ([Incident]?, Error?) -> Void) {
        let db = Firestore.firestore()
        db.collection("incidents").getDocuments { snapshot, error in
            if let error = error {
                completionHandler(nil, error)
                return
            }

            var resultList: [Incident] = []
            for doc in snapshot?.documents ?? [] {
                let data = doc.data()

                let id = (data["id"] as? String) ?? doc.documentID
                let title = (data["title"] as? String) ?? "Untitled Incident"

                // Use NSNumber for flexible number parsing from Firestore
                let latNumber = data["latitude"] as? NSNumber
                let lonNumber = data["longitude"] as? NSNumber
                guard let lat = latNumber?.doubleValue, let lon = lonNumber?.doubleValue else {
                    continue
                }

                let desc = (data["description"] as? String) ?? ""
                let severityStr = (data["severity"] as? String) ?? "LOW"
                let statusStr = (data["status"] as? String) ?? "OPEN"

                let tempNumber = data["temperature"] as? NSNumber
                let temp = tempNumber?.doubleValue
                let weatherCond = data["weatherCondition"] as? String

                let localImg = data["localImagePath"] as? String
                let remoteImg = data["remoteImageUrl"] as? String

                let createdNumber = data["createdAt"] as? NSNumber
                let created = createdNumber?.int64Value ?? Int64(Date().timeIntervalSince1970 * 1000)

                let updatedNumber = data["updatedAt"] as? NSNumber
                let updated = updatedNumber?.int64Value ?? Int64(Date().timeIntervalSince1970 * 1000)

                let severity = parseSeverity(severityStr)
                let status = parseStatus(statusStr)
                let kotlinTemp = temp.map { KotlinDouble(double: $0) }
                let weather = WeatherInfo(temperatureCelsius: kotlinTemp, condition: weatherCond)

                let incident = Incident(
                    id: id,
                    title: title,
                    description: desc,
                    severity: severity,
                    status: status,
                    latitude: lat,
                    longitude: lon,
                    weather: weather,
                    localImagePath: localImg,
                    remoteImageUrl: remoteImg,
                    createdAt: created,
                    updatedAt: updated,
                    isSynced: true
                )
                resultList.append(incident)
            }
            completionHandler(resultList, nil)
        }
    }
}

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()

        if let apiKey = Bundle.main.object(forInfoDictionaryKey: "MAPS_API_KEY_IOS") as? String {
            GMSServices.provideAPIKey(apiKey)
        }

        NativeMapRegistry.shared.provider = GoogleMapsProvider()
        NativeSyncRegistry.shared.provider = FirebaseSyncProvider()
    }

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}
