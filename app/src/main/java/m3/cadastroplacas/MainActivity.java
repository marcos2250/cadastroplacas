package m3.cadastroplacas;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText placa, serial, obs, ano, versao;
    private Spinner cor, uf;
    private RadioGroup semestre;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* SQLiteDatabase é usado para abrir ou criar o banco através do método
        openOrCreateDatabase(banco, modo de abertura, padrão)
        */
        db = openOrCreateDatabase("dbplacas.db", MODE_PRIVATE, null);
        db.execSQL("create table if not exists tbplacas (" +
                "id integer primary key autoincrement, " +
                "placa char(7)," +
                "serial char(6)," +
                "ano char(4)," +
                "semestre char(1)," +
                "versao char(4)," +
                "cor varchar(16)," +
                "uf char(2)," +
                "obs varchar(255)" +
                ")");

        /* Associar a variavel java com o recurso do id xml*/
        placa = (EditText) findViewById(R.id.placa);
        serial = (EditText) findViewById(R.id.serial);
        ano = (EditText) findViewById(R.id.ano);
        obs = (EditText) findViewById(R.id.obs);
        versao = (EditText) findViewById(R.id.versao);

        semestre = (RadioGroup) findViewById(R.id.semestre);
        cor = (Spinner) findViewById(R.id.cor);
        uf = (Spinner) findViewById(R.id.uf);
    }

    public void salvar(View view) {
        String id = null;
        Cursor cursor = db.rawQuery("select id from tbplacas where placa='" + placa.getText().toString() + "'", null); // comando select com cursor
        cursor.moveToFirst(); // posiciona o curso no primeiro registro
        if (!cursor.isAfterLast()) {
            id = cursor.getString(0);
        }
        cursor.close();

        ContentValues values = new ContentValues(); // pegar as coluna com os valores digitados
        if (id != null) {
            values.put("id", id);
        }
        values.put("placa", placa.getText().toString());
        values.put("serial", serial.getText().toString());
        values.put("ano", ano.getText().toString());
        values.put("semestre", semestre.getCheckedRadioButtonId());
        values.put("versao", versao.getText().toString());
        values.put("cor", cor.getSelectedItem().toString());
        values.put("uf", uf.getSelectedItem().toString());
        values.put("obs", obs.getText().toString());

        if (id == null) {
            db.insert("tbplacas", null, values); //salvar
        } else {
            db.update("tbplacas", values, "id="+id, null); //atualizar
        }

        Toast.makeText(MainActivity.this, "Gravado com sucesso", Toast.LENGTH_SHORT).show();
    }

    public void carregar(View view) {
        String id = null;
        Cursor cursor = db.rawQuery("select * from tbplacas where placa='" + placa.getText().toString() + "'", null); // comando select com cursor
        cursor.moveToFirst(); // posiciona o curso no primeiro registro
        if (!cursor.isAfterLast()) {
            placa.setText(cursor.getString(1));
            serial.setText(cursor.getString(2));
            ano.setText(cursor.getString(3));
            int semestreReg = cursor.getInt(4);
            if (semestreReg >= 0) {
                ((RadioButton) semestre.getChildAt(semestreReg)).setChecked(true);
            }
            versao.setText(cursor.getString(5));
            cor.setSelection(getSpinnerIndex(uf, cursor.getString(6)));
            uf.setSelection(getSpinnerIndex(uf, cursor.getString(7)));
            obs.setText(cursor.getString(8));
        }

        cursor.close();
    }

    public void importarExportar(View view) {
        Intent intent = new Intent(this, ImportExportActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void listar(View view) {
        Intent intent = new Intent(this, ListaActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private int getSpinnerIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }
}
