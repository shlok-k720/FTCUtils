package com.rmsftc.utils.telemetry.selection;

import com.rmsftc.utils.telemetry.DisplayFormat;
import com.rmsftc.utils.telemetry.Strings;

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

    /** Renders the question in the default plain-text format. */
    public String getString() {
        return getString(DisplayFormat.instance);
    }

    /**
     * Renders the question and its choices:
     * <pre>
     * &lt;beforeQuestion&gt;name&lt;afterQuestion&gt;
     * &lt;afterAnswerChoicesAppear&gt;
     * &lt;choices, one per line&gt;
     * </pre>
     */
    public String getString(DisplayFormat format) {
        return Strings.combine(
                format.beforeQuestion(),
                name,
                format.afterQuestion(),
                Strings.newline(),
                format.afterAnswerChoicesAppear(),
                Strings.newline(),
                choices.getString(format));
    }
}
