package com.example.riiss.budgetapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    int Budget=0;
    int minus=0;
    int totalafterminus=0;
    int value=0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Budget");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Budget");
        final TextView textView=(TextView)findViewById(R.id.budget);

        RedorGreen();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                     value=dataSnapshot.getValue(Integer.class);
                     textView.setText(String.valueOf(value + " Kr"));
                     Budget= value;
                     RedorGreen();
            }

            @Override
            public void onCancelled(DatabaseError error) {

                textView.setText("Ingen Budget :(");

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View view) {
                Snackbar.make(view, "Lägger till...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Budget");
                builder.setMessage("Skriv in din budget ");
                final EditText editbudget = new EditText(MainActivity.this);
                editbudget.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(editbudget);
                editbudget.setText("0");
                builder.setPositiveButton("Klar", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int press){

                        TextView textView=(TextView)findViewById(R.id.budget);
                        String total=editbudget.getText().toString();

                        Budget=Integer.valueOf(total);
                        myRef.setValue(Budget);

                        textView.setText(String.valueOf(Budget));

                        Toast.makeText(getApplicationContext(), "Ny budget: " + total,
                                Toast.LENGTH_SHORT).show();
                                RedorGreen();
                    }
                });

                builder.setNegativeButton("Tillbacka" , null);
                builder.create();
                builder.show();

            }


        });
    }


    public void avdrag(final View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Avdrag");
        builder.setMessage("Skriv in hur mycket du vill ta av budgeten ");
        final EditText editbudget = new EditText(MainActivity.this);
        editbudget.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editbudget);
        editbudget.setText("0");
        builder.setPositiveButton("Klar", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int press){

                TextView textView=(TextView)findViewById(R.id.budget);
                String total=editbudget.getText().toString();

                minus=Integer.valueOf(total);
                totalafterminus=Budget-minus;
                myRef.setValue(totalafterminus);
                textView.setText(String.valueOf(totalafterminus));

                Toast.makeText(getApplicationContext(), "avdrag " + minus + " Kr",
                        Toast.LENGTH_SHORT).show();
                        RedorGreen();

            }
        });

        builder.setNegativeButton("Tillbacka" , null);
        builder.create();
        builder.show();

    }

    private void addNotification() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Notification Alert, Click Me!");
        mBuilder.setContentText("Hi, This is Android Notification Detail!");
        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());
    }

    public void RedorGreen(){

        TextView textView=(TextView)findViewById(R.id.budget);
        if(textView.getText().toString().contains("-")) {
            textView.setTextColor(Color.RED);
            addNotification();
        }
        else
            textView.setTextColor(Color.GREEN);
    }

}
