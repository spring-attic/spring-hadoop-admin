<#import "/spring.ftl" as spring />
<#assign url><@spring.url relativeUrl="${servletPath}/workflows"/></#assign>
<#assign job_url><@spring.url relativeUrl="${servletPath}/jobs"/></#assign>
"workflows" : {
      "resource" : "${baseUrl}${url}",
      "uploaded" : {
<#if workflows?? && workflows?size!=0>
	<#list workflows as wf>
		"${wf.workflowName}" : {
			"valid" : "${wf.valid?string}",
			"registered" : "${wf.registered?string}",
			<#if wf.registered>
			"job" : "${baseUrl}${job_url}/${wf.jobName}",
			</#if>
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