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
package org.more.hypha.beans.xml;
import java.util.HashMap;
import java.util.Map;
import org.more.core.xml.XmlStackDecorator;
import org.more.core.xml.stream.StartElementEvent;
import org.more.hypha.beans.define.Collection_ValueMetaData;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * ���ڽ����������ͱ�ǩ�Ļ��ࡣ
 * @version 2010-9-23
 * @author ������ (zyc@byshell.org)
 */
public abstract class TagBeans_AbstractCollection<T extends Collection_ValueMetaData<?>> extends TagBeans_AbstractValueMetaDataDefine<T> {
    /**����{@link TagBeans_AbstractCollection}����*/
    public TagBeans_AbstractCollection(XmlDefineResource configuration) {
        super(configuration);
    }
    /**����ģ�����ԡ�*/
    public enum PropertyKey {
        collectionType, initSize
    }
    protected Map<Enum<?>, String> getPropertyMappings() {
        HashMap<Enum<?>, String> propertys = new HashMap<Enum<?>, String>();
        //propertys.put(PropertyKey.collectionType, "collectionType");
        propertys.put(PropertyKey.initSize, "initSize");
        return propertys;
    }
    /**����������û����������ʱ����ͨ���÷�������ȡĬ�����͡�*/
    protected abstract Class<?> getDefaultCollectionType();
    /**��ʼִ�б�ǩ*/
    public void beginElement(XmlStackDecorator<Object> context, String xpath, StartElementEvent event) {
        super.beginElement(context, xpath, event);
        Collection_ValueMetaData<?> valueMetaData = this.getDefine(context);
        String arrayTypeString = event.getAttributeValue("collectionType");
        // 1.ת��collectionType��������
        if (arrayTypeString == null)
            arrayTypeString = this.getDefaultCollectionType().getName();
        //2.����ֵ
        valueMetaData.setCollectionType(arrayTypeString);
    }
}