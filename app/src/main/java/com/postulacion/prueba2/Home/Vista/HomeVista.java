package com.postulacion.prueba2.Home.Vista;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.postulacion.prueba2.Home.Adapter.AdapterHome;
import com.postulacion.prueba2.R;

public class HomeVista extends AppCompatActivity {

    private ViewPager vpHome;
    private TabLayout tlHome;
    private String TAG = "HomeVista ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_vista);

        vpHome = findViewById(R.id.vpHome);
        tlHome = findViewById(R.id.tlHome);

        AdapterHome adapter = new AdapterHome(getSupportFragmentManager(), HomeVista.this, HomeVista.this);

        vpHome.setAdapter(adapter);
        tlHome.setupWithViewPager(vpHome);

        //agregar iconos a los tab y agregar titulo de la pantalla respecto al tab

    }
}