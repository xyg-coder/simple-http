package com.xygcoder.simplehttpexample;

import com.xygcoder.simplehttpexample.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MockDb {
  public static User getUserById(Long id) {
    return User.getMockUserTables().get(id);
  }

  public static User getUserByUsername(String username) {
    for (User user : User.getMockUserTables().values()) {
      if (user.getUsername().equals(username)) {
        return user;
      }
    }
    return null;
  }

  public static List<User> getUsersByIdRange(Long idLower, Long idUpper) {
    TreeMap<Long, User> mockDb = (TreeMap<Long, User>) User.getMockUserTables();
    List<User> users = new ArrayList<>();
    Map.Entry<Long, User> curEntry = mockDb.floorEntry(idLower);
    while (curEntry != null && curEntry.getKey() <= idUpper) {
      users.add(curEntry.getValue());
      curEntry = mockDb.floorEntry(curEntry.getKey() + 1);
    }
    return users;
  }
}
