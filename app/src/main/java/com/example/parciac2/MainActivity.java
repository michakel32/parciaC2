package com.example.parciac2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 100;
    private static final int REQUEST_CODIGO_CAPTURAR_IMAGEN = 300;
    private static final int REQUEST_CODIGO_CAMARA = 200;

    ImageView Ewhast, Ecorreo, Galery, Mostrar;
    Button btn_tomarF;
    String rutaabsoluta;
    //Elias es GEY
    private Uri mSelectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Ecorreo = findViewById(R.id.Ecorreo_btn);
        Ewhast = findViewById(R.id.Ewhast_btn);
        Galery = findViewById(R.id.btn_galeria);

        Mostrar = findViewById(R.id.foto);

        btn_tomarF = findViewById(R.id.Tfoto);


        //toma la foto
        btn_tomarF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procesofoto();
            }
        });

        //envia por whathsapp
        Ewhast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedImageUri == null) {
                    Toast.makeText(MainActivity.this, "Seleccione una imagen primero", Toast.LENGTH_SHORT).show();
                } else {
                    sendImageViaWhatsApp(mSelectedImageUri);
                }
            }
        });

        Ecorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedImageUri == null) {
                    Toast.makeText(MainActivity.this, "Seleccione una imagen primero", Toast.LENGTH_SHORT).show();
                } else {
                    sendImageViaEmail(mSelectedImageUri);
                }
            }
        });
        //configuracion de la camara


        Galery.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        }
    });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            mSelectedImageUri = data.getData();
            Mostrar.setImageURI(mSelectedImageUri);
            Ewhast.setEnabled(true);
            Ecorreo.setEnabled(true);
        }
        //configuracion de la camara
        if (requestCode==REQUEST_CODIGO_CAPTURAR_IMAGEN){
            if (resultCode == Activity.RESULT_OK){
                Mostrar.setImageURI(Uri.parse(rutaabsoluta));

            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void sendImageViaWhatsApp(Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(intent, "Compartir imagen"));
    }

    private void sendImageViaEmail(Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Compartir imagen"));
    }


    //configuracion de la camara
    public void Tomafoto(){

        Intent intentcamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intentcamera.resolveActivity(getPackageManager())!=null){
            File archivofoto = null;
            archivofoto = generofoto();

            if (archivofoto!=null){
                Uri rutafoto = FileProvider.getUriForFile(MainActivity.this,"com.example.parciac2",archivofoto);
                intentcamera.putExtra(MediaStore.EXTRA_OUTPUT, rutafoto);
                startActivityForResult(intentcamera, REQUEST_CODIGO_CAPTURAR_IMAGEN);
            }
        }
    }

    private File generofoto() {

        String prefijoArchivo = "ER_";
        File directorioImagen = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = null;

        try {
            imagen = File.createTempFile(prefijoArchivo,".jpg",directorioImagen);
            rutaabsoluta = imagen.getAbsolutePath();
        }catch (Exception error){
            Log.e("ErrorGenerarFoto", error.getMessage().toString());
        }
        return imagen;

    }

    private void procesofoto() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            Tomafoto();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            },REQUEST_CODIGO_CAMARA);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODIGO_CAMARA){
            if (permissions.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Tomafoto();
            }else {
                Toast.makeText(MainActivity.this, "Error no hay permisos", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



}
