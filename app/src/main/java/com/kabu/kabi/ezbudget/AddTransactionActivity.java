package com.kabu.kabi.ezbudget;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kabu.kabi.ezbudget.database.AppDatabase;
import com.kabu.kabi.ezbudget.database.TransactionEntry;

import java.util.Date;

public class AddTransactionActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TRANSACTION_ID = "extraTransactionId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TRANSACTION_ID = "instanceTransactionId";
    // Constant for logging
    private static final String TAG = AddTransactionActivity.class.getSimpleName();
    private static final int DEFAULT_TRANSACTION_ID = -1;
    // Fields for views
    EditText mEditTextName;
    EditText mEditTextPrice;
    RadioGroup mRadioGroup;
    Button mButton;
    private Date mCreatedDate;

    private int mTransactionId = DEFAULT_TRANSACTION_ID;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        mEditTextName = findViewById(R.id.editTextTransactionName);
        mEditTextPrice = findViewById(R.id.editTextTransactionPrice);
        mRadioGroup = findViewById(R.id.radioGroup);

        mButton = findViewById(R.id.saveButton);

        initViews();
        mRadioGroup.check(R.id.radButton2);

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TRANSACTION_ID)) {
            mTransactionId = savedInstanceState.getInt(INSTANCE_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TRANSACTION_ID)) {
            mButton.setText(R.string.update_button);
            if (mTransactionId == DEFAULT_TRANSACTION_ID) {
                mTransactionId = intent.getIntExtra(EXTRA_TRANSACTION_ID, DEFAULT_TRANSACTION_ID);

                AddTransactionViewModelFactory factory = new AddTransactionViewModelFactory(mDb, mTransactionId);
                final AddTransactionViewModel viewModel = ViewModelProviders.of(this, factory).get(AddTransactionViewModel.class);
                viewModel.getTrans().observe(this, new Observer<TransactionEntry>() {
                    @Override
                    public void onChanged(@Nullable TransactionEntry transactionEntry) {
                        mCreatedDate = viewModel.getTrans().getValue().getCreatedAt();
                        viewModel.getTrans().removeObserver(this);
                        populateUI(transactionEntry);
                    }
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(INSTANCE_TRANSACTION_ID, mTransactionId);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void initViews() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });
    }

    private void populateUI(TransactionEntry transactionEntry) {
        if (transactionEntry == null) {
            return;
        }
        mEditTextName.setText(transactionEntry.getName());
        mEditTextPrice.setText("" + transactionEntry.getPrice());
        setTypeInViews(transactionEntry.getType());
    }

    private void setTypeInViews(int type) {
        if (type == 1) {
            mRadioGroup.check(R.id.radButton1);
        } else {
            mRadioGroup.check(R.id.radButton2);
        }
    }

    private void onSaveButtonClicked() {
        String name = mEditTextName.getText().toString();
        String priceString = mEditTextPrice.getText().toString();
        int type = getTypeFromViews();
        Date date = new Date();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name can't be empty", Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Price can't be empty", Toast.LENGTH_LONG).show();
            return;
        }

        double price = Double.parseDouble(priceString);

        final TransactionEntry transactionEntry = new TransactionEntry(name, price, type, date, date);
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTransactionId == DEFAULT_TRANSACTION_ID) {
                    mDb.transactionDao().insertTransaction(transactionEntry);
                } else {
                    //Update so no need to update the created Date
                    transactionEntry.setId(mTransactionId);
                    transactionEntry.setCreatedAt(mCreatedDate);
                    mDb.transactionDao().update(transactionEntry);
                }
                finish();
            }
        });

    }

    private int getTypeFromViews() {
        int type = 2;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                type = 1;
                break;
            case R.id.radButton2:
                type = 2;
                break;
        }
        return type;
    }


}
