package sg.edu.np.mad.devent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText et_email;
    private Button btn_resetPassword;
    private ProgressBar progressBar;
    FirebaseAuth auth;
    TextView txt_onWardToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dementia);

        et_email = (EditText) findViewById(R.id.txt_email);
        btn_resetPassword = (Button) findViewById(R.id.btn_submit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt_onWardToLogin = (TextView)findViewById(R.id.loginactivity);

        auth = FirebaseAuth.getInstance();

        btn_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPasswordCauseIGotDementia();
            }
        });

        txt_onWardToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ayeAyeCaptain = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(ayeAyeCaptain);
            }
        });
    }

    private void resetPasswordCauseIGotDementia() {
        String email = et_email.getText().toString().trim();

        if (email.isEmpty()){
            et_email.setError("Email is required!");
            et_email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Email needs to be valid!!");
            et_email.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(ForgetPasswordActivity.this, email, Toast.LENGTH_SHORT).show();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgetPasswordActivity.this,"Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                    Intent loginActivity = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgetPasswordActivity.this,"Try Again! Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
