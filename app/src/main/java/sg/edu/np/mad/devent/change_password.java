package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class change_password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Button accept = findViewById(R.id.acceptchange);
        Button cancel = findViewById(R.id.cancelchange);
        EditText first = (EditText) findViewById(R.id.enternewpass);
        EditText second = (EditText) findViewById(R.id.confirmnewpass);
        final Integer[] saltvalue = {0};

        String user_id_unique = "W222"; // Change this to get from intent;

        // Firebase for storing Image
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference event_path = database.getReference("Event");
        DatabaseReference user_path = database.getReference("Users");

        user_path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile p = snapshot.getValue(Profile.class);
                saltvalue[0] = p.Saltvalue;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firsttext = first.getText().toString();
                String secondtext = second.getText().toString();
                Log.d("first",firsttext);
                Log.d("second",secondtext);
                if(firsttext.equals(secondtext)){
                    if(firsttext != null){
                        if(firsttext.length() > 4){
                            user_path.child(user_id_unique).child("hashespassword")
                                    .setValue(Profile.HashPassword(saltvalue[0],firsttext));
                            Toast.makeText(change_password.this, "Password changed", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        Toast.makeText(change_password.this, "Password too short", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(change_password.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(change_password.this, "Passwords are not the same", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}