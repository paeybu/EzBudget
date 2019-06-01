package com.kabu.kabi.ezbudget;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kabu.kabi.ezbudget.database.AppDatabase;
import com.kabu.kabi.ezbudget.database.TransactionEntry;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TransactionAdapter.ItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private TransactionAdapter mAdapter;
    private TextView mIncomeSum, mExpenseSum, mDifferenceSum;

    private AppDatabase mDb;
    private double mTotalIncome, mTotalExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerViewTransaction);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mIncomeSum = findViewById(R.id.incomeSummary);
        mExpenseSum = findViewById(R.id.expenseSummary);
        mDifferenceSum = findViewById(R.id.differenceSummary);

        mAdapter = new TransactionAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("Delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        int position = viewHolder.getAdapterPosition();
                                        List<TransactionEntry> trans = mAdapter.getTransactionEntries();
                                        mDb.transactionDao().delete(trans.get(position));
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setupViewModel();
                    }
                }).create().show();
            }
        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTransactionIntent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(addTransactionIntent);
            }
        });

        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    private void summary() {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mTotalIncome = mDb.transactionDao().getIncomeSum();
                mTotalExpense = mDb.transactionDao().getExpenseSum();
            }
        });
        double net = mTotalIncome - mTotalExpense;
        mIncomeSum.setText("Total incomes: " + mTotalIncome);
        mIncomeSum.setTextColor(getResources().getColor(R.color.green));
        mExpenseSum.setText("Total expenses: " + mTotalExpense);
        mExpenseSum.setTextColor(getResources().getColor(R.color.red));
        mDifferenceSum.setText("Net: " + net);

        if (net < 0) {
            mDifferenceSum.setTextColor(getResources().getColor(R.color.red));
        } else {
            mDifferenceSum.setTextColor(getResources().getColor(R.color.green));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViewModel();
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTransactions().observe(this, new Observer<List<TransactionEntry>>() {
            @Override
            public void onChanged(@Nullable List<TransactionEntry> transactionEntries) {
                Log.d(TAG, "Updating list of transactions from livedata in viewmodel");
                mAdapter.setTransactions(transactionEntries);
                summary();
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
        intent.putExtra(AddTransactionActivity.EXTRA_TRANSACTION_ID, itemId);
        startActivity(intent);
    }
}
