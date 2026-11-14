package com.rmsftc.utils.telemetry.selection;

import com.rmsftc.utils.telemetry.Logger;

import java.util.ArrayDeque;
import java.util.Arrays;

public class Prompter {
    ArrayDeque<Question> questions;
    Logger logger;

    public Prompter(Logger logger, Question... questions) {
        this.logger = logger;
        this.questions = new ArrayDeque<>();
        this.questions.addAll(Arrays.asList(questions));
    }


    public Prompter prompt(Question question) {
        questions.add(question);
        return this;
    }
}
