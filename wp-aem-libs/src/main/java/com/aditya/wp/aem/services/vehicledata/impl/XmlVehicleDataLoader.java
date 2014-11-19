/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.aditya.gmwp.aem.exception.VehicleDataException;
import com.aditya.gmwp.aem.exception.XmlVehicleDataLoaderException;
import com.aditya.gmwp.aem.services.vehicledata.data.Attribute;
import com.aditya.gmwp.aem.services.vehicledata.data.Bodystyle;
import com.aditya.gmwp.aem.services.vehicledata.data.Brand;
import com.aditya.gmwp.aem.services.vehicledata.data.Carline;
import com.aditya.gmwp.aem.services.vehicledata.data.Series;
import com.aditya.gmwp.aem.services.vehicledata.data.VehicleData;
import com.aditya.gmwp.aem.services.vehicledata.data.impl.AttributeImpl;
import com.aditya.gmwp.aem.services.vehicledata.data.impl.BodystyleImpl;
import com.aditya.gmwp.aem.services.vehicledata.data.impl.CarlineImpl;
import com.aditya.gmwp.aem.services.vehicledata.data.impl.SeriesImpl;
import com.aditya.gmwp.aem.services.vehicledata.data.impl.VehicleDataImpl;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
final class XmlVehicleDataLoader {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final Logger LOG = LoggerFactory.getLogger(XmlVehicleDataLoader.class);
    private final Brand brand;
    private final Locale locale;
    private final String lslrHandle;
    private final InputStream xmlIn;
    private final boolean loadAttributes;
    private final XmlHttpLoader xhl;
    private VehicleDataImpl vd;

    /**
     * Enumeration for templates for which carline attributes are configured.
     * 
     * @author nperry, namics AG
     * @since GMWP Release 2.4
     */
    public enum XmlVehicleDataNamesAndDescriptions {
        BODYSTYLE("bodystyle"),
        CARLINE("carline"),
        CODE("code"),
        TITLE("title"),
        FORMATTEDPRICE("formatted_price"),
        FORMATTEDNETPRICE("formatted_price_net"),
        NETPRICE("price_net"),
        FORMATTEDFLEETPRICE("formatted_fleet_price"),
        VEHICLE_ATTRIBUTES("vehicle-attributes"),
        APP_PARAM("app-param"),
        APP_PARAMS("app-params"),
        APP("app"),
        APPEND("append"),
        SERIES("series"),
        FORMATTEDMAXINCENTIVE("formatted_max_incentive"),
        FORMATTEDMININCENTIVE("formatted_min_incentive"),
        INCENTIVESTART("incentive_start"),
        INCENTIVEEND("incentive_end"),
        ALTERNATEID("alternate_id"),
        TRUE("true"),
        UNABLE("Unable to load vehicle data: "),
        INVALID_XML("Invalid xml file. Number of 'vehicledata'-elements is not 1 but "),
        INVALID_ATTRIBUTES_NODE("Could not load attributes from XML file - number of " + VEHICLE_ATTRIBUTES.value() + " elements was not 1 but ");

        private String value;

        /**
         * Private constructor. Creates a new enum value.
         * 
         * @param value
         *            string
         */
        private XmlVehicleDataNamesAndDescriptions(final String value) {

            this.value = value;
        }

        /**
         * Returns the template name.
         * 
         * @return value string
         */
        public String value() {

            return this.value;
        }

    }

    /**
     * Creates a new instance.
     * 
     * @param brand
     *            the brand for which data will be loaded
     * @param locale
     *            the locale containing country and language for which data will be loaded
     * @param lslrHandle
     *            the handle to the LSLR
     * @param xmlIn
     *            the stream to load XML data from.
     */
    XmlVehicleDataLoader(final Brand brand, final Locale locale, final String lslrHandle, final InputStream xmlIn) {

        this(brand, locale, lslrHandle, xmlIn, false, null);
    }

    /**
     * Creates a new instance.
     * 
     * @param brand
     *            the brand for which data will be loaded
     * @param locale
     *            the locale containing country and language for which data will be loaded
     * @param lslrHandle
     *            the handle to the LSLR
     * @param xmlIn
     *            the stream to load XML data from.
     * @param loadAttributes
     *            inidcates whether attrbutes should be loaded
     * @param xhl
     *            XmlHttpLoader to load attributes from DDP service per bodystyle entry in
     *            market.xml
     */
    XmlVehicleDataLoader(final Brand brand, final Locale locale, final String lslrHandle, final InputStream xmlIn, final boolean loadAttributes,
            final XmlHttpLoader xhl) {

        if (LOG.isDebugEnabled()) {
            this.xmlIn = dumpMarketXml(lslrHandle, xmlIn);
        } else {
            this.xmlIn = xmlIn;
        }

        this.brand = brand;
        this.locale = locale;
        this.lslrHandle = lslrHandle;
        this.loadAttributes = loadAttributes;
        this.xhl = xhl;
    }

    /**
     * Method just for debugging purposes. Dumps the whole XML data that is read from the market.xml
     * to the log-file.
     * 
     * @param lslrHandle
     *            the handle to the LSLR
     * @param xmlIn
     *            the stream for loading market.xml data from.
     * @return a stream providing market.xml data
     */
    private InputStream dumpMarketXml(final String lslrHandle,
                                      final InputStream xmlIn) {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final BufferedOutputStream bufos = new BufferedOutputStream(baos);
        BufferedInputStream bufis;
        if (xmlIn instanceof BufferedInputStream) {
            bufis = (BufferedInputStream) xmlIn;
        } else {
            bufis = new BufferedInputStream(xmlIn);
        }
        final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
        int read = -1;
        try {
            try {
                while ((read = bufis.read(buf, 0, buf.length)) != -1) {
                    bufos.write(buf, 0, read);
                }
            } finally {
                bufis.close();
                bufos.close();
            }
        } catch (IOException e) {
            final String message = XmlVehicleDataNamesAndDescriptions.UNABLE.value + e.getMessage();
            throw new XmlVehicleDataLoaderException(message, e);
        }
        LOG.debug("Loading VehicleData for " + lslrHandle + " from the following market.xml: \n" + new String(baos.toByteArray()));
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Does the actual work of parsing the XML and creating the object-structure.
     * 
     * @return a new instance of VehicleData if XML data can be parsed and processed.
     * @throws VehicleDataException
     */
    final VehicleData load() throws VehicleDataException {

        this.vd = null;
        try {

            final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = docBuilder.parse(new InputSource(this.xmlIn));

            this.vd = new VehicleDataImpl(this.brand, this.locale.getCountry().toLowerCase(this.locale), this.locale.getLanguage().toLowerCase(this.locale));

            final NodeList vehicledataElems = doc.getElementsByTagName("vehicledata");

            if (vehicledataElems.getLength() != 1) {
                throw new IllegalArgumentException(XmlVehicleDataNamesAndDescriptions.INVALID_XML.value() + vehicledataElems.getLength());
            }

            final Element vehicledataElem = (Element) vehicledataElems.item(0);
            final NodeList xmlCarlines = vehicledataElem.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.CARLINE.value());
            for (int i = 0; i < xmlCarlines.getLength(); i++) {

                final Element carlineElem = (Element) xmlCarlines.item(i);
                final String code = carlineElem.getAttribute(XmlVehicleDataNamesAndDescriptions.CODE.value());
                final String title = carlineElem.getAttribute(XmlVehicleDataNamesAndDescriptions.TITLE.value());
                final String formattedPrice = carlineElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDPRICE.value());
                final String formattedNetPrice = carlineElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDNETPRICE.value());
                final String formattedFleetPrice = carlineElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDFLEETPRICE.value());
                final String formattedMaxIncentive = carlineElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDMAXINCENTIVE.value());
                final String formattedMinIncentive = carlineElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDMININCENTIVE.value());

                int modelYear = Carline.INVALID_MODEL_YEAR;
                final String help = carlineElem.getAttribute("model-year");
                if (StringUtils.isNotBlank(help)) {
                    try {
                        modelYear = Integer.parseInt(help);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Market " + this.lslrHandle + " DOES USE model-years, read model-year '" + modelYear + "' for carline '" + code + "'");
                        }
                    } catch (NumberFormatException e) {
                        LOG.error("Unable to convert given model-year '" + help + "' to a number! Brand: " + this.brand + ", Locale: " + this.locale
                                + ", Carline:" + code);
                    }
                }
                final String suffix = carlineElem.getAttribute("model-year-suffix");

                final CarlineImpl.Builder builder = new CarlineImpl.Builder();
                final CarlineImpl carline = builder.vehicleData(this.vd).code(code).title(title).formattedPrice(formattedPrice).formattedNetPrice(formattedNetPrice).formattedFleetPrice(formattedFleetPrice).modelYear(modelYear).modelYearSuffix(suffix).formattedMaxIncentive(formattedMaxIncentive).formattedMinIncentive(formattedMinIncentive).build();

                this.vd.addCarline(carline);

                loadBodystyles(carlineElem, carline);
            }

        } catch (final ParserConfigurationException e) {
            final String message = XmlVehicleDataNamesAndDescriptions.UNABLE.value() + e.getMessage();
            throw new VehicleDataException(message, e);
        } catch (final IOException e) {
            final String message = XmlVehicleDataNamesAndDescriptions.UNABLE.value() + e.getMessage();
            throw new VehicleDataException(message, e);
        } catch (final SAXException e) {
            final String message = XmlVehicleDataNamesAndDescriptions.UNABLE.value() + e.getMessage();
            throw new VehicleDataException(message, e);
        }
        return this.vd;
    }

    /**
     * Get bodystyle element.
     * 
     * @return Bodystyle element.
     * @param doc
     *            document element
     */
    private Element getBodystyleElement(final Document doc) {

        Element vehicledataElem = null;
        try {
            final String[] elementNames = { XmlVehicleDataNamesAndDescriptions.CARLINE.value(), XmlVehicleDataNamesAndDescriptions.BODYSTYLE.value() };
            NodeList vehicleAttributeElems = doc.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.VEHICLE_ATTRIBUTES.value());
            if (vehicleAttributeElems.getLength() != 1) {
                throw new IllegalArgumentException(XmlVehicleDataNamesAndDescriptions.INVALID_ATTRIBUTES_NODE.value() + vehicleAttributeElems.getLength());

            }
            vehicledataElem = (Element) vehicleAttributeElems.item(0);
            for (String name : elementNames) {
                vehicleAttributeElems = vehicledataElem.getElementsByTagName(name);
                vehicledataElem = (Element) vehicleAttributeElems.item(0);
            }
        } catch (Exception e) {
            final String message = XmlVehicleDataNamesAndDescriptions.UNABLE.value() + e.getMessage();
            throw new XmlVehicleDataLoaderException(message, e);
        }
        return vehicledataElem;
    }

    /**
     * Only called when an attributes file is uploaded from the harddrive (testing).
     * 
     * @return a new instance of VehicleData if XML data can be parsed and processed.
     * @param vd
     *            Vehicle data
     * @param xmlIn
     *            Input Stream with attrbute values
     */
    public VehicleData loadAttributes(final VehicleData vd,
                                      final InputStream xmlIn) {

        try {

            final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = docBuilder.parse(new InputSource(xmlIn));

            final NodeList vehicleAttributeElems = doc.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.VEHICLE_ATTRIBUTES.value());

            if (vehicleAttributeElems.getLength() != 1) {
                throw new IllegalArgumentException(XmlVehicleDataNamesAndDescriptions.INVALID_ATTRIBUTES_NODE.value() + vehicleAttributeElems.getLength());
            }

            final Element vehicledataElem = (Element) vehicleAttributeElems.item(0);
            final NodeList xmlCarlines = vehicledataElem.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.CARLINE.value());
            for (int i = 0; i < xmlCarlines.getLength(); i++) {

                final Element carlineElem = (Element) xmlCarlines.item(i);
                final String code = carlineElem.getAttribute(XmlVehicleDataNamesAndDescriptions.CODE.value());

                final CarlineImpl carline = (CarlineImpl) vd.getCarline(code, -1, null);

                loadAttributeBodystyles(carlineElem, carline);
            }

        } catch (ParserConfigurationException e) {
            final String message = XmlVehicleDataNamesAndDescriptions.UNABLE.value() + e.getMessage();
            throw new XmlVehicleDataLoaderException(message, e);
        } catch (IOException e) {
            final String message = XmlVehicleDataNamesAndDescriptions.UNABLE.value() + e.getMessage();
            throw new XmlVehicleDataLoaderException(message, e);
        } catch (SAXException e) {
            final String message = XmlVehicleDataNamesAndDescriptions.UNABLE.value() + e.getMessage();
            throw new XmlVehicleDataLoaderException(message, e);
        }

        return vd;
    }

    public VehicleData loadFeatures(final VehicleData vd,
                                    final InputStream is) {
        return vd;
    }

    /**
     * Load app parameter.
     * 
     * @param appParamsElem
     *            the app-params XML element
     * @param bodystyle
     *            the bodystyle object where data will be added to
     */
    private void loadAppParams(final Element appParamsElem,
                               final BodystyleImpl bodystyle) {

        final NodeList paramsList = appParamsElem.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.APP_PARAM.value());
        for (int i = 0; i < paramsList.getLength(); i++) {
            final Element paramElem = (Element) paramsList.item(i);
            bodystyle.addAppParams(paramElem.getAttribute(XmlVehicleDataNamesAndDescriptions.APP.value()),
                    paramElem.getAttribute(XmlVehicleDataNamesAndDescriptions.APPEND.value()));
        }
    }

    /**
     * Load app paraamter.
     * 
     * @param appParamsElem
     *            the app-params XML element
     * @param series
     *            the series object where data will be added to
     */
    private void loadAppParams(final Element appParamsElem,
                               final SeriesImpl series) {

        final NodeList paramsList = appParamsElem.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.APP_PARAM.value());
        for (int i = 0; i < paramsList.getLength(); i++) {
            final Element paramElem = (Element) paramsList.item(i);
            series.addAppParams(paramElem.getAttribute(XmlVehicleDataNamesAndDescriptions.APP.value()),
                    paramElem.getAttribute(XmlVehicleDataNamesAndDescriptions.APPEND.value()));
        }
    }

    /**
     * Loads all bodystyles for one carline.
     * 
     * @param carlineElem
     *            the carline-node from XML data.
     * @param carline
     *            the carline object where newly created bodystyle-objects will be added to.
     */
    private void loadBodystyles(final Element carlineElem,
                                final CarlineImpl carline) {

        final NodeList xmlBodytsyles = carlineElem.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.BODYSTYLE.value());
        for (int i = 0; i < xmlBodytsyles.getLength(); i++) {

            final Element bodystyleElem = (Element) xmlBodytsyles.item(i);
            final String code = bodystyleElem.getAttribute(XmlVehicleDataNamesAndDescriptions.CODE.value());
            final String title = bodystyleElem.getAttribute(XmlVehicleDataNamesAndDescriptions.TITLE.value());
            final String formattedPrice = bodystyleElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDPRICE.value());
            final String formattedNetPrice = bodystyleElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDNETPRICE.value());
            final String formattedFleetPrice = bodystyleElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDFLEETPRICE.value());
            final String formattedMaxIncentive = bodystyleElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDMAXINCENTIVE.value());
            final String formattedMinIncentive = bodystyleElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDMININCENTIVE.value());

            final BodystyleImpl.Builder builder = new BodystyleImpl.Builder();
            final BodystyleImpl bodystyle = builder.carline(carline).code(code).formattedPrice(formattedPrice).formattedNetPrice(formattedNetPrice).formattedFleetPrice(formattedFleetPrice).formattedMaxIncentive(formattedMaxIncentive).formattedMinIncentive(formattedMinIncentive).build();
            final String vcCarlineCode = bodystyleElem.getAttribute("vcCarlineCode");
            if (StringUtils.isNotBlank(vcCarlineCode)) {
                bodystyle.setVcCarlineCode(vcCarlineCode);
            }
            carline.addBodystyle(bodystyle);

            final NodeList childs = bodystyleElem.getChildNodes();
            for (int j = 0; j < childs.getLength(); j++) {
                final Node n = childs.item(j);
                if (XmlVehicleDataNamesAndDescriptions.APP_PARAMS.value().equals(n.getNodeName())) {
                    loadAppParams((Element) n, bodystyle);
                    break;
                }
            }

            if (this.loadAttributes) {
                try {
                    loadAttributeBodystylesWithInputStream(bodystyle, this.xhl.loadAttributes(carline, bodystyle));
                } catch (Exception e) {
                    LOG.error(
                            String.format("vehicle_attributes.xml, loadAttributes() failed for carline %1$s, bodystyle %2$s", carline.getCode(),
                                    bodystyle.getCode()), e);
                }
            }

            loadSeries(bodystyleElem, bodystyle);
        }
    }

    /**
     * CURRENTLY NOT USED Loads an attributes file per bodystyle by input stream . (Could be needed
     * later if there is an attributes xml file per bodystyle)
     * 
     * @param bodystyle
     *            the bodystyle-node from XML data.
     * @param attributesXml
     *            the xml file containing the attributes data
     */
    private void loadAttributeBodystylesWithInputStream(final BodystyleImpl bodystyle,
                                                        final InputStream attributesXml) {

        if (attributesXml != null) {
            DocumentBuilder docBuilder;
            try {
                docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

                final Document doc = docBuilder.parse(new InputSource(attributesXml));

                final Element bodystyleElem = getBodystyleElement(doc);

                bodystyle.setAttributes(getAttributes(bodystyleElem));

            } catch (ParserConfigurationException e) {
                final String message = "Unable to parse load vehicle attributes data: " + e.getMessage();
                throw new XmlVehicleDataLoaderException(message, e);
            } catch (SAXException e) {
                final String message = "SAX error while parsing load vehicle attributes data: " + e.getMessage();
                throw new XmlVehicleDataLoaderException(message, e);
            } catch (IOException e) {
                final String message = "IO error while loading vehicle attributes data: " + e.getMessage();
                throw new XmlVehicleDataLoaderException(message, e);
            } finally {
                try {
                    attributesXml.close();
                } catch (IOException e) {
                    final String message = "IO error while closing vehicle attributes data input stream: " + e.getMessage();
                    throw new XmlVehicleDataLoaderException(message, e);
                }
            }
        }

    }

    /**
     * Gets Attrbutes from bodystyle element.
     * 
     * @param bodystyleElem
     *            the bodystyle-node from XML data.
     * @return attributes attribute map with id as key
     */
    private Map<String, Attribute> getAttributes(final Element bodystyleElem) {

        final Map<String, Attribute> attributes = new HashMap<String, Attribute>();
        final NodeList xmlAttributes = bodystyleElem.getElementsByTagName("attribute");
        for (int j = 0; j < xmlAttributes.getLength(); j++) {
            final Element attributeElem = (Element) xmlAttributes.item(j);
            if (StringUtils.equals(attributeElem.getParentNode().getNodeName(), XmlVehicleDataNamesAndDescriptions.BODYSTYLE.value())) {
                final String id = attributeElem.getAttribute("gmds_id");
                final String value = attributeElem.getAttribute("value");
                final String description = attributeElem.getAttribute("description");
                final String unit = attributeElem.getAttribute("unit_of_measure");
                final String filteType = attributeElem.getAttribute("filter_type");
                final Attribute attribute = new AttributeImpl(id, value, description, unit, filteType);
                attributes.put(id, attribute);
                this.vd.addAttributeToAvailableAttributes(attribute);
            }

        }
        return attributes;
    }

    /**
     * ONLY called when an attributes file is uploaded from the harddrive (testing). Updates all
     * bodystyles for one carline with Attributes.
     * 
     * @param carlineElem
     *            the carline-node from XML data.
     * @param carline
     *            the carline object where newly created bodystyle-objects will be added to.
     */
    private void loadAttributeBodystyles(final Element carlineElem,
                                         final CarlineImpl carline) {

        final NodeList xmlBodytsyles = carlineElem.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.BODYSTYLE.value());
        for (int i = 0; i < xmlBodytsyles.getLength(); i++) {

            final Element bodystyleElem = (Element) xmlBodytsyles.item(i);
            final String code = bodystyleElem.getAttribute(XmlVehicleDataNamesAndDescriptions.CODE.value());
            final Bodystyle bodyStyle = carline.getBodystyle(code);
            if (bodyStyle != null) {
                bodyStyle.setAttributes(getAttributes(bodystyleElem));
                // loadAttributeSeries(bodystyleElem, bodyStyle);
            }

        }
    }

    /**
     * Loads all series for one bodystyle.
     * 
     * @param bodystyleElem
     *            the bodystyle-node from XML data.
     * @param bodystyle
     *            the bodystyle-object where newly created series-objects will be added to.
     */
    private void loadSeries(final Element bodystyleElem,
                            final BodystyleImpl bodystyle) {

        final NodeList xmlSeries = bodystyleElem.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.SERIES.value());
        for (int i = 0; i < xmlSeries.getLength(); i++) {
            final Element seriesElem = (Element) xmlSeries.item(i);
            final String code = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.CODE.value());
            final String title = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.TITLE.value());
            final String formattedPrice = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDPRICE.value());
            final String formattedNetPrice = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDNETPRICE.value());
            final String formattedFleetPrice = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDFLEETPRICE.value());
            final String formattedMaxIncentive = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.FORMATTEDMAXINCENTIVE.value());
            final String incentiveStart = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.INCENTIVESTART.value());
            final String incentiveEnd = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.INCENTIVEEND.value());
            final String formattedConfig = seriesElem.getAttribute("formatted_config");
            final String formattedDrive = seriesElem.getAttribute("formatted_drive");
            final String price = seriesElem.getAttribute("price");
            final String netPrice = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.NETPRICE.value());

            final String alternateId = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.ALTERNATEID.value());

            final boolean cheapestAcrossConfiguration;
            if (XmlVehicleDataNamesAndDescriptions.TRUE.value().equals(seriesElem.getAttribute("cheapestSeriesAcrossConfig"))) {
                cheapestAcrossConfiguration = true;
            } else {
                cheapestAcrossConfiguration = false;
            }
            final boolean cheapestDriveTypeAcrossConfigurationAndSeries;
            if (XmlVehicleDataNamesAndDescriptions.TRUE.value().equals(seriesElem.getAttribute("cheapestDriveType"))) {
                cheapestDriveTypeAcrossConfigurationAndSeries = true;
            } else {
                cheapestDriveTypeAcrossConfigurationAndSeries = false;
            }
            final String configCode = seriesElem.getAttribute("config_code");
            final SeriesImpl.Builder builder = new SeriesImpl.Builder();
            final SeriesImpl series = builder.code(code).title(title).formattedConfig(formattedConfig).
            		formattedDrive(formattedDrive).formattedPrice(formattedPrice).
            		formattedNetPrice(formattedNetPrice).formattedFleetPrice(formattedFleetPrice).
            		cheapestAcrossConfiguration(cheapestAcrossConfiguration).cheapestDriveTypeAcrossConfigurationAndSeries(cheapestDriveTypeAcrossConfigurationAndSeries).
            		configCode(configCode).price(price).netPrice(netPrice).formattedMaxIncentive(formattedMaxIncentive).
            		incentiveStart(incentiveStart).incentiveEnd(incentiveEnd).alternateId(alternateId).build(); 

            bodystyle.addSeries(series);
            final NodeList childs = seriesElem.getChildNodes();
            for (int j = 0; j < childs.getLength(); j++) {
                final Node n = childs.item(j);
                if (XmlVehicleDataNamesAndDescriptions.APP_PARAMS.value().equals(n.getNodeName())) {
                    loadAppParams((Element) n, series);
                    break;
                }
            }
        }
    }

    /**
     * CURRENTLY NOT USED Updates all series for one. (Could be needed later if there is an
     * attributes per series are needed)
     * 
     * @param bodystyleElem
     *            the bodystyle-node from XML data.
     * @param bodystyle
     *            the bodystyle-object where series-objects will be updated.
     */
    @SuppressWarnings("unused")
    private void loadAttributeSeries(final Element bodystyleElem,
                                     final Bodystyle bodystyle) {

        final NodeList xmlSeries = bodystyleElem.getElementsByTagName(XmlVehicleDataNamesAndDescriptions.SERIES.value());
        for (int i = 0; i < xmlSeries.getLength(); i++) {
            final Element seriesElem = (Element) xmlSeries.item(i);
            final String code = seriesElem.getAttribute(XmlVehicleDataNamesAndDescriptions.CODE.value());
            final Series series = bodystyle.getSeries(code);
            if (series != null) {
                series.setAttributes(getAttributes(bodystyleElem));
            }
        }
    }
}
