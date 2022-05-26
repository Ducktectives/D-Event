package sg.edu.np.mad.devent.ui.gallery;

import android.media.Image;
import android.widget.Button;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<ImageView> mImage;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mImage = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ImageView> getImage() {
        return mImage;
    }
}