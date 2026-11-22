package com.rmsftc.utils.telemetry.selection;

import com.rmsftc.utils.telemetry.DisplayFormat;
import com.rmsftc.utils.telemetry.Strings;

public class Choices {
    public AnswerChoice[] choices;

    private int currentSelection;


    public Choices(int currentSelection, AnswerChoice choice, AnswerChoice... otherChoices) {
        this.choices = new AnswerChoice[otherChoices.length + 1];
        this.choices[0] = choice;
        this.currentSelection = currentSelection;
        System.arraycopy(otherChoices, 0, this.choices, 1, otherChoices.length);
    }

    protected Choices(AnswerChoice[] choices, int currentSelection) {
        this.choices = choices;
        this.currentSelection = currentSelection;
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    public void select(int i) {
        this.currentSelection = i;
    }

    /** How many choices there are. */
    public int size() {
        return choices.length;
    }

    /** The currently selected choice. */
    public AnswerChoice getSelected() {
        return choices[currentSelection];
    }

    /** Renders the choices in the default plain-text format. */
    public String getString() {
        return getString(DisplayFormat.instance);
    }

    /**
     * Renders one choice per line: the current-selection indicator (or the
     * other-choice indicator) before each name, and the format's
     * after-choice string after it.
     */
    public String getString(DisplayFormat format) {
        StringBuilder builder = Strings.builder();
        for (int i = 0; i < choices.length; i++) {
            if (i > 0) {
                builder.append(Strings.newline());
            }
            boolean current = i == currentSelection;
            builder.append(current
                    ? format.currentChoiceIndicator()
                    : format.allOtherChoiceIndicator());
            builder.append(choices[i].name);
            builder.append(current ? format.afterCurrentChoice() : format.afterOtherChoice());
        }
        return builder.toString();
    }
}
