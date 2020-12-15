package com.postulacion.prueba2.Peliculas.Modelo;

import com.postulacion.prueba2.Peliculas.Interfaces.InterfacesPeliculas;

public class PeliculasModelo implements InterfacesPeliculas.Modelo {

    private InterfacesPeliculas.Presentador presentador;

    public PeliculasModelo(InterfacesPeliculas.Presentador presentador) {
        this.presentador = presentador;

    }

}
