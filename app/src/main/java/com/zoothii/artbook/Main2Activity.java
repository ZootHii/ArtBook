package com.zoothii.artbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Main2Activity extends AppCompatActivity {

    Bitmap selectedImage;
    ImageView selectedImageView;
    EditText nameText, authorText, typeText, notesText;
    Button saveButton;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        selectedImageView = findViewById(R.id.selectImageView);
        nameText = findViewById(R.id.nameText);
        authorText = findViewById(R.id.authorText);
        typeText = findViewById(R.id.typeText);
        notesText = findViewById(R.id.notesText);
        saveButton = findViewById(R.id.saveButton);

        database = this.openOrCreateDatabase("Books",MODE_PRIVATE,null);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.matches("new")){
            matchesNew();
        } else if(info.matches("edit")) {
            matchesEdit();
        } else if (info.matches("old")) {
            matchesOld();
        }
    }

    // save ederken daha küçültülmüş halini kaydettik
    private void Save(){

        String name = nameText.getText().toString();
        String author = authorText.getText().toString();
        String type = typeText.getText().toString();
        String notes = notesText.getText().toString();

        if (selectedImage == null){
            try {
                database = this.openOrCreateDatabase("Books", MODE_PRIVATE, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY, name VARCHAR, author VARCHAR, type VARCHAR, notes VARCHAR, image BLOB)");

                String sqlString = "INSERT INTO books (name, author, type, notes/*, image*/) VALUES (?, ?, ?, ?)";
                //üstteki string i sql de çalıştırmaya ve aşağıdakileri yapmaya yarar
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1,name);
                sqLiteStatement.bindString(2,author);
                sqLiteStatement.bindString(3,type);
                sqLiteStatement.bindString(4,notes);
                sqLiteStatement.execute();

            } catch (Exception e){
                e.printStackTrace();
            }
        } else {

            Bitmap smallImage = makeSmallerImage(selectedImage, 300); // seçili resmi küçültüp bitmap halinde aldık
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // gerekli alttakine yazmak için
            smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream); // aldığımız resmi PNG formatında küçültüp aldık
            byte[] bytteArray = outputStream.toByteArray(); // resmi byte a çeviricez sqlite a kaydedebilmek için

            try {
                database = this.openOrCreateDatabase("Books", MODE_PRIVATE, null);
                database.execSQL("CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY, name VARCHAR, author VARCHAR, type VARCHAR, notes VARCHAR, image BLOB)");

                String sqlString = "INSERT INTO books (name, author, type, notes, image) VALUES (?, ?, ?, ?, ?)";
                //üstteki string i sql de çalıştırmaya ve aşağıdakileri yapmaya yarar
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1,name);
                sqLiteStatement.bindString(2,author);
                sqLiteStatement.bindString(3,type);
                sqLiteStatement.bindString(4,notes);
                sqLiteStatement.bindBlob(5,bytteArray);
                sqLiteStatement.execute();


            } catch (Exception e){
                e.printStackTrace();
            }

        }

        // Arkadaki aktiviteleri kapat yenisini başlat bu şekilde ekledikten sonra list içinde görebiliyoruz.
        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //editleyip save ediyoruz
    private void editSave() {

        Intent intent = getIntent();
        int artId = intent.getIntExtra("artId", 1);

        //Bitmap currentBit = ((BitmapDrawable) selectedImageView.getDrawable()).getBitmap();

        String name = nameText.getText().toString();
        String author = authorText.getText().toString();
        String type = typeText.getText().toString();
        String notes = notesText.getText().toString();

        if (selectedImage == null){
            try {
                database = this.openOrCreateDatabase("Books", MODE_PRIVATE, null);

                String sqlString = "UPDATE books SET name=?, author=?, type=?, notes=? WHERE id=?";
                //üstteki string i sql de çalıştırmaya ve aşağıdakileri yapmaya yarar
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1,name);
                sqLiteStatement.bindString(2,author);
                sqLiteStatement.bindString(3,type);
                sqLiteStatement.bindString(4,notes);
                sqLiteStatement.bindString(5,String.valueOf(artId));
                sqLiteStatement.execute();

            } catch (Exception e){
                e.printStackTrace();
            }
        } else {

            Bitmap smallImage = makeSmallerImage(selectedImage, 300); // seçili resmi küçültüp bitmap halinde aldık
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // gerekli alttakine yazmak için
            smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream); // aldığımız resmi PNG formatında küçültüp aldık
            byte[] bytteArray = outputStream.toByteArray(); // resmi byte a çeviricez sqlite a kaydedebilmek için

            try {
                database = this.openOrCreateDatabase("Books", MODE_PRIVATE, null);
                String sqlString = "UPDATE books SET name=?, author=?, type=?, notes=?, image=? WHERE id=?";
                //üstteki string i sql de çalıştırmaya ve aşağıdakileri yapmaya yarar
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1,name);
                sqLiteStatement.bindString(2,author);
                sqLiteStatement.bindString(3,type);
                sqLiteStatement.bindString(4,notes);
                sqLiteStatement.bindBlob(5,bytteArray);
                sqLiteStatement.bindString(6,String.valueOf(artId));
                sqLiteStatement.execute();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
        // Arkadaki aktiviteleri kapat yenisini başlat bu şekilde ekledikten sonra list içinde görebiliyoruz.
        Intent backIntent = new Intent(Main2Activity.this, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backIntent);
    }

    //metodumuz çalıştırıldığında izin istiyoruz ve izin daha önce verilmişse direkt olarak galeriye giriyoruz

    public void selectImage(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);
        }
    }

    // eğer izin verilince galeriyi açacaksak bu metodu kullanıcaz (yani izin vere bastığımız anda galeriye giricez eğer bu metodu kullanmazsak
    // iki kere tıklamamız gerekicek biz bunu istemiyoruz)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // bu metod ile alınan veriyi bitmap e çevirip imageview a ekliyoruz
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri imageData = data.getData();
            try {
                // Uri yi bitmap e çevirmek için yapılan işlemler
                if (Build.VERSION.SDK_INT >= 28){ // versiyon değişikliği yüzünden bu kodu kullanıyoruz 28 ve sonrası için
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    selectedImageView.setImageBitmap(selectedImage);
                } else { // öncesi içinse bu kodu kullanıyoruz
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    selectedImageView.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // algoritma yazdık sqlite ta 1mb üstü dosyalar hata çıkardığı için küçülttük
    public Bitmap makeSmallerImage(Bitmap selectedImage, int maxSize){

        int width = selectedImage.getWidth();
        int height = selectedImage.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if(bitmapRatio > 1){
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(selectedImage, width, height, true);
    }


    public void matchesNew(){

        nameText.setText("");
        authorText.setText("");
        typeText.setText("");
        notesText.setText("");
        saveButton.setVisibility(View.VISIBLE);
        Bitmap imageButton = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.imagebutton);
        selectedImageView.setImageBitmap(imageButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });
    }

    public void matchesEdit(){
        Intent intent = getIntent();
        int artId = intent.getIntExtra("artId",1);

        /*Bitmap imageButton = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.imagebutton);
        selectedImageView.setImageBitmap(imageButton);*/

        saveButton.setText("EDIT and SAVE");
        saveButton.setVisibility(View.VISIBLE);

        try {
            Cursor cursor = database.rawQuery("SELECT * FROM books WHERE id = ?",new String[] {String.valueOf(artId)});
            int nameIx = cursor.getColumnIndex("name");
            int authorIx = cursor.getColumnIndex("author");
            int typeIx = cursor.getColumnIndex("type");
            int notesIx = cursor.getColumnIndex("notes");
            int imageIx = cursor.getColumnIndex("image");


            while (cursor.moveToNext()){
                nameText.setText(cursor.getString(nameIx));
                authorText.setText(cursor.getString(authorIx));
                typeText.setText(cursor.getString(typeIx));
                notesText.setText(cursor.getString(notesIx));

                byte[] byteArray = cursor.getBlob(imageIx);
                Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                selectedImageView.setImageBitmap(bm);
            }
            cursor.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSave();
            }
        });

    }

    public void matchesOld(){
        Intent intent = getIntent();
        int artId = intent.getIntExtra("artId",1);
        saveButton.setVisibility(View.INVISIBLE);
        nameText.setEnabled(false);
        authorText.setEnabled(false);
        typeText.setEnabled(false);
        notesText.setKeyListener(null);
        selectedImageView.setEnabled(false);

        try {

            Cursor cursor = database.rawQuery("SELECT * FROM books WHERE id = ?",new String[] {String.valueOf(artId)});
            int nameIx = cursor.getColumnIndex("name");
            int authorIx = cursor.getColumnIndex("author");
            int typeIx = cursor.getColumnIndex("type");
            int notesIx = cursor.getColumnIndex("notes");
            int imageIx = cursor.getColumnIndex("image");

            while (cursor.moveToNext()){
                nameText.setText(cursor.getString(nameIx));
                authorText.setText(cursor.getString(authorIx));
                typeText.setText(cursor.getString(typeIx));
                notesText.setText(cursor.getString(notesIx));

                byte[] bytes = cursor.getBlob(imageIx);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length); // byte ı bitmap yapıyoruz
                selectedImageView.setImageBitmap(bitmap);

            }
            cursor.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}


