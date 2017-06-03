package br.com.tonhaosemacento.rachaconta;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;

import br.com.tonhaosemacento.rachaconta.models.Contacts;
import br.com.tonhaosemacento.rachaconta.models.Expense;

import static br.com.tonhaosemacento.rachaconta.AddExpenseActivity.REQ_CODE_CHILD;

public class AddPayersActivity  extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    Context mContext = this;
    ListView lstView;
    ArrayList<Contacts> contacts;
    FloatingActionButton btnConfirmPayers;
    FloatingActionButton btnAddPayer;
    Expense expense;
    String jsonExpense = "";

    public final static int REQ_CODE_CONTACT = 2;
    public final static int REQ_CODE_ITEMS = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payers);
        findViews();
        setActions();
    }

    /**
     * used to bind the wiews with variables
     * **/
    private void findViews(){
        lstView = (ListView) findViewById(R.id.list_payers);
        btnConfirmPayers = (FloatingActionButton) findViewById(R.id.btn_confirm_payer);
        btnAddPayer = (FloatingActionButton) findViewById(R.id.btn_add_payers);
    }

    /**
     * set actions with the binded objects
     * **/
    private void setActions(){
        btnAddPayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,AddPayerActivity.class);
                intent.putExtra("EXPENSE",new Gson().toJson(expense));
                AddPayersActivity.this.startActivityForResult(intent,REQ_CODE_CONTACT);

            }
        });
        btnConfirmPayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int resultCode = RESULT_OK;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("EXPENSE",new Gson().toJson(expense));
                setResult(resultCode,resultIntent);

                finish();*/
                if ((expense.getContacts().size()) > 0) {
                    Intent intent = new Intent(mContext, AddItemsActivity.class);
                    intent.putExtra("EXPENSE", new Gson().toJson(expense));
                    AddPayersActivity.this.startActivityForResult(intent,REQ_CODE_ITEMS);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonExpense = extras.getString("EXPENSE");
            expense = new Gson().fromJson(jsonExpense,Expense.class);
            Toast.makeText(mContext,jsonExpense, Toast.LENGTH_SHORT).show();
        }

    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_CONTACT) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                jsonExpense = extras.getString("CONTACT");
                expense = new Gson().fromJson(jsonExpense, Expense.class);
                Toast.makeText(mContext, jsonExpense , Toast.LENGTH_SHORT).show();
            }

        }

        if(requestCode == REQ_CODE_ITEMS) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                jsonExpense = extras.getString("ITEM");
                expense = new Gson().fromJson(jsonExpense, Expense.class);
                Toast.makeText(mContext, jsonExpense , Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
