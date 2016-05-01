package br.com.rabbitmessenger.rabbitmessenger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import br.com.rabbitmessenger.rabbitmessenger.R;
import br.com.rabbitmessenger.rabbitmessenger.fragments.ContactsFragment;

public class ContactsActivity extends AppCompatActivity implements ContactsFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onListFragmentInteraction(String contact) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }

}
