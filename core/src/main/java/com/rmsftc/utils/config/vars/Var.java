package com.rmsftc.utils.config.vars;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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
 * <p>Named variables (built with {@link #Var(String, Object, Validator[])})
 * carry a {@link Section} (dashboard category + name) and register
 * themselves in {@link Variables}. {@link #set(Object)} enforces the
 * mutability lock first, then the {@link Validator}s. Unnamed variables
 * have no section, no validators, and are not registered.
 *
 * <pre>
 * Var&lt;Double&gt; kP = new Var&lt;&gt;(0.1);
 * kP.set(0.15);
 * double gain = kP.get();
 *
 * Var&lt;Double&gt; maxSpeed = new Var&lt;&gt;("Max speed", 0.8, Validator.inRange(0.0, 1.0));
 * Modifier&lt;Double&gt; slow = maxSpeed.at(0.4); // temporary override, revertible
 * </pre>
 *
 * @param <T> the value type
 */
public class Var<T> implements VarProvider<T> {
    private T value;
    private final VarProvider<T> provider;
    private final Validation<T> validation;
    private boolean mutable;

    /** The dashboard section (category + name), or null when unnamed. */
    public final Section section;

    /** A variable holding {@code value} (null if not given). */
    public Var() {
        this((T) null);
    }

    /** A variable holding {@code value}. */
    public Var(T value) {
        this(null, value, null, Collections.<Validator<T>>emptyList());
    }

    /**
     * A variable delegating to {@code provider}: {@link #get()} and
     * {@link #set(Object)} read and write through it, so this variable is
     * another name for the same underlying value.
     */
    public Var(VarProvider<T> provider) {
        this(null, null, provider, Collections.<Validator<T>>emptyList());
    }

    /**
     * A named, field-backed variable. The name becomes the dashboard
     * section name (category {@link Section#VARIABLES_CATEGORY}), and the
     * variable registers itself in {@link Variables}. The initial value is
     * not validated; {@link #set(Object)} validates every later value
     * against {@code validators}.
     */
    @SafeVarargs
    public Var(String name, T value, Validator<T>... validators) {
        this(name, value, null, Arrays.asList(validators));
    }

    /**
     * A named, provider-backed variable: like
     * {@link #Var(String, Object, Validator[])} but reads and writes
     * through {@code provider}.
     */
    @SafeVarargs
    public Var(String name, VarProvider<T> provider, Validator<T>... validators) {
        this(name, null, provider, Arrays.asList(validators));
    }

    private Var(String name, T value, VarProvider<T> provider,
                Collection<Validator<T>> validators) {
        this.section = name == null ? null : new Section(Section.VARIABLES_CATEGORY, name, false);
        this.validation = new Validation<>(validators);
        this.value = value;
        this.provider = provider;
        this.mutable = true;
        if (name != null) {
            Variables.add(this);
        }
    }

    /** The name, or null when unnamed. */
    public String getName() {
        return section == null ? null : section.name;
    }

    @Override
    public T get() {
        return provider != null ? provider.get() : value;
    }

    @Override
    public void set(T value) {
        if (!mutable) {
            throw new IllegalStateException("Cannot modify an immutable Var");
        }
        validation.validate(value);
        if (provider != null) {
            provider.set(value);
        } else {
            this.value = value;
        }
    }

    /** Locks the variable: {@link #set(Object)} throws until {@link #makeMutable()}. */
    public void makeFinal() {
        this.mutable = false;
    }

    /** Unlocks the variable. */
    public void makeMutable() {
        this.mutable = true;
    }

    public boolean isMutable() {
        return mutable;
    }

    /**
     * A temporary override of this variable: {@link Modifier#apply()} sets
     * it to {@code value}, {@link Modifier#revert()} restores the value it
     * had when this was called. The modifier is registered in
     * {@link Variables}.
     */
    public Modifier<T> at(T value) {
        Modifier<T> modifier = new Modifier<>(this, value);
        Variables.addModifier(modifier);
        return modifier;
    }

    @Override
    public String toString() {
        return "Var(" + get() + ")";
    }
}
