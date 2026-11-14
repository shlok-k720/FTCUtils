package com.rmsftc.utils.telemetry;

import static com.rmsftc.utils.telemetry.Strings.*;

import java.util.Locale;


public interface DisplayFormat {
    Locale getLocale();
    String beforeQuestion();
    String afterQuestion();
    String afterAnswerChoicesAppear();
    String currentChoiceIndicator();
    String allOtherChoiceIndicator();

    DisplayFormat instance = new DisplayFormat() {
        @Override
        public Locale getLocale() {
            return Locale.US;
        }

        @Override
        public String beforeQuestion() {
            return empty();
        }

        @Override
        public String afterQuestion() {
            return empty();
        }

        @Override
        public String afterAnswerChoicesAppear() {
            return rowOfDashes();
        }

        @Override
        public String currentChoiceIndicator() {
            return "> ";
        }

        @Override
        public String allOtherChoiceIndicator() {
            return empty();
        }
    };


}
