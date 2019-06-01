package com.kabu.kabi.ezbudget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kabu.kabi.ezbudget.database.TransactionEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private static final String DATE_FORMAT = "dd/MM/yyy hh:mm:ss";
    final private ItemClickListener mItemClickListener;
    private List<TransactionEntry> mTransactionEntries;
    private Context mContext;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public TransactionAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.transaction_layout, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionEntry transactionEntry = mTransactionEntries.get(position);
        String name = transactionEntry.getName();
        double price = transactionEntry.getPrice();
        String createdAt = dateFormat.format(transactionEntry.getCreatedAt());
        int type = transactionEntry.getType();

        holder.transactionNameView.setText(name);
        holder.updatedAtView.setText(createdAt);
        String value = String.format("%,.2f", price);
        holder.priceView.setText(value);

        if (type == 1) {
            holder.priceView.setTextColor(mContext.getResources().getColor(R.color.green));
        } else {
            holder.priceView.setTextColor(mContext.getResources().getColor(R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        if (mTransactionEntries == null) return 0;
        return mTransactionEntries.size();
    }

    public void setTransactions(List<TransactionEntry> transactionEntries) {
        mTransactionEntries = transactionEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView transactionNameView;
        TextView updatedAtView;
        TextView priceView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            transactionNameView = itemView.findViewById(R.id.transactionName);
            updatedAtView = itemView.findViewById(R.id.transactionCreatedAt);
            priceView = itemView.findViewById(R.id.priceTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int elementId = mTransactionEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

    public List<TransactionEntry> getTransactionEntries() {
        return mTransactionEntries;
    }
}
