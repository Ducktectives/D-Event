package sg.edu.np.mad.devent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class profile_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Profile p = new Profile("TestName","TestDesc",true);
        // Idk if im actually supposed to make the profile class or if we even need one

        // Getting all the views as variables
        TextView EditDesc = findViewById(R.id.EditDesc);
        TextView UserDesc = findViewById(R.id.UserDescription);
        TextView UserName = findViewById(R.id.username);

        // Set default texts
        UserDesc.setText(p.Description);
        UserName.setText(p.Username);

        // OnClickListener to start the edit description activity
        EditDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(profile_page.this,EditDescription.class);
                startActivity(edit);
            }
        });

        // Get new Description
        // Don't change if there is none set.
        Intent fromEdit = getIntent();
        Bundle newDesc = fromEdit.getExtras();
        if(newDesc != null){
            String setNewDesc = (String) newDesc.get("new");
            UserDesc.setText(setNewDesc);
            p.Description = setNewDesc;
        }



    }
}
