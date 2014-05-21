<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="html" />

	<xsl:template match="/">
		<html>
			<head>
				<title>
					Shared password left to expire
				</title>

				<style type="text/css">
					p, div, span, label, a, input, textarea, button,
					input.button, input.file
					{
						font-family: Verdana,Tahoma,Arial,serif;
						font-size: small;
						font-weight: normal;
					}

					*
					{
						margin: 0px;
						padding: 0px;
					}

					body
					{
						font-family: Arial;
					}

					.topline
					{
						background-color: rgb(14, 84, 98);
						margin: 0px 0px 4px;
						border-bottom: 0.3em solid rgb(112, 163, 204);
						font-size: small;
						font-weight: normal;
						padding: 2px 3px;
						height: 30px;
						color: rgb(255, 255, 255);
					}

					.header_text
					{
						text-align: center;
						vertical-align: middle;
						margin: 5px auto;
						white-space: nowrap;
						max-width: 50%;
						overflow: hidden;
						font-size: 18px;
						font-weight: bold;
					}

					.close_page:hover
					{
						background-color: rgb(112, 163, 204);
					}

					.close_page
					{
						float: left;
						vertical-align: middle;
						width: 65px;
						height: 19px;
						border: 2px solid rgb(112, 163, 204);
						border-radius: 5px 5px 5px 5px;
						padding: 0px 5px 5px;
						margin-left: 5px;
						cursor: pointer;
						white-space: nowrap;
						text-align: center;
					}

					.results
					{
						border-width: 6px 2px 2px;
						border-style: solid;
						border-color: rgb(14, 84, 98);
						margin: 10px;
					}

					div.results th
					{
						width: 12%;
						font-family: Arial;
						font-size: 11px;
						color: black;
						background: threedface;
						border-top: 1px solid threedhighlight;
						border-bottom: 1px solid threedshadow;
						border-left: 1px solid threedhighlight;
						border-right: 1px solid threedshadow;
					}

					div.results td
					{
						font-size: small;
					}
					
					.inici
					{
						font-family: Arial,Helvetica,sans-serif;
						font-size: 11pt;
						font-weight: bold;
						color: rgb(255, 255, 255);
					}
				</style>
			</head>
			<body>
				<div id="header" class="topline">
					<div id="close_page" class="close_page" onclick="window.close();">
						<span id="closeText" class="inici">
							Close
						</span>
					</div>

					<div id="header_text" class="header_text">
						Shared password about to expire before
						<xsl:value-of select="accountssummary/expirationdate" />
					</div>
				</div>

				<div id="results" class="results">
					<xsl:choose>
						<!-- Check accounts found -->
						<xsl:when test="string-length(accountssummary/account) &gt; 0">
							<table cellspacing="0" cellpadding="0">
								<tr>
									<th>Name</th>
									<th>Full name</th>
									<th>Policy</th>
									<th>Last password set</th>
									<th>Password expiration</th>
									<th>Account type</th>
									<th>System</th>
								</tr>
		
								<xsl:for-each select="accountssummary/account">
									<tr>
										<td>
											<xsl:value-of select="name" />
										</td>
		
										<td>
											<xsl:value-of select="description" />
										</td>
		
										<td>
											<xsl:value-of select="policy" />
										</td>
		
										<td>
											<xsl:value-of select="lastpasswordset" />
										</td>
		
										<td>
											<xsl:value-of select="passwordexpiration" />
										</td>
		
										<td>
											<xsl:value-of select="accountype" />
										</td>
										
										<td>
											<xsl:value-of select="system" />
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</xsl:when>
						<xsl:otherwise>
							<p>Accounts with password near to expire not found</p>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
