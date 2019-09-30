package org.chieftain.dynamicds.utils;

import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Java反射工具类
 */
public class ReflectUtils {

	/**
	 * 根据成员变量名称获取其值
	 *
	 * @param obj
	 * @param targetField
	 * @param <T>
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static <T> Object getFieldValue(Object obj, String targetField) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException {

		List<Field> list = getFields(obj.getClass(), true);
		for (Field field : list) {
			if (field.getName().equals(targetField)) {
				field.setAccessible(true);
				return field.get(obj);
			}
		}

		return null;
	}

	/**
	 * 获取属性列表
	 *
	 * @param clazz
	 * @param containSupperClass
	 * @param <T>
	 * @return
	 */
	public static <T> List<Field> getFields(Class<T> clazz, boolean containSupperClass) {

		List<Field> list = new ArrayList<Field>();

		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			list.add(field);
		}
		if (containSupperClass) {
			if (clazz.getSuperclass() != null && !clazz.getSuperclass().getSimpleName().equals(Object.class.getSimpleName())) {
				list.addAll(getFields(clazz.getSuperclass(), containSupperClass));
			}
		}

		return list;
	}

    /**
	 * 获取所有方法
	 *
	 * @param clazz
	 * @param containSupperClass
	 * @param <T>
	 * @return
	 */
	public static <T> List<Method> getMethods(Class<T> clazz, boolean containSupperClass) {

		List<Method> list = new ArrayList<Method>();

		Method[] ms = clazz.getDeclaredMethods();

		for (Method m : ms) {
			list.add(m);
		}
		if (containSupperClass) {
			if (clazz.getSuperclass() != null && !clazz.getSuperclass().getSimpleName().equals(Object.class.getSimpleName())) {
				list.addAll(getMethods(clazz.getSuperclass(), containSupperClass));
			}
		}

		return list;
	}


	/**
	 * 调用对象的无参方法
	 *
	 * @param instance
	 * @param method
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public static <T> Object invoke(Object instance, String method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Method m = instance.getClass().getMethod(method, new Class[]{});
		return m.invoke(instance, new Object[]{});
	}


	/**
	 * 通过类的实例，调用指定的方法
	 *
	 * @param instance
	 * @param method
	 * @param paramClasses
	 * @param params
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static <T> Object invoke(Object instance, String method, Class<T>[] paramClasses, Object[] params) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Method _m = instance.getClass().getMethod(method, paramClasses);
		return _m.invoke(instance, params);
	}

	public static Properties convertProperties (Object object, String... excludeField) {
		Assert.notNull(object, "object can not be null.");
		List<String> excludeFields;
		if (null == excludeField) {
			excludeFields = new ArrayList<>();
		} else {
			excludeFields = Arrays.asList(excludeField);
		}
		try {
			Properties properties = new Properties();
			List<Field> fields = ReflectUtils.getFields(object.getClass(), false);
			for (Field field : fields) {
				field.setAccessible(true);
				Object fieldVal = field.get(object);
				if (null == fieldVal) {
					continue;
				}
				String key = field.getName();
				if (excludeFields.contains(key)) {
					continue;
				}
				if ("$assertionsDisabled".equals(key)) {
					continue;
				}
				properties.setProperty(key, fieldVal.toString());
			}
			return properties;
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			ReflectTestBean rtb = new ReflectTestBean();
			rtb.setF1("1");
			rtb.setF2("2");
			rtb.setF3("3");
			List<Field> fields = ReflectUtils.getFields(ReflectTestBean.class, false);
			for (Field field : fields) {
				field.setAccessible(true);
				System.out.println(field.getName().concat("-").concat(field.get(rtb).toString()));
			}
			Properties properties = ReflectUtils.convertProperties(rtb, null);
			System.out.println("ok");
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
}