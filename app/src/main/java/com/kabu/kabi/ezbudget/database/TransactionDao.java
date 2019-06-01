package com.kabu.kabi.ezbudget.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM `transaction` ORDER BY created_at")
    LiveData<List<TransactionEntry>> loadAllTransactions();

    @Query("SELECT SUM(price) FROM `transaction` WHERE type = 1")
    double getIncomeSum();

    @Query("SELECT SUM(price) FROM `transaction` WHERE type = 2")
    double getExpenseSum();

    @Insert
    void insertTransaction(TransactionEntry transactionEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TransactionEntry transactionEntry);

    @Delete
    void delete(TransactionEntry transactionEntry);

    @Query("SELECT * FROM `transaction` WHERE id = :id")
    LiveData<TransactionEntry> loadTransactionById(int id);
}
