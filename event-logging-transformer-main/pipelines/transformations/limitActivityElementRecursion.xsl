<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        version="2.0">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <!-- Output all nodes/attrs as they are unless matched elsewhere -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!--
        Replace the recursively defined ActivityComplexType with five
        complex types that define fixed nesting:
        ActivityComplexType can have a Parent of type Activity2ComplexType,
        which can have a Parent of type Activity3ComplexType, etc.
    -->
    <xsl:template match="/xs:schema/xs:complexType[@name='ActivityComplexType']" priority="2">

        <xsl:call-template name="createActivityComplexType">
            <xsl:with-param name="activityTypeNode" select="."/>
            <xsl:with-param name="number" select="0"/>
            <xsl:with-param name="childNumber" select="2"/>
        </xsl:call-template>

        <xsl:call-template name="createActivityComplexType">
            <xsl:with-param name="activityTypeNode" select="."/>
            <xsl:with-param name="number" select="2"/>
            <xsl:with-param name="childNumber" select="3"/>
        </xsl:call-template>

        <xsl:call-template name="createActivityComplexType">
            <xsl:with-param name="activityTypeNode" select="."/>
            <xsl:with-param name="number" select="3"/>
            <xsl:with-param name="childNumber" select="4"/>
        </xsl:call-template>

        <xsl:call-template name="createActivityComplexType">
            <xsl:with-param name="activityTypeNode" select="."/>
            <xsl:with-param name="number" select="4"/>
            <xsl:with-param name="childNumber" select="5"/>
        </xsl:call-template>

        <xsl:call-template name="createActivityComplexType">
            <xsl:with-param name="activityTypeNode" select="."/>
            <xsl:with-param name="number" select="5"/>
            <xsl:with-param name="childNumber" select="6"/>
        </xsl:call-template>

    </xsl:template>

    <!-- Create Activity[2-5]ComplexType as modified copies of the original -->
    <xsl:template name="createActivityComplexType">
        <xsl:param name="activityTypeNode"/>
        <xsl:param name="number"/>
        <xsl:param name="childNumber"/>

        <xs:complexType>
            <xsl:attribute name="name">
                <xsl:value-of select="concat('Activity', $number, 'ComplexType')"/>
            </xsl:attribute>
            <xsl:apply-templates select="$activityTypeNode/@*|$activityTypeNode/node()" mode="activity">
                <xsl:with-param name="number" select="$number"/>
                <xsl:with-param name="childNumber" select="$childNumber"/>
            </xsl:apply-templates>
        </xs:complexType>
    </xsl:template>

    <!-- Rename the generated complex types -->
    <xsl:template match="xs:complexType[@name='ActivityComplexType']/@name" mode="activity" priority="2">
        <xsl:param name="number"/>
        <xsl:attribute name="name" select="concat('Activity', replace(string($number), '0', ''), 'ComplexType')"/>
    </xsl:template>

    <!-- Remove Parent from the deepest generated type -->
    <xsl:template match="xs:element[@name='Parent']" mode="activity" priority="2">
        <xsl:param name="childNumber"/>
        <xsl:if test="$childNumber &lt;= 5">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()" mode="activity">
                    <xsl:with-param name="childNumber" select="$childNumber"/>
                </xsl:apply-templates>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <!-- Rename the type of Parent in each generated level -->
    <xsl:template match="xs:element[@name='Parent']/@type" mode="activity" priority="2">
        <xsl:param name="childNumber"/>
        <xsl:attribute name="type" select="concat('evt:Activity', $childNumber, 'ComplexType')"/>
    </xsl:template>

    <!-- Identity template that passes parameters through for other templates -->
    <xsl:template match="@*|node()" mode="activity">
        <xsl:param name="number"/>
        <xsl:param name="childNumber"/>
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="activity">
                <xsl:with-param name="number" select="$number"/>
                <xsl:with-param name="childNumber" select="$childNumber"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
<!-- vim: set tabstop=4 shiftwidth=4 expandtab : -->
