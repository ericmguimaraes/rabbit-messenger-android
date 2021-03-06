package br.com.rabbitmessenger.rabbitmessenger.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.rabbitmessenger.rabbitmessenger.activities.LoginActivity;
import br.com.rabbitmessenger.rabbitmessenger.adapters.MyContactsRecyclerViewAdapter;
import br.com.rabbitmessenger.rabbitmessenger.R;
import br.com.rabbitmessenger.rabbitmessenger.model.Message;
import br.com.rabbitmessenger.rabbitmessenger.model.User;
import br.com.rabbitmessenger.rabbitmessenger.model.UserSingleton;
import br.com.rabbitmessenger.rabbitmessenger.util.RabbitMQManager;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactsFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    private List<User> contacts;

    private MyContactsRecyclerViewAdapter adapter;

    RecyclerView recyclerView;

    ProgressBar progressBar;

    private LinearLayoutManager linearLayoutManager;

    private volatile List<Message> messages;

    private Handler messagesHandler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactsFragment() {
        messages = new ArrayList<>();
        messagesHandler = new Handler();

        messagesHandler.postDelayed(messagesChecker,500);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        showProgress(true);

        Context context = view.getContext();
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MyContactsRecyclerViewAdapter(contacts, mListener,linearLayoutManager);
        recyclerView.setAdapter(adapter);

        updateList();

        RabbitMQManager.getINSTANCE().addOnMessageReceivedListener(new RabbitMQManager.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(Message msg) {
                synchronized (messages) {
                    messages.add(msg);
                }
            }
        });

        try {
            RabbitMQManager.getINSTANCE().startService();
        } catch (IOException e) {
            Log.e("RECEIVING",e.getMessage(),e);
            Toast.makeText(getContext(),"Ops, tivemos um problema.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        showProgress(false);

        return view;
    }

    Runnable messagesChecker = new Runnable() {
        @Override
        public void run() {
            synchronized (messages) {
                for (Message msg : messages) {
                    UserSingleton.getINSTANCE().addMessage(msg);
                    adapter.notifyNewMessage(msg.getSender());
                }
                messages.clear();
            }
            messagesHandler.postDelayed(messagesChecker,500);
        }
    };

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

    public void updateList() {
        contacts = new ArrayList<>();
        contacts = RabbitMQManager.getINSTANCE().getUsers();
        adapter.setContacts(contacts);
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
        void onListFragmentInteraction(User contact);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            recyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
