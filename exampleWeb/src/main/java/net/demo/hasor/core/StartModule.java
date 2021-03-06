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
package net.demo.hasor.core;
import net.demo.hasor.manager.oauth.GithubOAuth;
import net.demo.hasor.manager.oauth.TencentOAuth;
import net.demo.hasor.manager.oauth.WeiboOAuth;
import net.hasor.restful.RenderEngine;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 *
 * @version : 2015年12月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartModule extends WebModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        apiBinder.installModule(new DataSourceModule());
        apiBinder.bindType(RenderEngine.class).uniqueName().toInstance(new FreemarkerRender());
        //
        // .Webs
        apiBinder.filter("/*").through(0, new EncodingFilter());
        apiBinder.filter("/*").through(0, new JumpFilter());
        //
        new TencentOAuth(apiBinder);// .Tencent
        new GithubOAuth(apiBinder); // .Github
        new WeiboOAuth(apiBinder);  // .Weibo
    }
}
