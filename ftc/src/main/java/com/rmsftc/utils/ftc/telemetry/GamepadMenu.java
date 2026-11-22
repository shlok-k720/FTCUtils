package com.rmsftc.utils.ftc.telemetry;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.rmsftc.utils.telemetry.selection.AnswerChoice;
import com.rmsftc.utils.telemetry.selection.Prompter;
import com.rmsftc.utils.telemetry.selection.Question;

import java.util.List;
import java.util.Objects;

/**
 * A gamepad-driven menu over a {@link Prompter}: dpad up/down move the
 * selection highlight, {@code a} confirms the highlighted choice. Call
 * {@link #update()} once per loop; it renders the current question through
 * the prompter's logger (build the prompter with a {@link TelemetryLogger}
 * to show the menu on the driver station / dashboard).
 *
 * <p>Inputs are edge-detected, so holding a button moves once. The menu is
 * done when {@link #isFinished()} is true; read the picks with
 * {@link #getAnswers()}.
 */
public class GamepadMenu {
    private final Prompter prompter;
    private final Gamepad gamepad;

    private boolean lastUp;
    private boolean lastDown;
    private boolean lastConfirm;

    public GamepadMenu(Prompter prompter, Gamepad gamepad) {
        this.prompter = Objects.requireNonNull(prompter, "prompter");
        this.gamepad = Objects.requireNonNull(gamepad, "gamepad");
    }

    public void update() {
        if (prompter.isFinished()) {
            return;
        }
        Question question = prompter.current();
        int size = question.choices.size();
        int selection = question.choices.getCurrentSelection();

        boolean up = gamepad.dpad_up;
        boolean down = gamepad.dpad_down;
        boolean confirm = gamepad.a;

        if (up && !lastUp) {
            prompter.highlight((selection - 1 + size) % size);
        }
        if (down && !lastDown) {
            prompter.highlight((selection + 1) % size);
        }
        if (confirm && !lastConfirm) {
            prompter.confirm();
        }

        lastUp = up;
        lastDown = down;
        lastConfirm = confirm;

        prompter.log();
    }

    public boolean isFinished() {
        return prompter.isFinished();
    }

    public List<AnswerChoice> getAnswers() {
        return prompter.getAnswers();
    }
}
