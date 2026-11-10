package com.rmsftc.ftcImpl;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.ValueProvider;
import com.acmerobotics.dashboard.config.VariableProvider;
import com.rmsftc.utils.config.vars.Modifier;
import com.rmsftc.utils.config.vars.Section;
import com.rmsftc.utils.config.vars.Var;
import com.rmsftc.utils.config.vars.Variables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * FTC Dashboard upload for the config vars system (FTChimera port).
 *
 * <p>This lives in the ftc module because the core module is pure JVM and
 * cannot reference the FTC Dashboard classes. The dashboard entries read
 * and write the live vars: changing a value on the dashboard calls
 * {@link Var#set(Object)} (so validation and the mutability lock still
 * apply).
 *
 * <p>Note: a modifier's dashboard entry is display-only — it shows the
 * {@code applied} boolean via a {@link VariableProvider}. Nothing wires a
 * dashboard toggle back to {@link Modifier#apply()} or
 * {@link Modifier#revert()}; those are called from code.
 */
public class DashboardVars {
    private static final Set<Var<?>> uploadedVars = new HashSet<>();
    private static final Set<Modifier<?>> uploadedModifiers = new HashSet<>();

    private DashboardVars() {}

    /**
     * Uploads a named var to the dashboard under its section (idempotent:
     * a second call does nothing). Throws
     * {@link IllegalArgumentException} for an unnamed var.
     */
    public static <T> void upload(Var<T> var) {
        if (uploadedVars.contains(var)) {
            return;
        }
        upload(var, requireSection(var));
        uploadedVars.add(var);
    }

    /** Uploads a var under an explicit section (not idempotent). */
    public static <T> void upload(Var<T> var, Section section) {
        FtcDashboard.getInstance().addConfigVariable(section.category, section.name,
                new ValueProvider<T>() {
                    @Override
                    public T get() {
                        return var.get();
                    }

                    @Override
                    public void set(T value) {
                        var.set(value);
                    }
                });
    }

    /**
     * Uploads a modifier to the dashboard (idempotent). The entry is named
     * "<var name> Modifier (val=..., revert=...)" and is recategorized to
     * {@link Section#MODIFIERS_CATEGORY} when the var is in
     * {@link Section#VARIABLES_CATEGORY}. Throws
     * {@link IllegalArgumentException} for a modifier of an unnamed var.
     */
    public static <T> void upload(Modifier<T> modifier) {
        if (uploadedModifiers.contains(modifier)) {
            return;
        }
        upload(modifier, modifierSection(modifier));
        uploadedModifiers.add(modifier);
    }

    /** Uploads a modifier under an explicit section (not idempotent). */
    public static <T> void upload(Modifier<T> modifier, Section section) {
        FtcDashboard.getInstance().addConfigVariable(section.category, section.name,
                new VariableProvider<>(modifier.applied()));
    }

    /** Uploads every registered var and modifier to the dashboard. */
    public static void registerAllToFtcDash() {
        registerAllVariablesToFtcDash();
        registerAllModifiersToFtcDash();
    }

    /** Uploads every registered var to the dashboard. */
    public static void registerAllVariablesToFtcDash() {
        for (Var<?> var : new ArrayList<>(Variables.variables)) {
            upload(var);
        }
    }

    /** Uploads every registered modifier to the dashboard. */
    public static void registerAllModifiersToFtcDash() {
        for (Modifier<?> modifier : new ArrayList<>(Variables.modifiers)) {
            upload(modifier);
        }
    }

    private static <T> Section requireSection(Var<T> var) {
        if (var.section == null) {
            throw new IllegalArgumentException("Only named vars can be uploaded to the dashboard");
        }
        return var.section;
    }

    private static <T> Section modifierSection(Modifier<T> modifier) {
        Var<T> var = modifier.getVar();
        if (var.section == null) {
            throw new IllegalArgumentException(
                    "Only modifiers of named vars can be uploaded to the dashboard");
        }
        Section section = new Section(var.section.category,
                var.section.name + " Modifier (val=" + modifier.getValue()
                        + ", revert=" + modifier.getOriginal() + ")", true);
        if (Objects.equals(var.section.category, Section.VARIABLES_CATEGORY)) {
            section.category = Section.MODIFIERS_CATEGORY;
        }
        return section;
    }
}
