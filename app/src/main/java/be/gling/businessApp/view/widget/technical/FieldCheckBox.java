package be.gling.businessApp.view.widget.technical;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.CheckBox;

import java.lang.annotation.Annotation;

/**
 * Created by florian on 15/10/15.
 */
public class FieldCheckBox extends CheckBox implements InputField {

    public FieldCheckBox(Context context) {
        this(context, null);
    }

    public FieldCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setValue(Object value) {
        if (value != null) {
            setChecked(Boolean.parseBoolean(value.toString()));
        }
    }

    @Override
    public Object getValue(Class<?> type) {
        return isChecked();
    }

    @Override
    public Integer control(Annotation[] declaredAnnotations) {
        return null;
    }

    @Override
    public void saveToInstanceState(String name, Bundle savedInstanceState) {
        savedInstanceState.putBoolean(name, this.isChecked());
    }

    @Override
    public void restoreFromInstanceState(String name, Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(name)) {
            this.setValue(savedInstanceState.getBoolean(name));
        }

    }
}
