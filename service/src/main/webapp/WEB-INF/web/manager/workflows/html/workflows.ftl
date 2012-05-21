<#import "/spring.ftl" as spring />
<div id="configuration">

	<h2>Upload Workflow Artifacts</h2>

	<#if date??>
		<@spring.bind path="date" />
		<@spring.showErrors separator="<br/>" classOrStyle="error" /><br/>
	</#if>
	

	<#assign url><@spring.url relativeUrl="${servletPath}/workflows"/></#assign>
	<#assign job_url><@spring.url relativeUrl="${servletPath}/jobs"/></#assign>
	<form id="registerFileForm" action="${url}" method="POST" enctype="multipart/form-data" encoding="multipart/form-data">

		<ol>
			<li><label for="filePath">Workflow Name</label><input id="path" type="text" name="path" /><li/>
			<li><label for="fileXml">Workflow Artifacts</label><input id="file" type="file" name="file" /></li>
		</ol>
		<input id="uploadFile" type="submit" value="Upload" name="uploadFile" />
		<!-- Spring JS does not support multipart forms so no Ajax here -->

	</form>
	
	<#if uploaded??>
	<p>Uploaded Workflow File: ${uploaded}</p>
	</#if>

	<br/><br/>
		
	<#if workflows?? && workflows?size!=0>

		<br/>

		<#assign files_url><@spring.url relativeUrl="${servletPath}/workflows"/></#assign>

		<h2>Uploaded Workflows</h2>

		<table title="Uploaded Workflows" class="bordered-table" width="100%">
			<tr>
				<th width="10%">Workflow Name</th>
				<th width="5%">Valid</th>
				<th width="5%">Registered</th>
				<th width="20%">Job</th>
				<th width="20%">File Path</th>
				<th width="20%">File Name</th>
				<th width="20%">Operation</th>
			</tr>
			<#list workflows as wf>
				<#if wf_index % 2 == 0>
					<#assign rowClass="name-sublevel1-even" />
				<#else>
					<#assign rowClass="name-sublevel1-odd" />
				</#if>
				<tr class="${rowClass}">
					<td rowspan="${wf.files?size}">${wf.workflowName}</td>
					<td rowspan="${wf.files?size}"><input type="checkbox" disabled="true"
					<#if wf.valid>
					checked="checked"
					</#if>					
					/></td>
					<td rowspan="${wf.files?size}"><input type="checkbox" disabled="true"
					<#if wf.registered>
					checked="checked"
					</#if>					
					/></td>
					<td rowspan="${wf.files?size}">
					<#if wf.registered>
						<a href="${baseUrl}${job_url}/${wf.jobName}">${wf.jobName}</a>
					</#if>	
					</td>
					<#assign filePath><a href="${files_url}/${wf.files?first.path}">files://${wf.files?first.path?html}</a></#assign>
					<td>${filePath}</td>
					<td>${wf.files?first.fileName}</td>
					<td rowspan="${wf.files?size}">
					<form action="${url}" method="POST" enctype="multipart/form-data" encoding="multipart/form-data">
						<input id="path" type="hidden" name="path" value="${wf.jobName}"/>
						<input id="file" type="file" name="file" />
						<input id="workflow_upload" type="submit" value="Upload"/>
					</form>
					<br/>
					<form action="${url}/register" method="POST">
						<input id="path" type="hidden" name="path" value="${wf.jobName}"/>
						<input id="workflow_register" type="submit" value="Register"/>
					</form>
					<form action="${url}" method="POST">
						<input id="pattern" type="hidden" name="pattern" value="${wf.jobName}/**"/>
						<input type="hidden" name="_method" value="DELETE"/>	
						<input id="workflow_delete" type="submit" value="Delete"/>
					</form>
					</td>
				</tr>
				<#list wf.files as file>
					<#if file_index == 0>
					<#else>					
						<tr class="${rowClass}">
							<#assign filePath><a href="${files_url}/${file.path}">files://${file.path?html}</a></#assign>
							<td>${filePath}</td>
							<td>${file.fileName}</td>
						</tr>
					</#if>
				</#list>				
			</#list>
		</table>

		
		<!--
		<form action="${files_url}" method="POST">
			<#if stoppedCount??>
				<p>Deleted ${deletedCount} files.</p>
			</#if>
			<label for="pattern">Filename pattern</label>
			<input id="pattern" type="text" name="pattern" value="**"/>
			<input type="hidden" name="_method" value="DELETE"/>	
			<input id="delete" type="submit" value="Delete" name="delete" />
		</form>
		-->
		
		<br/><br/>
		
		<#if deletedCount??>
			<p>Deleted ${deletedCount} files.</p>
		</#if>
		
	<#else>
		<p>There are no files to display.</p>
	</#if>

</div><!-- configuration -->