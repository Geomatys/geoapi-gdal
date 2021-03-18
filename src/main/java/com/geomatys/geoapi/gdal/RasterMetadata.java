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

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Collection;
import java.util.Collections;
import org.gdal.gdal.Dataset;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.opengis.metadata.ApplicationSchemaInformation;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.MetadataExtensionInformation;
import org.opengis.metadata.PortrayalCatalogueReference;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Series;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.content.CoverageContentType;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.RangeElementDescription;
import org.opengis.metadata.distribution.Distribution;
import org.opengis.metadata.distribution.Format;
import org.opengis.metadata.identification.AggregateInformation;
import org.opengis.metadata.identification.BrowseGraphic;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.identification.Progress;
import org.opengis.metadata.identification.Resolution;
import org.opengis.metadata.identification.Usage;
import org.opengis.metadata.maintenance.MaintenanceInformation;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.spatial.SpatialRepresentationType;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.util.InternationalString;
import org.opengis.util.RecordType;

import static java.util.Collections.emptySet;


/**
 * Metadata about a GDAL dataset for a raster, which is assumed two-dimensional.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 1.0
 * @since   1.0
 *
 * @see <a href="http://gdal.org/gdal_datamodel.html">GDAL data model</a>
 * @see <a href="http://gdal.org/java/org/gdal/gdal/Dataset.html">Java API for GDAL Dataset</a>
 */
final class RasterMetadata extends GridGeometry implements Metadata, DataIdentification, Citation, CoverageDescription {
    /**
     * The dataset name, or {@code null} if none.
     */
    private final String description;

    /**
     * Whether each point represents a cell, and area or a volume.
     */
    private final CellGeometry cellGeometry;

    /**
     * Fetches metadata from the given GDAL dataset.
     */
    RasterMetadata(final Dataset ds) throws IOException {
        super(ds);
        description  = trim(ds.GetDescription());
        String value = trim(ds.GetMetadataItem("AREA_OR_POINT"));
        if ("Point".equalsIgnoreCase(value)) {
            cellGeometry = CellGeometry.POINT;
        } else if ("Area".equalsIgnoreCase(value)) {
            cellGeometry = CellGeometry.AREA;
        } else {
            cellGeometry = null;
        }
    }

    /**
     * Trims the leading and trailing spaces in the given string and returns {@code null} if the result is empty.
     */
    private static String trim(String value) {
        return (value = value.trim()).isEmpty() ? null : value;
    }

    /* ISO 19115:2014 properties for which we provide information. */
    @Override public Collection<ScopeCode>             getHierarchyLevels()           {return Collections.singleton(ScopeCode.DATASET);}
    @Override public Collection<Identification>        getIdentificationInfo()        {return Collections.<Identification>singleton(this);}
    @Override public Collection<SpatialRepresentation> getSpatialRepresentationInfo() {return Collections.<SpatialRepresentation>singleton(this);}
    @Override public Collection<ContentInformation>    getContentInfo()               {return Collections.<ContentInformation>singleton(this);}
    @Override public Citation                          getCitation()                  {return this;}
    @Override public InternationalString               getTitle()                     {return new Literal(description);}
    @Override public CellGeometry                      getCellGeometry()              {return cellGeometry;}

    /* ISO 19115:2014 properties that are empty of null for now. */
    @Override public RecordType                 getAttributeDescription()    {return null;}
    @Override public InternationalString        getAbstract()                {return null;}
    @Override public Collection<TopicCategory>  getTopicCategories()         {return emptySet();}
    @Override public Collection<Extent>         getExtents()                 {return emptySet();}

    /** Optional properties. */
    @Override public String                                   getFileIdentifier()             {return null;}
    @Override public Locale                                   getLanguage()                   {return null;}
    @Override public CharacterSet                             getCharacterSet()               {return null;}
    @Override public String                                   getParentIdentifier()           {return null;}
    @Override public Collection<String>                       getHierarchyLevelNames()        {return Collections.emptyList();}
    @Override public Collection<ResponsibleParty>             getContacts()                   {return Collections.emptyList();}
    @Override public Date                                     getDateStamp()                  {return null;}
    @Override public String                                   getMetadataStandardName()       {return null;}
    @Override public String                                   getMetadataStandardVersion()    {return null;}
    @Override public String                                   getDataSetUri()                 {return null;}
    @Override public Collection<Locale>                       getLocales()                    {return Collections.emptyList();}
    @Override public Collection<ReferenceSystem>              getReferenceSystemInfo()        {return Collections.emptyList();}
    @Override public Collection<MetadataExtensionInformation> getMetadataExtensionInfo()      {return Collections.emptyList();}
    @Override public Distribution                             getDistributionInfo()           {return null;}
    @Override public Collection<DataQuality>                  getDataQualityInfo()            {return Collections.emptyList();}
    @Override public Collection<PortrayalCatalogueReference>  getPortrayalCatalogueInfo()     {return Collections.emptyList();}
    @Override public Collection<Constraints>                  getMetadataConstraints()        {return Collections.emptyList();}
    @Override public Collection<ApplicationSchemaInformation> getApplicationSchemaInfo()      {return Collections.emptyList();}
    @Override public MaintenanceInformation                   getMetadataMaintenance()        {return null;}
    @Override public Collection<AcquisitionInformation>       getAcquisitionInformation()     {return Collections.emptyList();}
    @Override public Collection<SpatialRepresentationType>    getSpatialRepresentationTypes() {return Collections.emptyList();}
    @Override public Collection<Resolution>                   getSpatialResolutions()         {return Collections.emptyList();}
    @Override public Collection<Locale>                       getLanguages()                  {return Collections.emptyList();}
    @Override public Collection<CharacterSet>                 getCharacterSets()              {return Collections.emptyList();}
    @Override public InternationalString                      getEnvironmentDescription()     {return null;}
    @Override public InternationalString                      getSupplementalInformation()    {return null;}
    @Override public InternationalString                      getPurpose()                    {return null;}
    @Override public Collection<String>                       getCredits()                    {return Collections.emptyList();}
    @Override public Collection<Progress>                     getStatus()                     {return Collections.emptyList();}
    @Override public Collection<ResponsibleParty>             getPointOfContacts()            {return Collections.emptyList();}
    @Override public Collection<MaintenanceInformation>       getResourceMaintenances()       {return Collections.emptyList();}
    @Override public Collection<BrowseGraphic>                getGraphicOverviews()           {return Collections.emptyList();}
    @Override public Collection<Format>                       getResourceFormats()            {return Collections.emptyList();}
    @Override public Collection<Keywords>                     getDescriptiveKeywords()        {return Collections.emptyList();}
    @Override public Collection<Usage>                        getResourceSpecificUsages()     {return Collections.emptyList();}
    @Override public Collection<Constraints>                  getResourceConstraints()        {return Collections.emptyList();}
    @Override public Collection<AggregateInformation>         getAggregationInfo()            {return Collections.emptyList();}
    @Override public Collection<InternationalString>          getAlternateTitles()            {return Collections.emptyList();}
    @Override public Collection<CitationDate>                 getDates()                      {return Collections.emptyList();}
    @Override public InternationalString                      getEdition()                    {return null;}
    @Override public Date                                     getEditionDate()                {return null;}
    @Override public Collection<Identifier>                   getIdentifiers()                {return Collections.emptyList();}
    @Override public Collection<ResponsibleParty>             getCitedResponsibleParties()    {return Collections.emptyList();}
    @Override public Collection<PresentationForm>             getPresentationForms()          {return Collections.emptyList();}
    @Override public Series                                   getSeries()                     {return null;}
    @Override public InternationalString                      getOtherCitationDetails()       {return null;}
    @Override public InternationalString                      getCollectiveTitle()            {return null;}
    @Override public String                                   getISBN()                       {return null;}
    @Override public String                                   getISSN()                       {return null;}
    @Override public CoverageContentType                      getContentType()                {return null;}
    @Override public Collection<RangeDimension>               getDimensions()                 {return Collections.emptyList();}
    @Override public Collection<RangeElementDescription>      getRangeElementDescriptions()   {return Collections.emptyList();}
}
