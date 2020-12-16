package com.postulacion.prueba2.SubirImagenes.Vista;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.postulacion.prueba2.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class Subir_Imagen extends Fragment implements View.OnClickListener {
    private Activity activity;
    private ImageView ivImagen;
    private String TAG = "Subir_Imagen";
    private static String app_Directory = "DCIM/Camera/";
    private String mPath = "";
    private ProgressBar progressBar;

    public Subir_Imagen(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subir__imagen, container, false);

        ivImagen = view.findViewById(R.id.ivSubir);

        view.findViewById(R.id.btnSubirCamara).setOnClickListener(this);
        view.findViewById(R.id.btnSubirGaleria).setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + " - " + resultCode + " - " + data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {


                MediaScannerConnection.scanFile(getContext(), new String[]{mPath}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                    }

                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                    }
                });
                //Log.w(TAG, "######################" + mPath + "");

                ///storage/emulated/0/DCIM/Camera//Img_1608132208.jpg
                File imagen = new File(mPath);
                Uri imageUri = Uri.fromFile(imagen);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageInByte = baos.toByteArray();
                    String encoded = Base64.encodeToString(imageInByte, Base64.DEFAULT);


                    byte[] contenido = Base64.decode(encoded, Base64.DEFAULT);
                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(contenido, 0, contenido.length);
                    ivImagen.setImageBitmap(bitmap2);
                    subirImagen(mPath);
                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult: " + e.getCause());
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                //Log.w(TAG, "######################" + picturePath + "");
                ///storage/emulated/0/bluetooth/IMG_20201213_194457.jpg
                subirImagen(picturePath);
                ivImagen.setImageBitmap(thumbnail);
            }
        }

    }

    public void subirImagen(String path) {
        try {
            ivImagen.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            Log.e(TAG, "subirImagen: " + path );
            InputStream stream = new FileInputStream(new File(path));
            String[] splitNombre = path.split("/");
            String nombre = splitNombre[splitNombre.length-1];

            Log.e("***" + TAG, "subirImagen: " + splitNombre.length + " - " + nombre );

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();

            StorageReference mountainsRef = storageRef.child("image_" + nombre );

            UploadTask uploadTask = mountainsRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "onFailure: " + exception );
                    ivImagen.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e(TAG, "onSuccess: " + taskSnapshot.getBytesTransferred() + " - " + taskSnapshot.getTotalByteCount() );
                    Toast.makeText(getContext(), "Carga de imagen, exitosa!!!", Toast.LENGTH_SHORT).show();
                    ivImagen.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });




        } catch (Exception e) {
            Log.e(TAG, "subirImagen: " + e.getCause());
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.btnSubirCamara:File file = new File(Environment.getExternalStorageDirectory(), app_Directory);
             boolean isDirectoryCreated = file.exists();

             if (!isDirectoryCreated) {
                 isDirectoryCreated = file.mkdirs();
             }

             String name = "";
             if (isDirectoryCreated) {
                 name = "Img_" + (System.currentTimeMillis() / 1000) + ".jpg";
             }

             mPath = Environment.getExternalStorageDirectory() + File.separator + app_Directory + File.separator + name;

             File imagen = new File(mPath);

             StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
             StrictMode.setVmPolicy(builder.build());

             Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
             intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
             startActivityForResult(intent, 1);
             break;
         case R.id.btnSubirGaleria:
             Intent intent2 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
             startActivityForResult(intent2, 2);
             break;
     }
    }
}