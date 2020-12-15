package com.postulacion.prueba2.Peliculas.Vista;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.postulacion.prueba2.Peliculas.Interfaces.InterfacesPeliculas;
import com.postulacion.prueba2.Peliculas.Presentador.PeliculasPresentador;
import com.postulacion.prueba2.R;
import com.postulacion.prueba2.Utilities.Conexion;

public class PeliculasVista extends Fragment implements InterfacesPeliculas.Vista {

    private PeliculasPresentador presentador;
    private String TAG ="PeliculasVista";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        presentador = new PeliculasPresentador(this);
        //implementacion
        //https://api.themoviedb.org/3/movie/550?api_key=a01845c41c53a55ae2454547f29544a

        Log.e(TAG, "onCreateView: " + new Conexion().peliculas(getString(R.string.pathMovies)));

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_peliculas, container, false);
    }
}