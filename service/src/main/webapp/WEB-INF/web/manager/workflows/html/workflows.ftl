<#import "/spring.ftl" as spring />
<div id="configuration">

	<h2>Upload Workflow Artifacts</h2>

	<#if date??>
		<@spring.bind path="date" />
		<@spring.showErrors separator="<br/>" classOrStyle="error" /><br/>
	</#if>
	

	<#assign url><@spring.url relativeUrl="${servletPath}/workflows"/></#assign>
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

		<table title="Uploaded Workflows" class="bordered-table">
			<tr>
				<th>Workflow Name</th>
				<th>Valid</th>
				<th>Registered</th>
				<th>Path</th>
				<th>File Name</th>
				<th>Operation</th>
			</tr>
			<#list workflows as wf>
				<#if wf_index % 2 == 0>
					<#assign rowClass="name-sublevel1-even" />
				<#else>
					<#assign rowClass="name-sublevel1-odd" />
				</#if>
				<tr class="${rowClass}">
					<td rowspan="${wf.files?size}">${wf.jobName}</td>
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
					<#assign filePath><a href="${files_url}/${wf.files?first.path}">files://${wf.files?first.path?html}</a></#assign>
					<td>${filePath}</td>
					<td>${wf.files?first.fileName}</td>
					<td rowspan="${wf.files?size}">
						<form action="${files_url}" method="POST">
							<input id="workflow_upload" type="button" value="Upload"/>
							<input id="workflow_register" type="button" value="Register"/>
							<input id="workflow_delete" type="button" value="Delete"/>							
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
		<#if startFile??>
			<ul class="controlLinks">
				<li>Rows: ${startFile}-${endFile} of ${totalFiles}</li> 
				<#if nextFile??><li><a href="${files_url}?startFile=${nextFile}&pageSize=${pageSize!20}">Next</a></li></#if>
				<#if previousFile??><li><a href="${files_url}?startFile=${previousFile}&pageSize=${pageSize!20}">Previous</a></li></#if>
				<!-- TODO: enable pageSize editing -->
				<li>Page Size: ${pageSize!20}</li>
			</ul>
		</#if>
		
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