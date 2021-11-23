# m-db

### 简介
##### 这是个什么？
欢迎使用本项目。

本项目基于mongodb-driver封装开发，极大简化了原生API，使得开发人员专注业务本身。项目采用注解风格模式，简化了类似xml这样的配置。

##### 主要功能

1：简单文档映射 \
2：内嵌文档映射 \
3：异步存储。批量存储 \
4：索引管理

### 使用说明

#### 一、 简单文档使用

简单文档：文档结构简单，字段类型基本为基础数据类型字段。\
使用步骤：
###### 1.1 :组装实体类PO对象
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

###### 1.2 添加数据
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
###### 1.3 批量添加
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
##### 1.4 查询

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
##### 1.4 修改



