package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {


    Button btn_register;
    EditText userName, userEmail, userContact,
            userJob, userPassword,registration_userConfirmPassword;


    // Firebase for storing Image
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;


    /* Firebase Auth */
    private FirebaseAuth mAuth;
    /* Firebase Auth */

    /* Arthur edit */
    private ProgressBar progressBar;
    /* Arthur edit */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        /* Firebase Auth */
        mAuth = FirebaseAuth.getInstance();
        /* Firebase Auth */

        /* Arthur edit */
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        /* Arthur edit */


        // Dialog upon clicking Terms and Conditions
        TextView tcprompt = (TextView)findViewById(R.id.tcprompt);

        tcprompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);

                builder.setTitle("Terms and Condition");
                builder.setMessage("Last updated: June 19, 2022\n" +
                        "\n" +
                        "Please read these terms and conditions carefully before using Our Service.\n" +
                        "\n" +
                        "Interpretation and Definitions\n" +
                        "Interpretation\n" +
                        "The words of which the initial letter is capitalized have meanings defined under the following conditions. The following definitions shall have the same meaning regardless of whether they appear in singular or in plural.\n" +
                        "\n" +
                        "Definitions\n" +
                        "For the purposes of these Terms and Conditions:\n" +
                        "\n" +
                        "Application means the software program provided by the Company downloaded by You on any electronic device, named D'Vent\n" +
                        "\n" +
                        "Application Store means the digital distribution service operated and developed by Apple Inc. (Apple App Store) or Google Inc. (Google Play Store) in which the Application has been downloaded.\n" +
                        "\n" +
                        "Affiliate means an entity that controls, is controlled by or is under common control with a party, where \"control\" means ownership of 50% or more of the shares, equity interest or other securities entitled to vote for election of directors or other managing authority.\n" +
                        "\n" +
                        "Country refers to: Singapore\n" +
                        "\n" +
                        "Company (referred to as either \"the Company\", \"We\", \"Us\" or \"Our\" in this Agreement) refers to Ducktectives, 535 Clementi Rd, Singapore 599489.\n" +
                        "\n" +
                        "Device means any device that can access the Service such as a computer, a cellphone or a digital tablet.\n" +
                        "\n" +
                        "Service refers to the Application.\n" +
                        "\n" +
                        "Terms and Conditions (also referred as \"Terms\") mean these Terms and Conditions that form the entire agreement between You and the Company regarding the use of the Service. This Terms and Conditions agreement has been created with the help of the TermsFeed Terms and Conditions Generator.\n" +
                        "\n" +
                        "Third-party Social Media Service means any services or content (including data, information, products or services) provided by a third-party that may be displayed, included or made available by the Service.\n" +
                        "\n" +
                        "You means the individual accessing or using the Service, or the company, or other legal entity on behalf of which such individual is accessing or using the Service, as applicable.\n" +
                        "\n" +
                        "Acknowledgment\n" +
                        "These are the Terms and Conditions governing the use of this Service and the agreement that operates between You and the Company. These Terms and Conditions set out the rights and obligations of all users regarding the use of the Service.\n" +
                        "\n" +
                        "Your access to and use of the Service is conditioned on Your acceptance of and compliance with these Terms and Conditions. These Terms and Conditions apply to all visitors, users and others who access or use the Service.\n" +
                        "\n" +
                        "By accessing or using the Service You agree to be bound by these Terms and Conditions. If You disagree with any part of these Terms and Conditions then You may not access the Service.\n" +
                        "\n" +
                        "Your access to and use of the Service is also conditioned on Your acceptance of and compliance with the Privacy Policy of the Company. Our Privacy Policy describes Our policies and procedures on the collection, use and disclosure of Your personal information when You use the Application or the Website and tells You about Your privacy rights and how the law protects You. Please read Our Privacy Policy carefully before using Our Service.\n" +
                        "\n" +
                        "Links to Other Websites\n" +
                        "Our Service may contain links to third-party web sites or services that are not owned or controlled by the Company.\n" +
                        "\n" +
                        "The Company has no control over, and assumes no responsibility for, the content, privacy policies, or practices of any third party web sites or services. You further acknowledge and agree that the Company shall not be responsible or liable, directly or indirectly, for any damage or loss caused or alleged to be caused by or in connection with the use of or reliance on any such content, goods or services available on or through any such web sites or services.\n" +
                        "\n" +
                        "We strongly advise You to read the terms and conditions and privacy policies of any third-party web sites or services that You visit.\n" +
                        "\n" +
                        "Termination\n" +
                        "We may terminate or suspend Your access immediately, without prior notice or liability, for any reason whatsoever, including without limitation if You breach these Terms and Conditions.\n" +
                        "\n" +
                        "Upon termination, Your right to use the Service will cease immediately.\n" +
                        "\n" +
                        "Limitation of Liability\n" +
                        "Notwithstanding any damages that You might incur, the entire liability of the Company and any of its suppliers under any provision of this Terms and Your exclusive remedy for all of the foregoing shall be limited to the amount actually paid by You through the Service or 100 USD if You haven't purchased anything through the Service.\n" +
                        "\n" +
                        "To the maximum extent permitted by applicable law, in no event shall the Company or its suppliers be liable for any special, incidental, indirect, or consequential damages whatsoever (including, but not limited to, damages for loss of profits, loss of data or other information, for business interruption, for personal injury, loss of privacy arising out of or in any way related to the use of or inability to use the Service, third-party software and/or third-party hardware used with the Service, or otherwise in connection with any provision of this Terms), even if the Company or any supplier has been advised of the possibility of such damages and even if the remedy fails of its essential purpose.\n" +
                        "\n" +
                        "Some states do not allow the exclusion of implied warranties or limitation of liability for incidental or consequential damages, which means that some of the above limitations may not apply. In these states, each party's liability will be limited to the greatest extent permitted by law.\n" +
                        "\n" +
                        "\"AS IS\" and \"AS AVAILABLE\" Disclaimer\n" +
                        "The Service is provided to You \"AS IS\" and \"AS AVAILABLE\" and with all faults and defects without warranty of any kind. To the maximum extent permitted under applicable law, the Company, on its own behalf and on behalf of its Affiliates and its and their respective licensors and service providers, expressly disclaims all warranties, whether express, implied, statutory or otherwise, with respect to the Service, including all implied warranties of merchantability, fitness for a particular purpose, title and non-infringement, and warranties that may arise out of course of dealing, course of performance, usage or trade practice. Without limitation to the foregoing, the Company provides no warranty or undertaking, and makes no representation of any kind that the Service will meet Your requirements, achieve any intended results, be compatible or work with any other software, applications, systems or services, operate without interruption, meet any performance or reliability standards or be error free or that any errors or defects can or will be corrected.\n" +
                        "\n" +
                        "Without limiting the foregoing, neither the Company nor any of the company's provider makes any representation or warranty of any kind, express or implied: (i) as to the operation or availability of the Service, or the information, content, and materials or products included thereon; (ii) that the Service will be uninterrupted or error-free; (iii) as to the accuracy, reliability, or currency of any information or content provided through the Service; or (iv) that the Service, its servers, the content, or e-mails sent from or on behalf of the Company are free of viruses, scripts, trojan horses, worms, malware, timebombs or other harmful components.\n" +
                        "\n" +
                        "Some jurisdictions do not allow the exclusion of certain types of warranties or limitations on applicable statutory rights of a consumer, so some or all of the above exclusions and limitations may not apply to You. But in such a case the exclusions and limitations set forth in this section shall be applied to the greatest extent enforceable under applicable law.\n" +
                        "\n" +
                        "Governing Law\n" +
                        "The laws of the Country, excluding its conflicts of law rules, shall govern this Terms and Your use of the Service. Your use of the Application may also be subject to other local, state, national, or international laws.\n" +
                        "\n" +
                        "Disputes Resolution\n" +
                        "If You have any concern or dispute about the Service, You agree to first try to resolve the dispute informally by contacting the Company.\n" +
                        "\n" +
                        "Severability and Waiver\n" +
                        "Severability\n" +
                        "If any provision of these Terms is held to be unenforceable or invalid, such provision will be changed and interpreted to accomplish the objectives of such provision to the greatest extent possible under applicable law and the remaining provisions will continue in full force and effect.\n" +
                        "\n" +
                        "Waiver\n" +
                        "Except as provided herein, the failure to exercise a right or to require performance of an obligation under these Terms shall not effect a party's ability to exercise such right or require such performance at any time thereafter nor shall the waiver of a breach constitute a waiver of any subsequent breach.\n" +
                        "\n" +
                        "Translation Interpretation\n" +
                        "These Terms and Conditions may have been translated if We have made them available to You on our Service. You agree that the original English text shall prevail in the case of a dispute.\n" +
                        "\n" +
                        "Changes to These Terms and Conditions\n" +
                        "We reserve the right, at Our sole discretion, to modify or replace these Terms at any time. If a revision is material We will make reasonable efforts to provide at least 30 days' notice prior to any new terms taking effect. What constitutes a material change will be determined at Our sole discretion.\n" +
                        "\n" +
                        "By continuing to access or use Our Service after those revisions become effective, You agree to be bound by the revised terms. If You do not agree to the new terms, in whole or in part, please stop using the website and the Service.\n" +
                        "\n" +
                        "Contact Us\n" +
                        "If you have any questions about these Terms and Conditions, You can contact us:groupprojectmail2024@gmail.com");
                builder.setNegativeButton("Close", null);

                builder.show();
            }
        });


        // Assigning the fields to a variable
        btn_register = findViewById(R.id.registration_button);

        EditText userName = (EditText)findViewById(R.id.registration_userName);
        EditText userEmail = (EditText)findViewById(R.id.registration_userEmail);
        EditText userContact = (EditText)findViewById(R.id.registration_userContact);
        EditText jobtitle = (EditText)findViewById(R.id.registration_userJob);
        EditText userPassword = (EditText)findViewById(R.id.registration_userPassword);
        EditText confirmpassword = (EditText)findViewById(R.id.registration_confirmuserpassword);
        CheckBox checkboxvalue = (CheckBox)findViewById(R.id.Termsandcondition);

        TextView errormessage = (TextView)findViewById(R.id.errormessage);


        // Getting Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");


        //Attempting to register for an account
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an object of Firebase Database Reference
                DatabaseReference reference ;
                reference = database.getReference();

                // Assigning user assigned variables
                Integer contact;
                String name  = userName.getText().toString();
                String email  = userEmail.getText().toString().toLowerCase();
                try {
                    contact = Integer.parseInt(userContact.getText().toString());
                }
                catch (Exception e){
                    contact = null;
                }
                String job  = jobtitle.getText().toString();
                String password  = userPassword.getText().toString();
                String cpass  = confirmpassword.getText().toString();
                String contacts = userContact.getText().toString().trim();

                String emailPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+[a-zA-Z0-9.-]+[a-zA-Z0-9.-]+[a-zA-Z0-9.-]";
                String namePattern = "^[a-zA-Z- ]{3,30}";
                String passwordPattern = "^[a-zA-Z0-9+_.-@!#$%^&* ]{8,20}";
                String jobTitlePattern = "^[a-zA-Z0-9 -]{3,30}";

                // Input validation to check if the values are empty
                if (name.isEmpty()){
                    errormessage.setError("Name is required");
                    return;
                }

                else if (!name.matches(namePattern)){
                    errormessage.setText("Kindly enter a valid name");
                }

                else if (email.isEmpty()){
                    errormessage.setText("Email is required");
                }

                else if (!email.trim().matches(emailPattern)){
                    errormessage.setText("Kindly enter a valid email");
                }

                else if (contact == null){
                    errormessage.setText("Contact number is required");
                }

                else if (!((contact < 100000000 && contact >= 80000000) || (contact >= 60000000 && contact < 70000000))) {
                    errormessage.setText("Kindly enter a valid contact");
                }

                else if (job.isEmpty()) {
                    errormessage.setText("Job Title is required");
                }

                else if (!job.matches(jobTitlePattern)) {
                    errormessage.setText("Invalid Job Title");
                }

                else if (password.isEmpty()) {
                    errormessage.setText("Password is required");
                }

                else if (password.length() < 8){
                    errormessage.setText("The password need to be at least 8 characters long");
                }

                else if (!password.equals(cpass)) {
                    errormessage.setText("The password fields do not match");
                }

                else if (!checkboxvalue.isChecked()) {
                    errormessage.setText("Please agree to our terms and conditions");
                }
                else {
                    //progressBar.setVisibility(View.VISIBLE);
                    //Toast.makeText(RegistrationActivity.this, "Email " + email, Toast.LENGTH_SHORT).show();

                    Integer finalContact = contact;
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // String username, String title, String email, int contactnum, int eventsattended, String profilePicReference
                                        Profile profile = new Profile(name, job, email, finalContact, 0, "");
                                        firebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(RegistrationActivity.this, "User has been registered successfully!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(RegistrationActivity.this, NavDrawer.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                            startActivity(intent);

                                                        } else {
                                                            Toast.makeText(RegistrationActivity.this, "Failed to register! Try again!", Toast.LENGTH_SHORT).show();

                                                        }
                                                        //progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Failed to register!", Toast.LENGTH_SHORT).show();
                                        //progressBar.setVisibility(View.GONE);

                                    }
                                }
                            });
                }



//                    reference.child("Users").orderByChild("email").equalTo(email.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                errormessage.setText("Email already exist, please try again");
//                            }
//                            else {
//                                // Create user account
//                                // Profile(int id, String username, String title, String email,Integer contact, String password)
//                                user = new Profile(name, job, email, finalContact, password);
//
//                                // Insert the user-defined object to the database
//                                reference.child("Users").child(email.toLowerCase().replace(".","")).setValue(user);
//
//                                // Saving account details to users device
//                                SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
//                                //save data of User Name and hashed password
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString("Email", email);
//                                editor.putString("Hahedpass", user.getHashedpassword());
//                                editor.apply();
//
//                                // Let user know that account creation is successful
//                                Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_LONG).show();
//
//                                // Send the profileID, email and name of user to the profile_page class
//                                Intent intent = new Intent(getApplicationContext(), NavDrawer.class);
//                                intent.putExtra("profile_id", profileID);
//                                intent.putExtra("Email", email);
//                                intent.putExtra("Username", name);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                // Start the intent
//                                startActivity(intent);
//                            }
//                        }

//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            // Let users know that account creation is unsuccessful
//                            Toast.makeText(getApplicationContext(), "Account Creation Failed", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
            }
        });
    }
}
