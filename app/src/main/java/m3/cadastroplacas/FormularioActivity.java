package m3.cadastroplacas;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class FormularioActivity extends AppCompatActivity {

    private EditText placa, serial, obs, ano, versao;
    private Spinner uf;
    private AutoCompleteTextView cor;
    private RadioGroup semestre;
    private SQLiteDatabase db;
    private String idcadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

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
        cor = (AutoCompleteTextView) findViewById(R.id.cor);
        uf = (Spinner) findViewById(R.id.uf);

        Intent intent = getIntent();
        idcadastro = intent.getStringExtra("idcadastro");

        if (idcadastro != null && !idcadastro.isEmpty()) {
            carregarDados("select * from tbplacas where id='" + idcadastro + "'");
        }
    }

    public void carregar(View view) {
        carregarDados("select * from tbplacas where placa='" + placa.getText().toString().toUpperCase() + "'");
    }

    public void salvar(View view) {
        ContentValues values = new ContentValues(); // pegar as coluna com os valores digitados
        if (idcadastro != null && !idcadastro.isEmpty()) {
            values.put("id", idcadastro);
        }
        values.put("placa", placa.getText().toString().toUpperCase().replaceAll("[^A-Z\\d]", ""));
        values.put("serial", serial.getText().toString());
        values.put("ano", ano.getText().toString());
        values.put("semestre", semestre.getCheckedRadioButtonId());
        values.put("versao", versao.getText().toString());
        values.put("cor", cor.getText().toString());
        values.put("uf", uf.getSelectedItem().toString());
        values.put("obs", obs.getText().toString());

        if (idcadastro != null && !idcadastro.isEmpty()) {
            db.insert("tbplacas", null, values); //salvar
        } else {
            db.update("tbplacas", values, "id=" + idcadastro, null); //atualizar
        }

        Toast.makeText(FormularioActivity.this.getApplicationContext(), "Gravado com sucesso", Toast.LENGTH_SHORT).show();
    }

    private void carregarDados(String sql) {
        Cursor cursor = db.rawQuery(sql, null); // comando select com cursor
        cursor.moveToFirst(); // posiciona o curso no primeiro registro
        if (!cursor.isAfterLast()) {
            idcadastro = cursor.getString(0);
            placa.setText(cursor.getString(1));
            serial.setText(cursor.getString(2));
            ano.setText(cursor.getString(3));
            int semestreReg = cursor.getInt(4);
            if (semestreReg >= 0) {
                ((RadioButton) semestre.getChildAt(semestreReg)).setChecked(true);
            }
            versao.setText(cursor.getString(5));
            cor.setText(cursor.getString(6));
            uf.setSelection(getSpinnerIndex(uf, cursor.getString(7)));
            obs.setText(cursor.getString(8));

            notificar("Dados carregados");
        } else {
            notificar("Veiculo nao cadastrado");
        }

        cursor.close();

    }

    private int getSpinnerIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    private void notificar(String msg) {
        Toast.makeText(FormularioActivity.this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
