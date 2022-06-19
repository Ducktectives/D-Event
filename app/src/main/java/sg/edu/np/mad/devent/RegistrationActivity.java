package sg.edu.np.mad.devent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

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
        EditText jobtitle = (EditText)findViewById(R.id.registration_userJob);
        EditText userPassword = (EditText)findViewById(R.id.registration_userPassword);
        EditText confirmpassword = (EditText)findViewById(R.id.registration_confirmuserpassword);
        CheckBox checkboxvalue = (CheckBox)findViewById(R.id.Termsandcondition);

        TextView errormessage = (TextView)findViewById(R.id.errormessage);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an object of Firebase Database Reference
                DatabaseReference reference ;
                reference = database.getReference();

                Integer contact;
                String profileID = UUID.randomUUID().toString();
                String name  = userName.getText().toString();
                String email  = userEmail.getText().toString();
                try {
                    contact = Integer.parseInt(userContact.getText().toString());
                }
                catch (Exception e){
                    contact = null;
                }
                String job  = jobtitle.getText().toString();
                String password  = userPassword.getText().toString();
                String cpass  = confirmpassword.getText().toString();


                if (name.isEmpty() || email.isEmpty() || contact == null || job.isEmpty() || password.isEmpty()){
                    errormessage.setText("Please enter a input for all the fields above");
                }
                else if (!password.equals(cpass)){
                    errormessage.setText("The password fields do not match");
                }
                else if (!checkboxvalue.isChecked()){
                    errormessage.setText("Please agree to our terms and conditions");
                }
                else {
                    //Profile(int id, String username, String title, String email,Integer contact, String password)
                    user = new Profile(profileID, name, job, email, contact, password);

                    // Insert the user-defined object to the database
                    reference.child("Users").setValue(user);

                    // Afterward, I would like to send ID of user to the EventFormActivity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("profile_id", profileID);

                    // Start the intent
                    startActivity(intent);
                }
            }
        });
    }
}