package fr.bobinho.bcrate.api.stream;

import fr.bobinho.bcrate.api.validate.BValidate;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Bobinho IndexedStream library
 */
public interface IndexedStream<K> {

    /**
     * Creates a new IndexedStream from a collection
     *
     * @param collection the collection
     * @param <K>        the type of IndexedStream
     * @return the IndexedStream
     */
    static @Nonnull <K> IndexedStream<K> from(@Nonnull Collection<K> collection) {
        BValidate.notNull(collection);

        Map<K, Integer> map = new HashMap<>();
        int i = 0;

        for (K element : collection) {
            map.put(element, i++);
        }

        return from(map);
    }

    /**
     * Creates a new IndexedStream from an array
     *
     * @param array the array
     * @param <K>   the type of IndexedStream
     * @return the IndexedStream
     */
    static @Nonnull <K> IndexedStream<K> from(@Nonnull K[] array) {
        BValidate.notNull(array);

        return from(Arrays.stream(array).toList());
    }

    /**
     * Creates a new IndexedStream from a map
     *
     * @param map the map
     * @param <K> the type of IndexedStream
     * @return the IndexedStream
     */
    static @Nonnull <K> IndexedStream<K> from(@Nonnull Map<K, Integer> map) {
        BValidate.notNull(map);

        return from(map.entrySet().stream());
    }

    /**
     * Creates a new IndexedStream from a stream
     *
     * @param stream the stream
     * @param <K>    the type of IndexedStream
     * @return the IndexedStream
     */
    static @Nonnull <K> IndexedStream<K> from(Stream<Map.Entry<K, Integer>> stream) {
        return () -> stream;
    }

    /**
     * Creates a new IndexedStream from a stream and a function
     *
     * @param stream   the stream
     * @param function the function
     * @param <K>      the type of IndexedStream
     * @return the IndexedStream
     */
    static @Nonnull <K> IndexedStream<K> from(@Nonnull Stream<K> stream, @Nonnull Function<? super K, Integer> function) {
        BValidate.notNull(stream);
        BValidate.notNull(function);

        return () -> stream.map(k -> new AbstractMap.SimpleImmutableEntry<>(k, function.apply(k)));
    }

    /**
     * Gets an IndexedStream consisting of the distinct elements of this stream
     *
     * @return the new IndexedStream
     */
    default @Nonnull IndexedStream<K> distinct() {
        return from(entries().distinct());
    }

    /**
     * Gets an IndexedStream consisting of the elements of this stream, additionally performing the provided action on each element as elements are consumed from the resulting stream
     *
     * @param consumer the consumer
     * @return the new IndexedStream
     */
    default @Nonnull IndexedStream<K> peek(@Nonnull BiConsumer<? super K, Integer> consumer) {
        BValidate.notNull(consumer);

        return from(entries().peek(e -> consumer.accept(e.getKey(), e.getValue())));
    }

    /**
     * Gets an IndexedStream consisting of the remaining elements of this stream after discarding the first n elements of the stream
     *
     * @param n the number of leading elements to skip
     * @return the new IndexedStream
     */
    default @Nonnull IndexedStream<K> skip(long n) {
        return from(entries().skip(n));
    }

    /**
     * Gets an IndexedStream consisting of the elements of this stream, truncated to be no longer than maxSize in length
     *
     * @param maxSize the max size
     * @return the new IndexedStream
     */
    default @Nonnull IndexedStream<K> limit(long maxSize) {
        return from(entries().limit(maxSize));
    }

    /**
     * Gets an IndexedStream consisting of the elements of this stream without the elements whose key does not satisfy the predicate
     *
     * @param mapper the mapper
     * @return the new IndexedStream
     */
    default @Nonnull IndexedStream<K> filterKey(@Nonnull Predicate<? super K> mapper) {
        BValidate.notNull(mapper);

        return from(entries().filter(e -> mapper.test(e.getKey())));
    }

    /**
     * Gets an IndexedStream consisting of the elements of this stream without the elements whose value does not satisfy the predicate
     *
     * @param mapper the mapper
     * @return the new IndexedStream
     */
    default @Nonnull IndexedStream<K> filterValue(@Nonnull Predicate<Integer> mapper) {
        BValidate.notNull(mapper);

        return from(entries().filter(e -> mapper.test(e.getValue())));
    }

    /**
     * Gets an IndexedStream consisting of the elements of this stream that match the given predicate
     *
     * @param mapper the mapper
     * @return the new IndexedStream
     */
    default @Nonnull IndexedStream<K> filter(@Nonnull BiPredicate<? super K, Integer> mapper) {
        BValidate.notNull(mapper);

        return from(entries().filter(e -> mapper.test(e.getKey(), e.getValue())));
    }

    /**
     * Gets an IndexedStream consisting of the results of applying the given function to the elements of this stream
     *
     * @param mapper the mapper
     * @param <R>    the type of the result
     * @return the new IndexedStream
     */
    default @Nonnull <R> Stream<R> map(@Nonnull BiFunction<? super K, Integer, ? extends R> mapper) {
        BValidate.notNull(mapper);

        return entries().map(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    /**
     * Gets a DoubleStream consisting of the results of applying the given function to the elements of this stream
     *
     * @param mapper the mapper
     * @return the new double stream
     */
    default @Nonnull DoubleStream mapToDouble(@Nonnull ToDoubleBiFunction<? super K, Integer> mapper) {
        BValidate.notNull(mapper);

        return entries().mapToDouble(e -> mapper.applyAsDouble(e.getKey(), e.getValue()));
    }

    /**
     * Gets an IntStream consisting of the results of applying the given function to the elements of this stream
     *
     * @param mapper the mapper
     * @return the new int stream
     */
    default @Nonnull IntStream mapToInt(@Nonnull ToIntBiFunction<? super K, Integer> mapper) {
        BValidate.notNull(mapper);

        return entries().mapToInt(e -> mapper.applyAsInt(e.getKey(), e.getValue()));
    }

    /**
     * Gets a LongStream consisting of the results of applying the given function to the elements of this stream
     *
     * @param mapper the mapper
     * @return the new long stream
     */
    default @Nonnull LongStream mapToLong(@Nonnull ToLongBiFunction<? super K, Integer> mapper) {
        BValidate.notNull(mapper);

        return entries().mapToLong(e -> mapper.applyAsLong(e.getKey(), e.getValue()));
    }

    /**
     * Gets an IndexedStream consisting of the results of replacing each element of this stream with the contents of a mapped stream produced by applying the provided mapping function to each element
     *
     * @param mapper the mapper
     * @param <R>    the type of the new stream
     * @return the new stream
     */
    default @Nonnull <R> Stream<R> flatMapToObj(@Nonnull BiFunction<? super K, Integer, ? extends Stream<R>> mapper) {
        BValidate.notNull(mapper);

        return entries().flatMap(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    /**
     * Gets a DoubleStream consisting of the results of replacing each element of this stream with the contents of a mapped stream produced by applying the provided mapping function to each element
     *
     * @param mapper the mapper
     * @return the new double stream
     */
    default @Nonnull DoubleStream flatMapToDouble(@Nonnull BiFunction<? super K, Integer, ? extends DoubleStream> mapper) {
        BValidate.notNull(mapper);

        return entries().flatMapToDouble(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    /**
     * Gets an IntStream consisting of the results of replacing each element of this stream with the contents of a mapped stream produced by applying the provided mapping function to each element
     *
     * @param mapper the mapper
     * @return the new int stream
     */
    default @Nonnull IntStream flatMapToInt(@Nonnull BiFunction<? super K, Integer, ? extends IntStream> mapper) {
        BValidate.notNull(mapper);

        return entries().flatMapToInt(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    /**
     * Gets a LongStream consisting of the results of replacing each element of this stream with the contents of a mapped stream produced by applying the provided mapping function to each element
     *
     * @param mapper the mapper
     * @return the new long stream
     */
    default @Nonnull LongStream flatMapToLong(@Nonnull BiFunction<? super K, Integer, ? extends LongStream> mapper) {
        BValidate.notNull(mapper);

        return entries().flatMapToLong(e -> mapper.apply(e.getKey(), e.getValue()));
    }

    /**
     * Gets an IndexedStream consisting of the elements of this stream, sorted by key according to the provided comparator
     *
     * @param comparator the comparator
     * @return the new IndexedStream
     */
    default @Nonnull IndexedStream<K> sortedByKey(@Nonnull Comparator<? super K> comparator) {
        BValidate.notNull(comparator);

        return from(entries().sorted(Map.Entry.comparingByKey(comparator)));
    }

    /**
     * Gets an IndexedStream consisting of the elements of this stream, sorted by value according to the provided comparator
     *
     * @param comparator the comparator
     * @return the new IndexedStream
     */
    default @Nonnull IndexedStream<K> sortedByValue(@Nonnull Comparator<Integer> comparator) {
        BValidate.notNull(comparator);

        return from(entries().sorted(Map.Entry.comparingByValue(comparator)));
    }

    /**
     * Checks if all elements of this stream match the provided predicate
     *
     * @param predicate the predicate
     * @return true if all elements of this stream match the provided predicate, false otherwise
     */
    default boolean allMatch(@Nonnull BiPredicate<? super K, Integer> predicate) {
        BValidate.notNull(predicate);

        return entries().allMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    /**
     * Checks if any element of this stream match the provided predicate
     *
     * @param predicate the predicate
     * @return true if any element of this stream match the provided predicate, false otherwise
     */
    default boolean anyMatch(@Nonnull BiPredicate<? super K, Integer> predicate) {
        BValidate.notNull(predicate);

        return entries().anyMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    /**
     * Checks if no element of this stream match the provided predicate
     *
     * @param predicate the predicate
     * @return true if no element of this stream match the provided predicate, false otherwise
     */
    default boolean noneMatch(@Nonnull BiPredicate<? super K, Integer> predicate) {
        BValidate.notNull(predicate);

        return entries().noneMatch(e -> predicate.test(e.getKey(), e.getValue()));
    }

    /**
     * Gets the count of elements in this stream
     *
     * @return the count of elements in this stream
     */
    default long count() {
        return entries().count();
    }

    /**
     * Gets a stream of all entries of the IndexedStream
     *
     * @return the stream with all entries of the IndexedStream
     */
    Stream<Map.Entry<K, Integer>> entries();

    /**
     * Gets a stream of all keys of the IndexedStream
     *
     * @return the stream with all keys of the IndexedStream
     */
    default @Nonnull Stream<K> keys() {
        return entries().map(Map.Entry::getKey);
    }

    /**
     * Gets a stream of all values of the IndexedStream
     *
     * @return the stream with all values of the IndexedStream
     */
    default @Nonnull Stream<Integer> values() {
        return entries().map(Map.Entry::getValue);
    }

    /**
     * Gets the maximum element of this stream by key according to the provided comparator
     *
     * @param comparator the comparator
     * @return the maximum element of this stream by key according to the provided comparator
     */
    default @Nonnull Optional<Map.Entry<K, Integer>> maxByKey(@Nonnull Comparator<? super K> comparator) {
        BValidate.notNull(comparator);

        return entries().max(Map.Entry.comparingByKey(comparator));
    }

    /**
     * Gets the maximum element of this stream by value according to the provided comparator
     *
     * @param comparator the comparator
     * @return the maximum element of this stream by value according to the provided comparator
     */
    default @Nonnull Optional<Map.Entry<K, Integer>> maxByValue(@Nonnull Comparator<Integer> comparator) {
        BValidate.notNull(comparator);

        return entries().max(Map.Entry.comparingByValue(comparator));
    }

    /**
     * Gets the minimum element of this stream by key according to the provided comparator
     *
     * @param comparator the comparator
     * @return the minimum element of this stream by key according to the provided comparator
     */
    default @Nonnull Optional<Map.Entry<K, Integer>> minByKey(@Nonnull Comparator<? super K> comparator) {
        BValidate.notNull(comparator);

        return entries().min(Map.Entry.comparingByKey(comparator));
    }

    /**
     * Gets the minimum element of this stream by value according to the provided comparator
     *
     * @param comparator the comparator
     * @return the minimum element of this stream by value according to the provided comparator
     */
    default @Nonnull Optional<Map.Entry<K, Integer>> minByValue(@Nonnull Comparator<Integer> comparator) {
        BValidate.notNull(comparator);

        return entries().min(Map.Entry.comparingByValue(comparator));
    }

    /**
     * Performs an action for each element of this stream
     *
     * @param action the action
     */
    default void forEach(@Nonnull BiConsumer<? super K, Integer> action) {
        BValidate.notNull(action);

        entries().forEach(e -> action.accept(e.getKey(), e.getValue()));
    }

    /**
     * Performs an action for each element of this IndexedStream, in the encounter order
     *
     * @param action the action
     */
    default void forEachOrdered(@Nonnull BiConsumer<? super K, Integer> action) {
        BValidate.notNull(action);

        entries().forEachOrdered(e -> action.accept(e.getKey(), e.getValue()));
    }

    /**
     * Accumulates the elements of this IndexedStream into a Map
     *
     * @return a new List containing the IndexedStream elements
     */
    default @Nonnull Map<K, Integer> toMap() {
        return entries().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Accumulates elements into a Map whose keys and values are the result of applying the provided mapping function
     *
     * @param operator the operator
     * @return a new List containing the IndexedStream mapped elements
     */
    default @Nonnull Map<K, Integer> toMap(@Nonnull BinaryOperator<Integer> operator) {
        BValidate.notNull(operator);

        return entries().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, operator));
    }

}