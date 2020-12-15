package com.postulacion.prueba2.Peliculas.Presentador;

import android.util.Log;

import com.postulacion.prueba2.Peliculas.Interfaces.InterfacesPeliculas;
import com.postulacion.prueba2.Peliculas.Modelo.PeliculasModelo;

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
        modelo.obtenerPeliculas();
    }

    @Override
    public void returnPeliculas(String resultado) {
        if (!resultado.contains("Exception")) {
            vista.peliculas(resultado);
        } else {
            Log.e(TAG, "returnPeliculas: " + resultado);
            //setear error de comunicacion
        }
    }
}
