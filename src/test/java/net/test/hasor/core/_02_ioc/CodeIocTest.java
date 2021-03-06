/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.test.hasor.core._02_ioc;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.test.hasor.core._01_bean.pojo.PojoBean;
import net.test.hasor.core._01_bean.pojo.PojoBeanFactory;
import net.test.hasor.core._01_bean.pojo.PojoInfo;
import net.test.hasor.core._02_ioc.example.IocBean;
import org.junit.Test;
import org.more.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 编码方式注入：
 * 
 * 1.valueIocTest
 *      值类型的属性注入。
 * 2.beanIocTest
 *      注入另一个Bean对象。
 * 3.faceoryIocTest
 *      Bean的属性注入来自于工厂。
 * 4.annoNameIocTest
 *      当同一个类型声明了不同实例时候，你需要通过名称注入你想要的
 * 
 * @version : 2015年11月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class CodeIocTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    /* 值类型的属性注入 */
    @Test
    public void valueIocTest() {
        System.out.println("--->>valueIocTest<<--");
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(PojoInfo.class).to(PojoBean.class)//
                        .injectValue("name", "娇娇")//         <-注入
                        .injectValue("address", "我的家里");//  <-注入
            }
        });
        //
        PojoInfo myBean = appContext.getInstance(PojoInfo.class);
        logger.debug(JSON.toString(myBean));
    }
    //
    /* 注入另一个Bean对象 */
    @Test
    public void beanIocTest() {
        System.out.println("--->>beanIocTest<<--");
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                //
                BindInfo<?> info = apiBinder.bindType(PojoInfo.class)//
                        .to(PojoBean.class).injectValue("name", "娇娇").toInfo();
                //
                apiBinder.bindType(IocBean.class).inject("iocBean", info);
            }
        });
        //
        IocBean myBean = appContext.getInstance(IocBean.class);
        logger.debug(JSON.toString(myBean));
    }
    //
    /* Bean的属性注入来自于工厂。 */
    @Test
    public void faceoryIocTest() {
        System.out.println("--->>faceoryIocTest<<--");
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                //
                apiBinder.bindType(IocBean.class).inject("iocBean", new PojoBeanFactory());
            }
        });
        //
        IocBean myBean = appContext.getInstance(IocBean.class);
        logger.debug(JSON.toString(myBean));
    }
}