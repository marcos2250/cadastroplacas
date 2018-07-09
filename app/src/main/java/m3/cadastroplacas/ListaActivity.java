package m3.cadastroplacas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        atualizaLista("select * from tbplacas order by placa");
    }

    public void novo(View view) {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra("idcadastro", "");
        startActivity(intent);
    }

    public void serial(View view) {
        atualizaLista("select * from tbplacas order by ano, serial, placa");
    }

    public void placa(View view) {
        atualizaLista("select * from tbplacas order by placa");
    }

    public void importarExportar(View view) {
        Intent intent = new Intent(this, ImportExportActivity.class);
        startActivity(intent);
    }

    private void atualizaLista(String sql) {
        /* Mostra lista de itens que foram cadastrados anteriormente */
        listView = (ListView) findViewById(R.id.lista);
        ArrayAdapter<String> adapter = new
                ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                listar(sql));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                Intent intent = new Intent(ListaActivity.this, FormularioActivity.class);
                String idcadastro = itemValue.split(":")[0];
                intent.putExtra("idcadastro", idcadastro);
                startActivity(intent);

            }
        });

        listView.setAdapter(adapter);
    }

    private List<String> listar(String sql) {
        SQLiteDatabase db = openOrCreateDatabase("dbplacas.db", MODE_PRIVATE, null);
        List<String> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null); // comando select com cursor
        cursor.moveToFirst(); // posiciona o curso no primeiro registro
        while (!cursor.isAfterLast()) {
            lista.add(cursor.getString(0) + ": "
                    + cursor.getString(1) + " "
                    + cursor.getString(2) + " "
                    + cursor.getString(3) + "/"
                    + cursor.getString(4) + " "
                    + cursor.getString(6) + " "
                    + cursor.getString(7) + " "
                    + cursor.getString(8));
            cursor.moveToNext();
        }
        cursor.close();
        return lista;
    }

}
