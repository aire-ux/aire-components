package com.aire.ux.plan;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * some operations are "widening" such as :nth-child, which means that they can "select" items that
 * have been excluded by previous selectors
 *
 * @param <T> the type contained within this set
 */
public interface WorkingSet<T> extends Iterable<T> {

  @Nonnull
  public static <T> WorkingSet<T> create() {
    return new LinkedWorkingSet<>();
  }

  @Nonnull
  public static <T> WorkingSet<T> create(@Nonnull Collection<? extends T> items) {
    return new LinkedWorkingSet<>(new LinkedHashSet<>(items), new LinkedHashSet<>());
  }

  @Nonnull
  public static <T> WorkingSet<T> backedBy(@Nonnull Set<T> items) {
    return new LinkedWorkingSet<T>(items, new LinkedHashSet<>());
  }

  static <T> WorkingSet<T> empty() {
    return sized(0, 0);
  }

  static <T> WorkingSet<T> sized(int inclusions) {
    return sized(inclusions, inclusions);
  }

  public static <T> WorkingSet<T> sized(int inclusions, int exclusions) {
    return new LinkedWorkingSet<>(new LinkedHashSet<>(inclusions), new LinkedHashSet<>(exclusions));
  }

  public static <T> WorkingSet<T> of(T... values) {
    return backedBy(new LinkedHashSet<>(Set.of(values)));
  }

  @SuppressWarnings("unchecked")
  static <T> WorkingSet<T> withExclusions(WorkingSet<T> workingSet) {
    return new LinkedWorkingSet<T>(
        new LinkedHashSet<>(), new LinkedHashSet<>(workingSet.exclusions()));
  }

  int size();

  boolean exclude(@Nonnull T value);

  boolean include(@Nonnull T value);

  boolean isExcluded(@Nonnull T value);

  boolean add(@Nonnull T value);

  boolean contains(@Nonnull T value);

  boolean remove(@Nonnull T value);

  boolean addAll(@Nonnull WorkingSet<T> values);

  boolean addAll(@Nonnull Collection<T> values);

  @Nonnull
  Collection<T> exclusions();

  @Nonnull
  Stream<T> stream();

  Set<T> results();

  void excludeAll(WorkingSet<T> workingSet);

  WorkingSet<T> flip();

  WorkingSet<T> intersect(WorkingSet<T> exclusions);

  void removeAll(Iterable<T> exclusions);
}

class LinkedWorkingSet<T> implements WorkingSet<T> {

  /** include list */
  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  final Set<T> exclusions;

  /** exclude list */
  final Set<T> inclusions;

  LinkedWorkingSet() {
    this(new LinkedHashSet<>(), new LinkedHashSet<>());
  }

  LinkedWorkingSet(final Set<T> inclusions, final Set<T> exclusions) {
    this.inclusions = inclusions;
    this.exclusions = exclusions;
  }

  @Override
  public int size() {
    return inclusions.size();
  }

  @Override
  public boolean exclude(T value) {
    return exclusions.add(value);
  }

  @Override
  public boolean include(T value) {
    exclusions.remove(value);
    return inclusions.add(value);
  }

  @Override
  public boolean isExcluded(T value) {
    return exclusions.contains(value);
  }

  @Override
  public boolean add(T value) {
    return inclusions.add(value);
  }

  @Override
  public boolean contains(@NotNull T value) {
    return inclusions.contains(value);
  }

  @Override
  public boolean remove(T value) {
    return inclusions.remove(value);
  }

  @Override
  public boolean addAll(@NotNull WorkingSet<T> values) {
    if (values instanceof LinkedWorkingSet vs) {
      var result = inclusions.addAll(vs.inclusions);
      result &= exclusions.addAll(vs.exclusions);
      return result;
    } else {
      boolean result = true;
      for (val v : values) {
        result &= add(v);
      }
      for (val v : values.exclusions()) {
        result &= add(v);
      }
      return result;
    }
  }

  @Override
  public boolean addAll(@NotNull Collection<T> values) {
    return inclusions.addAll(values);
  }

  @NotNull
  @Override
  public @Nonnull Collection<T> exclusions() {
    return exclusions;
  }

  @NotNull
  @Override
  public Stream<T> stream() {
    return inclusions.stream();
  }

  @Override
  public Set<T> results() {
    return Collections.unmodifiableSet(inclusions);
  }

  @Override
  public void excludeAll(WorkingSet<T> workingSet) {
    if (workingSet instanceof LinkedWorkingSet ws) {
      exclusions.addAll(ws.exclusions);
    }
  }

  @Override
  public WorkingSet<T> flip() {
    return new LinkedWorkingSet<>(exclusions, inclusions);
  }

  @Override
  public WorkingSet<T> intersect(WorkingSet<T> other) {
    val ws = (LinkedWorkingSet) other;
    inclusions.removeAll(ws.inclusions);
    exclusions.removeAll(ws.exclusions);
    return this;
  }

  @Override
  public void removeAll(Iterable<T> exclusions) {
    for (val exclusion : exclusions) {
      inclusions.remove(exclusion);
    }
  }

  @Nonnull
  @Override
  public Iterator<T> iterator() {
    return inclusions.iterator();
  }

  @Override
  public String toString() {
    return "WorkingSet[inclusions: \n %s \n, exclusions: \n %s \n]"
        .formatted(inclusions, exclusions);
  }
}
