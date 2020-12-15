package com.postulacion.prueba2.Peliculas.Interfaces;

import com.postulacion.prueba2.Peliculas.Bean.BeanMovies;

import java.util.ArrayList;

public class InterfacesPeliculas {

    public interface Vista {
        void peliculas(ArrayList<BeanMovies> movies);
        void mostrarLoader(boolean mostrar);
    }

    public interface Presentador {
        void obtenerPeliculas();
        void returnPeliculas(String resultado);
    }

    public interface Modelo {
        void obtenerPeliculas();
    }

}
