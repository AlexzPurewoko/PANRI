package id.kenshiro.app.panri.adapter;

import android.graphics.Color;
import android.graphics.Point;
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

import com.mylexz.utils.MylexzActivity;

import java.util.List;

import id.kenshiro.app.panri.R;

public class AdapterRecycler extends RecyclerView.Adapter {
    private List<DataPerItems> data;
    private List<CardView> dataList;
    private OnItemClickListener listener = null;
    private int lengthItem = 0;
    private MylexzActivity mylexzActivity;

    public AdapterRecycler(List<DataPerItems> data, MylexzActivity mylexzActivity) {
        this.data = data;
        this.mylexzActivity = mylexzActivity;

    }

    private float fact_size_font = 11f;
    private Point screenSize = new Point();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView rootElement = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_adapter, null, false);
        LinearLayout layout = new LinearLayout(viewGroup.getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.setOrientation(LinearLayout.VERTICAL);
        mylexzActivity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        RecyclerView.LayoutParams rootParams = new RecyclerView.LayoutParams(
                screenSize.x,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
        rootElement.setLayoutParams(rootParams);
        rootElement.addView(layout);
        rootElement.setCardElevation(8f);
        rootElement.setContentPadding(16,16,16,16);
        rootElement.setRadius(8f);
        rootElement.setUseCompatPadding(true);
        final ViewItemHolder vih = new ViewItemHolder(rootElement);
        rootElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v, vih.position);
                }
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
        void onClick(View target, int position);
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
            float size = mylexzActivity.getResources().getDimension(R.dimen.size_text_incard);
            item.setTextSize(size);
            item.setGravity(Gravity.CENTER_HORIZONTAL);
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
