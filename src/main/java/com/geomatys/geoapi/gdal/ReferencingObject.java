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

import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;


/**
 * Base class of wrapper around GDAL referencing objects.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 1.0
 * @since   1.0
 */
abstract class ReferencingObject implements IdentifiedObject, ReferenceIdentifier {
    /**
     * Name of this referencing object.
     */
    private final String name;

    /**
     * For subclass constructors.
     *
     * @param  name  name of this referencing object.
     */
    ReferencingObject(final String name) {
        this.name = name;
    }

    @Override public final ReferenceIdentifier getName()  {return this;}
    @Override public final String              getCode()  {return name;}
    @Override public final String              toString() {return toWKT();}

    /** Unimplemented methods. */
    @Override public Collection<GenericName>  getAlias()       {return Collections.emptyList();}
    @Override public Set<ReferenceIdentifier> getIdentifiers() {return Collections.emptySet();}
    @Override public Citation                 getAuthority()   {return null;}
    @Override public String                   getCodeSpace()   {return null;}
    @Override public String                   getVersion()     {return null;}
    @Override public InternationalString      getRemarks()     {return null;}
}
