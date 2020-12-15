package com.postulacion.prueba2.Utilities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.postulacion.prueba2.Peliculas.Bean.BeanMovies;
import com.postulacion.prueba2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InfoMovies extends Dialog {
    private ArrayList<BeanMovies> movies;
    private int position;
    private String TAG = "InfoMovies";

    public InfoMovies(Context context, ArrayList<BeanMovies> movies, int position) {
        super(context, R.style.Theme_AppCompat_Dialog);
        this.movies = movies;
        this.position = position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_movies);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);

        TextView tvNombreMovie = findViewById(R.id.tvInfoNombreMovie);
        TextView tvLenguaje = findViewById(R.id.tvInfoLenguajeOriginal);
        TextView tvLanzamiento = findViewById(R.id.tvInfoLanzamiento);
        TextView tvDescripcion = findViewById(R.id.tvInfoDescripcion);

        Log.e(TAG, "onCreate: " + movies.get(position).getTitle());

        tvNombreMovie.setText("Titulo:\n" + movies.get(position).getTitle());
        tvLenguaje.setText("Lenguaje: " + movies.get(position).getOriginal_language());
        tvLanzamiento.setText("Fecha lanzamiento:\n" + movies.get(position).getRelease_date());
        tvDescripcion.setText("Descripción:\n" + movies.get(position).getOverview());

        ImageView ivMovie = findViewById(R.id.ivInfoMovie);

        Picasso
                .with(getContext())
                .load("https://image.tmdb.org/t/p/w185" + movies.get(position).getPoster_path())
                .placeholder(R.drawable.img_place_holder)
                .error(R.drawable.img_error)
                .into(ivMovie);

        findViewById(R.id.btnInfoAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

}
