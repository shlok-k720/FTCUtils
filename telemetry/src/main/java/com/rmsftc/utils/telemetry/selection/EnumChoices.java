package com.rmsftc.utils.telemetry.selection;

import java.util.Arrays;

public class EnumChoices extends Choices {
    public EnumChoices(Class<? extends Enum<?>> choice) {
        super(Arrays.stream(choice.getEnumConstants())
                .map(Enum::name)
                .map(AnswerChoice::new)
                .toArray(AnswerChoice[]::new), 0);
    }
}
