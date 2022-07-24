package sg.edu.np.mad.devent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar progressbar = (ProgressBar)findViewById(R.id.progressBar);

        progressbar.setProgress(25);

        progressbar.setProgress(50);
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        String savedemail = sharedPreferences.getString("Email", "");
        String savedhashpassword = sharedPreferences.getString("Hahedpass", "");

        if (savedemail.equals("") || savedhashpassword.equals("")){
            progressbar.setProgress(75);
            Intent i1 = new Intent(SplashScreen.this, LoginActivity.class);
            i1.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
            progressbar.setProgress(100);
            startActivity(i1);
        }
        else {
            progressbar.setProgress(75);
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://dvent---ducktectives-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference Ref = database.getReference("Users");

            //Using get to get info from database once, rather than setting an event listener
            Ref.orderByChild("email").equalTo(savedemail.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        // Getting the values required to authenticate the user
                        Ref.child(savedemail.toLowerCase().replace(".", "")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data. Please reload.", task.getException());
                                }
                                else {
                                    Log.d("firebase", String.valueOf(task.getResult().child("username").getValue()));
                                    String username = task.getResult().child("username").getValue(String.class);
                                    String hashedpassword = task.getResult().child("hashedpassword").getValue(String.class);
                                    String email = task.getResult().child("email").getValue(String.class);
                                    String profileid = task.getResult().child("id").getValue(String.class);
                                    if (hashedpassword.equals(savedhashpassword) && email.equals(savedemail)){

                                        Intent login = new Intent(SplashScreen.this, NavDrawer.class);
                                        // Passing the Email and Username to the next activity for user
                                        login.putExtra("Email", email);
                                        login.putExtra("Username", username);
                                        login.putExtra("profile_id", profileid);
                                        login.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(login);
                                    }
                                }
                            }
                        });
                    }
                    else{
                        Intent i2 = new Intent(SplashScreen.this, LoginActivity.class);
                        i2.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        progressbar.setProgress(100);
                        startActivity(i2);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Intent i3 = new Intent(SplashScreen.this, LoginActivity.class);
                    i3.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    progressbar.setProgress(100);
                    startActivity(i3);
                }
            });
        }
    }
}