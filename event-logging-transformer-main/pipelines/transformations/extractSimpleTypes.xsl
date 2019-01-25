<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:evt="event-logging:3"
        version="2.0">
    <xsl:output method="xml" version="1.0" encoding="UTF-8"
                indent="yes"/>

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xs:schema">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>

            <xsl:for-each select="//xs:element/xs:simpleType">
                <xsl:copy>
                    <xsl:variable name="ancestorNames">
                        <xsl:apply-templates select="ancestor::xs:element[1]"
                                             mode="ancestorNames"/>
                    </xsl:variable>

                    <xsl:attribute name="name"
                                   select="concat($ancestorNames, 'SimpleType')"/>
                    <xsl:apply-templates select="node() | @*"/>
                </xsl:copy>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xs:element[xs:simpleType]">
        <xsl:copy>
            <xsl:variable name="ancestorNames">
                <xsl:apply-templates select="." mode="ancestorNames"/>
            </xsl:variable>

            <xsl:attribute name="type"
                           select="concat('evt:', $ancestorNames, 'SimpleType')"/>
            <xsl:apply-templates select="node()[name() != 'xs:simpleType'] | @*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xs:element" mode="ancestorNames">
        <xsl:value-of select="ancestor::xs:element[1]/@name"/>
        <xsl:value-of
                select="substring-before(ancestor::xs:complexType[1]/@name, 'ComplexType')"/>
        <xsl:value-of select="@name"/>
    </xsl:template>
</xsl:stylesheet>