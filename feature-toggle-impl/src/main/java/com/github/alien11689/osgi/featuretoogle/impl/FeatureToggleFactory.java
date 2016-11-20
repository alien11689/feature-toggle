package com.github.alien11689.osgi.featuretoogle.impl;

import com.github.alien11689.osgi.featuretoogle.api.FeatureToggle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class FeatureToggleFactory implements ManagedServiceFactory {

    private final Map<String, ServiceRegistration<FeatureToggle>> pidsToFeatures = new ConcurrentHashMap<>();

    private final BundleContext bundleContext;

    FeatureToggleFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public String getName() {
        return "com.github.alien11689.osgi.featuretoogle.impl";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        deleted(pid);

        String name = (String) properties.get("name");

        if (invalidFeatureName(name)) {
            return;
        }

        boolean enabled = isFeatureEnabled(properties);
        List<String> scopes = getFeatureScopes(properties);

        FeatureToggle featureToggle = new FeatureToggleImpl(name, scopes, enabled);

        Dictionary<String, Object> serviceProperties = new Hashtable<>();
        serviceProperties.put("name", name);
        serviceProperties.put("scopes", scopes.toArray(new String[0]));
        serviceProperties.put("enabled", enabled);


        ServiceRegistration<FeatureToggle> featureToggleServiceRegistration = bundleContext.registerService(FeatureToggle.class, featureToggle, serviceProperties);

        pidsToFeatures.put(pid, featureToggleServiceRegistration);

    }

    private List<String> getFeatureScopes(Dictionary<String, ?> properties) {
        String scopeProperty = (String) properties.get("scopes");
        if (scopeProperty == null || scopeProperty.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(scopeProperty.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }

    private Boolean isFeatureEnabled(Dictionary<String, ?> properties) {
        return Boolean.valueOf((String) properties.get("enabled"));
    }

    private boolean invalidFeatureName(String name) {
        return name == null || name.trim().isEmpty();
    }

    @Override
    public void deleted(String pid) {
        ServiceRegistration<FeatureToggle> removed = pidsToFeatures.remove(pid);
        if (removed != null) {
            removed.unregister();
        }
    }

}
