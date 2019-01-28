<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        version="2.0">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|node()" mode="removeSomeAtts">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="removeSomeAtts"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xs:element[@name='Event']/@minOccurs" mode="removeSomeAtts">
    </xsl:template>

    <xsl:template match="xs:element[@name='Event']/@maxOccurs" mode="removeSomeAtts">
    </xsl:template>

    <xsl:template match="xs:schema">
        <xsl:variable name="root" select="."/>

        <xsl:copy>
            <xsl:apply-templates select="@*"/>

            <xsl:apply-templates select="$root/xs:annotation"/>

            <xsl:apply-templates select="//xs:element[@name='Event']"
                                 mode="removeSomeAtts"/>

            <xsl:for-each select="xs:element/@name">
                <xsl:sort select="."/>
                <xsl:variable name="name" select="."/>
                <xsl:apply-templates select="$root/xs:element[@name = $name]"/>
            </xsl:for-each>

            <xsl:for-each select="xs:group/@name">
                <xsl:sort select="."/>
                <xsl:variable name="name" select="."/>
                <xsl:apply-templates select="$root/xs:group[@name = $name]"/>
            </xsl:for-each>

            <xsl:for-each select="xs:complexType/@name">
                <xsl:sort select="."/>
                <xsl:variable name="name" select="."/>
                <xsl:apply-templates select="$root/xs:complexType[@name = $name]"/>
            </xsl:for-each>

            <xsl:for-each select="xs:simpleType/@name">
                <xsl:sort select="."/>
                <xsl:variable name="name" select="."/>
                <xsl:apply-templates select="$root/xs:simpleType[@name = $name]"/>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//xs:element[@name='Event']">
        <xsl:copy>
            <xsl:apply-templates select="@*[name() != 'name']"/>
            <xsl:attribute name="ref" select="'evt:Event'"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
<!-- vim: set tabstop=4 shiftwidth=4 expandtab : -->
