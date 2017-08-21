
#### 获取unsafe实例

##### 1. JAVA方式获取unsafe实例
```
 private static Unsafe getUnsafeInstance() throws SecurityException, NoSuchFieldException, IllegalArgumentException,  
            IllegalAccessException {  
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");  
        theUnsafeInstance.setAccessible(true);  
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);  
    }  
```

##### 2. android方式获取unsafe实例

```
static class Unsafe {
    static final String UNSAFE_CLASS = "sun.misc.Unsafe";
    static Object THE_UNSAFE = Reflection.get(null, UNSAFE_CLASS, "THE_ONE", null);

    public static long getObjectAddress(Object o) {
        Object[] objects = {o};
        Integer baseOffset = (Integer) Reflection.call(null, UNSAFE_CLASS,
                "arrayBaseOffset", THE_UNSAFE, new Class[]{Class.class}, new Object[]{Object[].class});
        return ((Number) Reflection.call(null, UNSAFE_CLASS, "getInt", THE_UNSAFE,
                new Class[]{Object.class, long.class}, new Object[]{objects, baseOffset.longValue()})).longValue();
    }
}
    
    
 private static class Reflection {
    public static Object call(Class<?> clazz, String className, 
    						String methodName, Object receiver, Class[] types, Object[] params) throws UnsupportedException {
        try {
            if (clazz == null) clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            return method.invoke(receiver, params);
        } catch (Throwable throwable) {
            throw new UnsupportedException("reflection error:", throwable);
        }
    }

    public static Object get(Class<?> clazz, String className, String fieldName, Object receiver) {
        try {
            if (clazz == null) clazz = Class.forName(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(receiver);
        } catch (Throwable e) {
            throw new UnsupportedException("reflection error:", e);
        }
    }
}

```


#### java对象占用内存大小

[内存占用](http://blog.csdn.net/aitangyong/article/details/46416667)

[对象大小](http://www.cnblogs.com/zhanjindong/p/3757767.html)









