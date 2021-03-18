/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 *
 *    The GDAL wrappers are provided as code examples, in the hope to facilitate
 *    GeoAPI implementations backed by other libraries. Implementers can take this
 *    source code and use it for any purpose, commercial or non-commercial, copyrighted
 *    or open-source, with no legal obligation to acknowledge the borrowing/copying
 *    in any way.
 */
package com.geomatys.geoapi.gdal;

import java.util.Iterator;
import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.MetadataScope;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.spatial.GridSpatialRepresentation;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.bridge.python.Environment;
import org.jpy.PyLib;
import org.jpy.PyModule;
import org.jpy.PyObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assume.*;
import static org.junit.Assert.*;


/**
 * Tests the binding using GDAL implementation as an arbitrary backend.
 * The tests require the {@code "jpy.config"} system property to be set
 * to the path of a {@code "jpyconfig.properties"} file, otherwise the
 * tests are skipped.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 4.0
 * @since   4.0
 */
public final strictfp class PythonTest {
    /**
     * Non-null if the Python interpreter is running.
     */
    private static File rootDirectory;

    /**
     * Starts the Python interpreter before any test is run.
     */
    @BeforeClass
    public static void startPython() {
        String config = System.getProperty("jpy.config");
        if (config != null && !config.trim().isEmpty()) {
            File root;
            try {
                root = new File(PythonTest.class.getResource("GDALTest.class").toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Test class is not a regular file.", e);
            }
            do {
                root = root.getParentFile();
                if (root == null) return;
            } while (!new File(root, "geoapi-java-python").isDirectory());
            if (!PyLib.isPythonRunning()) {
                PyLib.startPython(new File(root, "geoapi/src/main/python").getPath(),
                                  new File(root, "geoapi-gdal/src/main/python").getPath());
            }
            rootDirectory = root;
        }
    }

    /**
     * Stops the Python interpreter after all tests have been run, successfully or not.
     */
    @AfterClass
    public static void stopPython() {
        if (rootDirectory != null) {
            rootDirectory = null;
            PyLib.stopPython();
        }
    }

    /**
     * Loads metadata from the given file. This methods use JPY-specific methods for getting the root object.
     * Once we have this root, all remaining method calls use GeoAPI only.
     */
    private static Metadata load(final String datafile) {
        assumeNotNull("The \"jpy.config\" system property must be set to the path of a \"jpyconfig.properties\" file.", rootDirectory);
        final PyModule pm = PyModule.importModule("opengis.wrapper.gdal");
        final PyObject dataset = pm.call("DataSet", new File(rootDirectory, datafile).toString());
        final Environment env = new Environment();
        return env.toJava(dataset.callMethod("metadata"), Metadata.class);
    }

    /**
     * Test a case using geographic coordinate reference system.
     */
    @Test
    public void testGeographic() {
        final Metadata metadata = load("geoapi-conformance/src/main/resources/org/opengis/test/dataset/Cube2D_geographic_packed.nc");
        final MetadataScope scope = first(metadata.getMetadataScopes());
        assertEquals("metadataScope.resourceScope", ScopeCode.DATASET, scope.getResourceScope());

        final GridSpatialRepresentation representation = (GridSpatialRepresentation) first(metadata.getSpatialRepresentationInfo());
        final List<? extends Dimension> axes = representation.getAxisDimensionProperties();
        assertEquals("axisDimensionProperties[0].dimensionName", DimensionNameType.COLUMN, axes.get(0).getDimensionName());
        assertEquals("axisDimensionProperties[0].dimensionName", DimensionNameType.ROW,    axes.get(1).getDimensionName());
    }

    /**
     * Returns the first element if the given collection, or {@code null} if none.
     */
    private static <E> E first(final Iterable<E> collection) {
        if (collection != null) {
            Iterator<E> it = collection.iterator();
            if (it.hasNext()) return it.next();
        }
        return null;
    }
}
