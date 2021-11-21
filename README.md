# m-db
### abstract
##### what is this?
Welcome to use this project.
### Java ORM to Mongodb 
This project is based on the mongodb driver package development, which greatly simplifies the native API and enables developers to focus on the business itself. The project uses an annotation style pattern that simplifies configuration like XML.

##### Features

1: Simple document mapping \
2: nested document mapping \
3: asynchronous storage. Batch storage \
4: index

### Instructions

#### 1. simple documents

Simple document: The document structure is simple, and the field type is basic data type field. \
Use steps:
###### 1.1: Assemble entity class PO objects
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
MongoDocument annotation @mongoDocument database name to specify the table name, @compoundIndex to specify which fields 
to use as the joint index. You can also use @indexed to specify a separate index and @mongoid to specify a primary key.
 Since mongodb can't implement mysql customization, indexes use this annotation to specify primary key customization

###### 1.2 insert data
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
###### 1.3 insert many 
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
##### 1.4 find

Query can be divided into two ways, one is to use the @mongoid annotation 'get' primary key to query,
 and the other is to use a field to 'find' query
 
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
##### 1.4 update



