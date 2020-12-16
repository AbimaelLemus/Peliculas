package com.postulacion.prueba2.Peliculas.Presentador;

import android.util.Log;

import com.postulacion.prueba2.Peliculas.Bean.BeanMovies;
import com.postulacion.prueba2.Peliculas.Interfaces.InterfacesPeliculas;
import com.postulacion.prueba2.Peliculas.Modelo.PeliculasModelo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PeliculasPresentador implements InterfacesPeliculas.Presentador {

    private InterfacesPeliculas.Modelo modelo;
    private InterfacesPeliculas.Vista vista;
    private String TAG = "PeliculasPresentador";

    public PeliculasPresentador(InterfacesPeliculas.Vista vista) {
        this.vista = vista;
        modelo = new PeliculasModelo(this);
    }

    @Override
    public void obtenerPeliculas() {
        vista.mostrarLoader(true);
        modelo.obtenerPeliculas();
    }

    @Override
    public void returnPeliculas(String resultado) {
        if (!resultado.contains("Exception")) {
            try {
                ArrayList<BeanMovies> beanMovies = new ArrayList<>();
                JSONObject mainObject = new JSONObject(resultado);
                JSONArray resArray = mainObject.getJSONArray("results"); //Getting the results object
                for (int i = 0; i < resArray.length(); i++) {
                    JSONObject jsonObject = resArray.getJSONObject(i);
                    BeanMovies movie = new BeanMovies();
                    movie.setId(jsonObject.getInt("id"));
                    movie.setTitle(jsonObject.getString("title"));
                    movie.setBackdrop_path(jsonObject.getString("backdrop_path"));
                    movie.setPoster_path(jsonObject.getString("poster_path"));
                    movie.setOriginal_language(jsonObject.getString("original_language"));
                    movie.setOverview(jsonObject.getString("overview"));
                    movie.setRelease_date(jsonObject.getString("release_date"));
                    beanMovies.add(movie);
                }

                vista.peliculas(beanMovies);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "parseo de json ");
                vista.mostrarLoader(false);
                //setear error de json
            }
        } else {
            Log.e(TAG, "returnPeliculas: " + resultado);
            vista.mostrarLoader(false);
            //setear error de comunicacion
        }
    }
}
