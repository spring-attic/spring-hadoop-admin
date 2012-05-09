
Make sure Spring Hadoop Admin is running:
open new terminal window, and change to "spring-hadoop-admin" root directory.
$ ./gradlew jettyRun

Upload one job following Readme in "spring-hadoop-admin" root directory.

1. build:
$ mvn clean package

2. run
$ java -jar target/admin-cli-1.0.0.BUILD-SNAPSHOT.jar

3. set Spring Hadoop Admin service URl:
springHadoopAdmin>target --url http://localhost:8081/spring-hadoop-admin
springHadoopAdmin>info

4. list jobs: 
springHadoopAdmin>jobs-all

5. run jobs:
springHadoopAdmin>launch-job --jobName {jobName}



to run Dfs shell:

1. run
$ java -jar target/admin-cli-1.0.0.BUILD-SNAPSHOT.jar

2. set HDFS URL
springHadoopAdmin>fs.default.name --url hdfs://localhost:9000
springHadoopAdmin>info

3. run commands:
springHadoopAdmin>dfs --ls

eclipse:
mvn eclipse:eclipse
File -> Import... -> Existing Project ...
 