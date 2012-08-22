
Make sure Spring Hadoop Admin is running:
open new terminal window, and change to "spring-hadoop-admin/scalable-service/master" directory.
$ ../../gradlew jettyRun

Upload one job following Readme in "spring-hadoop-admin" root directory.

1. build:
$ ../../gradelw 

2. run
$ java -jar build/libs/plugin-admin-1.0.0.BUILD-SNAPSHOT.jar

3. set Spring Hadoop Admin service URl:
springHadoopAdmin>service target --url http://localhost:8081/spring-hadoop-admin
springHadoopAdmin>info

4. list jobs: 
springHadoopAdmin>job list

5. run jobs:
springHadoopAdmin>job launch --jobName {jobName}



to run Dfs shell:

1. run
$ java -jar build/libs/plugin-admin-1.0.0.BUILD-SNAPSHOT.jar

2. set HDFS URL
springHadoopAdmin>cfg fs --namenode hdfs://localhost:9000
springHadoopAdmin>info

3. run commands:
springHadoopAdmin>fs ls /


eclipse:
mvn eclipse:eclipse
File -> Import... -> Existing Project ...
 
