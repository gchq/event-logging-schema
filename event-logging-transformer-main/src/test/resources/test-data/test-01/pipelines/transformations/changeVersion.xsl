<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:evt="file://xml/schema/accounting/events"
	version="2.0">
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />
	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<!-- Change the version number attribute -->
	<xsl:template match="/xs:schema/@version" >
		<xsl:attribute name="version">7.8.9</xsl:attribute>
	</xsl:template>

	<!-- Change the id attribute -->
	<!--<xsl:template match="/xs:schema/@id" >-->
		<!--<xsl:attribute name="id">new-id-v7.8.9</xsl:attribute>-->
	<!--</xsl:template>-->

</xsl:stylesheet>