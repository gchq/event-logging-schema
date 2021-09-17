<?xml version="1.0" encoding="UTF-8" ?>
 
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    version="2.0">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

    <!-- Copy everything into the output -->
    <xsl:template match="@*|node()">
        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>



    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    Set the desired version and namespace the schema here.
    Note: the version of the gov schema does not have to
    be in step with the source schema.
    -->
    <xsl:variable
        name="schemaVersion"
        select="'2.5.1-beta.3'" />

    <xsl:variable
        name="enumerationVersion"
        select="'2.5.1'" />

    <xsl:variable
        name="schemaNamespace"
        select="'file://xml/schema/accounting/events'" />
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->




    <!-- match on the root schema element to change its namespace-->
    <xsl:template match="xs:schema">

        <xsl:element name="xs:schema" namespace="http://www.w3.org/2001/XMLSchema">
            <!-- Copy the existing attributes -->
            <xsl:copy-of select="@*"/>

            <!-- Override the target and events namespaces -->
            <xsl:attribute name="targetNamespace">
                <xsl:value-of select="$schemaNamespace"/>
            </xsl:attribute>
            <xsl:namespace name="evt">
                <xsl:value-of select="$schemaNamespace"/>
            </xsl:namespace>

            <xsl:attribute name="version" select="$schemaVersion" />

            <!-- Continue applying templates to the children of this element -->
            <xsl:apply-templates select="./*" />

        </xsl:element>
    </xsl:template>

    <xsl:template match="xs:schema/xs:simpleType[@name = 'VersionSimpleType']/xs:restriction">
        <xs:restriction base="xs:string">
            <xs:enumeration>
                <!-- 
                   Add a line in here for each version of the schema
                   (including this one) that this version is backwards 
                   compatible with 
                   e.g. if $schemaVersion is 4.2.2 you may have
                     <xsl:attribute name="value" select="4.0.0" />
                     <xsl:attribute name="value" select="4.0.1" />
                     <xsl:attribute name="value" select="4.1.0" />
                     <xsl:attribute name="value" select="4.2.0" />
                     <xsl:attribute name="value" select="4.2.1" />
                     <xsl:attribute name="value" select="$schemaVersion" />
                -->
                <xsl:attribute name="value" select="$enumerationVersion" />
            </xs:enumeration>
        </xs:restriction>
    </xsl:template>

</xsl:stylesheet>

