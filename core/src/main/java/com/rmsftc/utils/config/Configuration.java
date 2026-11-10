package com.rmsftc.utils.config;

public abstract class Configuration<T extends IConfiguration<T>> implements IConfiguration<T> {
    protected Configurator<T> configurator;
    public Configuration(Configurator<T> configurator) {
        this.configurator = configurator;
        config();
    }

    protected Configuration<T> config() {
        return (Configuration<T>) config(configurator);
    }
}
