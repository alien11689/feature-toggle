package com.github.alien11689.osgi.featuretoogle.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Hashtable;

public class FeatureActivator implements BundleActivator {

    private ServiceRegistration<ManagedServiceFactory> serviceRegistration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        FeatureToggleFactory featureToggleFactory = new FeatureToggleFactory(bundleContext);
        Dictionary<String, String> props = new Hashtable<>();
        props.put("service.pid", featureToggleFactory.getName());
        serviceRegistration = bundleContext.registerService(ManagedServiceFactory.class, featureToggleFactory, props);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }
    }
}
