package sg.edu.np.mad.devent;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class LinearLayoutManagerWrapper extends LinearLayoutManager {

    public LinearLayoutManagerWrapper(Context context) {
        super(context);
    }


//    @Override
//    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//
//        try {
//
//            super.onLayoutChildren(recycler, state);
//
//        } catch (IndexOutOfBoundsException e) {
//
//            Log.e("Layout", "Inconsistency detected");
//        }
//
//    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
