package com.tranhaison.englishportugesedictionary.databases.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import com.tranhaison.englishportugesedictionary.R;
import com.tranhaison.englishportugesedictionary.databases.DatabaseHelper;

public class LoadDatabase extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private AlertDialog alertDialog;
    private DatabaseHelper databaseHelper;

    public LoadDatabase(Context context, DatabaseHelper databaseHelper) {
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogue_view = inflater.inflate(R.layout.alert_progress_dialogue, null);

        builder.setView(dialogue_view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        databaseHelper.createDatabase();
        databaseHelper.close();

        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        alertDialog.dismiss();
        databaseHelper.openDatabase();
    }
}
