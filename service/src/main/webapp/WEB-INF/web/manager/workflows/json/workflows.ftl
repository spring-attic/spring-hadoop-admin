<#import "/spring.ftl" as spring />
<#assign url><@spring.url relativeUrl="${servletPath}/workflows"/></#assign>
"workflows" : {
      "resource" : "${baseUrl}${url}",
      "uploaded" : {
<#if workflows?? && workflows?size!=0>
	<#list workflows as wf>
		"${wf.jobName}" : {
			"valid" : "${wf.valid?string}",
			"registered" : ${wf.registered?string},
			"files" : {
				<#list wf.files as file>
					"${file.fileName}" : {
						"resource" : "${baseUrl}${url}/${file.path}",
						""path" : "files://${file.path?string}"
					}<#if file_index != wf.files?size-1>,</#if>
				</#list>
			}
		}<#if wf_index != workflows?size-1>,</#if>
	</#list>	
</#if>
      }
   }