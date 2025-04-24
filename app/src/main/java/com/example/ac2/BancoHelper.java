package com.example.ac2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class BancoHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "meubanco.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Remédios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_REMEDIO = "Remedio";
    private static final String COLUMN_HORARIO = "Horário";
    private static final String COLUMN_TOMADO = "Tomado";

    public BancoHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_REMEDIO + " TEXT,"
                + COLUMN_HORARIO + " DATE,"
                + COLUMN_TOMADO + " BIT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long inserirRemedio(String nome, String horario, boolean tomado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMEDIO, nome);
        values.put(COLUMN_HORARIO, horario);
        values.put(COLUMN_TOMADO, tomado);
        return db.insert(TABLE_NAME, null, values);
    }

    public Cursor listarRemedios() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor listarRemediosPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int atualizarRemedio(int id, String novoNome, String novoHorario, boolean novoTomado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REMEDIO, novoNome);
        values.put(COLUMN_HORARIO, novoHorario);
        values.put(COLUMN_TOMADO, novoTomado);
        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)
        });
    }

    public int excluirRemedio(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

}
