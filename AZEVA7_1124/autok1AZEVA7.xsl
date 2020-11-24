<?xml version="1.0" encoding="UTF-8"?>
  <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   version="1.0">
     <xsl:template match="/"  >
        <xsl:for-each select="//auto">
            <xsl:sort select="ar"/>
            <xsl:value-of select="@rsz"/>:<xsl:value-of select="ar"/>
        </xsl:for-each>
    </xsl:template>    
  </xsl:stylesheet>
