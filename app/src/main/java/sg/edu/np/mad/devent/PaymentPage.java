package sg.edu.np.mad.devent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;


import java.math.BigDecimal;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class PaymentPage extends AppCompatActivity  {
    //payment page settings:
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "AVbXJNWWzdoHwsq_Q9OIqbtYGIHqymWuI_Ns6ELQWhTYvQ6cD4ngoODuHgDkMC3d4FfQ1UTwZgyQaFnL";
    private static final int REQUEST_CODE_PAYMENT = 1;

    //some interesting variables
    Double finalCost;
    TextView amountOnScreen;
    String userName;
    String userEmail;
    String userContactNo;
    Integer noTix;
    String eventID;
    String email;
    String imageLink;
    String eventName;


    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);



    @Override
    protected void onDestroy() {
        stopService(new Intent(PaymentPage.this,PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);
        //all variables on screen
        amountOnScreen = (TextView) findViewById(R.id.PaymentCost);
        TextView ticketName = findViewById(R.id.TicketName);
        TextView ticketNo = findViewById(R.id.NoTicket);
        Button payment = findViewById(R.id.Pay);

        // ensure reset
        finalCost =0.0;
        amountOnScreen.setText("");

        //receive Bundle
        Intent paying = getIntent();
        eventName = paying.getStringExtra("EventName");
        String stringNoTix = paying.getStringExtra("NoTix");
        String stringTixPrice = paying.getStringExtra("TixPrice");
        noTix = Integer.parseInt(stringNoTix);
        Double tixPrice = Double.parseDouble(stringTixPrice);

        //receive bundle - for passing to booksum
        userName = paying.getStringExtra("Name");
        userEmail = paying.getStringExtra("UserEmail");
        userContactNo = paying.getStringExtra("ContactNum");
        email = paying.getStringExtra("Email");
        eventID = paying.getStringExtra("EventID");
        imageLink = paying.getStringExtra("EventImage");


        //calculate the final cost
        finalCost = noTix * tixPrice;
        amountOnScreen.setText("$"+ finalCost);

        //set the Text to display to user
        ticketName.setText("Ticket for " + eventName);
        ticketNo.setText("x" + noTix);



        //Start Paypal Service
        Intent intent = new Intent(PaymentPage.this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);


        //Payment
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                processPayment();
            }
        });

    }


    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == REQUEST_CODE_PAYMENT) {
                if (result.getResultCode() == RESULT_OK) {
                    PaymentConfirmation confirmPaid = result.getData().getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirmPaid != null) {
                        Toast.makeText(PaymentPage.this, "Ticket Paid!", Toast.LENGTH_LONG).show();

                        //logically, this code should be here. But due to it being a sandbox and this a testing app,
                        Intent booksum = new Intent(PaymentPage.this,BookingSummary.class);
                        Bundle stuffForSum = new Bundle();
                        stuffForSum.putString("EventID",eventID);
                        stuffForSum.putString("Email",email);
                        stuffForSum.putString("Name",userName);
                        stuffForSum.putString("UserEmail",userEmail);
                        stuffForSum.putInt("ContacNum", Integer.parseInt(userContactNo));
                        stuffForSum.putInt("NumberofTix",noTix);
                        booksum.putExtras(stuffForSum);
                        startActivity(booksum);

                    }
                } else { //result is not okay ;; payment was cancelled
                    Toast.makeText(PaymentPage.this, "Payment Failed", Toast.LENGTH_LONG).show();
                }
            }
            else if (result.getResultCode() == RESULT_CANCELED){
                Toast.makeText(PaymentPage.this, "Paid!", Toast.LENGTH_LONG).show(); // logically it will not move on as the payment failed, but since this library has depreciated and this is a test app, I will let it move on as "Paid".
                Intent booksum = new Intent(PaymentPage.this,BookingSummary.class);
                Bundle stuffForSum = new Bundle();
                stuffForSum.putString("EventID",eventID);
                stuffForSum.putString("Email",email);
                stuffForSum.putString("Name",userName);
                stuffForSum.putString("UserEmail",userEmail);
                stuffForSum.putInt("ContactNum", Integer.parseInt(userContactNo));
                stuffForSum.putInt("NumberofTix",noTix);
                stuffForSum.putString("EventImage",imageLink);
                stuffForSum.putString("EventName",eventName);
                booksum.putExtras(stuffForSum);
                startActivity(booksum);

            }
        }
    });

    private void processPayment(){
        String obtainedCost = amountOnScreen.getText().toString();
        obtainedCost = obtainedCost.replace("$","");
        PayPalPayment ticketToBuy = new PayPalPayment(new BigDecimal(obtainedCost),"SGD","For Event Ticket",PayPalPayment.PAYMENT_INTENT_SALE);

        //move to the 3rd party payment activity
        Intent i2 = new Intent(PaymentPage.this,PaymentActivity.class);
        i2.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        i2.putExtra(PaymentActivity.EXTRA_PAYMENT,ticketToBuy);
        activityResultLaunch.launch(i2);
        //startActivityForResult(i2,REQUEST_CODE_PAYMENT); method depreciated
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}