package com.rmsftc.utils.config.vars;

/**
 * A temporary override of a {@link Var}: {@link #apply()} sets the var to
 * the override value, {@link #revert()} restores the value the var had
 * when this modifier was created. Ported from the FTChimera team's vars
 * system (without the dashboard upload, which lives in the ftc module's
 * {@code DashboardVars} because it needs the FTC Dashboard classes).
 *
 * <p>Both operations go through {@link Var#set(Object)}, so a locked var
 * or a value that fails validation still throws.
 */
public class Modifier<T> {
    volatile Var<T> var;
    T value;
    T original;
    private boolean applied;

    public Modifier(Var<T> variable, T value) {
        this.var = variable;
        this.value = value;
        this.original = var.get();
    }

    /** Sets the var to the override value. */
    public void apply() {
        this.var.set(value);
        applied = true;
    }

    /** Restores the value the var had when this modifier was created. */
    public void revert() {
        this.var.set(original);
        applied = false;
    }

    /** The value the var had when this modifier was created. */
    public T getOriginal() {
        return original;
    }

    /** The override value this modifier applies. */
    public T getValue() {
        return value;
    }

    public boolean applied() {
        return applied;
    }

    public Var<T> getVar() {
        return var;
    }
}
