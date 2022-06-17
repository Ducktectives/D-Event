package sg.edu.np.mad.devent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {
    // Create a user-defined object
    Profile user = new Profile();

    Button btn_register;
    EditText userName, userEmail, userContact,
            userJob, userPassword,registration_userConfirmPassword;

    // Firebase for storing Image
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btn_register = findViewById(R.id.registration_button);

        EditText userName = (EditText)findViewById(R.id.registration_userName);
        EditText userEmail = (EditText)findViewById(R.id.registration_userEmail);
        EditText userContact = (EditText)findViewById(R.id.registration_userContact);
        EditText userPassword = (EditText)findViewById(R.id.registration_userPassword);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an object of Firebase Database Reference
                DatabaseReference reference ;
                reference = database.getReference();


                String profileID = UUID.randomUUID().toString();
                String name  = userName.getText().toString();
                String email  = userEmail.getText().toString();
                Integer contact  = Integer.parseInt(userContact.getText().toString());
                String password  = userPassword.getText().toString();

                //Profile(int id, String username, String title, String email,Integer contact, String password)
                user = new Profile(profileID, name, null, email, contact, password);

                // Insert the user-defined object to the database
                reference.child("Users").setValue(user);

                // Afterward, I would like to send ID of user to the EventFormActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("profile_id", profileID);

                // Start the intent
                startActivity(intent);
            }
        });

    }
}