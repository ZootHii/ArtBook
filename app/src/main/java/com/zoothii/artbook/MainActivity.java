package com.zoothii.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> nameArray;
    ArrayList<Integer> idArray;
    ArrayAdapter arrayAdapter;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        registerForContextMenu(listView); // BASILI TUTMA MENUSU İÇİN GEREKLİ
        nameArray = new ArrayList<>();
        idArray = new ArrayList<>();

        database = this.openOrCreateDatabase("Books",MODE_PRIVATE,null);

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nameArray);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("artId",idArray.get(position));
                intent.putExtra("info","old");

                startActivity(intent);
            }
        });

        getData();

    }

    public void getData(){

        try {
            Cursor cursor = database.rawQuery("SELECT * FROM books",null);
            int nameIx = cursor.getColumnIndex("name");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                nameArray.add(cursor.getString(nameIx));
                idArray.add(cursor.getInt(idIx));
            }

            arrayAdapter.notifyDataSetChanged(); // veri ekledim listview da göster

            cursor.close();

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    // LIST VIEW DA BASILI TUTUNCA ÇIKAN MENU
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.listView){
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.long_click, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit_item:

                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("artId",idArray.get(info.position));
                intent.putExtra("info","edit");

                startActivity(intent);
                return true;
            case R.id.delete_item:

                try {
                    String sqlString = "DELETE FROM books WHERE id=?";
                    SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                    sqLiteStatement.bindString(1,idArray.get(info.position).toString());
                    sqLiteStatement.execute();

                    System.out.println("Name: "+idArray.get(info.position));

                    nameArray.remove(info.position);
                    idArray.remove(info.position);

                    arrayAdapter.notifyDataSetChanged(); // veri ekledim listview da göster

                } catch (Exception e){
                    e.printStackTrace();
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflater // OLUŞTURDUĞUMUZ MENÜYÜ AKTİVİTEYE BAĞLAR
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_art, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_art_item){ // tıklanan item bizim oluşturduğumuz iteme eşit ise
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }
}
