package sg.edu.np.mad.devent;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Creating the Input Filter to ensure that the Ticket Price does not run past 2dp
class DecimalDigitsInputFilter implements InputFilter {
    private Pattern mPattern;
    DecimalDigitsInputFilter(int digitsBeforePoint,int digitsAfterPoint){
       mPattern = Pattern.compile("[0-9]{0," + (digitsBeforePoint- 1) + "}+((\\.[0-9]{0," + (digitsAfterPoint - 1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned toBeFiltered, int i2, int i3) {
        Matcher matchThePattern = mPattern.matcher(toBeFiltered);
        if (!matchThePattern.matches()){
            return "";
        }
        return null;
    }
}
