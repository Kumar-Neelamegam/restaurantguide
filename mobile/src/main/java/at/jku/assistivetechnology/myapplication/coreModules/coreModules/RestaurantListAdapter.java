package at.jku.assistivetechnology.myapplication.coreModules.coreModules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import at.jku.assistivetechnology.domain.objects.RestaurantObject;
import at.jku.assistivetechnology.myapplication.R;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {


    private List<RestaurantObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;


    // data is passed into the constructor
    public RestaurantListAdapter(Context context, List<RestaurantObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_rowitem, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textview_Sno.setText(String.valueOf(mData.get(position).getId()));
        holder.textview_RestaurantName.setText(mData.get(position).getRestaurantName());
        holder.textview_Distance.setText(mData.get(position).getRestaurantDistance());
    }

    private int calculateDistance() {
        return 0;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textview_Sno;
        TextView textview_RestaurantName;
        TextView textview_Distance;

        ViewHolder(View itemView) {
            super(itemView);
            textview_Sno = itemView.findViewById(R.id.txtvw_sno);
            textview_RestaurantName = itemView.findViewById(R.id.txtvw_restaurantname);
            textview_Distance = itemView.findViewById(R.id.txtvw_distance);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    RestaurantObject getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
