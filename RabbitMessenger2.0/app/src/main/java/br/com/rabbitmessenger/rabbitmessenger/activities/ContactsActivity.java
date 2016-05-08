package br.com.rabbitmessenger.rabbitmessenger.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.security.AccessController;

import br.com.rabbitmessenger.rabbitmessenger.R;
import br.com.rabbitmessenger.rabbitmessenger.fragments.ContactsFragment;
import br.com.rabbitmessenger.rabbitmessenger.model.User;
import br.com.rabbitmessenger.rabbitmessenger.util.RabbitMQManager;

public class ContactsActivity extends AppCompatActivity implements ContactsFragment.OnListFragmentInteractionListener {

    private FloatingActionButton fab;

    ContactsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

        fragment = ContactsFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.content,fragment).commit();
    }

    private void createDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ContactsActivity.this);
        alertDialog.setTitle("Adicionar Contato");
        alertDialog.setMessage("Nome do contato");

        final EditText input = new EditText(ContactsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_launcher);

        alertDialog.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        User u = new User(input.getText().toString());
                        RabbitMQManager.getINSTANCE().saveUser(u);
                        fragment.updateList();
                        }
                    }
                );

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onListFragmentInteraction(User contact) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("contact", contact.getName());
        startActivity(intent);
    }
}
