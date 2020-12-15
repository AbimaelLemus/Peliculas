package com.postulacion.prueba2.Peliculas.Presentador;

import com.postulacion.prueba2.Peliculas.Interfaces.InterfacesPeliculas;
import com.postulacion.prueba2.Peliculas.Modelo.PeliculasModelo;
import com.postulacion.prueba2.Peliculas.Vista.PeliculasVista;

public class PeliculasPresentador implements InterfacesPeliculas.Presentador {

    private InterfacesPeliculas.Modelo modelo;
    private InterfacesPeliculas.Vista vista;

    public PeliculasPresentador(InterfacesPeliculas.Vista vista) {
        this.vista = vista;
        modelo = new PeliculasModelo(this);
    }
}
