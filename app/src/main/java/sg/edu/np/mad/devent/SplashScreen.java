package sg.edu.np.mad.devent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar progressbar = (ProgressBar)findViewById(R.id.progressBar);

        progressbar.setProgress(25);

        progressbar.setProgress(50);

        /*
        if (){
            progressbar.setProgress(75);
            Intent i1 = new Intent(SplashScreen.this, loginpage.class);
            progressbar.setProgress(100);
            startActivity(i1);
        }
        else {
            progressbar.setProgress(75);
            Intent i1 = new Intent(SplashScreen.this, loginpage.class);
            progressbar.setProgress(100);
            startActivity(i1);
        }
        */
    }
}