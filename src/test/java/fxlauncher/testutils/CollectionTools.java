package fxlauncher.testutils;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class CollectionTools {

  @SuppressWarnings("unchecked")
  public static <T> Set<T> generateSet(T... members) {
    return Stream.of(members).collect(toSet());
  }

  @SuppressWarnings("unchecked")
  public static <T> List<T> generateList(T... members) {
    return Stream.of(members).collect(toList());
  }

  public static <K, V> Map<K, V> generateMap(K[] keys, V[] values) {
    if (keys.length != values.length)
      throw new IllegalArgumentException("Must be the same number of keys as values");
    if (Stream.of(keys).distinct().count() != keys.length) {
      throw new IllegalArgumentException("Keys must be unique.");
    }

    return range(0, keys.length).boxed().collect(toMap(i -> keys[i], i -> values[i]));
  };

  public static <K, V> void assertMapEquals(Map<K, V> thisMap, Map<K, V> thatMap) {
    assertEquals(thisMap.keySet(), thatMap.keySet());
    thisMap.keySet().forEach(key -> assertEquals(thisMap.get(key), thatMap.get(key)));
  }

  @SuppressWarnings("unchecked")
  public static <T> Set<T> asSet(T... items) {
    return Stream.of(items).collect(toSet());
  }

  public static <T> Set<T> asSet(Collection<T> items) {
    return items.stream().collect(toSet());
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] asArray(T... items) {
    return items;
  }
}
