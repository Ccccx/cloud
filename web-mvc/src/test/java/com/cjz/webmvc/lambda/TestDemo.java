package com.cjz.webmvc.lambda;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author chengjinzhou
 * @version 1.0
 * @date 2019-04-03 18:27
 */
@Slf4j
public class TestDemo {

    public static <T> Object getParam(T obj, Function<T, Object> fc) {
        return fc.apply(obj);
    }

    public static void out(Object str) {
        System.out.println(str);
    }

    public static void out(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    /**
     * map 根据排序
     */
    @Test
    public void mapSort() {
        Map<String, Double> map = new HashMap<>();
        Map<String, Double> result = new LinkedHashMap<>();
        map.put("2", 90.6);
        map.put("3", 84.5);
        map.put("1", 101.1);
        // 根据val降序
        map.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        result.forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });
        // 根据key排序
        result.clear();
        map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        result.forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });
    }

    @Test
    public void t1() {
        List<User> list = User.randUserData(10);
        list.forEach(v -> System.out.println(v.toString()));
        split();
        // List 转换 Map
        Map<String, List<User>> collect = list.stream().filter(v -> v.getAge() >= 18).collect(Collectors.groupingBy(User::getGender));

        final Map<String, Set<User>> groupSetMap = list.stream().collect(Collectors.groupingBy(User::getGender, Collectors.mapping(v -> v, Collectors.toSet())));

        final int sum = list.stream().mapToInt(v -> v.getBalance()).sum();
        System.out.println("总金额: " + sum);

        final Map<String, Map<String, List<User>>> mapMap = list.stream().collect(Collectors.groupingBy(User::getGender, Collectors.groupingBy(User::getName)));

        // List 获取指定不重复属性
        Set<String> set = list.stream().map(User::getName).collect(Collectors.toSet());

        collect.forEach((k, v) -> {
            System.out.println(k);
            v.forEach(System.out::println);
        });

        // List按属性拆分Map
        Map<String, String> collect1 = list.stream().collect(Collectors.toMap(User::getGender, User::getName, (k1, k2) -> k1 + k2));
        collect1.forEach((k, v) -> {
            System.out.println(k + " \t " + v);
        });

        // List按属性做key拆分Map
        Map<String, User> userMap = list.stream().collect(Collectors.toMap(User::getGender, a -> a, (k1, k2) -> k1));

        Map<String, String> map = new LinkedHashMap<>();
        map.put("2", "222222" + System.currentTimeMillis());
        map.put("1", "111111" + System.currentTimeMillis());
        map.put("3", "333333" + System.currentTimeMillis());
        map.put("0", "000000" + System.currentTimeMillis());

        // 排序
        List<String> collect2 = map.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue).collect(Collectors.toList());

        collect2.forEach(v -> {
            System.out.print(v + "\t");
        });
        split();
        // List转换字符串
        String collect3 = collect2.stream().collect(Collectors.joining("','", "'", "'"));
        System.out.println(collect3);

        split();

        // 并行处理
        long[] longs = new long[2000];
        Arrays.parallelSetAll(longs, index -> ThreadLocalRandom.current().nextInt(10000000));
        Arrays.stream(longs).limit(10).forEach(i -> System.out.print(i + "\t"));
        System.out.println();
        Arrays.parallelSort(longs);
        Arrays.stream(longs).limit(10).forEach(i -> System.out.print(i + "\t"));

        split();
        Function<User, String> function = User::getName;
        list.forEach(v -> {
            System.out.print(function.apply(v) + "\t");
        });
    }

    @Test
    public void t2() throws NoSuchMethodException {
        // 不允许Null，否则NPE
        Optional<String> demo = Optional.of("This is Demo");
        // 允许Null
        Optional<String> empty = Optional.ofNullable(null);

        if (demo.isPresent()) {
            System.out.println(demo.get());
            System.out.println(demo.orElse("demo 没有值时，打印这句话！"));
        }
        System.out.println(empty.orElseGet(() -> "empty 没有值时，打印这句话！"));


        final Method demoMethod = TestDemo.class.getMethod("demoMethod", List.class, Integer.class, Object.class);
        for (Parameter parameter : demoMethod.getParameters()) {
            System.out.println("Parameter : " + parameter.getName());
        }
    }

    @Test
    public void t3() {
        final Clock clock = Clock.systemUTC();
        out(clock.instant());
        out(clock.millis());

        // 只显示日期
        out(LocalDate.now());
        // 通过clock 构建
        out(LocalDate.now(clock));

        // 只显示日期
        out(LocalTime.now());
        // 通过clock 构建
        out(LocalTime.now(clock));

        // 显示日期时间
        out(LocalDateTime.now());
        // 通过clock 构建
        out(LocalDateTime.now(clock));

        final LocalDateTime start = LocalDateTime.of(2019, 7, 31, 0, 0);
        final LocalDateTime now = LocalDateTime.now();
        // Duration 操作时间区间
        final Duration duration = Duration.between(start, now);
        out(duration.toDays());
        out(duration.toHours());
        split();
        // Base64
        final String text = "Base64 finally in Java 8!";
        final String encodeToString = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        out(encodeToString);
        out(new String(Base64.getDecoder().decode(encodeToString), StandardCharsets.UTF_8));

    }

    @Test
    public void t4() {
        final TestBean bean = new TestBean();
        BiConsumer<TestBean, Long> setId = TestBean::setId;
        setId.accept(bean, 2L);
        System.out.println(getParam(bean, TestBean::getId));
        System.out.println(setId.getClass());
        String tmp = "str";
        System.out.println(Optional.ofNullable(tmp).map((str) -> str + "_").orElseGet(() -> "111111111111111"));
        // 判断是不是lambda
        System.out.println("是否为Lambda " + setId.getClass().isSynthetic());

    }

    /**
     * lambda 获取真实对象
     *
     * @throws Exception ig
     */
    @Test
    public void t5() throws Exception {
        final SFunction<User, String> getAddress = User::getAddress;
        final Class<?> aClass = getAddress.getClass();
        log.info("Lambda? {}  Class: {}  Name: {}", aClass.isSynthetic(), aClass, aClass.getCanonicalName());
        final SerializedLambda fc = fc(getAddress);
        log.info("{}", fc.getImplClass());

        // 直接调用writeReplace
        Method writeReplace = getAddress.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        Object sl = writeReplace.invoke(getAddress);
        java.lang.invoke.SerializedLambda serializedLambda = (java.lang.invoke.SerializedLambda) sl;
        log.info("{}", serializedLambda.getImplClass());
    }

    public <T> SerializedLambda fc(SFunction<T, ?> fc) {
        final SerializedLambda resolve = SerializedLambda.resolve(fc);
        return resolve;
    }

    public void demoMethod(List list, Integer sum, Object obj) {

    }

    public void split() {
        System.out.println("\n---------------------------------");
    }
}
