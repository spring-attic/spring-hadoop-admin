<#import "/spring.ftl" as spring />
<#assign url><@spring.url relativeUrl="${servletPath}/templates"/></#assign>
"templates" : {
      "resource" : "${baseUrl}${url}",
      "uploaded" : [
<#if templates?? && templates?size!=0>
	<#list templates as file>
		{
			"name" : "${file.templateName}",
			"version" : "${file.templateVersion}",
			"resource" : "${baseUrl}${url}/${file.fileInfo.path}",
			"path" : "files://${file.fileInfo.path?string}"
		}<#if file_index != templates?size-1>,</#if>
	</#list>	
</#if>
      ]
   }