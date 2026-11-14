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

    public String getString() {
        return Strings.combine(); //TODO
    }
}

