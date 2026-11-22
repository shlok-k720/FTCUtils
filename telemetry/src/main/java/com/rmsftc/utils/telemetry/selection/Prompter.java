package com.rmsftc.utils.telemetry.selection;

import com.rmsftc.utils.telemetry.DisplayFormat;
import com.rmsftc.utils.telemetry.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Runs a queue of {@link Question}s one at a time. Rendering is just
 * {@link #getString()} — the current question as text — and answering is
 * programmatic: {@link #highlight(int)} moves the selection,
 * {@link #select(int)} answers with a specific choice, and
 * {@link #confirm()} answers with the highlighted one. Each answer is
 * recorded (see {@link #getAnswers()}) and the next question becomes
 * current. A gamepad-driven menu lives in the ftc module on top of this.
 */
public class Prompter {
    ArrayDeque<Question> questions;
    Logger logger;

    private final List<AnswerChoice> answers = new ArrayList<>();
    private DisplayFormat displayFormat = DisplayFormat.instance;

    public Prompter(Logger logger, Question... questions) {
        this.logger = logger;
        this.questions = new ArrayDeque<>();
        this.questions.addAll(Arrays.asList(questions));
    }

    public Prompter prompt(Question question) {
        questions.add(question);
        return this;
    }

    /** The question waiting for an answer, or {@code null} when finished. */
    public Question current() {
        return questions.peek();
    }

    /** True when every question has been answered. */
    public boolean isFinished() {
        return questions.isEmpty();
    }

    /** Moves the current question's selection highlight (no answer yet). */
    public void highlight(int i) {
        Question question = current();
        if (question != null) {
            question.choices.select(i);
        }
    }

    /**
     * Answers the current question with choice {@code i}, records the
     * answer, and advances to the next question. Returns the chosen answer,
     * or {@code null} if there was no question left.
     */
    public AnswerChoice select(int i) {
        Question question = questions.poll();
        if (question == null) {
            return null;
        }
        question.choices.select(i);
        AnswerChoice answer = question.choices.getSelected();
        answers.add(answer);
        return answer;
    }

    /** Answers the current question with its highlighted choice. */
    public AnswerChoice confirm() {
        Question question = current();
        if (question == null) {
            return null;
        }
        return select(question.choices.getCurrentSelection());
    }

    /** Answers given so far, in question order. */
    public List<AnswerChoice> getAnswers() {
        return Collections.unmodifiableList(new ArrayList<>(answers));
    }

    /** The current question rendered as text, or {@code ""} when finished. */
    public String getString() {
        Question question = current();
        return question == null ? "" : question.getString(displayFormat);
    }

    /** Logs the current question through this prompter's logger. */
    public void log() {
        if (!isFinished()) {
            logger.log(getString());
        }
    }

    public DisplayFormat getDisplayFormat() {
        return displayFormat;
    }

    /** Sets the rendering format (e.g. {@link DisplayFormat#HTML}). */
    public Prompter setDisplayFormat(DisplayFormat displayFormat) {
        this.displayFormat = displayFormat;
        return this;
    }
}
