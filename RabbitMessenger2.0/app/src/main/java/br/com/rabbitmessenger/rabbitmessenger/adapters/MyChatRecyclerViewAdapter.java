package br.com.rabbitmessenger.rabbitmessenger.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.rabbitmessenger.rabbitmessenger.R;
import br.com.rabbitmessenger.rabbitmessenger.fragments.ChatFragment.OnListFragmentInteractionListener;
import br.com.rabbitmessenger.rabbitmessenger.model.Message;
import br.com.rabbitmessenger.rabbitmessenger.model.UserSingleton;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> {

    private final List<Message> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;

    public MyChatRecyclerViewAdapter(List<Message> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        UserSingleton.getINSTANCE().read(holder.mItem);
        holder.mContentView.setText(mValues.get(position).getContent());
        if(holder.mItem.getSender().equals(UserSingleton.getINSTANCE().getUsername()))
            ((CardView)holder.mView).setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));

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

    public void addMessage(Message msg) {
        mValues.add(msg);
        notifyItemInserted(mValues.size()-1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
