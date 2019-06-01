package com.kabu.kabi.ezbudget;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.kabu.kabi.ezbudget.database.AppDatabase;

public class AddTransactionViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mTransactionId;

    public AddTransactionViewModelFactory(AppDatabase db, int transactionId) {
        mDb = db;
        mTransactionId = transactionId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTransactionViewModel(mDb, mTransactionId);
    }
}
