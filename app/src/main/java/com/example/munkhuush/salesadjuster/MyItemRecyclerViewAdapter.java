package com.example.munkhuush.salesadjuster;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.munkhuush.salesadjuster.Item.ItemContent;
import com.example.munkhuush.salesadjuster.ItemFragment.OnListFragmentInteractionListener;
import com.example.munkhuush.salesadjuster.Item.ItemContent.SaleItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.example.munkhuush.salesadjuster.Item.ItemContent.SaleItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<ItemContent.SaleItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<ItemContent.SaleItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        Log.i("SIZE",""+ mValues.size());
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
//        view.setPadding(50, 50, 50, 50);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i("INDEX", ""+position+" "+mValues.get(position).favoritePercent);
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).favoriteName);
        if(holder.mItem.favoriteType.equals("amount")){
            holder.mContentView.setText(mValues.get(position).favoriteAmount +"$");
            Log.d("TYPE", "AMOUNT $$$");
        }else{
            holder.mContentView.setText(mValues.get(position).favoritePercent+"%");
            Log.d("TYPE", "PERCENT %");
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public ItemContent.SaleItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.itemTitleTextView);
            mContentView = (TextView) view.findViewById(R.id.itemPercentTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
