package com.example.ac2;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btnCadastra;
    ListView listRemedios;
    ArrayAdapter<String> adapterListView;
    ArrayList<String> listaRemedios;
    ArrayList<Integer> listaId;
    BancoHelper bancoHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try{
            carregarRemedio();
            btnCadastra = findViewById(R.id.btnCadastrar);
            listRemedios = findViewById(R.id.listRemedios);
            bancoHelper = new BancoHelper(this);
            btnCadastra.setOnClickListener(V -> {
                Intent intent = new Intent(MainActivity.this, TelaCadastro_Edicao.class);
                startActivity(intent);
            });
            listRemedios.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(MainActivity.this, TelaCadastro_Edicao.class);
                intent.putExtra("id", listaId.get(position));
            });
            listRemedios.setOnItemLongClickListener((adapterView, view1, pos, I) ->
            {

                int idLivro = listaId.get(pos);
                int resultado = bancoHelper.excluirRemedio(idLivro);
                if (resultado > 0) {
                    Toast.makeText(this, "Remédio excluído!", Toast.LENGTH_SHORT).show();
                    carregarRemedio();
                }
                return true;
            });


        }
        catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void carregarRemedio(){
        Cursor cursor = bancoHelper.listarRemedios();
        listaRemedios = new ArrayList<>();
        listaId = new ArrayList<>();

        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String nome = cursor.getString(1);
                String horario = cursor.getString(2);
                boolean lido = cursor.getInt(3) == 1;
                listaRemedios.add(id + " - " + nome + " - " + horario + " - " + (lido ? "Lido" : "Não lido"));
                listaId.add(id);
            } while(cursor.moveToNext());
        }

        adapterListView = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaRemedios);
        listRemedios.setAdapter(adapterListView);
    }

    public static class BackgroundService extends Service {
        BancoHelper bancoHelper;
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private void showNotification(String nome) {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_MUTABLE);
            String nomeRemedio = intent.getStringExtra("nome");

            Notification notification = new NotificationCompat.Builder(this, "default")
                    .setContentTitle("Tomar remédio!")
                    .setContentText("Lembrete para tomar o remédio " + nomeRemedio + ".")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            bancoHelper = new BancoHelper(this);
            new Thread(() -> {
                while(true) {
                    try {
                        Cursor cursor = bancoHelper.listarRemedios();
                        if (cursor.moveToFirst()) {
                            do {
                                int id = cursor.getInt(0);
                                String nome = cursor.getString(1);
                                String horario = cursor.getString(2);
                                boolean tomado = cursor.getInt(3) == 1;

                                if (!tomado && verificaHorario(horario)) {
                                    showNotification(nome);
                                }
                            } while (cursor.moveToNext());
                        }

                    }
                    catch(Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();

            return START_STICKY;
        }
        private boolean verificaHorario(String horario){
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String agora = sdf.format(Calendar.getInstance().getTime());
                return agora.equals(horario);
            }
            catch(Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

    }
}