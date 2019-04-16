<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xpath-default-namespace="http://www.w3.org/2001/XMLSchema"
        version="2.0">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <!-- Copy all content not matched by other templates -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Change the schema version -->
    <xsl:template match="schema/@version">
        <xsl:attribute name="version">999.99.9</xsl:attribute>
    </xsl:template>

    <!-- Change the schema id -->
    <xsl:template match="schema/simpleType[@name='VersionSimpleType']/restriction">
        <xs:restriction base="xs:string">
            <xs:enumeration value="999.99.9"/>
        </xs:restriction>
    </xsl:template>
    
</xsl:stylesheet>
<!-- vim: set tabstop=4 shiftwidth=4 expandtab : -->

