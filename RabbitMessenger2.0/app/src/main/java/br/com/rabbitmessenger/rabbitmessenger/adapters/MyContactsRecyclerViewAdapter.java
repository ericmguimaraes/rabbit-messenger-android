package br.com.rabbitmessenger.rabbitmessenger.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.rabbitmessenger.rabbitmessenger.R;
import br.com.rabbitmessenger.rabbitmessenger.fragments.ContactsFragment.OnListFragmentInteractionListener;
import br.com.rabbitmessenger.rabbitmessenger.model.UserSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyContactsRecyclerViewAdapter extends RecyclerView.Adapter<MyContactsRecyclerViewAdapter.ViewHolder> {

    private List<String> contacts;
    private final OnListFragmentInteractionListener mListener;
    private List<String> senders;
    LinearLayoutManager linearLayoutManager;

    public MyContactsRecyclerViewAdapter(List<String> items, OnListFragmentInteractionListener listener, LinearLayoutManager linearLayoutManager) {
        contacts = items;
        mListener = listener;
        senders = new ArrayList<>();
        this.linearLayoutManager=linearLayoutManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = contacts.get(position);
        holder.mContactView.setText(holder.mItem);
        if(isItemAmongSenders(holder.mItem))
            holder.messageIcon.setVisibility(View.VISIBLE);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    holder.messageIcon.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean isItemAmongSenders(String mItem) {
        senders = UserSingleton.getINSTANCE().getSenders();
        boolean isAmong = false;
        for (String s :senders) {
            if (s.equals(mItem))
                isAmong = true;
            break;
        }
        return isAmong;
    }

    private int getSenderPosition(String mItem) {
        int position = -1;
        for (int i=0; i<contacts.size(); i++) {
            if (contacts.get(i).equals(mItem)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    public void notifyNewMessage(String sender) {
        notifyDataSetChanged();
        int position = getSenderPosition(sender);
        if(position!=-1) {
            View view = linearLayoutManager.findViewByPosition(position);
            if(view!=null) {
                ImageView messageView = (ImageView) view.findViewById(R.id.messageIcon);
                messageView.setVisibility(View.VISIBLE);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContactView;
        public final ImageView messageIcon;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContactView = (TextView) view.findViewById(R.id.contact);
            messageIcon = (ImageView) view.findViewById(R.id.messageIcon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContactView.getText() + "'";
        }
    }
}
