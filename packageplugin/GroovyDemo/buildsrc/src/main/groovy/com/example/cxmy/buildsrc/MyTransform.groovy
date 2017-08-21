package com.example.cxmy.buildsrc

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ddmlib.Log
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Created by cxmy on 2017/4/12.
 */
public class MyTransform extends Transform {

    Project project

    // 构造函数，我们将Project保存下来备用
    public MyTransform(Project project) {
        this.project = project
    }

    // 设置我们自定义的Transform对应的Task名称
    @Override
    String getName() {
        return "MyTrans"
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    //这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transform的作用范围
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        // Transform的inputs有两种类型，一种是项目内的目录，一种是第三方的jar包，要分开遍历
        inputs.each { TransformInput input ->
            //对类型为“directory” 的 input 进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                transformDirectory(outputProvider, directoryInput)

//                L.i("directoryInput :"+directoryInput.file.absolutePath);
//
//                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
//                MyInject.injectDir(directoryInput.file.absolutePath,"com/example/cxmy/groovydemo")
//                // 获取output目录
//                def dest = outputProvider.getContentLocation(directoryInput.name,
//                        directoryInput.contentTypes, directoryInput.scopes,
//                        Format.DIRECTORY)
//
//                // 将input的目录复制到output指定目录
//                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            //对第三方的 jar 包文件，进行遍历
            input.jarInputs.each { JarInput jarInput ->
                transformJar(outputProvider, jarInput)
//                // 重命名输出文件（同目录copyFile会冲突）
//                def jarName = jarInput.name
//                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
//                if (jarName.endsWith(".jar")) {
//                    jarName = jarName.substring(0, jarName.length() - 4)
//                }
//                File copyJarFile = jarInput.file;
//                def libJar = jarInput.file.getAbsoluteFile().name;
//
//
//                if(libJar.contains("lib.jar")){
//                    L.i("start unzip :"+libJar)
//                    JarZipUtil.unzipJar(jarInput.file.getAbsolutePath(),"./tmp/lib");
//                    MyInject.injectDir("./tmp/lib","com/example")
//                    JarZipUtil.zipJar("./tmp/lib","./tmp/lib.jar")
//                    copyJarFile = new File("./tmp/lib.jar");
//                }
//
//                //生成输出路径
//                def dest = outputProvider.getContentLocation(jarName + md5Name,
//                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
//                L.i("===============")
//                L.i("jar dest :"+ dest);
//                L.i("jar jarInput.file :"+ copyJarFile);
//                //将输入内容复制到输出
//                if(!copyJarFile.absolutePath.contains("support-v4")){
//                    L.i("jar jarInput.file 2:"+ copyJarFile);
//                    FileUtils.copyFile(copyJarFile, dest)
//                }
            }
        }
    }

    private static void transformDirectory(TransformOutputProvider outputProvider, DirectoryInput directoryInput) {
        L.i("directoryInput :"+directoryInput.file.absolutePath);

        //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
        MyInject.injectDir(directoryInput.file.absolutePath,"com/example/cxmy/groovydemo")
        // 获取output目录
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)

        // 将input的目录复制到output指定目录
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    private static void transformJar(TransformOutputProvider outputProvider, JarInput jarInput) {
        // 重命名输出文件（同目录copyFile会冲突）
        def jarName = jarInput.name
        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4)
        }
        File copyJarFile = jarInput.file;
        def libJar = jarInput.file.getAbsoluteFile().name;


        if(libJar.contains("lib.jar")){
            L.i("start unzip :"+libJar)
            JarZipUtil.unzipJar(jarInput.file.getAbsolutePath(),"./tmp/lib");
            MyInject.injectDir("./tmp/lib","com/example")
            JarZipUtil.zipJar("./tmp/lib","./tmp/lib.jar")
            copyJarFile = new File("./tmp/lib.jar");
        }

        //生成输出路径
        def dest = outputProvider.getContentLocation(jarName + md5Name,
                jarInput.contentTypes, jarInput.scopes, Format.JAR)
        L.i("===============")
        L.i("jar dest :"+ dest);
        L.i("jar jarInput.file :"+ copyJarFile);
        //将输入内容复制到输出
        if(!copyJarFile.absolutePath.contains("support-v4")){
            L.i("jar jarInput.file 2:"+ copyJarFile);
            FileUtils.copyFile(copyJarFile, dest)
        }
    }
}