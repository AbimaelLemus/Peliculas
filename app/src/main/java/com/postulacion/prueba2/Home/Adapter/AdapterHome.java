package com.postulacion.prueba2.Home.Adapter;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.postulacion.prueba2.Peliculas.Vista.PeliculasVista;
import com.postulacion.prueba2.SubirImagenes.Vista.Subir_Imagen;
import com.postulacion.prueba2.Ubicaciones.Vista.Ubicaciones;

public class AdapterHome extends FragmentStatePagerAdapter {
    private Context context;
    private Activity activity;

    public AdapterHome(FragmentManager fm, Context context, Activity activity) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PeliculasVista();
            case 1:
                return new Ubicaciones(context);
            case 2:
                return new Subir_Imagen(activity);
            default:
                return new PeliculasVista();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            /*case 0:
                return "Peliculas";*/
            case 1:
                return "Ubicaciones";
            case 2:
                return "Subir Imágenes";
            default:
                return "Peliculas";
        }
        //return null;
    }

}

