package com.vnat.placeautocompleteretrofit.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnat.placeautocompleteretrofit.Model.Result;
import com.vnat.placeautocompleteretrofit.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private List<Result> list;
    private Integer LIMIT = 5;

    public ResultAdapter(List<Result> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ResultViewHolder holder, final int position) {
        final Result obj = list.get(position);

        holder.txtName.setText(obj.getName());
        holder.txtAddress.setText(obj.getFormattedAddress());

    }

    @Override
    public int getItemCount() {
        return list.size() > LIMIT ? LIMIT : list.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtName)
        TextView txtName;

        @BindView(R.id.txtAddress)
        TextView txtAddress;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }
}