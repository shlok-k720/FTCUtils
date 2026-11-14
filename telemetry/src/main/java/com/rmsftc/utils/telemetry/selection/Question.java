package com.rmsftc.utils.telemetry.selection;

import com.rmsftc.utils.telemetry.DisplayFormat;

public class Question {
    public final String name;
    public final Choices choices;

    public Question(String name, AnswerChoice defaultChoice, AnswerChoice... choices) {
        this.name = name;
        this.choices = new Choices(0, defaultChoice, choices);
    }

    public Question(String name, Choices choices) {
        this.name = name;
        this.choices = choices;
    }


    public String getString() {
        return DisplayFormat.instance.beforeQuestion();//TODO
    }

}
