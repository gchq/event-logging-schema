<?xml version="1.0" encoding="UTF-8" ?>
 
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" 
  xmlns:evt="file://xml/schema/accounting/events"
  version="2.0">

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

  <!-- Copy everything not matched by another more specific template -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>

  <!-- Replace a sequence of Event elements with a choice 
       of MetaData|Event|Error elements -->
  <xsl:template match="xs:element[@name = 'Events']/xs:complexType/xs:sequence">

    <xs:choice minOccurs="1" maxOccurs="unbounded">

      <!-- Add the MetaData element for passing the http headers and other
           key/value meta data -->
      <xs:element name="MetaData" minOccurs="1" maxOccurs="1">
        <xs:annotation>
          <xs:documentation>This structure is used to capture the key value pair meta data associated with events sent to a headless Stroom</xs:documentation>
        </xs:annotation>
        <xs:complexType>
          <xs:sequence>
            <xs:element name="Entry" minOccurs="0" maxOccurs="100">
              <xs:complexType>
                <xs:attribute name="Key" type="evt:SafeString" use="required"/>
                <xs:attribute name="Value" type="evt:SafeString" use="required"/>
              </xs:complexType>
            </xs:element>
          </xs:sequence>
        </xs:complexType>
      </xs:element>

      <!-- Copy the Event element reference -->
      <xsl:copy-of select="xs:element[@ref = 'evt:Event']"/>

      <!-- Add an Error element for capturing errors during processing 
           in headless stroom -->
      <xs:element name="Error" minOccurs="1" maxOccurs="1">
        <xs:annotation>
          <xs:documentation>This structure is used to capture the details of any errors that occurr duing processing of the events in a headless Stroom</xs:documentation>
        </xs:annotation>
        <xs:complexType>
          <xs:attribute name="Type" type="evt:SafeString" use="required"/>
          <xs:attribute name="Location" type="evt:SafeString" use="required"/>
          <xs:attribute name="Message" type="evt:SafeString" use="required"/>
        </xs:complexType>
      </xs:element>

    </xs:choice>

  </xsl:template>

</xsl:stylesheet>

