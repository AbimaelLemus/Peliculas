package com.postulacion.prueba2.Ubicaciones.Vista;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.postulacion.prueba2.R;

import java.util.List;
import java.util.Locale;

public class Ubicaciones extends Fragment {
    private String TAG = "Ubicaciones";
    private LocationManager locationManager;
    private Context context;
    private TextView tvUbicacion;
    private TextView tvTiempoRestante;
    private CountDownTimer countDownTimer;
    private boolean runTimer = false;

    public Ubicaciones(Context context) {
        this.context = context;
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

        tvUbicacion = view.findViewById(R.id.tvUbiNombre);
        tvTiempoRestante = view.findViewById(R.id.tvUbiTiempoRestante);

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
            if (!runTimer) {

                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> list;
                try {
                    list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address data = list.get(0);
                    tvUbicacion.setText(data.getLocality() + ", " + data.getAdminArea() + ", " + data.getCountryName());

                } catch (Exception e) {
                    mostrarToast("NO SE PUDO OBTENER LA UBICACION");
                }

                countDownTimer = new CountDownTimer(30 * 60 * 1000, 1000) {

                    @SuppressLint("LongLogTag")
                    public void onTick(long millisUntilFinished) {
                        runTimer = true;
                        long segundos = millisUntilFinished / 1000;
                        long minutos = segundos / 60;
                        String tiempo = "Tiempo para obtener siguiente ubicacion";
                        //Log.i(tiempo,  (minutos + 1) + " M.");
                        tvTiempoRestante.setText(tiempo + "\n" + (minutos + 1) + " m");
                    }

                    public void onFinish() {
                        runTimer = false;
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> list;
                        try {
                            list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Address data = list.get(0);
                            tvUbicacion.setText(data.getLocality() + ", " + data.getAdminArea() + ", " + data.getCountryName());

                        } catch (Exception e) {
                            mostrarToast("NO SE PUDO OBTENER LA UBICACION");
                        }
                        //locationManager.removeUpdates(locationListenerNetwork);
                    }
                }.start();
            }

        }

        public void cancelarTimer() {
            if (countDownTimer != null) {
                countDownTimer.cancel();
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

    public void mostrarToast(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
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