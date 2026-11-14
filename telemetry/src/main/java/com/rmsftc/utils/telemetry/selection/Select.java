package com.rmsftc.utils.telemetry.selection;

import com.rmsftc.utils.telemetry.Logger;

public final class Select {

    public static Prompter prompt(Logger logger, Question question) {
        return new Prompter(logger).prompt(question);
    }

    public static AnswerChoice a(String name) {
        return new AnswerChoice(name);
    }

    public static Question q(String name, AnswerChoice defaultChoice, AnswerChoice... choices) {
        return new Question(name, defaultChoice, choices);
    }

    public static Question q(String name, Choices choices) {
        return new Question(name, choices);
    }

    public static Choices c(AnswerChoice choice, AnswerChoice... otherChoices) {
        return new Choices(0, choice, otherChoices);
    }

    public static Prompter p(Logger logger) {
        return new Prompter(logger);
    }
}
