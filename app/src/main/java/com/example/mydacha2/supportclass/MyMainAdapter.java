package com.example.mydacha2.supportclass;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.R;


public class MyMainAdapter extends RecyclerView.Adapter<MyMainAdapter.ViewHolder> {
    private final MyListMain[] listData;
    private final MyClickListener myClickListener;

    public MyMainAdapter(MyListMain[] listData, MyClickListener myClickListener) {
        this.listData = listData;
        this.myClickListener = myClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.fragment_item_main, parent, false);

        return new ViewHolder(listItem);
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = 33)
    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(listData[position].getName());
        holder.imageView.setImageResource(listData[position].getImage());
        holder.mIdView.setText(String.valueOf(listData[position].getId()));

        holder.linearLayout.setOnClickListener(view -> {
            Resources res = view.getResources();
            final Animation animAlpha = AnimationUtils.loadAnimation(view.getContext(), R.anim.alpha);
            view.startAnimation(animAlpha);
            if (!animAlpha.hasStarted()){
                view.postDelayed(() -> myClickListener.onItemClick(listData[position].getId()), res.getInteger(R.integer.animeMills));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mIdView;
        public ImageView imageView;
        public TextView textView;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView =  itemView.findViewById(R.id.imageView);
            this.textView =  itemView.findViewById(R.id.textViewName);
            this.mIdView =  itemView.findViewById(R.id.id_object);
            mIdView.setVisibility(TextView.INVISIBLE);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + textView.getText() + "'";
        }
    }
}