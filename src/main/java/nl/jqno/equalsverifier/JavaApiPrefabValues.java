/*
 * Copyright 2010-2015 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.ConditionalPrefabValueBuilder;
import nl.jqno.equalsverifier.internal.PrefabValues;
import nl.jqno.equalsverifier.internal.TypeTag;

import javax.naming.Reference;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.*;

/**
 * Creates instances of classes for use in a {@link PrefabValues} object.
 *
 * Contains hand-made instances of well-known Java API classes that cannot be
 * instantiated dynamically because of an internal infinite recursion of types,
 * or other issues.
 *
 * @author Jan Ouwens
 */
public final class JavaApiPrefabValues {
    private PrefabValues prefabValues;

    /**
     * Private constructor. Use {@link #addTo(PrefabValues)}.
     */
    private JavaApiPrefabValues(PrefabValues prefabValues) {
        this.prefabValues = prefabValues;
    }

    /**
     * Adds instances of Java API classes that cannot be instantiated
     * dynamically to {@code prefabValues}.
     *
     * @param prefabValues The instance of prefabValues that should
     *          contain the Java API instances.
     */
    public static void addTo(PrefabValues prefabValues) {
        new JavaApiPrefabValues(prefabValues).addJavaClasses();
    }

    private void addJavaClasses() {
        addPrimitiveClasses();
        addClasses();
        addCollection();
        addLists();
        addMaps();
        addSets();
        addQueues();
        addJava8ApiClasses();
        addJavaFxClasses();
        addGoogleGuavaClasses();
        addJodaTimeClasses();
    }

    private void addPrimitiveClasses() {
        put(boolean.class, true, false);
        put(byte.class, (byte)1, (byte)2);
        put(char.class, 'a', 'b');
        put(double.class, 0.5D, 1.0D);
        put(float.class, 0.5F, 1.0F);
        put(int.class, 1, 2);
        put(long.class, 1L, 2L);
        put(short.class, (short)1, (short)2);

        put(Boolean.class, true, false);
        put(Byte.class, (byte)1, (byte)2);
        put(Character.class, 'a', 'b');
        put(Double.class, 0.5D, 1.0D);
        put(Float.class, 0.5F, 1.0F);
        put(Integer.class, 1, 2);
        put(Long.class, 1L, 2L);
        put(Short.class, (short)1, (short)2);

        put(Object.class, new Object(), new Object());
        put(Class.class, Class.class, Object.class);
        put(String.class, "one", "two");
    }

    @SuppressFBWarnings(value = "DMI_HARDCODED_ABSOLUTE_FILENAME", justification = "Just need an instance, not for actual use.")
    private void addClasses() {
        put(BigDecimal.class, BigDecimal.ZERO, BigDecimal.ONE);
        put(BigInteger.class, BigInteger.ZERO, BigInteger.ONE);
        put(Calendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
        put(Date.class, new Date(0), new Date(1));
        put(DateFormat.class, DateFormat.getTimeInstance(), DateFormat.getDateInstance());
        put(File.class, new File(""), new File("/"));
        put(Formatter.class, new Formatter(), new Formatter());
        put(GregorianCalendar.class, new GregorianCalendar(2010, 7, 4), new GregorianCalendar(2010, 7, 5));
        put(Locale.class, new Locale("nl"), new Locale("hu"));
        put(Pattern.class, Pattern.compile("one"), Pattern.compile("two"));
        put(Reference.class, new Reference("one"), new Reference("two"));
        put(SimpleDateFormat.class, new SimpleDateFormat("yMd"), new SimpleDateFormat("dMy"));
        put(Scanner.class, new Scanner("one"), new Scanner("two"));
        put(TimeZone.class, TimeZone.getTimeZone("GMT+1"), TimeZone.getTimeZone("GMT+2"));
        put(Throwable.class, new Throwable(), new Throwable());
        put(UUID.class, new UUID(0, -1), new UUID(1, 0));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addCollection() {
        addCollectionToPrefabValues(Collection.class, new ArrayList(), new ArrayList());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addLists() {
        addCollectionToPrefabValues(List.class, new ArrayList(), new ArrayList());
        addCollectionToPrefabValues(CopyOnWriteArrayList.class, new CopyOnWriteArrayList(), new CopyOnWriteArrayList());
        addCollectionToPrefabValues(LinkedList.class, new LinkedList(), new LinkedList());
        addCollectionToPrefabValues(ArrayList.class, new ArrayList(), new ArrayList());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addMaps() {
        addMapToPrefabValues(Map.class, new HashMap(), new HashMap());
        addMapToPrefabValues(SortedMap.class, new TreeMap(), new TreeMap());
        addMapToPrefabValues(NavigableMap.class, new TreeMap(), new TreeMap());
        addMapToPrefabValues(ConcurrentNavigableMap.class, new ConcurrentSkipListMap(), new ConcurrentSkipListMap());
        put(EnumMap.class, Dummy.RED.map(), Dummy.BLACK.map());
        addMapToPrefabValues(ConcurrentHashMap.class, new ConcurrentHashMap(), new ConcurrentHashMap());
        addMapToPrefabValues(HashMap.class, new HashMap(), new HashMap());
        addMapToPrefabValues(Hashtable.class, new Hashtable(), new Hashtable());
        addMapToPrefabValues(LinkedHashMap.class, new LinkedHashMap(), new LinkedHashMap());
        addMapToPrefabValues(Properties.class, new Properties(), new Properties());
        addMapToPrefabValues(TreeMap.class, new TreeMap(), new TreeMap());
        addMapToPrefabValues(WeakHashMap.class, new WeakHashMap(), new WeakHashMap());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addSets() {
        addCollectionToPrefabValues(Set.class, new HashSet(), new HashSet());
        addCollectionToPrefabValues(SortedSet.class, new TreeSet(), new TreeSet());
        addCollectionToPrefabValues(NavigableSet.class, new TreeSet(), new TreeSet());
        addCollectionToPrefabValues(CopyOnWriteArraySet.class, new CopyOnWriteArraySet(), new CopyOnWriteArraySet());
        addCollectionToPrefabValues(TreeSet.class, new TreeSet(), new TreeSet());
        put(EnumSet.class, EnumSet.of(Dummy.RED), EnumSet.of(Dummy.BLACK));

        BitSet redBitSet = new BitSet();
        BitSet blackBitSet = new BitSet();
        blackBitSet.set(0);
        put(BitSet.class, redBitSet, blackBitSet);
    }

    @SuppressWarnings("rawtypes")
    private void addQueues() {
        put(Queue.class, new ArrayBlockingQueue(1), new ArrayBlockingQueue(1));
        put(BlockingQueue.class, new ArrayBlockingQueue(1), new ArrayBlockingQueue(1));
        put(Deque.class, new ArrayDeque(1), new ArrayDeque(1));
        put(BlockingDeque.class, new LinkedBlockingDeque(1), new LinkedBlockingDeque(1));
        put(ArrayBlockingQueue.class, new ArrayBlockingQueue(1), new ArrayBlockingQueue(1));
        put(ConcurrentLinkedQueue.class, new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue());
        put(DelayQueue.class, new DelayQueue(), new DelayQueue());
        put(LinkedBlockingQueue.class, new LinkedBlockingQueue(), new LinkedBlockingQueue());
        put(PriorityBlockingQueue.class, new PriorityBlockingQueue(), new PriorityBlockingQueue());
        put(SynchronousQueue.class, new SynchronousQueue(), new SynchronousQueue());
    }

    private void addJava8ApiClasses() {
        ConditionalPrefabValueBuilder.of("java.time.ZoneId")
                .callFactory("of", classes(String.class), objects("+1"))
                .callFactory("of", classes(String.class), objects("-10"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("java.time.format.DateTimeFormatter")
                .withConstant("ISO_TIME")
                .withConstant("ISO_DATE")
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("java.util.concurrent.CompletableFuture")
                .instantiate(classes(), objects())
                .instantiate(classes(), objects())
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("java.util.concurrent.locks.StampedLock")
                .instantiate(classes(), objects())
                .instantiate(classes(), objects())
                .addTo(prefabValues);
    }

    private void addJavaFxClasses() {
        TypeTag list = new TypeTag(List.class);
        ConditionalPrefabValueBuilder.of("javafx.collections.ObservableList")
                .callFactory("javafx.collections.FXCollections", "observableList", classes(List.class), objects(prefabValues.getRed(list)))
                .callFactory("javafx.collections.FXCollections", "observableList", classes(List.class), objects(prefabValues.getBlack(list)))
                .addTo(prefabValues);
        TypeTag map = new TypeTag(Map.class);
        ConditionalPrefabValueBuilder.of("javafx.collections.ObservableMap")
                .callFactory("javafx.collections.FXCollections", "observableMap", classes(Map.class), objects(prefabValues.getRed(map)))
                .callFactory("javafx.collections.FXCollections", "observableMap", classes(Map.class), objects(prefabValues.getBlack(map)))
                .addTo(prefabValues);
        TypeTag set = new TypeTag(Set.class);
        ConditionalPrefabValueBuilder.of("javafx.collections.ObservableSet")
                .callFactory("javafx.collections.FXCollections", "observableSet", classes(Set.class), objects(prefabValues.getRed(set)))
                .callFactory("javafx.collections.FXCollections", "observableSet", classes(Set.class), objects(prefabValues.getBlack(set)))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("javafx.beans.property.BooleanProperty")
                .withConcreteClass("javafx.beans.property.SimpleBooleanProperty")
                .instantiate(classes(boolean.class), objects(true))
                .withConcreteClass("javafx.beans.property.SimpleBooleanProperty")
                .instantiate(classes(boolean.class), objects(false))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("javafx.beans.property.DoubleProperty")
                .withConcreteClass("javafx.beans.property.SimpleDoubleProperty")
                .instantiate(classes(double.class), objects(1.0D))
                .withConcreteClass("javafx.beans.property.SimpleDoubleProperty")
                .instantiate(classes(double.class), objects(2.0D))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("javafx.beans.property.FloatProperty")
                .withConcreteClass("javafx.beans.property.SimpleFloatProperty")
                .instantiate(classes(float.class), objects(1.0F))
                .withConcreteClass("javafx.beans.property.SimpleFloatProperty")
                .instantiate(classes(float.class), objects(2.0F))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("javafx.beans.property.IntegerProperty")
                .withConcreteClass("javafx.beans.property.SimpleIntegerProperty")
                .instantiate(classes(int.class), objects(1))
                .withConcreteClass("javafx.beans.property.SimpleIntegerProperty")
                .instantiate(classes(int.class), objects(2))
                .addTo(prefabValues);
        Class<?> observableList = forName("javafx.collections.ObservableList");
        ConditionalPrefabValueBuilder.of("javafx.beans.property.ListProperty")
                .withConcreteClass("javafx.beans.property.SimpleListProperty")
                .instantiate(classes(observableList), prefabValues)
                .withConcreteClass("javafx.beans.property.SimpleListProperty")
                .instantiate(classes(observableList), prefabValues)
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("javafx.beans.property.LongProperty")
                .withConcreteClass("javafx.beans.property.SimpleLongProperty")
                .instantiate(classes(long.class), objects(1L))
                .withConcreteClass("javafx.beans.property.SimpleLongProperty")
                .instantiate(classes(long.class), objects(2L))
                .addTo(prefabValues);
        Class<?> observableMap = forName("javafx.collections.ObservableMap");
        ConditionalPrefabValueBuilder.of("javafx.beans.property.MapProperty")
                .withConcreteClass("javafx.beans.property.SimpleMapProperty")
                .instantiate(classes(observableMap), prefabValues)
                .withConcreteClass("javafx.beans.property.SimpleMapProperty")
                .instantiate(classes(observableMap), prefabValues)
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("javafx.beans.property.ObjectProperty")
                .withConcreteClass("javafx.beans.property.SimpleObjectProperty")
                .instantiate(classes(Object.class), objects(new Object()))
                .withConcreteClass("javafx.beans.property.SimpleObjectProperty")
                .instantiate(classes(Object.class), objects(new Object()))
                .addTo(prefabValues);
        Class<?> observableSet = forName("javafx.collections.ObservableSet");
        ConditionalPrefabValueBuilder.of("javafx.beans.property.SetProperty")
                .withConcreteClass("javafx.beans.property.SimpleSetProperty")
                .instantiate(classes(observableSet), prefabValues)
                .withConcreteClass("javafx.beans.property.SimpleSetProperty")
                .instantiate(classes(observableSet), prefabValues)
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("javafx.beans.property.StringProperty")
                .withConcreteClass("javafx.beans.property.SimpleStringProperty")
                .instantiate(classes(String.class), objects("one"))
                .withConcreteClass("javafx.beans.property.SimpleStringProperty")
                .instantiate(classes(String.class), objects("two"))
                .addTo(prefabValues);
    }

    private void addGoogleGuavaClasses() {
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableList")
                .callFactory("of", classes(Object.class), objects("red"))
                .callFactory("of", classes(Object.class), objects("black"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableMap")
                .callFactory("of", classes(Object.class, Object.class), objects("red", "value"))
                .callFactory("of", classes(Object.class, Object.class), objects("black", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSet")
                .callFactory("of", classes(Object.class), objects("red"))
                .callFactory("of", classes(Object.class), objects("black"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSortedMap")
                .callFactory("of", classes(Comparable.class, Object.class), objects("red", "value"))
                .callFactory("of", classes(Comparable.class, Object.class), objects("black", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSortedSet")
                .callFactory("of", classes(Comparable.class), objects("red"))
                .callFactory("of", classes(Comparable.class), objects("black"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableMultiset")
                .callFactory("of", classes(Object.class), objects("red"))
                .callFactory("of", classes(Object.class), objects("black"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSortedMultiset")
                .callFactory("of", classes(Comparable.class), objects("red"))
                .callFactory("of", classes(Comparable.class), objects("black"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableListMultimap")
                .callFactory("of", classes(Object.class, Object.class), objects("red", "value"))
                .callFactory("of", classes(Object.class, Object.class), objects("black", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableSetMultimap")
                .callFactory("of", classes(Object.class, Object.class), objects("red", "value"))
                .callFactory("of", classes(Object.class, Object.class), objects("black", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableBiMap")
                .callFactory("of", classes(Object.class, Object.class), objects("red", "value"))
                .callFactory("of", classes(Object.class, Object.class), objects("black", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.ImmutableTable")
                .callFactory("of", classes(Object.class, Object.class, Object.class), objects("red", "X", "value"))
                .callFactory("of", classes(Object.class, Object.class, Object.class), objects("black", "X", "value"))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.collect.Range")
                .callFactory("open", classes(Comparable.class, Comparable.class), objects(1, 2))
                .callFactory("open", classes(Comparable.class, Comparable.class), objects(3, 4))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("com.google.common.base.Optional")
                .callFactory("of", classes(Object.class), objects("red"))
                .callFactory("of", classes(Object.class), objects("black"))
                .addTo(prefabValues);
    }

    private void addJodaTimeClasses() {
        ConditionalPrefabValueBuilder.of("org.joda.time.Chronology")
                .withConcreteClass("org.joda.time.chrono.GregorianChronology")
                .callFactory("getInstanceUTC", classes(), objects())
                .withConcreteClass("org.joda.time.chrono.ISOChronology")
                .callFactory("getInstanceUTC", classes(), objects())
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("org.joda.time.DateTimeZone")
                .callFactory("forOffsetHours", classes(int.class), objects(+1))
                .callFactory("forOffsetHours", classes(int.class), objects(-10))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("org.joda.time.PeriodType")
                .callFactory("days", classes(), objects())
                .callFactory("hours", classes(), objects())
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("org.joda.time.YearMonth")
                .instantiate(classes(int.class, int.class), objects(2009, 6))
                .instantiate(classes(int.class, int.class), objects(2014, 7))
                .addTo(prefabValues);
        ConditionalPrefabValueBuilder.of("org.joda.time.MonthDay")
                .instantiate(classes(int.class, int.class), objects(6, 1))
                .instantiate(classes(int.class, int.class), objects(7, 26))
                .addTo(prefabValues);
    }

    private <T> void put(Class<T> type, T red, T black) {
        prefabValues.put(new TypeTag(type), red, black);
    }

    private <T extends Collection<Object>> void addCollectionToPrefabValues(Class<T> type, T red, T black) {
        red.add("red");
        black.add("black");
        put(type, red, black);
    }

    private <T extends Map<Object, Object>> void addMapToPrefabValues(Class<T> type, T red, T black) {
        red.put("red_key", "red_value");
        black.put("black_key", "black_value");
        put(type, red, black);
    }

    private enum Dummy {
        RED, BLACK;

        public EnumMap<Dummy, String> map() {
            EnumMap<Dummy, String> result = new EnumMap<>(Dummy.class);
            result.put(this, toString());
            return result;
        }
    }
}
