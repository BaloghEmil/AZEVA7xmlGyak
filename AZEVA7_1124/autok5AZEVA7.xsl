<?xml version="1.0" encoding="UTF-8"?>
  <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0" xmlns:kl="kl.xml">
  
    <xsl:template match="/" >
       <xsl:element name="eredmeny">
         <xsl:for-each-group select="//auto" group-by="tipus/text()">
            <xsl:sort select="count( current-group())"/>
            <xsl:element name="tipus">
               <xsl:element name="tip"><xsl:value-of select="current-grouping-key()"/></xsl:element>
               <xsl:element name="db"><xsl:value-of select="count( current-group())"/></xsl:element>
            </xsl:element>
         </xsl:for-each-group>
      </xsl:element>
    </xsl:template>

</xsl:stylesheet>