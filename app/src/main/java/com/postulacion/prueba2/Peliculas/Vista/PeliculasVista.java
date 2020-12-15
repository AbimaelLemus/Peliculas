package com.postulacion.prueba2.Peliculas.Vista;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.postulacion.prueba2.Peliculas.Adapter.AdapterMovies;
import com.postulacion.prueba2.Peliculas.Bean.BeanMovies;
import com.postulacion.prueba2.Peliculas.Interfaces.InterfacesPeliculas;
import com.postulacion.prueba2.Peliculas.Presentador.PeliculasPresentador;
import com.postulacion.prueba2.R;
import com.postulacion.prueba2.Utilities.Loader;

import java.util.ArrayList;

public class PeliculasVista extends Fragment implements InterfacesPeliculas.Vista {

    private PeliculasPresentador presentador;
    private String TAG = "PeliculasVista";
    private RecyclerView rvMovies;
    private Loader loader;

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

        loader = new Loader(getContext());
        presentador = new PeliculasPresentador(this);
        presentador.obtenerPeliculas();

        return view;
    }

    @Override
    public void mostrarLoader(boolean mostrar) {
        if (mostrar){
            loader.show();
        }else {
            loader.dismiss();
        }
    }

    @Override
    public void peliculas(ArrayList<BeanMovies> movies) {
        rvMovies.setAdapter(new AdapterMovies(movies));
        rvMovies.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mostrarLoader(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loader!=null && loader.isShowing()){
            loader.dismiss();
        }
    }
}