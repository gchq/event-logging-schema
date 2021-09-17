<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        version="2.0">

    <xsl:output method="xml" 
                version="1.0" 
                encoding="UTF-8"
                indent="yes"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Limit the number of Event elements -->
    <xsl:template match="/xs:schema/xs:element[@name='Events']/xs:complexType/xs:sequence/xs:element[@name='Event']/@maxOccurs" 
                  priority="2">
        <xsl:attribute name="maxOccurs" select="'100000'"/>
    </xsl:template>

    <!-- Limit the number of unbounded elements -->
    <xsl:template match="xs:element[@maxOccurs = 'unbounded']/@maxOccurs">
        <xsl:attribute name="maxOccurs" select="'500'"/>
    </xsl:template>

    <!-- Limit the number of unbounded choice items -->
    <xsl:template match="xs:choice[@maxOccurs = 'unbounded']/@maxOccurs">
        <xsl:attribute name="maxOccurs" select="'500'"/>
    </xsl:template>

    <!-- Limit the number of unbounded sequence items -->
    <xsl:template match="xs:sequence[@maxOccurs = 'unbounded']/@maxOccurs">
        <xsl:attribute name="maxOccurs" select="'500'"/>
    </xsl:template>

    <!-- Limit the number of unbounded group items -->
    <xsl:template match="xs:group[@maxOccurs = 'unbounded']/@maxOccurs">
        <xsl:attribute name="maxOccurs" select="'500'"/>
    </xsl:template>

</xsl:stylesheet>
<!-- vim: set tabstop=4 shiftwidth=4 expandtab : -->
