<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:evt="event-logging:3"
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
  <xsl:template match="xs:element[@name = 'Event']/xs:complexType/xs:sequence">

    <!-- Copy the existing unnamed complex type unchanged -->
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>

    <!-- Now add the new Version attribute -->
    <xs:attribute name="Version" type="evt:VersionSimpleType">
      <xs:annotation>
        <xs:documentation>The version of the schema that this event conforms to.
          Only for use when sending event fragments.</xs:documentation>
      </xs:annotation>
    </xs:attribute>

  </xsl:template>

</xsl:stylesheet>

