package com.example.ac2;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class TelaCadastro_Edicao extends AppCompatActivity {
    Button btnSalvar;
    EditText txtNomeRemedio;
    EditText txtHorario;
    CheckBox cbxTomado;
    BancoHelper bancoHelper;
    ArrayList<String> listaRemedios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int id = getIntent().getIntExtra("id", 0);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cadastro_edicao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtNomeRemedio = findViewById(R.id.txtNomeRemedio);
        txtHorario = findViewById(R.id.txtHorario);
        cbxTomado = findViewById(R.id.cbxTomado);

        btnSalvar.setOnClickListener(V -> {
            String nome = txtNomeRemedio.getText().toString();
            String horario = txtHorario.getText().toString();
            boolean tomado = cbxTomado.isChecked();

            if(!nome.isEmpty() && !horario.isEmpty()){
                long resultado = bancoHelper.inserirRemedio(nome, horario, tomado);
                if(resultado != 1){
                    Toast.makeText(this, "Livro Salvo!", Toast.LENGTH_SHORT).show();
                    txtNomeRemedio.setText("");
                    txtHorario.setText("");
                    cbxTomado.setChecked(false);
                    btnSalvar.setText(R.string.salvar);
                } else {
                    Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                }
            }
            if (id != 0) {
                btnSalvar.setText(R.string.salvar);
                Cursor cursor = bancoHelper.listarRemediosPorId(id);
                txtNomeRemedio.setText(cursor.getString(1));
                txtHorario.setText(cursor.getString(2));
                cbxTomado.setChecked(cursor.getInt(3) == 1);
                bancoHelper.atualizarRemedio(id, txtNomeRemedio.getText().toString(), txtHorario.getText().toString(), cbxTomado.isChecked());
            }
        });
    }
}