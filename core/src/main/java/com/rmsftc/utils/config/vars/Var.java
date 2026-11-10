package com.rmsftc.utils.config.vars;

/**
 * A read/write variable: a value that can be read with {@link #get()} and
 * changed with {@link #set(Object)}. It is a {@link VarProvider}, so it can
 * be used anywhere a {@link ValGetter} or a {@link ValSetter} is expected.
 *
 * <p>A {@code Var} either owns its value ({@link #Var(Object)}) or delegates
 * to another provider ({@link #Var(VarProvider)}), for example a field on a
 * mechanism or a dashboard-tunable value. Use {@link Val} for a value that
 * should only be read.
 *
 * <pre>
 * Var&lt;Double&gt; kP = new Var&lt;&gt;(0.1);
 * kP.set(0.15);
 * double gain = kP.get();
 * </pre>
 *
 * @param <T> the value type
 */
public class Var<T> implements VarProvider<T> {
    private T value;
    private final VarProvider<T> provider;

    /** A variable holding {@code value} (null if not given). */
    public Var() {
        this((T) null);
    }

    /** A variable holding {@code value}. */
    public Var(T value) {
        this.value = value;
        this.provider = null;
    }

    /**
     * A variable delegating to {@code provider}: {@link #get()} and
     * {@link #set(Object)} read and write through it, so this variable is
     * another name for the same underlying value.
     */
    public Var(VarProvider<T> provider) {
        this.provider = provider;
    }

    @Override
    public T get() {
        return provider != null ? provider.get() : value;
    }

    @Override
    public void set(T value) {
        if (provider != null) {
            provider.set(value);
        } else {
            this.value = value;
        }
    }

    @Override
    public String toString() {
        return "Var(" + get() + ")";
    }
}
