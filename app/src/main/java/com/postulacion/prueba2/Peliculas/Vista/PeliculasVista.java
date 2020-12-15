package com.postulacion.prueba2.Peliculas.Vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.postulacion.prueba2.Peliculas.Adapter.AdapterMovies;
import com.postulacion.prueba2.Peliculas.Interfaces.InterfacesPeliculas;
import com.postulacion.prueba2.Peliculas.Presentador.PeliculasPresentador;
import com.postulacion.prueba2.R;

public class PeliculasVista extends Fragment implements InterfacesPeliculas.Vista {

    private PeliculasPresentador presentador;
    private String TAG = "PeliculasVista";
    private RecyclerView rvMovies;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_peliculas, container, false);

        rvMovies = view.findViewById(R.id.rvMovies);

        presentador = new PeliculasPresentador(this);
        presentador.obtenerPeliculas();
        return view;
    }

    @Override
    public void peliculas(String jsonMovies) {
        rvMovies.setAdapter(new AdapterMovies(jsonMovies));
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}