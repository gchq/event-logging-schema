<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		version="2.0">

	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />

	<!-- Copy everything, change nothing -->
	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="xs:simpleType[1]">
        <!-- Add our new type -->
		<xs:simpleType name="MyNewRegexSimpleType">
			<xs:restriction base="xs:string">
				<!-- xslt will ignore curly braces in attributes so have to do '{{' to produce '{' -->
				<xs:pattern value="[0-9]{{1,3}}[A-Z]{{3}}$" />
			</xs:restriction>
		</xs:simpleType>

		<!-- Now copy the existing types -->
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>

	</xsl:template>

</xsl:stylesheet>