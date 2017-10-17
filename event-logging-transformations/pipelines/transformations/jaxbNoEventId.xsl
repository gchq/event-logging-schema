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

	<xsl:template match="xs:appinfo" />






	<!-- Remove the Event/Id element from the schema as clients should not be using it-->
	<xsl:template
		match="//xs:element[@name='Event']/xs:complexType/xs:sequence/xs:element[@name='Id']" />
	

</xsl:stylesheet>