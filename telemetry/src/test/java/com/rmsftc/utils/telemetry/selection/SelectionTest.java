package com.rmsftc.utils.telemetry.selection;

import com.rmsftc.utils.telemetry.DisplayFormat;
import com.rmsftc.utils.telemetry.StringLogger;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectionTest {

    enum Mode { AUTO, TELEOP }

    @Test
    void choicesRenderWithIndicator() {
        Choices choices = new Choices(1, new AnswerChoice("Red"), new AnswerChoice("Blue"));
        assertEquals("Red\n> Blue", choices.getString());
        assertEquals(2, choices.size());
        assertEquals("Blue", choices.getSelected().name);
    }

    @Test
    void questionRendersNameDashesAndChoices() {
        Question question = new Question("Alliance?",
                new AnswerChoice("Red"), new AnswerChoice("Blue"));
        assertEquals("Alliance?\n--------\n> Red\nBlue", question.getString());
    }

    @Test
    void questionRendersAsHtml() {
        Question question = new Question("Alliance?",
                new AnswerChoice("Red"), new AnswerChoice("Blue"));
        assertEquals("Alliance?<br/>\n<hr/>\n<b>Red</b>\nBlue",
                question.getString(DisplayFormat.HTML));
    }

    @Test
    void prompterFlow() {
        StringLogger logger = new StringLogger();
        Prompter prompter = new Prompter(logger,
                new Question("Alliance?", new AnswerChoice("Red"), new AnswerChoice("Blue")),
                new Question("Park?", new AnswerChoice("Yes"), new AnswerChoice("No")));

        assertFalse(prompter.isFinished());
        assertEquals("Alliance?", prompter.current().name);

        prompter.highlight(1);
        assertEquals("Alliance?\n--------\nRed\n> Blue", prompter.getString());
        AnswerChoice first = prompter.confirm();
        assertEquals("Blue", first.name);

        assertEquals("Park?", prompter.current().name);
        AnswerChoice second = prompter.select(0);
        assertEquals("Yes", second.name);

        assertTrue(prompter.isFinished());
        assertNull(prompter.current());
        assertEquals("", prompter.getString());
        assertEquals(Arrays.asList("Blue", "Yes"),
                Arrays.asList(prompter.getAnswers().get(0).name, prompter.getAnswers().get(1).name));
        assertNull(prompter.select(0));
    }

    @Test
    void prompterLogsCurrentQuestion() {
        StringLogger logger = new StringLogger();
        Prompter prompter = new Prompter(logger,
                new Question("Alliance?", new AnswerChoice("Red"), new AnswerChoice("Blue")));
        prompter.log();
        assertEquals(1, logger.getLines().size());
        assertTrue(logger.getLines().get(0).contains("Alliance?"));
    }

    @Test
    void enumChoicesRenderEnumNames() {
        EnumChoices choices = new EnumChoices(Mode.class);
        assertEquals("> AUTO\nTELEOP", choices.getString());
    }
}
