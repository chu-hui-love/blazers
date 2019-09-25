package com.chuhui.blazers.dyproxy.dynamicproxy.util;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import static com.chuhui.blazers.dyproxy.dynamicproxy.util.CustomDynamicProxyVersion1.writeToFile;

/**
 * CustomDynamicProxyVersion2
 * <p>
 * 手写动态代理 第二版
 *
 * @author: 纯阳子
 * @date: 2019/9/22
 */
public class CustomDynamicProxyVersion2 {

    /**
     * 换行符
     */
    static final String LINE_SEP = System.lineSeparator();
    /**
     * 制表符
     */
    static final String TABLE_SEP = "\t";

    /**
     * 生成的代理类名字
     */
    static final String PROXY_CLASS_NAME = "CustomDynamicProxyServiceImpl";

    /**
     * 生成的代理类.java的存放位置
     */
    static final String JAVA_FILENAME = "D:\\com\\chuhui\\" + PROXY_CLASS_NAME + ".java";


    static public Object proxyGenerator(Class clazz, CustomeInvokeHandler handler) {

        /**
         * 1. 生成一些基本信息
         * 包声明
         * 导入
         * 类声明和私有属性
         */
        String packageName = "package com.chuhui;" + LINE_SEP;
        String importPackage = "import " + clazz.getName() + ";" + LINE_SEP;
        String importPackage2 = "import " + CustomeInvokeHandler.class.getName() + ";" + LINE_SEP;
        String importPackage3 = "import java.lang.reflect.Method;" + LINE_SEP + LINE_SEP;

        String declareClass = "public class " + PROXY_CLASS_NAME + " implements " + clazz.getSimpleName() + " {" + LINE_SEP;
        String declaredFiled1 = TABLE_SEP + "private " + CustomeInvokeHandler.class.getSimpleName() + " handler;" + LINE_SEP + LINE_SEP;

        /**
         * 2. 生成构造函数
         */
        String constructorMethod = TABLE_SEP + "public " + PROXY_CLASS_NAME + "(CustomeInvokeHandler handler) {" + LINE_SEP
                + TABLE_SEP + TABLE_SEP + "this.handler = handler;" + LINE_SEP

                + TABLE_SEP + "}" + LINE_SEP + LINE_SEP;


        /**
         * 3. 实现目标对象的接口的方法,这里的接口,就是第0步获取的接口
         */
        StringBuilder methodBuilder = new StringBuilder();

        Method[] declaredMethods = clazz.getDeclaredMethods();

        for (Method declaredMethod : declaredMethods) {

            String overrideStr = TABLE_SEP + "@Override" + LINE_SEP;

            String returnTypeName = declaredMethod.getReturnType().getSimpleName();
            String methodName = declaredMethod.getName();
            StringBuilder methodDeclaredName = new StringBuilder(overrideStr + TABLE_SEP + "public " + returnTypeName + " " + methodName + "(");

            /**
             * 3.1 接口的方法,可能会有参数,获取参数的类型,以及给每个参数类型后面添加一个形参
             */
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();

            String noTypeParam = "";

            String paramTypes = "";

            if (parameterTypes.length > 0) {

                for (int i = 0; i < parameterTypes.length; i++) {

                    Class<?> parameterType = parameterTypes[i];
                    methodDeclaredName.append(parameterType.getSimpleName() + " arg" + i + ",");

                    paramTypes += parameterType.getSimpleName() + ".class,";

                    noTypeParam += "arg" + i + ",";
                }

                methodDeclaredName.deleteCharAt(methodDeclaredName.length() - 1);
                noTypeParam = noTypeParam.substring(0, noTypeParam.length() - 1);
                paramTypes = paramTypes.substring(0, paramTypes.length() - 1);
            }
            methodDeclaredName.append("){" + LINE_SEP + LINE_SEP);

            /**
             * 3.2 创建方法体
             *
             * 注意:这里属于我们自己要完成的逻辑,和目标对象无关
             *
             *  组织调用目标对象的方法.
             */

            String exceptionStart = TABLE_SEP + TABLE_SEP + "try{" + LINE_SEP;

            String buildMethod = TABLE_SEP + TABLE_SEP + TABLE_SEP + "Method method=" + clazz.getSimpleName() + ".class.getDeclaredMethod(\"" + methodName + "\"," + paramTypes + ");" + LINE_SEP;

            /**
             * 3.3 设置返回类型
             */
            String returnStr="";
            if(!void.class.getSimpleName().equals(returnTypeName)){
                returnStr=returnTypeName+" ";
            }

            String handlerMethod = TABLE_SEP + TABLE_SEP + TABLE_SEP + returnStr+"handler.invoke(method,new Object[]{" + noTypeParam + "});" + LINE_SEP;

            String exceptionEnd = TABLE_SEP + TABLE_SEP + "}catch(Throwable e){" + LINE_SEP
                    + TABLE_SEP + TABLE_SEP + TABLE_SEP + "e.printStackTrace();" + LINE_SEP
                    + TABLE_SEP + TABLE_SEP + "}" + LINE_SEP;

            methodDeclaredName.append(exceptionStart).append(buildMethod).append(handlerMethod).append(exceptionEnd);

            methodDeclaredName.append(TABLE_SEP + "}" + LINE_SEP);
            methodBuilder.append(methodDeclaredName);
        }

        String lastChar = "}" + LINE_SEP;

        String finalStr = packageName + importPackage + importPackage2 + importPackage3 + declareClass + declaredFiled1 + constructorMethod + methodBuilder.toString() + lastChar;

        /**
         * 4. 将生成的描述代理类的字符串写到文件中
         *
         * 建议在这一步,debug一下,查看一下生产的.java文件,上面的一堆逻辑,就是为了产生这个.java文件
         */
        writeToFile(finalStr);

        try {

            /**
             * 5. 将.java文件编译成.class文件
             *
             * 这段code是从网络上抄来的..只是为了完成我们的功能,看看就行
             *
             */
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjects(JAVA_FILENAME);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);
            fileManager.close();
            task.call();


            /**
             * 6. 将产生的.class文件加载到内存中
             */
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:D:\\ \\\\\\")});
            Class<?> aClass = classLoader.loadClass("com.chuhui." + PROXY_CLASS_NAME);

            /**
             * 7. 通过反射的方式创建一个实例.
             *
             * 这里需要补充一个知识,在java中创建对象的方式,受眼界所限,目前为止,笔者所掌握的有三种:
             * 1. 直接new
             * 2. 通过类的类对象的newInstance方法
             * 3. 通过类的类对象的getConstructor().newInstance方法
             */
            Object constructor = aClass.getConstructor(CustomeInvokeHandler.class).newInstance(handler);
            return constructor;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
