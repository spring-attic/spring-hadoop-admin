spring-hadoop-admin
===================

admin application for spring hadoop.

# checkout the code

$ git checkout git@github.com:SpringSource/spring-hadoop-admin.git

# build & run

$ ./gradlew jettyRun

# Upload spring hadoop example

3a.In the browser, open "http://localhost:8081/spring-hadoop-admin"

3b.Click "File" menu

3c.In the "Server Path" textfield, input "wordcount"

3d.Click "Browse..." button, and nagivate to "spring-hadoop-admin/samples/wordcount-batch" folder,

3e.Select "hadoop-examples-1.0.0.jar

3f.Cick "Upload" button,

repeat 3c to 3f and upload "data.jar", "hadoop.properties" and "wordcount-context.xml" respectively.


# check uploaded jobs

4a. Click "Jobs" mens, you should see the uploaded jobs. Run the job following Spring Batch Admin guide.

 
