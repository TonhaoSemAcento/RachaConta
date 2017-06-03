package br.com.tonhaosemacento.rachaconta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.tonhaosemacento.rachaconta.models.Contacts;
import br.com.tonhaosemacento.rachaconta.models.Expense;
import br.com.tonhaosemacento.rachaconta.utils.SharedPrefManager;

//this class is the part 1/3 of the expense including
public class AddExpenseActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    Context mContext = this;
    FloatingActionButton btnConfirmExpense;
    android.support.design.widget.TextInputEditText ExpenseDescription;
    Switch othersCanEdit;
    Expense expense;
    SharedPrefManager sharedPrefManager;
    private String cUsername, cEmail;
    String jsonExpense="";

    //return of intent
    public final static int REQ_CODE_CHILD = 1;

    /**
     * onCreate is ther first method to be called when activity is called
     * **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        findViews();
        setActions();
    }

    /**
     * used to bind the wiews with variables
     * **/
    private void findViews(){
        btnConfirmExpense  = (FloatingActionButton) findViewById(R.id.btn_confirm_expense);
        ExpenseDescription = (android.support.design.widget.TextInputEditText) findViewById(R.id.tie_name_expense);
        othersCanEdit = (Switch) findViewById(R.id.switch_can_edit_expense);
    }

    /**
     * set actions with the binded objects
     * **/
    private void setActions(){

        sharedPrefManager = new SharedPrefManager(mContext);
        cUsername = sharedPrefManager.getName();
        cEmail = sharedPrefManager.getUserEmail();



        btnConfirmExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPayers();
            }
        });
    }


    private void callPayers(){
        if ( ExpenseDescription.getText().toString().isEmpty()){
            Toast.makeText(AddExpenseActivity.this, getString(R.string.missing_description), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(mContext,AddPayersActivity.class );
            expense = new Expense(ExpenseDescription.getText().toString() , othersCanEdit.isChecked(), cUsername );
            //activity will return the contacts payers and items of expense
            intent.putExtra("EXPENSE",new Gson().toJson(expense));
            ((Activity) mContext).startActivityForResult(intent,REQ_CODE_CHILD);
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_CHILD) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                jsonExpense = extras.getString("EXPENSE");
                expense = new Gson().fromJson(jsonExpense,Expense.class);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
