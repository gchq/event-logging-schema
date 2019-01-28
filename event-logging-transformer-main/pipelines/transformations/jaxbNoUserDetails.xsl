<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        version="2.0">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <!-- Remove any appinfo appinfo elements-->
    <xsl:template match="xs:appinfo"/>

    <!-- Remove all elements inside the UserDetailsComplexType as clients will not be expected to -->
    <!-- populate this section-->
    <xsl:template match="xs:complexType[@name = 'UserDetailsComplexType']/xs:sequence/xs:element"/>

</xsl:stylesheet>
<!-- vim: set tabstop=4 shiftwidth=4 expandtab : -->
