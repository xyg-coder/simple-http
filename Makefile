clean:
	mvn clean

package:
	mvn clean
	mvn package

run:
	java -jar ${CURDIR}/simple-http-example/target/simple-http-example-1.0-SNAPSHOT.jar

package-run:
	mvn clean
	mvn package
	java -jar ${CURDIR}/simple-http-example/target/simple-http-example-1.0-SNAPSHOT.jar
