package com.buzzware.nowapp.FieldChecker;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by Sajeel on 12/24/2018.
 */

public class FieldChecker {

    public static FieldChecker fieldChecker;

    public static FieldChecker GetFieldChecker()
    {
        if(fieldChecker == null)
        {
            fieldChecker= new FieldChecker();
        }
        return fieldChecker;
    }

    public boolean isValidEmail(EditText email) {
        if(!TextUtils.isEmpty(email.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
        {
            return true;
        }
        else
        return false;
    }
}
