package id.kenshiro.app.panri.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import id.kenshiro.app.panri.R;

public class AdapterRecycler extends RecyclerView.Adapter {
    private List<DataPerItems> data;
    private List<CardView> dataList;
    private OnItemClickListener listener = null;
    private int lengthItem = 0;
    public AdapterRecycler(List<DataPerItems> data){
        this.data = data;

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView rootElement = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_adapter, viewGroup, false);
        LinearLayout layout = new LinearLayout(viewGroup.getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.setOrientation(LinearLayout.VERTICAL);
        rootElement.addView(layout);
        rootElement.setCardElevation(8f);
        rootElement.setContentPadding(16,16,16,16);
        rootElement.setRadius(8f);
        ViewItemHolder vih = new ViewItemHolder(rootElement);
        rootElement.setOnClickListener((v)-> {
            if(listener != null){
                listener.onClick(v, vih.position);
            }
        });
        return vih;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.i("AdapterRecycler", "int i = "+i);
        String text = data.get(i).items;
        Log.i("AdapterRecycler", "text i = "+text);
        ViewItemHolder vih = (ViewItemHolder) viewHolder;
        vih.item.setText(text);
        vih.setPosition(i);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public interface OnItemClickListener{
        public void onClick(View target, int position);
    }
    public class ViewItemHolder extends RecyclerView.ViewHolder {
        TextView item;
        public int position = 0;
        public ViewItemHolder(View v){
            super(v);
            item = new TextView(v.getContext());
            item.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            item.setTextColor(Color.parseColor("#000000"));
            item.setTextSize(15f);
            item.setGravity(Gravity.CENTER);
            CardView root = (CardView) v;
            LinearLayout child = (LinearLayout) root.getChildAt(0);
            child.addView(item);
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
    public static class DataPerItems {
        public String items;
        public int resImages;
        public DataPerItems(String items){
            this.items = items;
        }
    }
}
