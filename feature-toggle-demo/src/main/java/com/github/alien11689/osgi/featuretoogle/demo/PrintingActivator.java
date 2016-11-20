package com.github.alien11689.osgi.featuretoogle.demo;

import com.github.alien11689.osgi.featuretoogle.api.FeatureToggle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class PrintingActivator implements BundleActivator {
    @Override
    public void start(BundleContext bundleContext) throws Exception {

        Set<String> scopes = new LinkedHashSet<>();

        System.out.println("All features:");
        Collection<ServiceReference<FeatureToggle>> serviceReferences = bundleContext.getServiceReferences(FeatureToggle.class, null);
        serviceReferences.forEach(sr -> {
                FeatureToggle feature = bundleContext.getService(sr);
                printFeatureToggle(feature);
                scopes.addAll(feature.getScopes());
                bundleContext.ungetService(sr);
            }
        );

        scopes.forEach(scope -> {
            System.out.println();
            System.out.println(scope + " scoped features:");
            try {
                bundleContext.getServiceReferences(FeatureToggle.class, "(scopes=" + scope + ")").forEach(sr -> {
                        FeatureToggle feature = bundleContext.getService(sr);
                        printFeatureToggle(feature);
                        bundleContext.ungetService(sr);
                    }
                );
            } catch (InvalidSyntaxException e) {
                e.printStackTrace();
            }
        });

        Arrays.asList(true, false).forEach(enabled -> {
            System.out.println();
            System.out.println((enabled ? "Enabled" : "Disabled") + " features:");
            try {
                bundleContext.getServiceReferences(FeatureToggle.class, "(enabled=" + enabled + ")").forEach(sr -> {
                        FeatureToggle feature = bundleContext.getService(sr);
                        printFeatureToggle(feature);
                        bundleContext.ungetService(sr);
                    }
                );
            } catch (InvalidSyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private void printFeatureToggle(FeatureToggle feature) {
        System.out.println(String.format("Feature %s of scope %s is enabled: %s", feature.getName(), String.join(",", feature.getScopes()), feature.isEnabled()));
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
    }
}
