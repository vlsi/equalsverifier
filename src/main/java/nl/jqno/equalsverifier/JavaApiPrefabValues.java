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
import nl.jqno.equalsverifier.internal.TypeTag.Wildcard;
import nl.jqno.equalsverifier.prefabvaluefactory.CollectionPrefabValueFactory;
import nl.jqno.equalsverifier.prefabvaluefactory.GenericPrefabValueFactory;
import nl.jqno.equalsverifier.prefabvaluefactory.MapPrefabValueFactory;

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
    private static final Comparator<Object> OBJECT_COMPARATOR = new Comparator<Object>() {
        @Override public int compare(Object o1, Object o2) { return Integer.compare(o1.hashCode(), o2.hashCode()); }
    };

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
        put(String.class, "one", "two");

        put(Class.class, Class.class, Object.class);
        prefabValues.put(new TypeTag(Class.class, new TypeTag(Wildcard.class)), Class.class, Object.class);
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
        prefabValues.addFactory(Collection.class, new CollectionPrefabValueFactory<Collection>() {
            @Override public Collection createEmpty() { return new ArrayList(); }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addLists() {
        // Add a raw list, as well as a factory, so we already have a List instance to use when adding JavaFX classes.
        addCollectionToPrefabValues(List.class, new ArrayList(), new ArrayList());
        prefabValues.addFactory(List.class, new CollectionPrefabValueFactory<List>() {
            @Override public List createEmpty() { return new ArrayList(); }
        });

        prefabValues.addFactory(CopyOnWriteArrayList.class, new CollectionPrefabValueFactory<CopyOnWriteArrayList>() {
            @Override public CopyOnWriteArrayList createEmpty() { return new CopyOnWriteArrayList<>(); }
        });
        prefabValues.addFactory(LinkedList.class, new CollectionPrefabValueFactory<LinkedList>() {
            @Override public LinkedList createEmpty() { return new LinkedList<>(); }
        });
        prefabValues.addFactory(ArrayList.class, new CollectionPrefabValueFactory<ArrayList>() {
            @Override public ArrayList createEmpty() { return new ArrayList<>(); }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addMaps() {
        // Add a raw map, as well as a factory, so we already have a Map instance to use when adding JavaFX classes.
        addMapToPrefabValues(Map.class, new HashMap(), new HashMap());
        prefabValues.addFactory(Map.class, new MapPrefabValueFactory<Map>() {
            @Override public Map createEmpty() { return new HashMap<>(); }
        });

        prefabValues.addFactory(SortedMap.class, new MapPrefabValueFactory<SortedMap>() {
            @Override public SortedMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(NavigableMap.class, new MapPrefabValueFactory<NavigableMap>() {
            @Override public NavigableMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(ConcurrentNavigableMap.class, new MapPrefabValueFactory<ConcurrentNavigableMap>() {
            @Override public ConcurrentNavigableMap createEmpty() { return new ConcurrentSkipListMap<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(ConcurrentHashMap.class, new MapPrefabValueFactory<ConcurrentHashMap>() {
            @Override public ConcurrentHashMap createEmpty() { return new ConcurrentHashMap<>(); }
        });
        prefabValues.addFactory(HashMap.class, new MapPrefabValueFactory<HashMap>() {
            @Override public HashMap createEmpty() { return new HashMap<>(); }
        });
        prefabValues.addFactory(Hashtable.class, new MapPrefabValueFactory<Hashtable>() {
            @Override public Hashtable createEmpty() { return new Hashtable<>(); }
        });
        prefabValues.addFactory(LinkedHashMap.class, new MapPrefabValueFactory<LinkedHashMap>() {
            @Override public LinkedHashMap createEmpty() { return new LinkedHashMap<>(); }
        });
        prefabValues.addFactory(Properties.class, new MapPrefabValueFactory<Properties>() {
            @Override public Properties createEmpty() { return new Properties(); }
        });
        prefabValues.addFactory(TreeMap.class, new MapPrefabValueFactory<TreeMap>() {
            @Override public TreeMap createEmpty() { return new TreeMap<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(WeakHashMap.class, new MapPrefabValueFactory<WeakHashMap>() {
            @Override public WeakHashMap createEmpty() { return new WeakHashMap<>(); }
        });
        put(EnumMap.class, Dummy.RED.map(), Dummy.BLACK.map());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addSets() {
        // Add a raw map, as well as a factory, so we already have a Map instance to use when adding JavaFX classes.
        addCollectionToPrefabValues(Set.class, new HashSet(), new HashSet());
        prefabValues.addFactory(Set.class, new CollectionPrefabValueFactory<Set>() {
            @Override public Set createEmpty() { return new HashSet<>(); }
        });

        prefabValues.addFactory(SortedSet.class, new CollectionPrefabValueFactory<SortedSet>() {
            @Override public SortedSet createEmpty() { return new TreeSet(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(NavigableSet.class, new CollectionPrefabValueFactory<NavigableSet>() {
            @Override public NavigableSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(CopyOnWriteArraySet.class, new CollectionPrefabValueFactory<CopyOnWriteArraySet>() {
            @Override public CopyOnWriteArraySet createEmpty() { return new CopyOnWriteArraySet<>(); }
        });
        prefabValues.addFactory(TreeSet.class, new CollectionPrefabValueFactory<TreeSet>() {
            @Override public TreeSet createEmpty() { return new TreeSet<>(OBJECT_COMPARATOR); }
        });

        put(EnumSet.class, EnumSet.of(Dummy.RED), EnumSet.of(Dummy.BLACK));

        BitSet redBitSet = new BitSet();
        BitSet blackBitSet = new BitSet();
        blackBitSet.set(0);
        put(BitSet.class, redBitSet, blackBitSet);
    }

    @SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification = "For consistency with the other factories")
    @SuppressWarnings("rawtypes")
    private void addQueues() {
        prefabValues.addFactory(Queue.class, new CollectionPrefabValueFactory<Queue>() {
            @Override public Queue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        prefabValues.addFactory(BlockingQueue.class, new CollectionPrefabValueFactory<BlockingQueue>() {
            @Override public BlockingQueue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        prefabValues.addFactory(Deque.class, new CollectionPrefabValueFactory<Deque>() {
            @Override public Deque createEmpty() { return new ArrayDeque<>(1); }
        });
        prefabValues.addFactory(BlockingDeque.class, new CollectionPrefabValueFactory<BlockingDeque>() {
            @Override public BlockingDeque createEmpty() { return new LinkedBlockingDeque<>(1); }
        });
        prefabValues.addFactory(ArrayBlockingQueue.class, new CollectionPrefabValueFactory<ArrayBlockingQueue>() {
            @Override public ArrayBlockingQueue createEmpty() { return new ArrayBlockingQueue<>(1); }
        });
        prefabValues.addFactory(ConcurrentLinkedQueue.class, new CollectionPrefabValueFactory<ConcurrentLinkedQueue>() {
            @Override public ConcurrentLinkedQueue createEmpty() { return new ConcurrentLinkedQueue<>(); }
        });
        prefabValues.addFactory(DelayQueue.class, new GenericPrefabValueFactory<DelayQueue>() {
            @Override public DelayQueue createEmpty() { return new DelayQueue<>(); }

            @Override
            public DelayQueue createRed(TypeTag typeTag, PrefabValues pf) {
                pf.putFor(new TypeTag(Delayed.class));
                DelayQueue result = createEmpty();
                result.add(pf.<Delayed>getRed(new TypeTag(Delayed.class)));
                return result;
            }

            @Override
            public DelayQueue createBlack(TypeTag typeTag, PrefabValues pf) {
                pf.putFor(new TypeTag(Delayed.class));
                DelayQueue result = createEmpty();
                result.add(pf.<Delayed>getBlack(new TypeTag(Delayed.class)));
                return result;
            }
        });
        prefabValues.addFactory(LinkedBlockingQueue.class, new CollectionPrefabValueFactory<LinkedBlockingQueue>() {
            @Override public LinkedBlockingQueue createEmpty() { return new LinkedBlockingQueue<>(); }
        });
        prefabValues.addFactory(PriorityBlockingQueue.class, new CollectionPrefabValueFactory<PriorityBlockingQueue>() {
            @SuppressWarnings("unchecked")
            @Override public PriorityBlockingQueue createEmpty() { return new PriorityBlockingQueue<>(1, OBJECT_COMPARATOR); }
        });
        prefabValues.addFactory(SynchronousQueue.class, new GenericPrefabValueFactory<SynchronousQueue>() {
            @Override public SynchronousQueue createEmpty() { return new SynchronousQueue<>(); }
            @Override public SynchronousQueue createRed(TypeTag typeTag, PrefabValues pf) { return createEmpty(); }
            @Override public SynchronousQueue createBlack(TypeTag typeTag, PrefabValues pf) { return createEmpty(); }
        });
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
