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
  
    <!--  Replace the existing DataComplexType that allows infinite recursion 
    with 5 similar complex types that define fixed nesting.
    i.e. DataComplexType can have children of type Data2ComplexType which can have 
    children of type Data3ComplexType, etc. -->
    <xsl:template match="/xs:schema/xs:complexType[@name='DataComplexType']" priority="2">

        <xsl:call-template name="createDataComplexType">
            <xsl:with-param name="dataTypeNode" select = "." />
            <xsl:with-param name="number" select = "0" />
            <xsl:with-param name="childNumber" select = "2" />
        </xsl:call-template>

        <xsl:call-template name="createDataComplexType">
            <xsl:with-param name="dataTypeNode" select = "." />
            <xsl:with-param name="number" select = "2" />
            <xsl:with-param name="childNumber" select = "3" />
        </xsl:call-template>

        <xsl:call-template name="createDataComplexType">
            <xsl:with-param name="dataTypeNode" select = "." />
            <xsl:with-param name="number" select = "3" />
            <xsl:with-param name="childNumber" select = "4" />
        </xsl:call-template>

        <xsl:call-template name="createDataComplexType">
            <xsl:with-param name="dataTypeNode" select = "." />
            <xsl:with-param name="number" select = "4" />
            <xsl:with-param name="childNumber" select = "5" />
        </xsl:call-template>

        <xsl:call-template name="createDataComplexType">
            <xsl:with-param name="dataTypeNode" select = "." />
            <xsl:with-param name="number" select = "5" />
            <xsl:with-param name="childNumber" select = "6" />
        </xsl:call-template>

    </xsl:template>

    <!-- Create the Data[0-9]?ComplexType as a modified copy of the original one -->
    <xsl:template name="createDataComplexType">
        <xsl:param name = "dataTypeNode" />
        <xsl:param name = "number" />
        <xsl:param name = "childNumber" />

        <xs:complexType>
            <xsl:attribute name="name">
                <xsl:value-of select="concat('Data',$number, 'ComplexType')"/>
            </xsl:attribute>
            <xsl:apply-templates select="$dataTypeNode/@*|$dataTypeNode/node()" mode="data">
                <xsl:with-param name="number" select = "$number" />
                <xsl:with-param name="childNumber" select = "$childNumber" />
            </xsl:apply-templates>
        </xs:complexType>
    </xsl:template>

    <!-- Rename the complex type -->
    <xsl:template match="xs:complexType[@name='DataComplexType']/@name" mode="data" priority="2">
        <xsl:param name = "number" />
        <xsl:param name = "childNumber" />
        <xsl:attribute name="name" select="concat('Data', replace(string($number),'0',''), 'ComplexType')"/>
    </xsl:template>

    <!-- Handle the copying of the child data element -->
    <xsl:template match="xs:sequence[./xs:element[@name='Data']]" mode="data" priority="2">
        <xsl:param name = "number" />
        <xsl:param name = "childNumber" />

        <!-- Only if we are not at the lowest level, add the child data element -->
        <xsl:if test="$childNumber &lt;= 5">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()" mode="data">
                    <xsl:with-param name="number" select = "$number" />
                    <xsl:with-param name="childNumber" select = "$childNumber" />
                </xsl:apply-templates>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <!-- Rename the type of the child element -->
    <xsl:template match="xs:sequence/xs:element[@name='Data']/@type" mode="data" priority="2">
        <xsl:param name = "childNumber" />
        <xsl:attribute name="type" select="concat('evt:Data', $childNumber, 'ComplexType')"/>
    </xsl:template>

    <!-- Identity template that passes our variable through for other templates to use -->
    <xsl:template match="@*|node()" mode="data">
        <xsl:param name = "number" />
        <xsl:param name = "childNumber" />
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="data">
                <xsl:with-param name="number" select = "$number" />
                <xsl:with-param name="childNumber" select = "$childNumber" />
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
<!-- vim: set tabstop=4 shiftwidth=4 expandtab : -->
