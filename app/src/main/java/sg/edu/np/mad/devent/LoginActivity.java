package sg.edu.np.mad.devent;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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



        submittologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the text entered by users
                String email = useremail.getText().toString();
                String password = userpassword.getText().toString();


                //For firebase
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference Ref = database.getReference("Users");

                //Using get to get info from database once, rather than setting an event listener
                Ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            // Getting the values required to authenticate the user
                            String hashpassword = snapshot.child(email).child("hashpassword").getValue(String.class);
                            Integer saltvalue = Integer.parseInt(snapshot.child(email).child("SaltValue").getValue(String.class));
                            String username = snapshot.child(email).child("username").getValue(String.class);
                            // Checking if the user credentials if it matches the record in the database
                            if (Profile.HashPassword(saltvalue, password).equals(hashpassword)){
                                Intent login = new Intent(LoginActivity.this, NavDrawer.class);
                                // Passing the Email and Username to the next activity for user
                                login.putExtra("Email", email);
                                login.putExtra("Username", username);
                                startActivity(login);
                            }
                        }
                        else {
                            // Giving a common user error when login failure
                            errormsg.setText("Username or Password is invalid");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}