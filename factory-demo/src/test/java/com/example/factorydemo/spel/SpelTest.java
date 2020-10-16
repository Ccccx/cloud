package com.example.factorydemo.spel;

import com.example.factorydemo.bean.Foo;
import org.junit.Test;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-12 13:54
 */
public class SpelTest {

	@Test
	public void t1() {
		SpelExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext ctx = new StandardEvaluationContext();

		final Foo foo = new Foo();
		foo.name = "cxx";
		foo.setAge(10);
		foo.setFlag(true);
		ctx.setRootObject(foo);

		// 取值
		SpelExpression expr = parser.parseRaw("name");
		Object value = expr.getValue(ctx);
		assertThat(value).isEqualTo("cxx");

		// 赋值
		expr = parser.parseRaw("age=4");
		value = expr.getValue(ctx);
		expr = parser.parseRaw("age");
		value = expr.getValue(ctx);
		assertThat(value).isEqualTo(4);
	}

	@Test
	public void t2() {
		try {
			// Create a parser
			SpelExpressionParser parser = new SpelExpressionParser();
			// Use the standard evaluation context
			StandardEvaluationContext ctx = new StandardEvaluationContext();
			ctx.registerFunction("repeat", SpelTest.class.getDeclaredMethod("repeat", String.class));

			Expression expr = parser.parseRaw("#repeat('hello')");
			Object value = expr.getValue(ctx);
			assertThat(value).isEqualTo("hellohello");
		} catch (EvaluationException | ParseException | NoSuchMethodException ex) {
			throw new AssertionError(ex.getMessage(), ex);
		}
	}

	public static String repeat(String s) {
		return s + s;
	}

}
