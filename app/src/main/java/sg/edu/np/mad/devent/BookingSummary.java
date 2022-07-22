package sg.edu.np.mad.devent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BookingSummary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);

        TextView EventName = (TextView)findViewById(R.id.BookingConfirmationEvent);
        TextView Name = (TextView)findViewById(R.id.ConfirmationEnteredName);
        TextView UserEmail = (TextView)findViewById(R.id.ConfirmationEnteredEmail);
        TextView ContactNum = (TextView)findViewById(R.id.ConfirmationEnteredContact);
        TextView NumofTix = (TextView)findViewById(R.id.ConfirmationEnteredTickets);

        Button Close = (Button)findViewById(R.id.ConfirmationClose);

        Intent autofill = getIntent();

        String Email = autofill.getStringExtra("Email");
        EventName.setText(autofill.getStringExtra("EventName"));
        Name.setText(autofill.getStringExtra("Name"));
        UserEmail.setText(autofill.getStringExtra("UserEmail"));
        ContactNum.setText(autofill.getStringExtra("ContactNum"));
        NumofTix.setText(autofill.getStringExtra("NumofTix"));

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(BookingSummary.this, profile_page.class);
                i2.putExtra("Email", Email);
                finishAndRemoveTask();
                startActivity(i2);
            }
        });
    }
}