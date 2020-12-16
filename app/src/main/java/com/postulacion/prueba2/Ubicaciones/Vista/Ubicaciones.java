package com.postulacion.prueba2.Ubicaciones.Vista;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.postulacion.prueba2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Ubicaciones extends Fragment {
    private String TAG = "Ubicaciones";
    private LocationManager locationManager;
    private Context context;
    private TextView tvUbicacion;
    private TextView tvTiempoRestante;
    private CountDownTimer countDownTimer;
    private int tiempoObtencionUbicacion = 30;
    private GoogleMap mMap;
    private Double latitud;
    private Double longitud;
    private SupportMapFragment mapFragment;
    private String verificacionTimer = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore db;

    public Ubicaciones(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ubicaciones, container, false);
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

        preferences = getActivity().getSharedPreferences(this.getResources().getString(R.string.prefDatosUser), Context.MODE_PRIVATE);
        editor = preferences.edit();
        verificacionTimer = preferences.getString("runTimer", null);

        tvUbicacion = view.findViewById(R.id.tvUbiNombre);
        tvTiempoRestante = view.findViewById(R.id.tvUbiTiempoRestante);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (statusGPS("onCreateView")) {//gps activo
            verificarPermisoUbicacio("onCreateView");
        } else {//gps inactivo
            configGPS();
        }

        return view;
    }

    private void verificarPermisoUbicacio(String ubicacion) {
        //Log.i(TAG, "verificarPermisoUbicacio: " + ubicacion );

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//permiso ubicacion inactivo

            boolean alertNativo = verificacionAlert("onCreateView");

            Log.d(TAG, "onCreateView: alertNativo:" + alertNativo);

            if (alertNativo) {//se pide alert nativo
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2000);
            } else { //se muestra alert que redirigue a configuracion
                configPermiso();
            }

        } else {//permiso ubicacion activo
            obtenerUbicacion();
        }
    }

    public boolean statusGPS(String ubicacion) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean verificacionAlert(String ubicacion) {
        return ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode);

        if (requestCode == 1000) {
            if (!statusGPS("onActivityResult")) {//el GPS continua inactivo
                mostrarToast("El GPS continua inactivo");
            } else {//el gps activo
                verificarPermisoUbicacio("onActivityResult");
            }
        } else if (requestCode == 3000) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //permiso ubicacion inactivo
                mostrarToast("No se activo el permiso de ubicación");
            } else { //permiso ubicacion activo
                obtenerUbicacion();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: " + requestCode + " - " + grantResults[0]);

        if (requestCode == 2000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //permiso ubicacion activo
                obtenerUbicacion();
            } else { //permiso ubicacion inactivo y posiblemente activo no volver a mostrar
                mostrarToast("No se activo el permiso de ubicación");
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void obtenerUbicacion() {
        locationManager.removeUpdates(locationListenerNetwork);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, locationListenerNetwork);
    }


    private final LocationListener locationListenerNetwork = new LocationListener() {

        public void onLocationChanged(final Location location) {
            verificacionTimer = preferences.getString("runTimer", null);
            //Log.w(TAG, "onLocationChanged: " + verificacionTimer);

            if (verificacionTimer == null) {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> list;
                try {
                    list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address data = list.get(0);
                    Log.e(TAG, "onLocationChanged: " + data.getLongitude() + " - " + data.getLatitude());

                    subirInformacion(data);

                    tvUbicacion.setText(data.getLocality() + ", " + data.getAdminArea() + ", " + data.getCountryName());
                    editor.putString("runTimer", "false").apply();
                    //locationManager.removeUpdates(locationListenerNetwork);


                } catch (Exception e) {
                    mostrarToast("NO SE PUDO OBTENER LA UBICACION");
                }

            } else {

                if (verificacionTimer.equals("false")) {
                    pintarDireccionesEnMapa();

                    countDownTimer = new CountDownTimer(tiempoObtencionUbicacion * 60 * 1000, 1000) {

                        @SuppressLint("LongLogTag")
                        public void onTick(long millisUntilFinished) {
                            editor.putString("runTimer", "true").apply();
                            long segundos = millisUntilFinished / 1000;
                            long minutos = segundos / 60;
                            String tiempo = "Tiempo para obtener siguiente ubicacion";
                            if (segundos == 3 || segundos == 2 || segundos == 1) {
                                Log.i(tiempo, segundos + " S.");
                            }
                            tvTiempoRestante.setText(tiempo + "\n" + (minutos + 1) + " m");
                        }

                        public void onFinish() {
                            editor.putString("runTimer", "false").apply();
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            List<Address> list;
                            try {
                                list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                Address data = list.get(0);
                                subirInformacion(data);
                                tvUbicacion.setText(data.getLocality() + ", " + data.getAdminArea() + ", " + data.getCountryName());

                            } catch (Exception e) {
                                mostrarToast("NO SE PUDO OBTENER LA UBICACION");
                            }
                            //locationManager.removeUpdates(locationListenerNetwork);
                        }
                    }.start();
                } else {
                    //cancelarTimer();
                }

            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void subirInformacion(Address data) {

        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create a new user with a first and last name
        Map<String, Object> ubicacionUsuario = new HashMap<>();
        ubicacionUsuario.put("longitud", "" + data.getLongitude());
        ubicacionUsuario.put("latitud", "" + data.getLatitude());
        ubicacionUsuario.put("ciudad", "" + data.getLocality());
        ubicacionUsuario.put("estado", "" + data.getAdminArea());
        ubicacionUsuario.put("pais", "" + data.getCountryName());
        ubicacionUsuario.put("fecha", fecha);
        ubicacionUsuario.put("hora", hora);

        Log.e(TAG, "subirInformacion: " + data.getLatitude() + " - " + data.getLongitude());

        /*db.collection("ubicacion")
                .document("direcciones")
                .set(ubicacionUsuario, SetOptions.merge());*/

        //collectionReference.document("direcciones").set(ubicacionUsuario);


        // Add a new document with a generated ID
        db.collection("ubicacion")
                .add(ubicacionUsuario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //mostrarToast("Informacion guardada correctamente!!!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mostrarToast("La ultima ubicacion no se guardo!!!");
                    }
                });

        pintarDireccionesEnMapa();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ##############");
        pintarDireccionesEnMapa();
    }

    private void pintarDireccionesEnMapa() {

        db.collection("ubicacion")
                //.whereEqualTo("pais", "México")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final GoogleMap googleMap) {
                                    mMap = googleMap;

                                    for (Marker marker : realTimeMarkers) {
                                        marker.remove();
                                    }

                                    int i = 0;

                                    //mMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)).title(ciudad + ", " + estado));
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        //Log.d(TAG, document.getId() + " => " + document.getData());

                                        latitud = Double.parseDouble(String.valueOf(document.getData().get("latitud")));
                                        longitud = Double.parseDouble(String.valueOf(document.getData().get("longitud")));
                                        String ciudad = String.valueOf(document.getData().get("ciudad"));
                                        String estado = String.valueOf(document.getData().get("estado"));

                                        //Log.e(TAG, "onComplete: " + (i+1) + " : " + latitud + " - " + longitud);
                                        i++;

                                        CameraPosition googlePlex = CameraPosition.builder()
                                                .target(new LatLng(latitud, longitud))
                                                .bearing(0)
                                                .tilt(45)
                                                .build();

                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(new LatLng(latitud, longitud)).title(ciudad + ", " + estado);


                                        tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                                    }
                                    Log.e(TAG, "onMapReady: totalDirecciones: " + (i + 1));


                                    realTimeMarkers.clear();
                                    realTimeMarkers.addAll(tmpRealTimeMarkers);

                                }
                            });


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


    public void mostrarToast(String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }

    private void configGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¿Deseas activar el GPS desde configuración?")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1000);
                    }
                })
                .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void configPermiso() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¿Deseas activar el permiso de ubicación desde configuración?")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 3000);
                    }
                })
                .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}