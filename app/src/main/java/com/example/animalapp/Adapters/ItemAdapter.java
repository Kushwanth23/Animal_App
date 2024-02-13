package com.example.animalapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animalapp.R;
import com.example.animalapp.databinding.ItemsDataBinding;
import com.example.animalapp.ui.dashboard.DashboardFragment;
import com.example.animalapp.ui.dashboard.DetailActivity;
import com.squareup.picasso.Picasso;
import com.example.animalapp.Model.Animals;

import java.util.ArrayList;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
    private Context context;
    public static ArrayList<Animals> animalsList;
    ItemsDataBinding binding;
    public ItemAdapter(Context context, ArrayList<Animals> list) {
        this.context = context;
        this.animalsList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemsDataBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animals model = animalsList.get(position);
        if (model != null){
            binding.name.setText(model.getName());
            binding.type.setText(model.getAnimalType());
            binding.gender.setText(model.getGender());
            binding.age.setText(String.valueOf(model.getAge()));
            try{
                Picasso.get().load(model.getImage()).placeholder(R.drawable.animals2)
                        .into(binding.productImage);
            } catch (Exception e){
                e.getMessage();
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("pos", position);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return animalsList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemsDataBinding binding;
        public ViewHolder(@NonNull ItemsDataBinding dataBinding){
            super(dataBinding.getRoot());
            binding = dataBinding;
        }
    }
}
