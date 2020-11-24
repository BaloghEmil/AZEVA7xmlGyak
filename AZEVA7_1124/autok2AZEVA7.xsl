<?xml version="1.0" encoding="UTF-8"?>
  <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0" xmlns:kl="kl.xml">
  
    <xsl:template match="/" >
       <xsl:value-of select="count ( //auto[number(ar) &#62; 30000])"></xsl:value-of>
    </xsl:template>
  
  </xsl:stylesheet>