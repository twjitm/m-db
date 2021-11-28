# m-db
[English](https://github.com/twjitm/m-db/blob/master/README.md) [简体中文](https://github.com/twjitm/m-db/blob/master/README_ZH_CN.md)
### 简介
##### 这是个什么？
欢迎使用本项目。

本项目基于mongodb-driver封装开发，极大简化了原生API，使得开发人员专注业务本身。项目采用注解风格模式，简化了类似xml这样的配置。

##### 主要功能

1：简单文档映射 \
2：内嵌文档映射 \
3：异步存储。批量存储 \
4：索引管理

<table>
<tr>
<th>主要功能</th>
<th>是否支持</th>
<th>是否异步</th>
</tr>
<tr>
<tr><td >读操作</td>
<td >是</td>
<td >否</td>
</tr>
<tr>
    <td>写操作</td>
    <td >是</td>
    <td >是</td>
</tr>
<tr>
    <td>批量读</td>
    <td >是</td>
    <td >否</td>
</tr>
<tr>
    <td>批量写</td>
    <td >是</td>
    <td >是</td>
</tr>
<tr>
    <td>索引维护</td>
    <td >是</td>
    <td >否</td>
</tr>
</table>


### 使用说明

### 索引管理

#### 一、索引维护
通过注解@Indexed 和@CompoundIndexed 实现简单索引和联合索引的自动创建与维护，简化mongodb 索引的使用和维护

  ```java
/**
 * 联合索引：多个字段组合索引构成
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CompoundIndexed {

    Indexed[] value();

    int order() default -1;

    boolean unique() default true;
}

/**
 * 普通索引：为某个字段建立索引
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexed {

    boolean unique() default false;

    String name();

    int order() default -1;
}

/**
 * 失效时间索引：本document 具有时效性
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpireIndex {

    boolean unique() default false;

    String name() default "";

    int order() default -1;
}


```
详细使用见IndexTest中，当给每个document 的po类上或字段标记索引时，则索引会进行创建。

![创建索引](https://raw.githubusercontent.com/twjitm/m-db-test/main/images/index.jpg)


#### 二、 简单文档使用

简单文档：文档结构简单，字段类型基本为基础数据类型字段。\
使用步骤：
###### 2.1 :组装实体类PO对象
``` java
/**
 * 一个简单文档
 */
@MongoDocument(table = "user_build")
@CompoundIndexed(value = {@Indexed(name = "uid"), @Indexed(name = "build_id")})
public class BuildPo extends AbstractMongoPo {

    @MongoId(name = "uid")
    private long uid;
    @MongoId(name = "build_id", tick = true)
    @BsonProperty("build_id")
    private long buildId;
    private int x;
    private int y;
    private String type;
    private String name; 
``` 
其中通过注解@MongoDocument 数据库名字，指定表名，注解@CompoundIndex 指定用那几个字段作为联合索引
也可以用@Indexed 来单独指定索引,使用@MongoId来指定主键，由于mongodb无法实现类似mysql自增，索引采用此注解来来是否指定主键自增

###### 2.2 添加数据
``` java
  public static void addBuild() throws MException {
        BuildPo buildPo = new BuildPo();
        buildPo.setY(6);
        buildPo.setX(7);
        buildPo.setUid(1);
        buildPo.setName("北京SOHO大厦");
        buildPo.setType("高端建筑");
        MongoManager.getInstance().add(buildPo);
    }
``` 
###### 2.3 批量添加
```java 
    private static void addManyBuild() throws MException {
        List<BuildPo> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            BuildPo po = new BuildPo();
            po.setUid(2049);
            po.setX(i);
            po.setY(i);
            list.add(po);
        }
        MongoManager.getInstance().addMany(list);
    }
```
例如添加之后的数据在mongodb中可以查询到 \
<img src="https://github.com/twjitm/m-db-test/blob/main/images/build.jpg?raw=true" width="100%" height="80%">

##### 2.4 查询

 查询方式可分为两种，一种是利用@mongoID注解的主键进行查询，一种是通过某个字段进行find 查询
 
 ```java 

 /**
     * 主键查询
     */
    public static void get() throws MException {
        BuildPo b = MongoManager.getInstance().get(BuildPo.class, PrimaryKey.builder("uid", 1), PrimaryKey.builder("build_id", 7));
        System.out.println(b.getBuildId());
    }

    public static void getAll() throws MException {
        List<BuildPo> bs = MongoManager.getInstance().getAll(BuildPo.class, PrimaryKey.builder("uid", 1));
        System.out.println("getAll size=" + bs.size());
    }

  public static void findOne() throws MException {
        BuildPo b = MongoManager.getInstance().findOne(BuildPo.class, Query.builder().and("uid", 1).and("build_id", 7));
        System.out.println(b.getX() + "|" + b.getY());

    }

    public static void findAll() throws MException {
        List<BuildPo> bs = MongoManager.getInstance().findAll(BuildPo.class, Query.builder().and("uid", 1).and("y", 6), QueryOptions.builder().limit(100));
        System.out.println("findAll size=" + bs.size());

    }
```
##### 2.5 修改

修改一个文档中的某个字段，使用方式如下，例如将查询出来的build 名字进行修改
```java

    public static void update() throws MException {
        BuildPo b = MongoManager.getInstance().get(BuildPo.class, PrimaryKey.builder("uid", 1), PrimaryKey.builder("build_id", 2));
        b.setName("new name build");
        MongoManager.getInstance().update(b);
    }

```
![](https://github.com/twjitm/m-db-test/blob/main/images/update.jpg?raw=true)

### 三、异步：写改

系统支持异步操作。将操作封装为一个task，添加到异步队列中，数据库执行线程会从队列中取得操作任务。

#### 3.1 开启异步操作
```java
 private static void init() {
        mongoManager = new MongoManager("127.0.0.1:27017", true);
    }

```

### 3.2 异步操作原理
```java

 private void execute() {
        if (pool.isEmpty()) {
            return;
        }
        pool.forEach((k, v) -> collectionManager.getCollection(k).bulkWrite(v));
        pool.clear();
    }
```






