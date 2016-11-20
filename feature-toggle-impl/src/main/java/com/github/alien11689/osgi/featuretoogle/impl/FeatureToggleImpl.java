package com.github.alien11689.osgi.featuretoogle.impl;

import com.github.alien11689.osgi.featuretoogle.api.FeatureToggle;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
class FeatureToggleImpl implements FeatureToggle {

    private final String name;

    private final List<String> scopes;

    private boolean enabled;

    @Override
    public <T> Optional<T> whenEnabled(Supplier<T> whenEnabled) {
        if (enabled) {
            return Optional.of(whenEnabled.get());
        }
        return Optional.empty();
    }

    @Override
    public void whenEnabled(Runnable action) {
        if (enabled) {
            action.run();
        }
    }
}
