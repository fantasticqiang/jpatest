package org.example.repository;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Data
public abstract class EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(columnDefinition = "datetime DEFAULT  CURRENT_TIMESTAMP COMMENT '创建时间'", updatable = false)
    @Column(columnDefinition = "datetime DEFAULT  CURRENT_TIMESTAMP COMMENT '创建时间'", updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Column(columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
    @Temporal(TemporalType.TIMESTAMP)
    //@org.hibernate.annotations.UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Column(columnDefinition = "datetime DEFAULT NULL COMMENT '删除时间'")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deleteTime;
}
