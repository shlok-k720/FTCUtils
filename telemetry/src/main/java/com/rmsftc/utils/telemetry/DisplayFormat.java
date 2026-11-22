package com.rmsftc.utils.telemetry;

import static com.rmsftc.utils.telemetry.Strings.*;

import java.util.Locale;

/**
 * How selection questions are rendered to text: the strings placed before
 * and after the question, between the question and its choices, and around
 * each choice. {@link #instance} is the plain-text format;
 * {@link #HTML} renders the same structure as HTML (for FTC Dashboard).
 */
public interface DisplayFormat {
    Locale getLocale();
    String beforeQuestion();
    String afterQuestion();
    String afterAnswerChoicesAppear();
    String currentChoiceIndicator();
    String allOtherChoiceIndicator();

    /** Placed after the current choice's name (e.g. a closing HTML tag). */
    default String afterCurrentChoice() {
        return empty();
    }

    /** Placed after every non-current choice's name. */
    default String afterOtherChoice() {
        return empty();
    }

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

    /** HTML rendering: line breaks, a rule under the question, bold selection. */
    DisplayFormat HTML = new DisplayFormat() {
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
            return "<br/>";
        }

        @Override
        public String afterAnswerChoicesAppear() {
            return "<hr/>";
        }

        @Override
        public String currentChoiceIndicator() {
            return "<b>";
        }

        @Override
        public String allOtherChoiceIndicator() {
            return empty();
        }

        @Override
        public String afterCurrentChoice() {
            return "</b>";
        }
    };
}
