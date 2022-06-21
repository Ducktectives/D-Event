package sg.edu.np.mad.devent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class EditDescription extends AppCompatActivity {
    // Not used anymore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_description);


        // Get all view as variables
        Button done = findViewById(R.id.done);
        Button cancel = findViewById(R.id.cancel);
        TextView edits = findViewById(R.id.editDescPopup);

        // Confirm edit description
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDesc = edits.getText().toString();
                Intent setNewDesc = new Intent(EditDescription.this,profile_page.class);
                setNewDesc.putExtra("new",newDesc);
                startActivity(setNewDesc);
            }
        });

        // Exit edit description
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelNewDesc
                        = new Intent(EditDescription.this,profile_page.class);
                startActivity(cancelNewDesc);
            }
        });
    }
}