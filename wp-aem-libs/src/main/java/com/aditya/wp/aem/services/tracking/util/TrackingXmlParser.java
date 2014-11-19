/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.tracking.data.TrackingData;
import com.aditya.gmwp.aem.services.tracking.data.TrackingDataEntry;
import com.aditya.gmwp.aem.services.tracking.data.TrackingDataVar;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class TrackingXmlParser {

    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();

    private static final Logger LOG = LoggerFactory.getLogger(TrackingXmlParser.class);

    // fields used in XML parsing
    private List<TrackingData> trackingDataList;
    private TrackingDataEntry currentEntry = null;
    private TrackingDataVar currentVar = null;
    private TrackingData trackingData = null;

    /**
     * Parses the xml.
     * 
     * @param in
     *            input stream for xml data
     * @return object representation of tracking data.
     * @throws XMLStreamException
     *             when reading or parsing the XML fails.
     */
    public final List<TrackingData> parseXML(final InputStream in) throws XMLStreamException {

        this.trackingDataList = new ArrayList<TrackingData>();
        final XMLEventReader eventReader = INPUT_FACTORY.createXMLEventReader(in);

        try {

            while (eventReader.hasNext()) {
                final XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    handleStartElement(event.asStartElement());
                } else if (event.isEndElement()) {
                    handleEndElement(event.asEndElement());
                } else if (event.isCharacters() && null != this.currentVar) {
                    this.currentVar.setData(event.asCharacters().getData());
                }
            }

        } finally {
            eventReader.close();
        }

        return this.trackingDataList;
    }

    /**
     * Parses the given StartElement and sets this object's parse fields.
     * 
     * @param startElement
     *            The StartElement to be handled.
     */
    private void handleStartElement(final StartElement startElement) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Found start element: " + startElement.getName().getLocalPart());
        }

        final String elemName = startElement.getName().getLocalPart().toLowerCase(Locale.ENGLISH);
        if ("omniture".equals(elemName)) {
            this.trackingData = new TrackingData();
            this.trackingData.setTrackingName(TrackingData.OMNITURE_PROPERTY);
        } else if ("floodlight".equals(elemName)) {
            this.trackingData = new TrackingData();
            this.trackingData.setTrackingName(TrackingData.FLOODLIGHT_PROPERTY);
        } else if ("spotlight".equals(elemName)) {
            this.trackingData = new TrackingData();
            this.trackingData.setTrackingName(TrackingData.SPOTLIGHT_PROPERTY);
        }
        if ("entry".equals(elemName)) {
            createTrackingDataEntry(startElement);
        } else if ("var".equals(elemName)) {
            this.currentVar = new TrackingDataVar();
            Attribute attr = null;
            attr = startElement.getAttributeByName(new QName("name"));
            if (null != attr) {
                this.currentVar.setName(attr.getValue());
            }
        } else if ("src".equals(elemName) || "typ".equals(elemName) || "cat".equals(elemName)) {
            this.currentVar = new TrackingDataVar();
            this.currentVar.setName(elemName);
        }
    }

    /**
     * Parses the given EndElement and sets this object's parse fields
     * 
     * @param endElement
     */
    private void handleEndElement(final EndElement endElement) {

        final String elemName = endElement.getName().getLocalPart().toLowerCase(Locale.ENGLISH);

        if ("entry".equals(elemName)) {
            this.trackingData.addEntry(this.currentEntry);
            this.currentEntry = null;
        } else if ("var".equals(elemName) || "src".equals(elemName) || "typ".equals(elemName) || "cat".equals(elemName)) {
            this.currentEntry.addVar(this.currentVar);
            this.currentVar = null;
        } else if ("omniture".equals(elemName) || "floodlight".equals(elemName) || "spotlight".equals(elemName)) {
            this.trackingDataList.add(this.trackingData);
        }

    }

    /**
     * Creates and initializes a new TrackingDataEntry and assigns it to this.currentEntry.
     * 
     * @param startElement
     *            The StartElement serving as the data source.
     */
    private void createTrackingDataEntry(final StartElement startElement) {

        this.currentEntry = new TrackingDataEntry();

        Attribute attr = null;

        attr = startElement.getAttributeByName(new QName("name"));
        if (null != attr) {
            this.currentEntry.setName(attr.getValue());
        }
        attr = startElement.getAttributeByName(new QName("type"));
        if (null != attr) {
            this.currentEntry.setType(attr.getValue());
        }
        attr = startElement.getAttributeByName(new QName("id"));
        if (null != attr) {
            this.currentEntry.setId(attr.getValue());
        }
        attr = startElement.getAttributeByName(new QName("url"));
        if (null != attr) {
            this.currentEntry.setUrl(attr.getValue());
        }
    }
}
