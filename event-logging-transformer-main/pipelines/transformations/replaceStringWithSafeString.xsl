<?xml version="1.0" encoding="UTF-8" ?>
 
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" 
  xmlns:evt="file://xml/schema/accounting/events"
  version="2.0">

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

  <!-- Copy everything not matched by another more specific template -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>

  <!-- Replace all instances of xs:string with evt:SafeString -->
	<xsl:template match="@type[.='xs:string']">
		<xsl:attribute name="{name()}">evt:SafeString</xsl:attribute>
	</xsl:template>

</xsl:stylesheet>


