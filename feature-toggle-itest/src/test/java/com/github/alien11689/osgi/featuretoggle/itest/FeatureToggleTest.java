package com.github.alien11689.osgi.featuretoggle.itest;

import com.github.alien11689.osgi.featuretoogle.api.FeatureToggle;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.osgi.metadata.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.InputStream;
import java.util.Collection;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class FeatureToggleTest {

    @ArquillianResource
    BundleContext bundleContext;

    @Deployment
    public static JavaArchive createDeployment() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar");
        archive.setManifest(new Asset() {
            public InputStream openStream() {
                OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
                builder.addBundleSymbolicName(archive.getName());
                builder.addBundleManifestVersion(2);
                builder.addImportPackages(Bundle.class, FeatureToggle.class, ConfigurationAdmin.class);
                return builder.openStream();
            }
        });
        return archive;
    }

    @Test
    public void shouldFindNoFeatureToggles() throws Exception {
        ServiceReference<ConfigurationAdmin> serviceReference = bundleContext.getServiceReference(ConfigurationAdmin.class);
        ConfigurationAdmin configAdmin = bundleContext.getService(serviceReference);
        Configuration t1 = configAdmin.createFactoryConfiguration("com.github.alien11689.osgi.featuretoogle.impl", null);
        Hashtable<String, Object> dictionary = new Hashtable<>();
        dictionary.put("name", "t1");
        dictionary.put("enabled", "true");
        t1.update(dictionary);

        Collection<ServiceReference<FeatureToggle>> serviceReferences = null;
        for (int i = 0; i < 10000; ++i) {
            serviceReferences = bundleContext.getServiceReferences(FeatureToggle.class, null);
            if (serviceReferences.size() == 0) {
                Thread.sleep(500);
            }
        }

        assertEquals(1, serviceReferences.size());
    }
}
