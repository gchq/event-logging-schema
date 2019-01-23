<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        version="2.0">

    <xsl:output method="xml" version="1.0" encoding="UTF-8"
                indent="yes"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xs:element|xs:choice|xs:sequence|xs:group">
        <xsl:copy>
            <xsl:if test="not(parent::xs:schema or parent::xs:group)">
                <xsl:if test="not(@minOccurs)">
                    <xsl:attribute name="minOccurs" select="'1'"/>
                </xsl:if>
                <xsl:if test="not(@maxOccurs)">
                    <xsl:attribute name="maxOccurs" select="'1'"/>
                </xsl:if>
            </xsl:if>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>