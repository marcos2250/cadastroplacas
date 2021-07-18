package m3.cadastroplacas;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

    private static final String[] CORES = new String [] {
        "Branco", "Cinza", "Prata", "Preto",
        "Vermelho", "Azul", "Verde", "Bege",
        "Amarelo", "Laranja", "Rosa", "Roxo", "Marrom"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        db = openOrCreateDatabase("dbplacas.db", MODE_PRIVATE, null);

        /* Associar a variavel java com o recurso do id xml*/
        placa = (EditText) findViewById(R.id.placa);
        serial = (EditText) findViewById(R.id.serial);
        ano = (EditText) findViewById(R.id.ano);
        obs = (EditText) findViewById(R.id.obs);
        versao = (EditText) findViewById(R.id.versao);

        semestre = (RadioGroup) findViewById(R.id.semestre);

        cor = (AutoCompleteTextView) findViewById(R.id.cor);
        cor.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CORES));

        uf = (Spinner) findViewById(R.id.uf);

        Intent intent = getIntent();
        String idcadastro = intent.getStringExtra("idcadastro");

        if (idcadastro != null && !idcadastro.isEmpty()) {
            carregarDados("select * from tbplacas where id='" + idcadastro + "'");
        }
    }

    public void carregar(View view) {
        String query = queryPlacaMercosul();
        carregarDados(query);
    }

    public void salvar(View view) {
        String idcadastro = obterIdCadastroAtual();

        ContentValues values = new ContentValues();
        if (idcadastro != null && !idcadastro.isEmpty()) {
            values.put("id", idcadastro);
        }
        values.put("placa", placa.getText().toString().toUpperCase().replaceAll("[^A-Z\\d]", ""));
        values.put("serial", serial.getText().toString());
        values.put("ano", ano.getText().toString());
        values.put("semestre", semestre.indexOfChild(semestre.findViewById(semestre.getCheckedRadioButtonId())));
        values.put("versao", versao.getText().toString());
        values.put("cor", cor.getText().toString());
        values.put("uf", uf.getSelectedItem().toString());
        values.put("obs", obs.getText().toString());

        long result;
        if (idcadastro == null || idcadastro.isEmpty()) {
            result = db.insert("tbplacas", null, values); //salvar
        } else {
            result = db.update("tbplacas", values, "id=" + idcadastro, null); //atualizar
        }

        if (result >= 0) {
            notificar("Gravado com sucesso");
        }
    }

    public void excluir(View view) {
        String idcadastro = obterIdCadastroAtual();
        int result = -1;
        if (idcadastro != null && !idcadastro.isEmpty()) {
            result = db.delete("tbplacas", "id=" + idcadastro, null); //deletar
        }
        if (result >= 0) {
            notificar("Item excluido");
        }
    }

    private String queryPlacaMercosul() {
        String vlEntrada = placa.getText().toString().toUpperCase().replaceAll("[^A-Z\\d]", "");
        if (vlEntrada.length() < 7) {
            notificar("Placa deve conter 7 posicoes!");
            return null;
        }

        String placaAntiga;
        String placaMercosul;
        char alpha = vlEntrada.charAt(4);
        if (alpha >= 48 && alpha <= 57) {
            placaAntiga = vlEntrada;
            placaMercosul = vlEntrada.substring(0,4) + String.valueOf((char) (alpha + 17)) + vlEntrada.substring(5,7);
        } else {
            placaAntiga = vlEntrada.substring(0,4) + String.valueOf((char) (alpha - 17)) + vlEntrada.substring(5,7);
            placaMercosul = vlEntrada;
        }

        return "select * from tbplacas where placa='" + placaAntiga + "' or placa='" + placaMercosul + "'";
    }

    private String obterIdCadastroAtual() {
        String idcadastro = null;
        String query = queryPlacaMercosul();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            idcadastro = cursor.getString(0);
        }
        cursor.close();
        return idcadastro;
    }

    private void carregarDados(String sql) {
        Cursor cursor = db.rawQuery(sql, null); // comando select com cursor
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
            cor.setText(cursor.getString(6));
            uf.setSelection(getSpinnerIndex(uf, cursor.getString(7)));
            obs.setText(cursor.getString(8));

            int count = cursor.getCount();
            if (count > 1) {
                notificar("Ha " + count + " registros de veiculos!");
            } else {
                notificar("Dados carregados");
            }
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
