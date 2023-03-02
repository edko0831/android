package com.example.mydacha2.supportclass;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydacha2.R;

import java.util.ArrayList;
import java.util.List;

public class MyControlPointAdapter extends  RecyclerView.Adapter<MyControlPointAdapter.ViewHolder> {

    private final List<MyListControlPoint> controlPoints;
    public final MyClickListener myClickListener;
    public final MyCheckedChangeListener myCheckedChangeListener;
    public final List<Long> selectControlPoint = new ArrayList<>();


    public MyControlPointAdapter(List<MyListControlPoint> listData, MyClickListener myClickListener, MyCheckedChangeListener myCheckedChangeListener) {
        this.controlPoints = listData;
        this.myClickListener = myClickListener;
        this.myCheckedChangeListener = myCheckedChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(controlPoints.get(position).getDescription());
        String imgUrl = controlPoints.get(position).getImgUrl();
        holder.checkBox.setVisibility(CheckBox.INVISIBLE);

        if (imgUrl != null && !imgUrl.isEmpty()) {
            Bitmap originalBitmap = BitmapFactory.decodeFile(imgUrl);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 100;

            holder.imageView.setImageBitmap(originalBitmap);

        } else {
            holder.imageView.setImageResource(android.R.drawable.ic_dialog_map);
        }
        holder.editText.setText(String.valueOf(controlPoints.get(position).getId()));

        if(selectControlPoint.size() > 0){
            if(selectControlPoint.contains(controlPoints.get(position).getId())) {
                holder.checkBox.setChecked(true);
                holder.checkBox.setVisibility(CheckBox.VISIBLE);
            }
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> myCheckedChangeListener.onCheckedChange(controlPoints.get(position).getId(), b));

    }

    @Override
    public int getItemCount() {
        return controlPoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ImageView imageView;
        public TextView textView;
        public RecyclerView recyclerView;
        public EditText editText;
        public CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.textView);
            this.editText = itemView.findViewById(R.id.id_object);
            this.checkBox = itemView.findViewById(R.id.checkBox);
            editText.setVisibility(EditText.INVISIBLE);

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
                        v.postDelayed(() -> myClickListener.onItemClick(controlPoints.get(getBindingAdapterPosition()).getId()), res.getInteger(R.integer.animeMills));
                    }
                }
            }

        @Override
        public boolean onLongClick(View v) {
            checkBox.setVisibility(CheckBox.VISIBLE);
            checkBox.setChecked(true);
            myClickListener.onItemLongClick(controlPoints.get(getBindingAdapterPosition()).getId());
            return true;
        }
    }
}
