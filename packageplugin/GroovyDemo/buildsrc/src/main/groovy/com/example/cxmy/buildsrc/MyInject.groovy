package com.example.cxmy.buildsrc

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtMethod
import javassist.CtNewMethod

/**
 * Created by cxmy on 2017/4/12.
 */
public class MyInject {

    private static ClassPool pool = ClassPool.getDefault()
    private static String injectStr = "System.out.println(\"I am cxmyDev\" ); ";

    public static void injectDir(String path, String packageName) {
        pool.appendClassPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                inject(file, path, packageName)
            }
        }
    }

    private static void inject(File file, String path, String packageName) {
        String filePath = file.absolutePath
        // 确保当前文件是我们编写的class ，而非其他文件
        if (filePath.endsWith(".class")
                && !filePath.contains('R$')
                && !filePath.contains('R.class')
                && !filePath.contains("BuildConfig.class")) {
            int index = filePath.indexOf(packageName);
            boolean isMyPackage = index != -1;
            if (isMyPackage) {
                int end = filePath.length() - 6 // ".class".length == 6
                String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                //开始修改class文件
                L.i("getCtClass() class name : "+className);
                CtClass c = pool.getCtClass(className)

                if (c.isFrozen()) {
                    c.defrost()
                }
                // 获取当前类的构造方法
                CtConstructor[] cts = c.getDeclaredConstructors()
                pool.importPackage("android.util.Log");
                if (cts == null || cts.length == 0) {
                    // 如果当前类没有默认构造方法，手动创建一个构造函数
                    CtConstructor constructor = new CtConstructor(new CtClass[0], c)
                    constructor.insertBeforeBody(injectStr)
                    c.addConstructor(constructor)
                } else {
                    // 当前类有默认构造方法，则直接在尾部追加
                    cts[0].insertBeforeBody(injectStr)
                }
                L.i("writeFile() path : "+path);
                c.writeFile(path)
                c.detach()
            }
        }
    }


}