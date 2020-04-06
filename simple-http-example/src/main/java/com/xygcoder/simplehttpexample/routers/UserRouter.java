package com.xygcoder.simplehttpexample.routers;

import com.xygcoder.opensource.annotations.SimpleHttpGet;
import com.xygcoder.opensource.annotations.SimpleHttpRouter;
import com.xygcoder.opensource.context.SimpleHttpContext;
import com.xygcoder.simplehttpexample.MockDb;
import com.xygcoder.simplehttpexample.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@SimpleHttpRouter("user")
public class UserRouter {
  private final static Logger LOG = LoggerFactory.getLogger(UserRouter.class);

  @SimpleHttpGet
  public void getUser(String id, String username) {
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

  @SimpleHttpGet("testmap")
  public void getUsers(Map<String, String> params) {
    LOG.info(params.toString());
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
