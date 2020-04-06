# simple-http

**simple-http** is one http framework based one Netty. The code should be easy to check.
Currently, it only supports `Get` method. More features will be added in the future.
Feel free to fork this project or submit comments.

## Run Demo

* Clone this project
* run `make package-run` in the root directory.
* One simple API server will bind to port `8080`. It provides three APIs:
`localhost:8080/user?id=1`, `localhost:8080/user?username=user_1` 
and `localhost:8080/user/range?lower=100&upper=200`

## How to use

The framework is in the `simple-http-code` and `simple-http-example` gives one example. Here I
will describe several points.

### Add dependency

```
<dependency>
    <groupId>com.xygcoder.opensource</groupId>
    <artifactId>simple-http-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

When **simple-http** invokes the handler method, it can match using the parameter name.
As [this answer](https://stackoverflow.com/questions/2237803/can-i-obtain-method-parameter-name-using-java-reflection)
indicates, this configuration is needed:
```
<properties>
    <!-- PLUGIN VERSIONS -->
    <maven-compiler-plugin.version>3.1</maven-compiler-plugin.version>

    <!-- OTHER PROPERTIES -->
    <java.version>1.8</java.version>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>${maven-compiler-plugin.version}</version>
            <configuration>
                <!-- Original answer -->
                <compilerArgument>-parameters</compilerArgument>
                <!-- Or, if you use the plugin version >= 3.6.2 -->
                <parameters>true</parameters>
                <testCompilerArgument>-parameters</testCompilerArgument>
                <source>${java.version}</source>
                <target>${java.version}</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Add router and action

This is one example for the router class.

```java
@SimpleHttpRouter("user")
public class UserRouter {
  @SimpleHttpGet
  public void getUser(String id, String username) {
    // this function can handle query like "localhost:8080/user?id=100"
    // this function can handle query like localhost:8080/user?username="user_1"
    User user = null;
    if (!id.isEmpty()) {
        Long idLong = Long.valueOf(id);
      user = MockDb.getUserById(idLong);
    } else {
      user = MockDb.getUserByUsername(username);
    }
    String json = user == null ? "" : user.toJson();
    SimpleHttpContext context = SimpleHttpContext.getContext();
    context.getResponse().setJson(json);
  }

  @SimpleHttpGet("range")
  public void getUsers(QueryRange queryRange) {
    // this function will handle query like "localhost:8080/user/range?lower=100&upper=200"
    if (queryRange.lower == null) {
      queryRange.setLower("0");
    }
    if (queryRange.upper == null) {
      queryRange.setUpper("1000000");
    }
    List<User> users = MockDb.getUsersByIdRange(queryRange.lower,
            queryRange.upper);
    String json = User.toJson(users);
    SimpleHttpContext context = SimpleHttpContext.getContext();
    context.getResponse().setJson(json);
  }

  public static class QueryRange {
    public Long lower;
    public Long upper;

    public void setLower(String lower) {
      this.lower = Long.parseLong(lower);
    }

    public void setUpper(String upper) {
      this.upper = Long.parseLong(upper);
    }
  }
}
```
* The handler can receive several kinds of parameters:
    1. `String`: if there is query param with the same name in the queries, this
    parameter will be set with the query value or empty string if not exists in
    queries.
    2. `Map<String, String>`: simple-http will store the query parameters to this map
    3. java bean. For the java bean, simple-http will search for functions starting with "set"
    and following the variable name. This variable name is used to get value from queries.
    4. `SimpleHttpContext`. simple-http will set this context using the context binding to current handler thread.
    Actually, you can also directly call `SimpleHttpContext context = SimpleHttpContext.getContext();` to get current
    context.
* To write response. You can fetch `SimpleHttpResponse` from `SimpleHttpContext` and write text, json or html.

### Run server

In the main function, call
```java
public class TestServer {
  public static void main(String[] args) throws Exception {
    SimpleHttpServer simpleHttpServer = new SimpleHttpServer();
    simpleHttpServer.run(TestServer.class);
  }
}
```
simple-http will search for handler methods using the package of the class passed in.
If you want to configure the packageName explicitly, add `packageName=xxx` in `resources/application.properties`.
The default port is 8080, you can also configure `port=xxxx` in `resources/application.properties`.

## Future plan

1. Add support for more methods (PUT, DELETE, POST, etc)
2. Add support for more url matches like
```
path('articles/2003/', views.special_case_2003),
path('articles/<int:year>/', views.year_archive),
path('articles/<int:year>/<int:month>/', views.month_archive),
path('articles/<int:year>/<int:month>/<slug:slug>/', views.article_detail),
```
