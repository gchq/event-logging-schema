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

  <!-- match on the first simpleType so we can add extra types in before it.
       All type blocks will be sorted in another xslt so we can add them in any order -->
  <xsl:template match="xs:simpleType[1]">

    <xs:simpleType name="SafeString">
      <xs:restriction base="xs:string">
        <xs:pattern>
          <!--
          Only allow a-z, A-Z, 0-9, space, underscore, hyphen, colon, forward slash, period
          Also allow strings like ~123 so that a replacement can be performed on the XML prior to validation
          to replace any non-supported characters with their unicode number
          -->
          <xsl:attribute name="value">([0-9a-zA-Z /_:.-]|([~][0-9]{3})){0,500}(\.{3})?</xsl:attribute>
        </xs:pattern>
      </xs:restriction>
    </xs:simpleType>

    <!--copy the matched simpleType so we don't lose it-->
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>

  </xsl:template>

</xsl:stylesheet>

