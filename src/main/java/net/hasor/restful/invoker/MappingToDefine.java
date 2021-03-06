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
package net.hasor.restful.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.restful.api.HttpMethod;
import net.hasor.restful.api.MappingTo;
import net.hasor.restful.api.Valid;
import org.more.UndefinedException;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class MappingToDefine {
    private Class<?>                targetType;
    private Provider<?>             targetProvider;
    private String                  mappingTo;
    private String                  mappingToMatches;
    private Map<String, Method>     httpMapping;
    private Map<Method, InnerValid> needValid;
    private AtomicBoolean inited = new AtomicBoolean(false);
    //
    protected MappingToDefine(Class<?> targetType) {
        this.targetType = targetType;
        MappingTo pathAnno = targetType.getAnnotation(MappingTo.class);
        if (pathAnno == null) {
            throw new UndefinedException("is not a valid Mapping Service.");
        }
        String servicePath = pathAnno.value();
        if (StringUtils.isBlank(servicePath)) {
            throw new NullPointerException("Service path is empty.");
        }
        if (!servicePath.matches("/.+")) {
            throw new IllegalStateException("Service path format error");
        }
        //
        this.httpMapping = new HashMap<String, Method>();
        List<Method> mList = BeanUtils.getMethods(targetType);
        if (mList != null && !mList.isEmpty()) {
            for (Method targetMethod : mList) {
                // .HeepMethod
                Annotation[] annos = targetMethod.getAnnotations();
                if (annos != null) {
                    for (Annotation anno : annos) {
                        HttpMethod httpMethodAnno = anno.annotationType().getAnnotation(HttpMethod.class);
                        if (httpMethodAnno != null) {
                            String bindMethod = httpMethodAnno.value();
                            if (!StringUtils.isBlank(bindMethod)) {
                                this.httpMapping.put(bindMethod.toUpperCase(), targetMethod);
                            }
                        }
                    }
                }
                if (targetMethod.getName().equals("execute") && !this.httpMapping.containsKey("execute")) {
                    this.httpMapping.put(HttpMethod.ANY, targetMethod);
                }
            }
        }
        //
        // .执行调用，每个方法的参数都进行判断，一旦查到参数上具有Valid 标签那么就调用doValid进行参数验证。
        this.needValid = new HashMap<Method, InnerValid>();
        for (String key : this.httpMapping.keySet()) {
            Method targetMethod = this.httpMapping.get(key);
            Annotation[][] paramAnno = targetMethod.getParameterAnnotations();
            Class<?>[] paramType = targetMethod.getParameterTypes();
            Map<String, Valid> validMap = new HashMap<String, Valid>();
            Map<String, Class<?>> paramTypeMap = new HashMap<String, Class<?>>();
            for (int paramIndex = 0; paramIndex < paramAnno.length; paramIndex++) {
                Annotation[] annoArrays = paramAnno[paramIndex];
                for (Annotation anno : annoArrays) {
                    if (anno == null || anno instanceof Valid == false) {
                        continue;
                    }
                    validMap.put(String.valueOf(paramIndex), (Valid) anno);
                    paramTypeMap.put(String.valueOf(paramIndex), paramType[paramIndex]);
                }
            }
            this.needValid.put(targetMethod, new InnerValid(validMap, paramTypeMap));
        }
        //
        this.mappingTo = servicePath;
        this.mappingToMatches = servicePath.replaceAll("\\{\\w{1,}\\}", "([^/]{1,})");
    }
    //
    //
    /**@return 获取映射的地址*/
    public String getMappingTo() {
        return this.mappingTo;
    }
    public String getMappingToMatches() {
        return this.mappingToMatches;
    }
    /**
     * 首先测试路径是否匹配，然后判断Restful实例是否支持这个 请求方法。
     * @return 返回测试结果。
     */
    public boolean matchingMapping(String httpMethod, String requestPath) {
        Hasor.assertIsNotNull(requestPath, "requestPath is null.");
        if (!requestPath.matches(this.mappingToMatches)) {
            return false;
        }
        for (String m : this.httpMapping.keySet()) {
            if (StringUtils.equals(httpMethod, m)) {
                return true;
            } else if (StringUtils.equals(m, HttpMethod.ANY)) {
                return true;
            }
        }
        return false;
    }
    //
    /** 执行初始化 */
    protected void init(final AppContext appContext) {
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        Hasor.assertIsNotNull(appContext, "appContext is null.");
        this.targetProvider = new Provider<Object>() {
            public Object get() {
                return appContext.getInstance(targetType);
            }
        };
    }
    /**
     * 调用目标
     * @throws Throwable 异常抛出
     */
    public final void invoke(InnerRenderData renderData) throws Throwable {
        String httpMethod = renderData.getHttpRequest().getMethod();
        Method targetMethod = this.httpMapping.get(httpMethod.trim().toUpperCase());
        if (targetMethod == null) {
            targetMethod = this.httpMapping.get(HttpMethod.ANY);
        }
        //
        Hasor.assertIsNotNull(targetMethod, "not font mapping Method.");
        InnerValid needValid = this.needValid.get(targetMethod);
        new Invoker(this, renderData).exeCall(this.targetProvider, targetMethod, needValid);
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}