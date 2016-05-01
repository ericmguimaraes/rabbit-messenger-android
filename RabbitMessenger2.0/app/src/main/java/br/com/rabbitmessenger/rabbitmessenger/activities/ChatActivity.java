package br.com.rabbitmessenger.rabbitmessenger.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.rabbitmessenger.rabbitmessenger.R;
import br.com.rabbitmessenger.rabbitmessenger.fragments.ChatFragment;
import br.com.rabbitmessenger.rabbitmessenger.model.Message;

public class ChatActivity extends AppCompatActivity implements ChatFragment.OnListFragmentInteractionListener {

    private String contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getIntent().getExtras().containsKey("contact")){
            contact = getIntent().getExtras().getString("contact");
            getSupportActionBar().setTitle(contact);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if(toolbar!=null)
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

    }

    @Override
    public void onListFragmentInteraction(Message message) {

    }

    public String getContact() {
        return contact;
    }
}
