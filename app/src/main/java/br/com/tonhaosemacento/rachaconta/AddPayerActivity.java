package br.com.tonhaosemacento.rachaconta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import br.com.tonhaosemacento.rachaconta.models.Contacts;
import br.com.tonhaosemacento.rachaconta.models.Expense;

public class AddPayerActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener{

        Context mContext = this;
        FloatingActionButton btnAddPayer;
        android.support.design.widget.TextInputEditText NamePayer;
        android.support.design.widget.TextInputEditText EmailPayer;
        Contacts contact;
        Expense expense;
        String jsonExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payer);

        findViews();
        setActions();
    }

    /**
     * used to bind the wiews with variables
     * **/
    private void findViews(){
        btnAddPayer  = (FloatingActionButton) findViewById(R.id.btn_add_payer);
        NamePayer    = (android.support.design.widget.TextInputEditText) findViewById(R.id.tie_name_payer);
        EmailPayer   = (android.support.design.widget.TextInputEditText) findViewById(R.id.tie_email_payer);

    }

    /**
     * set actions with the binded objects
     * **/
    private void setActions(){

        btnAddPayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (( NamePayer.getText().toString().isEmpty()) || (EmailPayer.getText().toString().isEmpty()))    {
                    Toast.makeText(AddPayerActivity.this, getString(R.string.missing_name_or_email), Toast.LENGTH_SHORT).show();
                } else {
                    int resultCode = RESULT_OK;
                    Intent resultIntent = new Intent();
                    expense.addContact(new Contacts(NamePayer.getText().toString(),EmailPayer.getText().toString()));
                    resultIntent.putExtra("CONTACT",new Gson().toJson(expense));

                    setResult(resultCode,resultIntent);


                    finish();
                }


            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonExpense = extras.getString("EXPENSE");
            expense = new Gson().fromJson(jsonExpense, Expense.class);
            Toast.makeText(mContext, String.valueOf(expense.getContacts().size()) , Toast.LENGTH_SHORT).show();
        }

    }


    @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
