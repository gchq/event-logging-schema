<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        version="2.0">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <!-- Copy all content not matched by other templates -->
    <xsl:template match="@*|node()" priority="5">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Remove any vim modeline comments from the file -->
    <xsl:template match="comment()" priority="6">
        <xsl:if test="not(matches(., '\s+vim:\s+.*'))">
            <!-- copy comments that are not vim modelines -->
            <xsl:copy-of select="."/>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
<!-- vim: set tabstop=4 shiftwidth=4 expandtab : -->
