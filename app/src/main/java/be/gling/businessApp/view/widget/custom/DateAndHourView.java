package be.gling.businessApp.view.widget.custom;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.joda.time.LocalDateTime;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;

import be.gling.businessApp.R;
import be.gling.businessApp.view.widget.spinner.SingleSelectionSpinner;
import be.gling.businessApp.view.widget.spinner.Writable;
import be.gling.businessApp.view.widget.technical.InputField;

/**
 * Created by florian on 14/10/15.
 */
public class DateAndHourView extends LinearLayout implements InputField {

    private final ArrayList<DayToSelect> dayToSelectList = new ArrayList<DayToSelect>();
    private final ArrayList<HourToSelect> hourToSelectList = new ArrayList<HourToSelect>();
    private LocalDateTime startDate = LocalDateTime.now();
    private int maxDay = 30;


    public DateAndHourView(Context context) {
        this(context, null, 30, LocalDateTime.now());
    }

    public DateAndHourView(Context context, int maxDay, LocalDateTime startDate) {
        this(context, null, maxDay, startDate);
    }

    public DateAndHourView(Context context, AttributeSet attrs) {
        this(context, attrs, 30, LocalDateTime.now());
    }

    public DateAndHourView(Context context, AttributeSet attrs, int maxDay, LocalDateTime startDate) {
        super(context, attrs);

        this.maxDay = maxDay;
        this.startDate = startDate;

        View view = inflate(context, R.layout.date_and_time_picker, null);
        addView(view);
        initialization();
    }

    public void setMaximalDay(int maximalDay) {
        maxDay = maximalDay;
        initialization();
    }

    public void setStartDate(Date startDate) {
        this.startDate = LocalDateTime.fromDateFields(startDate);
        initialization();
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        initialization();
    }

    public void addChangeListener(final ChangeValue changeValue) {
        SingleSelectionSpinner<DayToSelect> daySelector = (SingleSelectionSpinner<DayToSelect>) findViewById(R.id.datePicker1);
        daySelector.setListener(new SingleSelectionSpinner.SingleSelectionSpinnerChangeListener<DayToSelect>() {
            @Override
            public void change(DayToSelect dayToSelect) {
                changeValue.changeValue(getValue());
            }
        });

        SingleSelectionSpinner<HourToSelect> hourSelector = (SingleSelectionSpinner<HourToSelect>) findViewById(R.id.timePicker1);
        hourSelector.setListener(new SingleSelectionSpinner.SingleSelectionSpinnerChangeListener<HourToSelect>() {
            @Override
            public void change(HourToSelect hourToSelect) {
                changeValue.changeValue(getValue());
            }
        });
    }

    private void initialization() {

        Date value = getValue();

        dayToSelectList.clear();
        hourToSelectList.clear();


        SingleSelectionSpinner<DayToSelect> daySelector = (SingleSelectionSpinner<DayToSelect>) findViewById(R.id.datePicker1);
        SingleSelectionSpinner<HourToSelect> hourSelector = (SingleSelectionSpinner<HourToSelect>) findViewById(R.id.timePicker1);


        //build date
        LocalDateTime now = startDate;
        now = now.withMillisOfDay(0);
        now = now.withHourOfDay(0);


        for (int i = 0; i < maxDay; i++) {
            dayToSelectList.add(new DayToSelect(now, now.getDayOfMonth() + "/" + now.getMonthOfYear()));
            now = now.plusDays(1);
        }

        daySelector.setItems(dayToSelectList);


        //build date
        for (int i = 0; i <= 24; i++) {
            hourToSelectList.add(new HourToSelect(i, i + ":00"));
        }

        hourSelector.setItems(hourToSelectList);

        if (value != null) {
            setValue(value);
        }

    }

    @Override
    public void setValue(Object value) {

        LocalDateTime localDateTime;

        if (value instanceof Date) {

            Date date = (Date) value;
            localDateTime = LocalDateTime.fromDateFields(date);
        } else if (value instanceof LocalDateTime) {
            localDateTime = (LocalDateTime) value;
        } else {
            return;
        }


        //hour
        SingleSelectionSpinner<HourToSelect> hourSelector = (SingleSelectionSpinner<HourToSelect>) findViewById(R.id.timePicker1);

        for (int i = 0; i < this.hourToSelectList.size(); i++) {
            HourToSelect hourToSelect = this.hourToSelectList.get(i);
            if (hourToSelect.getHour() == localDateTime.getHourOfDay()) {
                hourSelector.setSelection(i);
                break;
            }
        }

        //day
        localDateTime = localDateTime.withMillisOfDay(0);
        SingleSelectionSpinner<DayToSelect> daySelector = (SingleSelectionSpinner<DayToSelect>) findViewById(R.id.datePicker1);

        for (int i = 0; i < this.dayToSelectList.size(); i++) {
            DayToSelect dayToSelect = this.dayToSelectList.get(i);
            if (dayToSelect.getDate().equals(localDateTime)) {
                daySelector.setSelection(i);
                break;
            }
        }
    }

    public Date getValue() {
        return (Date) getValue(null);
    }

    @Override
    public Object getValue(Class<?> type) {

        SingleSelectionSpinner<DayToSelect> daySelector = (SingleSelectionSpinner<DayToSelect>) findViewById(R.id.datePicker1);
        SingleSelectionSpinner<HourToSelect> hourSelector = (SingleSelectionSpinner<HourToSelect>) findViewById(R.id.timePicker1);

        if (daySelector.getSelectedItem() != null && hourSelector.getSelectedItem() != null) {
            LocalDateTime date = daySelector.getSelectedItem().getDate();

            date = date.withHourOfDay(hourSelector.getSelectedItem().getHour());

            return date.toDate();
        }
        return null;
    }

    @Override
    public Integer control(Annotation[] declaredAnnotations) {
        return null;
    }

    @Override
    public void saveToInstanceState(String name, Bundle savedInstanceState) {
        Date value = getValue();
        if (value != null) {
            savedInstanceState.putLong(name, value.getTime());
        }
    }

    @Override
    public void restoreFromInstanceState(String name, Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(name)) {
            setValue(new Date(savedInstanceState.getLong(name)));
        }
    }

    public static class DayToSelect implements Writable {

        private LocalDateTime date;
        private String string;

        public DayToSelect(LocalDateTime date, String string) {
            this.date = date;
            this.string = string;
        }

        @Override
        public String getString() {
            return string;
        }

        public LocalDateTime getDate() {
            return date;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DayToSelect that = (DayToSelect) o;

            return date.equals(that.date);

        }

        @Override
        public int hashCode() {
            return date.hashCode();
        }
    }

    private class HourToSelect implements Writable {

        private int hour;
        private String string;

        public HourToSelect(int hour, String string) {
            this.hour = hour;
            this.string = string;
        }

        @Override
        public String getString() {
            return string;
        }

        public int getHour() {
            return hour;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HourToSelect that = (HourToSelect) o;

            return hour == that.hour;

        }

        @Override
        public int hashCode() {
            return hour;
        }
    }

    public interface ChangeValue {
        void changeValue(Date value);
    }

}
