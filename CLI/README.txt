
Make sure Spring Hadoop Admin is running:
open new terminal window, and change to "spring-hadoop-admin" root directory.
$ ./gradlew jettyRun



1. build:
$ mvn clean package

2. run
$ java -jar target/admin-cli-1.0.0.BUILD-SNAPSHOT.jar

3. set Spring Hadoop Admin service URl:
springHadoopAdmin>target http://localhost:8081/spring-hadoop-admin

4. list jobs: 
springHadoopAdmin>jobs-all

5. run jobs:
springHadoopAdmin>job-execute --jobName {jobName}



