package com.insoft.tabusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.insoft.tabusearch.Utils.RegisterAPI;
import com.insoft.tabusearch.Utils.UtilsAPI;
import com.insoft.tabusearch.json.TabusearchRequestJson;
import com.insoft.tabusearch.json.TabusearchResponseJson;
import com.insoft.tabusearch.model.Tabulist;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.optimization.v1.MapboxOptimization;
import com.mapbox.api.optimization.v1.models.OptimizationResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.OAEPParameterSpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconTextFit;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconTextFitPadding;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;

public class OptimizationActivity extends AppCompatActivity implements OnMapReadyCallback,
        MapboxMap.OnMapClickListener, MapboxMap.OnMapLongClickListener {

    private static final String ICON_GEOJSON_SOURCE_ID = "icon-source-id";
    private static final String FIRST = "first";
    private static final String LAST = "last";
    private static final String ANY = "any";
    private static final String TEAL_COLOR = "#23D2BE";
    private static final float POLYLINE_WIDTH = 5;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private DirectionsRoute optimizedRoute;
    private MapboxOptimization optimizedClient;
    private List<Point> stops = new ArrayList<>();
    private Point origin;
    private RegisterAPI registerAPI;
    private List<Tabulist> datalist;
    private String userlat, userlong;
    private int id_tujuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_optimization);
        registerAPI = UtilsAPI.getApiService();
        userlat = getIntent().getStringExtra("userlat");
        userlong = getIntent().getStringExtra("userlong");
        id_tujuan = getIntent().getIntExtra("id_tujuan", 0);
        addFirstStopToStopsList();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Toast.makeText(OptimizationActivity.this, "construct "+userlat, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return true;
    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {
        stops.clear();
        if (mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                resetDestinationMarkers(style);
                removeOptimizedRoute(style);
                addFirstStopToStopsList();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.addOnMapLongClickListener(OptimizationActivity.this);
                fetch_data();
            }
        });
    }


    private void initMarkerIconSymbolLayer(@NonNull Style loadedMapStyle, List<Tabulist> datalist) {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.marker, null);
        Bitmap mbitmap = BitmapUtils.getBitmapFromDrawable(drawable);
        loadedMapStyle.addImage("icon-image", mbitmap);
        loadedMapStyle.addSource(new GeoJsonSource(ICON_GEOJSON_SOURCE_ID,
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude()))));
        loadedMapStyle.addLayer(new SymbolLayer("icon-layer-id", ICON_GEOJSON_SOURCE_ID).withProperties(
                iconImage("icon-image"),
                iconSize(1f),
                iconAllowOverlap(true),
                textField("User location"),
                iconIgnorePlacement(true),
                iconOffset(new Float[]{0f, -7f})

        ));

        for(int i =0;i<datalist.size(); i++) {
            loadedMapStyle.addSource(new GeoJsonSource(ICON_GEOJSON_SOURCE_ID+"-"+i,
                    Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(datalist.get(i).getLongitude()), Double.parseDouble(datalist.get(i).getLatitude())))));
            loadedMapStyle.addLayer(new SymbolLayer("icon-layer-id-"+i, ICON_GEOJSON_SOURCE_ID+"-"+i).withProperties(
                    iconImage("icon-image"),
                    iconSize(1f),
                    iconAllowOverlap(true),
                    textField(datalist.get(i).getNama_wisata()),
                    iconIgnorePlacement(true),
                    iconOffset(new Float[]{0f, -7f})

            ));
        }

    }

    private void initOptimizedRouteLineLayer(@NonNull Style loadedMapStyle, List<Tabulist> datalist) {
        loadedMapStyle.addSource(new GeoJsonSource("optimized-route-source-id"));

        for(int i =0; i<datalist.size();i++) {
            loadedMapStyle.addLayerBelow(new LineLayer("optimized-route-layer-id-"+i, "optimized-route-source-id")
                    .withProperties(
                            lineColor(Color.parseColor(TEAL_COLOR)),
                            lineWidth(POLYLINE_WIDTH)
                    ), "icon-layer-id-"+i);
        }

    }


    private void resetDestinationMarkers(@NonNull Style style) {
        GeoJsonSource optimizedLineSource = style.getSourceAs(ICON_GEOJSON_SOURCE_ID);
        if (optimizedLineSource != null) {
            optimizedLineSource.setGeoJson(Point.fromLngLat(origin.longitude(), origin.latitude()));
        }
    }

    private void removeOptimizedRoute(@NonNull Style style) {
        GeoJsonSource optimizedLineSource = style.getSourceAs("optimized-route-source-id");
        if (optimizedLineSource != null) {
            optimizedLineSource.setGeoJson(FeatureCollection.fromFeatures(new Feature[] {}));
        }
    }

    private boolean alreadyTwelveMarkersOnMap() {
        return stops.size() == 12;
    }


    private void addDestinationMarker(@NonNull Style style, LatLng point) {
        List<Feature> destinationMarkerList = new ArrayList<>();
        for (Point singlePoint : stops) {
            destinationMarkerList.add(Feature.fromGeometry(
                    Point.fromLngLat(singlePoint.longitude(), singlePoint.latitude()))
            );
        }

        destinationMarkerList.add(Feature.fromGeometry(Point.fromLngLat(point.getLongitude(), point.getLatitude())));
        GeoJsonSource iconSource = style.getSourceAs(ICON_GEOJSON_SOURCE_ID);
        if (iconSource != null) {
            iconSource.setGeoJson(FeatureCollection.fromFeatures(destinationMarkerList));
        }
    }

    private void addPointToStopsList(LatLng point) {
        stops.add(Point.fromLngLat(point.getLongitude(), point.getLatitude()));
    }

    private void addFirstStopToStopsList() {
// Set first stop
        Double duserlat = Double.parseDouble(userlat);
        Double duserlong = Double.parseDouble(userlong);
        origin = Point.fromLngLat(duserlong, duserlat);
        stops.add(origin);
    }

    private void getOptimizedRoute(@NonNull final Style style, List<Point> coordinates) {
        optimizedClient = MapboxOptimization.builder()
                .source(FIRST)
                .destination(LAST)
                .coordinates(coordinates)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                .build();

        optimizedClient.enqueueCall(new Callback<OptimizationResponse>() {
            @Override
            public void onResponse(Call<OptimizationResponse> call, Response<OptimizationResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(OptimizationActivity.this, R.string.no_success, Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body() != null) {
                        List<DirectionsRoute> routes = response.body().trips();
                        Double distance_trip = response.body().trips().get(0).distance();
                        Double time_trip = response.body().trips().get(0).duration();
//                        Toast.makeText(OptimizationActivity.this, "Distance "+distance_trip+" , Time "+time_trip,
//                                Toast.LENGTH_SHORT).show();

                        if (routes != null) {
                            if (routes.isEmpty()) {
                                Toast.makeText(OptimizationActivity.this, R.string.successful_but_no_routes,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                optimizedRoute = routes.get(0);

                                drawOptimizedRoute(style, optimizedRoute);
                            }
                        } else {
                            Toast.makeText(OptimizationActivity.this, String.format(getString(R.string.null_in_response),
                                    "The Optimization API response's list of routes"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(OptimizationActivity.this, String.format(getString(R.string.null_in_response),
                                "The Optimization API response's body"), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OptimizationResponse> call, Throwable throwable) {
                Log.d("Error: %s", throwable.getMessage());
            }
        });
    }

    private void drawOptimizedRoute(@NonNull Style style, DirectionsRoute route) {
        GeoJsonSource optimizedLineSource = style.getSourceAs("optimized-route-source-id");
        if (optimizedLineSource != null) {
            optimizedLineSource.setGeoJson(FeatureCollection.fromFeature(Feature.fromGeometry(
                    LineString.fromPolyline(route.geometry(), PRECISION_6))));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
// Cancel the directions API request
        if (optimizedClient != null) {
            optimizedClient.cancelCall();
        }
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void fetch_data() {
        TabusearchRequestJson param = new TabusearchRequestJson();
        param.setId_tujuan(id_tujuan);
        param.setOrigin_lat(userlat);
        param.setOrigin_long(userlong);

        registerAPI = UtilsAPI.getApiService();
        registerAPI.tabulist(param).enqueue(new Callback<TabusearchResponseJson>() {
            @Override
            public void onResponse(Call<TabusearchResponseJson> call, retrofit2.Response<TabusearchResponseJson> response) {
                if (response.isSuccessful()) {
                    datalist = response.body().getData();
                    String message = response.body().getMessage();
                    Style style = mapboxMap.getStyle();

                    initMarkerIconSymbolLayer(style, datalist);
                    initOptimizedRouteLineLayer(style, datalist);

                    Double duserlat = Double.parseDouble(userlat);
                    Double duserlong = Double.parseDouble(userlong);
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(duserlat , duserlong))
                            .zoom(12)
                            .tilt(13)
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);

                    if (style != null) {
                        for (int i = 0; i < datalist.size(); i++) {
                            Double dbllat = Double.parseDouble(datalist.get(i).getLatitude());
                            Double dbllong = Double.parseDouble(datalist.get(i).getLongitude());
                            LatLng point = new LatLng(dbllat, dbllong);

                            addDestinationMarker(style, point);
                            addPointToStopsList(point);
                            getOptimizedRoute(style, stops);
                        }
                    }
                } else {
                    Log.d("REs", response.toString());
                }
            }

            @Override
            public void onFailure(Call<TabusearchResponseJson> call, Throwable t) {
                Toast.makeText(OptimizationActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}