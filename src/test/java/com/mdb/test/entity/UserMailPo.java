package com.mdb.test.entity;

import com.mdb.entity.AbstractMongoPo;
import com.mdb.enums.MongoDocument;
import com.mdb.enums.MongoId;
import com.mdb.enums.index.Indexed;

@MongoDocument(database = "mdb", table = "user_mail")
public class UserMailPo extends AbstractMongoPo {
    @Indexed(name = "uid")
    @MongoId(name = "uid", root = true)
    private long uid;

    @MongoId(name = "mail_id", tick = true, root = true)
    @Indexed(name = "mail_id", unique = true)
    private long mailId;

    private String title;
    private String content;

    @Indexed(name = "ttl", unique = false, expireAfterSeconds = 300)
    private long ttl;


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getMailId() {
        return mailId;
    }

    public void setMailId(long mailId) {
        this.mailId = mailId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}

