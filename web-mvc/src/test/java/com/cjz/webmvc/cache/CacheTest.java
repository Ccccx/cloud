package com.cjz.webmvc.cache;

import com.google.common.base.Optional;
import com.google.common.cache.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


/**
 * @author chengjz
 * @version 1.0
 * @since 2020-06-17 14:03
 */
public class CacheTest {
	/**
	 * String实例的大写形式
	 */
	@Test
	public void t1() throws ExecutionException {
		final CacheLoader<String, String> load = new CacheLoader<String, String>() {
			@Override
			public String load(String key) throws Exception {
				return key.toUpperCase();
			}
		};
		final LoadingCache<String, String> cache = CacheBuilder.newBuilder().build(load);
		assertEquals(0, cache.size());
		// getUnchecked（）操作–如果该值不存在，它将计算该值并将其加载到缓存中。
		assertEquals("HELLO", cache.getUnchecked("hello"));
		assertEquals(1, cache.size());

	}

	/**
	 * 手动刷新 和 自动刷新
	 */
	@Test
	public void whenLiveTimeEnd_thenRefresh() {
		CacheLoader<String, String> loader;
		loader = new CacheLoader<String, String>() {
			@Override
			public String load(String key) {
				return key.toUpperCase();
			}
		};

		LoadingCache<String, String> cache = CacheBuilder.newBuilder()
				// 自动刷新
				.refreshAfterWrite(1,TimeUnit.MINUTES)
				.build(loader);

		// 手动刷新
		String value = cache.getUnchecked("hello");
		cache.refresh("hello");
	}

	/**
	 * 我们可以使用maximumSize（）限制缓存的大小。如果缓存达到限制，则最早的项目将被逐出。
	 *
	 * 在以下代码中，我们将缓存大小限制为3条记录：
	 */
	@Test
	public void t2() {
		final CacheLoader<String, String> load = new CacheLoader<String, String>() {
			@Override
			public String load(String key) throws Exception {
				return key.toUpperCase();
			}
		};
		final LoadingCache<String, String> cache = CacheBuilder.newBuilder().maximumSize(3).build(load);
		cache.getUnchecked("first");
		cache.getUnchecked("second");
		cache.getUnchecked("third");
		cache.getUnchecked("forth");
		assertEquals(3, cache.size());
		assertNull(cache.getIfPresent("first"));
		assertEquals("FORTH", cache.getIfPresent("forth"));
	}

	/**
	 * 自定义权重函数来限制缓存大小。在以下代码中，我们将长度用作自定义权重函数：
	 * 缓存可能会删除多个记录，从而为新的大记录留出空间。
	 */
	@Test
	public void whenCacheReachMaxWeight_thenEviction() {
		CacheLoader<String, String> loader = new CacheLoader<String, String>() {
			@Override
			public String load(String key) {
				return key.toUpperCase();
			}
		};

		Weigher<String, String> weighByLength = (key, value) -> value.length();

		LoadingCache<String, String> cache = CacheBuilder.newBuilder()
				.maximumWeight(16)
				.weigher(weighByLength)
				.build(loader);

		cache.getUnchecked("first");
		cache.getUnchecked("second");
		cache.getUnchecked("third");
		cache.getUnchecked("last");
		assertEquals(3, cache.size());
		assertNull(cache.getIfPresent("first"));
		assertEquals("LAST", cache.getIfPresent("last"));
	}

	/**
	 * 除了使用大小驱逐旧记录外，我们还可以使用时间。在下面的示例中，我们自定义缓存以删除空闲2ms的记录：
	 * @throws InterruptedException
	 */
	@Test
	public void whenEntryIdle_thenEviction()
			throws InterruptedException {
		CacheLoader<String, String> loader = new CacheLoader<String, String>() {
			@Override
			public String load(String key) {
				return key.toUpperCase();
			}
		};

		LoadingCache<String, String> cache = CacheBuilder.newBuilder()
				.expireAfterAccess(2, TimeUnit.MILLISECONDS)
				.build(loader);

		cache.getUnchecked("hello");
		assertEquals(1, cache.size());

		cache.getUnchecked("hello");
		Thread.sleep(300);

		cache.getUnchecked("test");
		assertEquals(1, cache.size());
		assertNull(cache.getIfPresent("hello"));
	}

	/***
	 * 根据记录的总生存时间逐出记录。在以下示例中，缓存将在存储2ms后删除记录：
	 * @throws InterruptedException
	 */
	@Test
	public void whenEntryLiveTimeExpire_thenEviction()
			throws InterruptedException {
		CacheLoader<String, String> loader = new CacheLoader<String, String>() {
			@Override
			public String load(String key) {
				return key.toUpperCase();
			}
		};

		LoadingCache<String, String> cache = CacheBuilder.newBuilder()
				.expireAfterWrite(2,TimeUnit.MILLISECONDS)
				.build(loader);

		cache.getUnchecked("hello");
		assertEquals(1, cache.size());
		Thread.sleep(300);
		cache.getUnchecked("test");
		assertEquals(1, cache.size());
		assertNull(cache.getIfPresent("hello"));
	}

	/**
	 * 接下来，让我们看看如何使缓存键具有弱引用–允许垃圾收集器收集其他地方未引用的缓存键。
	 *
	 * 默认情况下，缓存键和值都具有强引用，但是我们可以使我们的缓存使用weakKeys（）使用弱引用来存储键，如以下示例所示：
	 */
	@Test
	public void whenWeakKeyHasNoRef_thenRemoveFromCache() {
		CacheLoader<String, String> loader;
		loader = new CacheLoader<String, String>() {
			@Override
			public String load(String key) {
				return key.toUpperCase();
			}
		};

		LoadingCache<String, String> cache = CacheBuilder.newBuilder().weakKeys().build(loader);
	}

	/**
	 * 通过使用softValues（）来允许垃圾收集器收集缓存的值：
	 * 注意：许多软引用可能会影响系统性能-最好使用maximumSize（）。
	 */
	@Test
	public void whenSoftValue_thenRemoveFromCache() {
		CacheLoader<String, String> loader;
		loader = new CacheLoader<String, String>() {
			@Override
			public String load(String key) {
				return key.toUpperCase();
			}
		};

		LoadingCache<String, String> cache = CacheBuilder.newBuilder().softValues().build(loader);
	}

	/**
	 * 让我们看看如何处理缓存空值。默认情况下，如果您尝试加载null值，则Guava Cache将抛出异常-因为缓存null没有任何意义。
	 *
	 * 但是，如果null值表示代码中有某些内容，则可以充分利用Optional类，如以下示例所示：
	 */
	@Test
	public void whenNullValue_thenOptional() {
		CacheLoader<String, Optional<String>> loader = new CacheLoader<String, Optional<String>>() {
			@Override
			public Optional<String> load(String key) {
				return Optional.fromNullable(getSuffix(key));
			}
		};

		LoadingCache<String, Optional<String>> cache = CacheBuilder.newBuilder().build(loader);

		assertEquals("txt", cache.getUnchecked("text.txt").get());
		assertFalse(cache.getUnchecked("hello").isPresent());
	}
	private String getSuffix(final String str) {
		int lastIndex = str.lastIndexOf('.');
		if (lastIndex == -1) {
			return null;
		}
		return str.substring(lastIndex + 1);
	}

	/**
	 * 我们可以使用putAll（）方法在缓存中插入多个记录。在下面的示例中，我们使用Map将多个记录添加到缓存中：
	 */
	@Test
	public void whenPreloadCache_thenUsePutAll() {
		CacheLoader<String, String> loader;
		loader = new CacheLoader<String, String>() {
			@Override
			public String load(String key) {
				return key.toUpperCase();
			}
		};

		LoadingCache<String, String> cache;
		cache = CacheBuilder.newBuilder().build(loader);

		Map<String, String> map = new HashMap<String, String>();
		map.put("first", "FIRST");
		map.put("second", "SECOND");
		cache.putAll(map);

		assertEquals(2, cache.size());
	}

	/**
	 * 有时，当从缓存中删除记录时，您需要采取一些措施。因此，让我们讨论RemovalNotification。
	 *
	 * 我们可以注册一个RemovalListener来获取删除记录的通知。我们还可以通过getCause（）方法访问删除原因。
	 *
	 * 在以下示例中，由于高速缓存中的第四个元素的大小，收到了RemovalNotification：
	 */
	@Test
	public void whenEntryRemovedFromCache_thenNotify() {
		CacheLoader<String, String> loader = new CacheLoader<String, String>() {
			@Override
			public String load(final String key) {
				return key.toUpperCase();
			}
		};

		RemovalListener<String, String> listener = new RemovalListener<String, String>() {
			@Override
			public void onRemoval(RemovalNotification<String, String> n){
				if (n.wasEvicted()) {
					String cause = n.getCause().name();
					assertEquals(RemovalCause.SIZE.toString(), cause);
				}
			}
		};

		LoadingCache<String, String> cache = CacheBuilder.newBuilder()
				.maximumSize(3)
				.removalListener(listener)
				.build(loader);

		cache.getUnchecked("first");
		cache.getUnchecked("second");
		cache.getUnchecked("third");
		cache.getUnchecked("last");
		assertEquals(3, cache.size());
	}
}
