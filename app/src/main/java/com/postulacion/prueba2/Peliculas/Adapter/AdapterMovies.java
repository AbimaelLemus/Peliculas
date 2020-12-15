package com.postulacion.prueba2.Peliculas.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.postulacion.prueba2.Peliculas.Bean.BeanMovies;
import com.postulacion.prueba2.R;
import com.squareup.picasso.Picasso;

public class AdapterMovies extends RecyclerView.Adapter<AdapterMovies.Movies> {
    private BeanMovies[] beanMovies;
    private Context context;
    private String TAG = "AdapterMovies";

    public AdapterMovies(String jsonMovies) {
        jsonMovies = "[" + jsonMovies + "]";
        beanMovies = new Gson().fromJson(jsonMovies, BeanMovies[].class);
    }

    @NonNull
    @Override
    public Movies onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_movies, parent, false);
        return new Movies(view, beanMovies);
    }

    @Override
    public void onBindViewHolder(@NonNull Movies holder, int position) {
        holder.setIsRecyclable(false);
        holder.tvTitle.setText(beanMovies[position].getTitle());
        Picasso
                .with(context)
                .load("https://image.tmdb.org/t/p/w185" + beanMovies[position].getPoster_path())
                /*.placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)*/
                .into(holder.ivPortada);
    }

    @Override
    public int getItemCount() {
        return beanMovies.length;
    }

    public class Movies extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private ImageView ivPortada;

        public Movies(@NonNull View itemView, BeanMovies[] beanMovies) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvCardMoviesTitle);
            ivPortada = itemView.findViewById(R.id.ivMovies);
        }


    }
}
