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

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()

        if let apiKey = Bundle.main.object(forInfoDictionaryKey: "MAPS_API_KEY_IOS") as? String {
            GMSServices.provideAPIKey(apiKey)
        }

        NativeMapRegistry.shared.provider = GoogleMapsProvider()
    }

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}
