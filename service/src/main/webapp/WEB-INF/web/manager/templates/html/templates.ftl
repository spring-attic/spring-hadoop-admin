<#import "/spring.ftl" as spring />
<div id="configuration">

	<h2>Upload Template</h2>

	<#if date??>
		<@spring.bind path="date" />
		<@spring.showErrors separator="<br/>" classOrStyle="error" /><br/>
	</#if>
	

	<#assign url><@spring.url relativeUrl="${servletPath}/templates"/></#assign>
	<form id="registerFileForm" action="${url}" method="POST" enctype="multipart/form-data" encoding="multipart/form-data">

		<input id="path" type="hidden" name="path" />
		<label for="fileXml">Template File</label><input id="file" type="file" name="file" />
		<input id="uploadFile" type="submit" value="Upload" name="uploadFile" />
		<!-- Spring JS does not support multipart forms so no Ajax here -->

	</form>
	
	<#if uploaded??>
	<p>Uploaded file: ${uploaded}</p>
	</#if>

	<br/><br/>
		
	<#if templates?? && templates?size!=0>

		<br/>

		<#assign files_url><@spring.url relativeUrl="${servletPath}/templates"/></#assign>

		<h2>Uploaded Templates</h2>

		<table title="Uploaded Files" class="bordered-table">
			<tr>
				<th>Name</th>
				<th>Version</th>
				<th>Path</th>
				<th>Filename</th>
			</tr>
			<#list templates as file>
				<#if file_index % 2 == 0>
					<#assign rowClass="name-sublevel1-even" />
				<#else>
					<#assign rowClass="name-sublevel1-odd" />
				</#if>
				<#assign filePath><a href="${files_url}/${file.fileInfo.path}">files://${file.fileInfo.path?html}</a></#assign>				
				<tr class="${rowClass}">
					<td>${file.templateName}</td>
					<td>${file.templateVersion}</td>
					<td>${filePath}</td>
					<td>${file.fileInfo.fileName}</td>
				</tr>
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
		
		<form action="${files_url}" method="POST">
			<#if stoppedCount??>
				<p>Deleted ${deletedCount} files.</p>
			</#if>
			<label for="pattern">Filename pattern</label>
			<input id="pattern" type="text" name="pattern" value="**"/>
			<input type="hidden" name="_method" value="DELETE"/>	
			<input id="delete" type="submit" value="Delete" name="delete" />
		</form>
		
		<br/><br/>
		
		<#if deletedCount??>
			<p>Deleted ${deletedCount} files.</p>
		</#if>
		
	<#else>
		<p>There are no files to display.</p>
	</#if>

</div><!-- configuration -->