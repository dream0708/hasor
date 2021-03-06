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
package net.hasor.core.info;
import net.hasor.core.Provider;
/**
 * 如果Bean配置了{@link Provider}，那么Hasor容器需要通过该接口获取到这个Provider。
 * @version : 2014年12月2日
 * @author 赵永春(zyc@hasor.net)
 */
public interface CustomerProvider<T> {
    /**获取Provider对象，可以直接取得对象实例。*/
    public Provider<T> getCustomerProvider();
}