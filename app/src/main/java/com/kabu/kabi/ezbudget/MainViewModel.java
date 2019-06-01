package com.kabu.kabi.ezbudget;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kabu.kabi.ezbudget.database.AppDatabase;
import com.kabu.kabi.ezbudget.database.TransactionEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    public LiveData<List<TransactionEntry>> getTransactions() {
        return transactions;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving data from the Db");
        transactions = database.transactionDao().loadAllTransactions();
    }

    private LiveData<List<TransactionEntry>> transactions;
}
