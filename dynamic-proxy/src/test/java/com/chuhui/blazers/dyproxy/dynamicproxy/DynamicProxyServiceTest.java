package com.chuhui.blazers.dyproxy.dynamicproxy;

import com.chuhui.blazers.dyproxy.dynamicproxy.jdkdynamicproxy.JdkDynamicProxy.SubtractTen;
import com.chuhui.blazers.dyproxy.dynamicproxy.jdkdynamicproxy.JdkDynamicProxy.SetFiveForCount;

import com.chuhui.blazers.dyproxy.dynamicproxy.util.CustomDynamicProxyVersion1;
import com.chuhui.blazers.dyproxy.dynamicproxy.util.CustomDynamicProxyVersion2;
import com.chuhui.blazers.dyproxy.dynamicproxy.util.CustomeInvokeHandler;
import org.junit.Test;


import java.lang.reflect.Method;

import static com.chuhui.blazers.dyproxy.dynamicproxy.jdkdynamicproxy.JdkDynamicProxy.generator;

/**
 * DynamicProxyServiceTest
 *
 * @author: cyzi
 * @Date: 2019/9/24 0024
 * @Description:TODO
 */
public class DynamicProxyServiceTest {


    /**
     * 使用jdk提供的动态代理
     * 拦截printParams方法
     */
    @Test
    public void userJdkDynamicProxy() {

        DynamicProxyService dynamicProxyService = new DynamicProxyServiceImpl();

        DynamicProxyService setFiveForCountService = generator(dynamicProxyService, new SetFiveForCount(dynamicProxyService));
        setFiveForCountService.printParams("cyzi", -1);
//        DynamicProxyService subtractTenService = generator(dynamicProxyService, new SubtractTen(dynamicProxyService));
//        subtractTenService.printParams("cyzi", 100);

    }


    @Test
    public void proxyGenerator1Case() {
        DynamicProxyService service = (DynamicProxyService) CustomDynamicProxyVersion1.proxyGenerator(new DynamicProxyServiceImpl());
        service.printParams("cyzi", 10);
    }

    @Test
    public void proxyGenerator2Case() {

        DynamicProxyService dynamicProxyService = new DynamicProxyServiceImpl();

        DynamicProxyService service = (DynamicProxyService) CustomDynamicProxyVersion2
                .proxyGenerator(dynamicProxyService, new CutomeInvokeHandlerImpl(dynamicProxyService));

        service.printParams("cyzi", 10);
    }

    static class CutomeInvokeHandlerImpl implements CustomeInvokeHandler {

        private Object target;

        public CutomeInvokeHandlerImpl(Object target) {
            this.target = target;
        }


        @Override
        public Object invoke(Method method, Object[] args) throws Throwable {
            System.err.println("自己的逻辑");
            return method.invoke(target, args);
        }
    }


}
