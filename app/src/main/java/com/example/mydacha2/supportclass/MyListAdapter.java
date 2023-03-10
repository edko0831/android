package com.example.mydacha2.supportclass;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.R;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private final List<MyListObjectControl> listData;
    private final MyClickListener myClickListener;
    private final MyCheckedChangeListener myCheckedChangeListener;
    private List<Long> selectObject = new ArrayList<>();
    View parent;

    public void setSelectObject(List<Long> selectObject) {
        this.selectObject = selectObject;
    }

    // RecyclerView recyclerView;
    public MyListAdapter(List<MyListObjectControl> listData, MyClickListener myClickListener, MyCheckedChangeListener myCheckedChangeListener) {
        this.listData = listData;
        this.myClickListener = myClickListener;
        this.myCheckedChangeListener = myCheckedChangeListener;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        this.parent = parent;
        return new ViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(listData.get(position).getDescription());
        String imgUrl = listData.get(position).getImgUrl();
        holder.checkBox.setVisibility(CheckBox.INVISIBLE);

        if (imgUrl != null && !imgUrl.isEmpty()) {
            Bitmap originalBitmap = BitmapFactory.decodeFile(imgUrl);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 100;

            holder.imageView.setImageBitmap(originalBitmap);
        } else {
            holder.imageView.setImageResource(android.R.drawable.ic_dialog_map);
        }
        holder.editText.setText(String.valueOf(listData.get(position).getId()));

        if(selectObject.size() > 0){
            if(selectObject.contains(listData.get(position).getId())) {
                holder.checkBox.setChecked(true);
                holder.checkBox.setVisibility(CheckBox.VISIBLE);
            }
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> myCheckedChangeListener.onCheckedChange(listData.get(position).getId(), b));
        holder.linearLayout.setOnClickListener(view -> {
          if (myClickListener != null) {
                final Animation animAlpha = AnimationUtils.loadAnimation(view.getContext(), R.anim.alpha);
                view.startAnimation(animAlpha);
                if (!animAlpha.hasStarted()){
                    Resources res = view.getResources();
                    view.postDelayed(() -> myClickListener.onItemClick(listData.get(position).getId()), res.getInteger(R.integer.animeMills));
                }
          }
        });
        holder.linearLayout.setOnLongClickListener(view -> {
            if (myClickListener != null) {
                holder.checkBox.setVisibility(CheckBox.VISIBLE);
                holder.checkBox.setChecked(true);
                myClickListener.onItemLongClick(listData.get(position).getId());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public LinearLayout linearLayout;
        public EditText editText;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.textView);
            this.editText = itemView.findViewById(R.id.id_object);
            this.checkBox = itemView.findViewById(R.id.checkBox);
            editText.setVisibility(EditText.INVISIBLE);

            linearLayout = itemView.findViewById(R.id.linearLayout);
        }

    }

}