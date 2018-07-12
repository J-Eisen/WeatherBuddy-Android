package com.example.jonaheisenstock.weatherbuddy;

import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PreferencesActivity extends AppCompatActivity {

    /*--- Labels ---*/
    String[] settingsLabels = {"Maximum Temperature", "Minimum Temperature", "Rain", "Snow", "Precipitation"};
    String[] toggleLabelsMeasure = {"Imperial", "Metric"};
    String[] tempLabelText = {"ºF", "ºC", "K"};

/* ---Triggers for Buddy---
    0: maxTemp          [SLIDER]
    1: minTemp          [SLIDER]
    2: rain             [NUM PICKER]
    3: snow             [NUM PICKER]
    4: precipitation    [SLIDER]
 */

    float[] triggers = {70, 40, 40, 40, 60};

    /*  ---Mins/Maxes---
        0: maxTemp          [SLIDER]
        1: minTemp          [SLIDER]
        2: rain  ONES DIGIT [NUM PICKER]
        3: rain  DECIMAL    [NUM PICKER]
        4: snow  ONES DIGIT [NUM PICKER]
        5: snow  ONES DIGIT [NUM PICKER]
        6: precipitation    [SLIDER]
        ---------------------- */

    int[] mins = {0, 0, 0, 0, 0, 0, 0};
    int[] maxes = {100, 100, 9, 9, 9, 9, 100};

    // TempType (0: ºF, 1: ºC, 2: K)
    int tempType = 0; //TODO: Change this to allow input and a default if no input occurs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        loadUI();
    }

    /**
     * loadUI
     */

    protected void loadUI() {
        /* ---------------------------
         * Initialize the UI's Widgets
         ---------------------------*/

        // TEXTVIEWs

        TextView[] labels = {findViewById(R.id.maxTempLabel), findViewById(R.id.minTempLabel),
                findViewById(R.id.rainLabel), findViewById(R.id.snowLabel), findViewById(R.id.precipLabel)};
        TextView[] displays = {findViewById(R.id.maxTempDisplay), findViewById(R.id.minTempDisplay), findViewById(R.id.precipDisplay)};
        TextView[] tempLabels = {findViewById(R.id.textLabelF), findViewById(R.id.textLabelC), findViewById(R.id.textLabelK)};

    /* ---Legend---
     *
     *   ---seekBars[]---
     *   0: minTemp
     *   1: maxTemp
     *   2: precipitation
     *
     *  ---NumberPickers---
     *  0: Ones Place
     *  1: Decimal Place
     */

        //  WIDGETS (in order of appearance)
        SeekBar[] seekBars = {findViewById(R.id.maxTempSeekBar), findViewById(R.id.minTempSeekBar), findViewById(R.id.precipSeekBar)};
        NumberPicker[] rain = {findViewById(R.id.rainNumberPicker), findViewById(R.id.rainDecimalPicker)};
        NumberPicker[] snow = {findViewById(R.id.snowNumberPicker), findViewById(R.id.snowDecimalPicker)};
        ToggleButton measureSystem = findViewById(R.id.toggleMeasure);
        SeekBar tempSystem = findViewById(R.id.seekBarTempType);

        // Setting up the labels for the widgets
        for (int i = 0; i < settingsLabels.length; i++) {
            labels[i].setText(settingsLabels[i]);
        }

        // Setting up the seekBars
        for (SeekBar seekBar : seekBars) {
            for (int i = 0; i < seekBars.length; i++) {
                if (i == 2)
                    i = 6;
                seekBar.setMin(mins[i]);
                seekBar.setMax(maxes[i]);
                if (i == 6)
                    i = 4;
                seekBar.setProgress((int) triggers[i]);
            }
        }

        // Setting up the numberPickers
        for (int i = 0; i < rain.length; i++) {
            rain[i].setMaxValue(9);
            rain[i].setMinValue(0);
            rain[i].setWrapSelectorWheel(true);
            snow[i].setMaxValue(9);
            snow[i].setMinValue(0);
            snow[i].setWrapSelectorWheel(true);
            if (i == 0) {
                rain[i].setValue((int) triggers[2] % 1);
                snow[i].setValue((int) triggers[3] % 1);
            } else {
                rain[i].setValue((int) triggers[2]);
                snow[i].setValue((int) triggers[3]);
            }
        }

        // Setting Up Measurement System Toggle Button
        measureSystem.setTextOn("Imperial");
        measureSystem.setTextOff("Metric");

        // Setting Up Temperature Measurement Picking Seekbar
        tempSystem.setProgress(0);
        for (int i = 0; i < tempLabels.length; i++) {
            tempLabels[i].setText(tempLabelText[i]);
        }
        updateDisplay(triggers, displays, tempSystem);
        seekBarUpdate(seekBars, displays, tempSystem);
        updateTemp(tempSystem, seekBars, displays);
    }

    protected void seekBarUpdate(final SeekBar[] seekBars, final TextView[] displays, final SeekBar tempBar) {
        for (SeekBar seekBar : seekBars) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                    int id = seekBar.getId();

                    if (id == seekBars[0].getId()) {
                        if (value <= seekBars[1].getProgress()) {
                            seekBars[1].setProgress(value);
                            triggers[1] = value;
                            updateDisplay(1, value, displays, tempBar);
                        }
                        triggers[0] = value;
                        updateDisplay(0, value, displays, tempBar);
                    } else if (id == seekBars[1].getId()) {
                        if (value >= seekBars[0].getProgress()) {
                            seekBars[0].setProgress(value);
                            triggers[0] = value;
                            updateDisplay(0, value, displays, tempBar);
                        }
                        triggers[1] = value;
                        updateDisplay(1, value, displays, tempBar);
                    } else if (id == seekBars[2].getId()) {
                        triggers[4] = value;
                        updateDisplay(2, value, displays, tempBar);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    /*
    Update seekBar displays
     */

    // Update One
    private void updateDisplay(int i, float value, TextView[] displays, SeekBar tempBar) {
        if (i == 2)
            displays[i].setText((int) value + " %");
        else if (i == 0 || i == 1) {
            if (tempBar.getProgress() < 2)
                displays[i].setText((int) value + " º");
            else
                displays[i].setText((int) value);
        }
    }

    // Update All
    private void updateDisplay(float values[], TextView[] displays, SeekBar tempBar) {
        if (tempBar.getProgress() < 2) {
            displays[0].setText((int) values[0] + " º");
            displays[1].setText((int) values[1] + " º");
        } else {
            displays[0].setText((int) values[0]);
            displays[1].setText((int) values[1]);
        }
        displays[2].setText((int) values[4] + " %");
    }

    /*
    Change Temperature System
     */

    private void updateTemp(final SeekBar tempBar, final SeekBar[] seekBars, final TextView[] displays) {
        tempBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int t = seekBar.getProgress();
                switch (t) {
                    // TO: Fahrenheit
                    case 0:
                        // Adjusting Seekbars
                        for (int i = 0; i < 2; i++) {
                            // FROM: Celsius
                            if (tempType == 1) {
                                triggers[i] = ((int) ((double) (seekBars[i].getProgress()) * 1.8 + 32));
                            }
                            // FROM: Kelvin (unnecessary)
                            else if (tempType == 2) {
                                triggers[i] = ((int) (1.8 * ((double) (seekBars[i].getProgress() - 273) + 32)));
                            }
                            seekBars[i].setMax(100);
                            seekBars[i].setMin(-10);
                            seekBars[i].setProgress((int) triggers[i]);
                        }
                        break;
                    // TO: Celsius
                    case 1:
                        // Adjusting Seekbars
                        for (int i = 0; i < 2; i++) {
                            // FROM: Fahrenheit
                            if (tempType == 0) {
                                triggers[i] = ((int) (((double) seekBars[i].getProgress() - 32) * 5 / 9));
                            }
                            // FROM: Kelvin
                            else if (tempType == 2) {
                                triggers[i] = (seekBars[i].getProgress() - 273);
                            }
                            seekBars[i].setMax(40);
                            seekBars[i].setMin(-20);
                            seekBars[i].setProgress((int) triggers[i]);
                        }
                        break;
                    // TO: Kelvin
                    case 2:
                        //TODO: Fix from Celsius
                        // Adjusting Seekbars
                        for (int i = 0; i < 2; i++) {
                            // FROM: Fahrenheit (unnecessary)
                            if (tempType == 0) {
                                triggers[i] = ((int) (((double) seekBars[i].getProgress() - 32) * 5 / 9 + 273));
                            }
                            // FROM: Celsius
                            else if (tempType == 1) {
                                triggers[i] = (seekBars[i].getProgress() + 273);
                            }
                            seekBars[i].setMax(310);
                            seekBars[i].setMin(250);
                            seekBars[i].setProgress((int) triggers[i]);
                        }
                        break;
                    default:
                        System.out.println("Error: Invalid temperature bar input.");
                }
                tempType = t;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // Updating all the displays
        for (int i = 0; i < 2; i++) {
            updateDisplay(i, triggers[i], displays, tempBar);
        }
    }

    private void numPickerLoop(final NumberPicker[] numberPicker) {
        numberPicker[1].setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal == 9 && newVal == 0) {
                    numberPicker[0].setValue(numberPicker[0].getValue() + 1);
                }
                else if (oldVal == 0 && newVal == 9) {
                    numberPicker[0].setValue(numberPicker[0].getValue() - 1);
                }
            }
        });
    }
}