package com.xygcoder.simplehttpexample.models;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class User {
  private long id;

  public String getUsername() {
    return username;
  }

  private String username;
  private Gender gender;

  public User(long id, String username, Gender gender) {
    this.id = id;
    this.username = username;
    this.gender = gender;
  }

  public enum Gender {
    MALE,
    FEMALE
  }

  public String toJson() {
    return String.format("{\"id\": \"%s\", \"username\": \"%s\", \"gender\": \"%s\"}",
            id, username, gender.name());
  }

  public static String toJson(List<User> users) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    boolean firstUser = true;
    for (User user : users) {
      if (!firstUser) {
        sb.append(",");
      }
      sb.append(user.toJson());
      firstUser = false;
    }
    sb.append("]");
    return sb.toString();
  }

  public static <T extends Enum<?>> T randomEnum(Random random, Class<T> clazz){
    int x = random.nextInt(clazz.getEnumConstants().length);
    return clazz.getEnumConstants()[x];
  }

  public static Map<Long, User> getMockUserTables() {
    Map<Long, User> userTable = new TreeMap<>();
    Random random = new Random();
    for (int i = 0; i < 1000000; ++i) {
      userTable.put(Long.valueOf(i),
              new User(i,
                      String.format("user_%d", i),
                      randomEnum(random, Gender.class)));
    }
    return userTable;
  }
}
