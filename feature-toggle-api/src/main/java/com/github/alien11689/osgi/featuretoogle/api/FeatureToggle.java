package com.github.alien11689.osgi.featuretoogle.api;

import org.osgi.annotation.versioning.ProviderType;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@ProviderType
public interface FeatureToggle {

    String getName();

    List<String> getScopes();

    boolean isEnabled();

    <T> Optional<T> whenEnabled(Supplier<T> action);

    void whenEnabled(Runnable action);
}
