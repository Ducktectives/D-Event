package sg.edu.np.mad.devent;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Getting the fields
        EditText useremail = (EditText)findViewById(R.id.loginuseremail);
        EditText userpassword = (EditText)findViewById(R.id.loginuserpassword);
        TextView errormsg = (TextView)findViewById(R.id.loginerror);

        Button submittologin = (Button)findViewById(R.id.loginsubmit);
        TextView createuseraccount = (TextView)findViewById(R.id.registernewaccount);



        submittologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the text entered by users
                String email = useremail.getText().toString();
                String password = userpassword.getText().toString();
                String emailPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+[a-zA-Z0-9.-]+[a-zA-Z0-9.-]+[a-zA-Z0-9.-]";

                //For firebase
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference Ref = database.getReference("Users");

                //Using get to get info from database once, rather than setting an event listener
                Ref.orderByChild("email").equalTo(email.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            // Getting the values required to authenticate the user
                            Ref.child(email.toLowerCase().replace(".", "")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data. Please reload.", task.getException());
                                    }
                                    else {
                                        Log.d("firebase", String.valueOf(task.getResult().child("username").getValue()));
                                        String username = task.getResult().child("username").getValue(String.class);
                                        String hashpassword = task.getResult().child("hashedpassword").getValue(String.class);
                                        Integer saltvalue = task.getResult().child("saltvalue").getValue(Integer.class);
                                        if (hashpassword.equals(Profile.HashPassword(saltvalue, password))){
                                            // Saving account details to users device
                                            SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                                            //save data of User Name and hashed password
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("Email", email);
                                            editor.putString("Hahedpass", hashpassword);
                                            editor.apply();

                                            // Let user know that login is successful
                                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();

                                            Intent login = new Intent(LoginActivity.this, NavDrawer.class);
                                            // Passing the Email and Username to the next activity for user
                                            login.putExtra("Email", email);
                                            login.putExtra("Username", username);
                                            startActivity(login);
                                        }
                                        else if (!email.trim().matches(emailPattern)){
                                            errormsg.setText("Kindly enter a valid email");
                                        }
                                        else {
                                            // Giving a common user error when login failure
                                            errormsg.setText("Email or Password is invalid");
                                        }
                                    }
                                }
                            });
                        }
                        else {
                            // Giving a common user error when login failure
                            errormsg.setText("Email or Password is invalid");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        createuseraccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registeractivitystart = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(registeractivitystart);
            }
        });
    }
}