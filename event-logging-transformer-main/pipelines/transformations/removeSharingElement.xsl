<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    version="2.0">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xs:appinfo" />

    <!-- Remove the SharingData elements (but not their complex types) as they are not intended for client use -->
    <xsl:template
        match="xs:element[@name = 'SharingData' and @type = 'evt:AnyContentComplexType']" />

</xsl:stylesheet>
