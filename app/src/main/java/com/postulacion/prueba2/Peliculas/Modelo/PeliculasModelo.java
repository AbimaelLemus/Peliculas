package com.postulacion.prueba2.Peliculas.Modelo;

import android.os.AsyncTask;

import com.postulacion.prueba2.Peliculas.Interfaces.InterfacesPeliculas;
import com.postulacion.prueba2.Utilities.Conexion;

public class PeliculasModelo implements InterfacesPeliculas.Modelo {

    private InterfacesPeliculas.Presentador presentador;

    public PeliculasModelo(InterfacesPeliculas.Presentador presentador) {
        this.presentador = presentador;

    }

    @Override
    public void obtenerPeliculas() {

        new AsynMovies(new AsynMovies.Retorno() {
            @Override
            public void retornoAsincrono(String resultado) {
                presentador.returnPeliculas(resultado);
            }
        }).execute();

    }

    public static class AsynMovies extends AsyncTask<String, String, String> {

        private Retorno retorno;

        public AsynMovies(Retorno retorno) {
            this.retorno = retorno;

        }

        @Override
        protected String doInBackground(String... strings) {
            //http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=
            return new Conexion().peliculas("https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=a01845c41c53a55ae2454547f29544af");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            retorno.retornoAsincrono(s);
        }

        public interface Retorno {
            void retornoAsincrono(String resultado);
        }


    }

}
