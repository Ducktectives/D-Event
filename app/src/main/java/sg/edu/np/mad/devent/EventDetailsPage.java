package sg.edu.np.mad.devent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class EventDetailsPage extends AppCompatActivity {
    ImageSlider imageSlider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

        imageSlider =findViewById(R.id.imageslider);

        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.a1,null));
        imageList.add(new SlideModel(R.drawable.a2,null));
        imageList.add(new SlideModel(R.drawable.a3,null));

        imageSlider.setImageList(imageList);
    }
}