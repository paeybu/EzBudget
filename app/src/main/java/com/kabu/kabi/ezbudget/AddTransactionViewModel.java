package com.kabu.kabi.ezbudget;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.kabu.kabi.ezbudget.database.AppDatabase;
import com.kabu.kabi.ezbudget.database.TransactionEntry;

class AddTransactionViewModel extends ViewModel {

    private LiveData<TransactionEntry> trans;

    public AddTransactionViewModel(AppDatabase db, int transactionId) {
        trans = db.transactionDao().loadTransactionById(transactionId);
    }

    public LiveData<TransactionEntry> getTrans() {
        return trans;
    }
}
