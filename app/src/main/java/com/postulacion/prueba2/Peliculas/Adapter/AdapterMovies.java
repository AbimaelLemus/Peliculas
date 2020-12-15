package com.postulacion.prueba2.Peliculas.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.postulacion.prueba2.Peliculas.Bean.BeanMovies;
import com.postulacion.prueba2.R;
import com.postulacion.prueba2.Utilities.InfoMovies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMovies extends RecyclerView.Adapter<AdapterMovies.Movies> {
    private ArrayList<BeanMovies> movies;
    private Context context;
    private String TAG = "AdapterMovies";

    public AdapterMovies(ArrayList<BeanMovies> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public Movies onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_movies, parent, false);
        return new Movies(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Movies holder, final int position) {
        holder.setIsRecyclable(false);
        Picasso
                .with(context)
                .load("https://image.tmdb.org/t/p/w185" + movies.get(position).getPoster_path())
                .placeholder(R.drawable.img_place_holder)
                .error(R.drawable.img_error)
                .into(holder.ivPortada);


        holder.llContenedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InfoMovies(context, movies, position).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class Movies extends RecyclerView.ViewHolder {
        private LinearLayout llContenedor;
        private ImageView ivPortada;

        public Movies(@NonNull View itemView) {
            super(itemView);
            llContenedor = itemView.findViewById(R.id.llMoviesContenedor);
            ivPortada = itemView.findViewById(R.id.ivMovies);
        }
    }
}
