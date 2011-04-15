SimpleHTTPServer in Java
========================

This is an equivalent to the [Python SimpleHTTPServer](http://docs.python.org/library/simplehttpserver.html).
It will serves the files relative to the current directory.

    $ cd ~/myDir
    $ java -jar target/SimpleHTTPServer-1.0.0.jar
    Serving HTTP on 0.0.0.0 port 8000 ...
    127.0.0.1 [Fri Apr 15 10:18:47 CEST 2011] GET HTTP/1.1 / 200
    127.0.0.1 [Fri Apr 15 10:18:47 CEST 2011] GET HTTP/1.1 /favicon.ico 404
    ...

It is usefull to test static HTML and Javascript interaction. 
It is a fork from the [Vorburger's HTTP Server](http://www.vorburger.ch/blog1/2006/06/simple-http-server-in-java.html).

How to integrate with my JUnit Tests
-----------------------------------

    import com.thoughtworks.selenium.DefaultSelenium;
    import org.junit.*;
    import org.simpleHTTPServer.SimpleHTTPServer;


    public class DemoTest {

      private static SimpleHTTPServer server;

      @BeforeClass
      public static void startSeleniumServer() throws Exception {
          server = new SimpleHTTPServer(8000, new File("."));
          server.start();
      }

      @Test
      public void testWhatYouWant() throws Exception {
          ...
      }

      @Test
      public void testWhatYouWant2() throws Exception {
          ...
      }

      @AfterClass
      public static void stopServer() throws Exception {
          server.stop();
      }
    }


How to use with maven
---------------------
Add in your pom : 

    <dependencies>
    ...
        <dependency>
            <groupId>org.simpleHTTPServer</groupId>
            <artifactId>SimpleHTTPServer</artifactId>
            <version>1.0.0</version>
            <type>jar</type>
        </dependency>
    ...
    </dependencies>
    
    <repositories>
        <repository>
            <id>filirom1-repo</id>
            <url>https://Filirom1@github.com/Filirom1/filirom1-mvn-repo/raw/master/releases</url>
        </repository>
        <repository>
            <id>maven-restlet</id>
            <url>http://maven.restlet.org</url>
        </repository>
    </repositories>

