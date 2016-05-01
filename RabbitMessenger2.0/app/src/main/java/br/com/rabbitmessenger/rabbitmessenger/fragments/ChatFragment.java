package br.com.rabbitmessenger.rabbitmessenger.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Date;
import java.util.List;

import br.com.rabbitmessenger.rabbitmessenger.activities.ChatActivity;
import br.com.rabbitmessenger.rabbitmessenger.adapters.MyChatRecyclerViewAdapter;
import br.com.rabbitmessenger.rabbitmessenger.R;
import br.com.rabbitmessenger.rabbitmessenger.model.Message;
import br.com.rabbitmessenger.rabbitmessenger.model.UserSingleton;
import br.com.rabbitmessenger.rabbitmessenger.util.RabbitMQManager;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    private String contact;

    private List<Message> messages;

    private RecyclerView recyclerView;

    private MyChatRecyclerViewAdapter adapter;

    private ImageView sendButton;

    private EditText messageEditText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    @SuppressWarnings("unused")
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        sendButton = (ImageView) view.findViewById(R.id.sendButton);
        messageEditText = (EditText) view.findViewById(R.id.messageEditText);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity().getApplicationContext();

        contact = ((ChatActivity) getActivity()).getContact();

        ((ChatActivity) getActivity()).getSupportActionBar().setTitle(contact);

        messages = UserSingleton.getINSTANCE().findMessagesBySender(contact);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyChatRecyclerViewAdapter(messages, mListener, getContext());
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!messageEditText.getText().toString().isEmpty()){
                    Message message = new Message();
                    message.setSender(UserSingleton.getINSTANCE().getUsername());
                    message.setReceiver(contact);
                    message.setContent(messageEditText.getText().toString());
                    message.setDate(new Date());
                    message.setRead(false);
                    new SendMessageAsyncTask(message).execute();
                    adapter.addMessage(message);
                }
                messageEditText.setText("");
            }
        });

        RabbitMQManager.getINSTANCE().addOnMessageReceivedListener(new RabbitMQManager.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(Message msg) {
                if(msg.getSender().equals(contact))
                    adapter.addMessage(msg);
            }
        });
        RabbitMQManager.getINSTANCE().startReceiver();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Message message);
    }

    public class SendMessageAsyncTask extends AsyncTask<Void, Void, Boolean> {

        Message message;

        public SendMessageAsyncTask(Message message){
            this.message=message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            RabbitMQManager.getINSTANCE().send(message);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

    }
}
