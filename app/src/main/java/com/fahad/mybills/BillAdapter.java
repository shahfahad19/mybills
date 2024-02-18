package com.fahad.mybills;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fahad.mybills.Utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private List<Bill> billsList;
    private LayoutInflater inflater;

    Context context;

    public interface ItemClickListener {
        void onItemClickListener(String id);
    }

    private ItemClickListener itemClickListener;

    public BillAdapter(Context context, ItemClickListener listener) {
        this.billsList = new ArrayList<>();
        this.itemClickListener = listener;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Bill> billsList) {
        this.billsList = billsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bill_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bill bill = billsList.get(position);
        holder.companyName.setText(bill.getCompany());
        holder.personName.setText(bill.getPersonName());
        holder.refNo.setText(bill.getRef());
        holder.dueDate.setText("Due: " +bill.getDueDate());
        holder.units.setText(bill.getUnits());
        holder.amount.setText("Rs. "+bill.getCurrentBill());

        if (bill.getType().equals("gas")) {
            holder.billLogo.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.gas));
        }
        else if (bill.getType().equals("electricity")) {
            holder.billLogo.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.electricity));
        }


        int days = DateUtils.calculateDaysLeft(bill.getDueDate());

        if (days < 0) {
            holder.infoMsg.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }
        else {
            holder.infoMsg.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            if (days == 0) {
                holder.infoMsg.setText("Last day of payment!");
                holder.infoMsg.setTextColor(ContextCompat.getColor(context, R.color.danger));
            }
            else if (days == 1) {
                holder.infoMsg.setText("1 day left to pay the bill");
                holder.infoMsg.setTextColor(ContextCompat.getColor(context, R.color.warning));
            }
            else {
                if (days <= 3) {
                    holder.infoMsg.setTextColor(ContextCompat.getColor(context, R.color.warning));
                }
                else {
                    holder.infoMsg.setTextColor(ContextCompat.getColor(context, R.color.success));
                }
                holder.infoMsg.setText(days+" days left to pay the bill");
            }
        }


        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClickListener(bill.getRef());
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.billsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView companyName, personName, refNo, dueDate, units, amount, infoMsg;
        ImageView billLogo;

        LinearLayout divider;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            companyName = itemView.findViewById(R.id.companyName);
            personName = itemView.findViewById(R.id.personName);
            refNo = itemView.findViewById(R.id.refNo);
            dueDate = itemView.findViewById(R.id.dueDate);
            units = itemView.findViewById(R.id.units);
            amount = itemView.findViewById(R.id.amount);
            infoMsg = itemView.findViewById(R.id.infoMsg);
            billLogo = itemView.findViewById(R.id.billLogo);
            divider = itemView.findViewById(R.id.divider);

        }
    }
}