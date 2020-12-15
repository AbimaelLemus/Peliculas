package com.postulacion.prueba2.Utilities;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Conexion {

    public String peliculas(String url) {
        try {

            URL new_url = new URL(url); //create a url from a String
            HttpURLConnection connection = (HttpURLConnection) new_url.openConnection(); //Opening a http connection  to the remote object
            connection.connect();

            InputStream inputStream = connection.getInputStream(); //reading from the object
            String results = IOUtils.toString(inputStream);  //IOUtils to convert inputstream objects into Strings type
            //parseJson(results,movies);
            inputStream.close();
            return results;
        } catch (Exception e) {
            return String.valueOf(e);
        }

    }

}
