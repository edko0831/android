package com.example.mydacha2.supportclass;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.R;

import java.util.ArrayList;
import java.util.List;

public class MyDataAdapter extends  RecyclerView.Adapter<MyDataAdapter.ViewHolder>{
    private final List<MyListData> myListData;
    public final MyClickListener myClickListener;
    public final MyCheckedChangeListener myCheckedChangeListener;
    public final List<Long> selectData = new ArrayList<>();

    public MyDataAdapter(List<MyListData> myListData, MyClickListener myClickListener, MyCheckedChangeListener myCheckedChangeListener) {
        this.myListData = myListData;
        this.myClickListener = myClickListener;
        this.myCheckedChangeListener = myCheckedChangeListener;
    }

    @NonNull
    @Override
    public MyDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(myListData.get(position).getName());
        String imgUrl = myListData.get(position).getPicture_url();
        holder.checkBox.setVisibility(CheckBox.INVISIBLE);

        if (imgUrl != null && !imgUrl.isEmpty()) {
            Bitmap originalBitmap = BitmapFactory.decodeFile(imgUrl);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 100;

            holder.imageView.setImageBitmap(originalBitmap);

        } else {
            holder.imageView.setImageResource(R.mipmap.people_foreground);
        }
        holder.editText.setText(String.valueOf(myListData.get(position).getId()));
        holder.button.setImageResource(R.mipmap.dots_foreground);
        if(selectData.size() > 0){
            if(selectData.contains(myListData.get(position).getId())) {
                holder.checkBox.setChecked(true);
                holder.checkBox.setVisibility(CheckBox.VISIBLE);
            }
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!holder.checkBox.isChecked()){
                holder.button.setVisibility(Button.INVISIBLE);
            } else {
                holder.button.setVisibility(Button.VISIBLE);
            }
            myCheckedChangeListener.onCheckedChange(myListData.get(position).getId(), b);
        });
    }

    @Override
    public int getItemCount() {return myListData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public ImageView imageView;
        public TextView textView;
        public RecyclerView recyclerView;
        public EditText editText;
        public CheckBox checkBox;
        public ImageButton button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.textView);
            this.editText = itemView.findViewById(R.id.id_object);
            this.checkBox = itemView.findViewById(R.id.checkBox);
            this.button = itemView.findViewById(R.id.imageButtonUpdate);

            button.setVisibility(Button.INVISIBLE);
            editText.setVisibility(EditText.INVISIBLE);

            button.setOnClickListener(v -> myClickListener.onItemClick(myListData.get(getBindingAdapterPosition()).getId()));

            recyclerView = itemView.findViewById(R.id.recyclerViewListControlPoint);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (myClickListener != null) {
                final Animation animAlpha = AnimationUtils.loadAnimation(v.getContext(), R.anim.alpha);
                v.startAnimation(animAlpha);
                if (!animAlpha.hasStarted()){
                    Resources res = v.getResources();
                    v.postDelayed(() -> myClickListener.onItemClick(myListData.get(getBindingAdapterPosition()).getId()), res.getInteger(R.integer.animeMills));
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            checkBox.setVisibility(CheckBox.VISIBLE);
            checkBox.setChecked(true);
            button.setVisibility(Button.VISIBLE);
            myClickListener.onItemLongClick(myListData.get(getBindingAdapterPosition()).getId());
            return true;
        }
    }
}
