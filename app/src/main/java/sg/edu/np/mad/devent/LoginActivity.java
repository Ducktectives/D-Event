package sg.edu.np.mad.devent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText email = (EditText)findViewById(R.id.loginuseremail);
        EditText password = (EditText)findViewById(R.id.loginuserpassword);



    }
}