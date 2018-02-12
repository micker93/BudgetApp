package com.example.riiss.budgetapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
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
    double Budget=0;
    double minus=0;
    double totalafterminus=0;
    int value=0;
    double percentage =0;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Budget");
    DatabaseReference myRefprocent = database.getReference("procent");

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
                builder.setMessage("Budget");
                final EditText editbudget = new EditText(MainActivity.this);
                editbudget.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(editbudget);
                editbudget.setHint("Skriv in din budget");
                builder.setPositiveButton("Klar", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int press){

                        TextView textView=(TextView)findViewById(R.id.budget);
                        String total=editbudget.getText().toString();

                        Budget=Double.valueOf(total);
                        percentage=Budget / 10 ;
                        myRef.setValue(Budget);
                        myRefprocent.setValue(percentage);

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

        if(percentage>value){

            addNotification();

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Avdrag");
        builder.setMessage("Skriv in hur mycket du vill ta av budgeten ");
        final EditText editbudget = new EditText(MainActivity.this);
        editbudget.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editbudget);
        editbudget.setHint("avdrag av din budget");
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

        String id="main_channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name="Channel Name";
        String desciption="Channgel Desc";
        int importance=NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel= new NotificationChannel(id,name,importance);
            notificationChannel.setDescription(desciption);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(false);
            if(notificationChannel !=null){

                notificationManager.createNotificationChannel(notificationChannel);

            }
        }

        NotificationCompat.Builder notifictaionbuilder= new NotificationCompat.Builder(this);
        notifictaionbuilder.setSmallIcon(R.mipmap.ic_launcher);
        notifictaionbuilder.setContentTitle("Varning!");
        notifictaionbuilder.setContentText("Din budget är nästan slut " + totalafterminus + " KR kvar");
        notifictaionbuilder.setLights(Color.WHITE,500,500);
        notifictaionbuilder.setColor(Color.RED);
        notifictaionbuilder.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1000,notifictaionbuilder.build());

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
