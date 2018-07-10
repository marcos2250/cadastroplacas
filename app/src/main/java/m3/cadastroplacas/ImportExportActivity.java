package m3.cadastroplacas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ImportExportActivity extends AppCompatActivity {

    private EditText text;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);
        db = openOrCreateDatabase("dbplacas.db", MODE_PRIVATE, null);
        text = (EditText) findViewById(R.id.text);
    }

    public void exportar(View view) {
        StringBuilder sb = new StringBuilder();
        Cursor cursor = db.rawQuery("select * from tbplacas order by placa", null); // comando select com cursor
        cursor.moveToFirst(); // posiciona o curso no primeiro registro
        while (!cursor.isAfterLast()) {
            sb.append(cursor.getString(1) + ";"
                    + cursor.getString(2) + ";"
                    + cursor.getString(3) + ";"
                    + cursor.getString(4) + ";"
                    + cursor.getString(5) + ";"
                    + cursor.getString(6) + ";"
                    + cursor.getString(7) + ";"
                    + cursor.getString(8) + "\r\n");
            cursor.moveToNext();
        }
        cursor.close();
        text.setText(sb.toString());
    }

    public void importar(View view) {
        int sucesso = 0;
        int erro = 0;
        String tab = text.getText().toString();
        String[] lines = tab.split("\\r?\\n");
        for (String line : lines) {
            String[] tokens = (line + " ").split(";");

            if (tokens.length < 8) {
                erro++;
                continue;
            }

            ContentValues values = new ContentValues(); // pegar as coluna com os valores digitados
            values.put("placa", tokens[0].toUpperCase().replaceAll("[^A-Z\\d]", ""));
            values.put("serial", tokens[1]);
            values.put("ano", tokens[2]);
            values.put("semestre", tokens[3]);
            values.put("versao", tokens[4]);
            values.put("cor", tokens[5]);
            values.put("uf", tokens[6]);
            values.put("obs", tokens[7]);

            if (db.insert("tbplacas", null, values) > 0) {
                sucesso++;
            } else {
                erro++;
            }
        }

        Toast.makeText(ImportExportActivity.this.getApplicationContext(),
                "Sucesso: " + sucesso + ", Erros: " + erro, Toast.LENGTH_LONG).show();
    }

}
