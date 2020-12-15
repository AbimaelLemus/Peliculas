package com.postulacion.prueba2.Peliculas.Interfaces;

public class InterfacesPeliculas {

    public interface Vista {
        void peliculas(String jsonMovies);
    }

    public interface Presentador {
        void obtenerPeliculas();

        void returnPeliculas(String resultado);
    }

    public interface Modelo {
        void obtenerPeliculas();
    }

}
