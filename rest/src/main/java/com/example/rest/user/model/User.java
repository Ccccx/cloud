package com.example.rest.user.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-26 13:57
 */
@Data
@Entity
@Table(name = "REST_USER", indexes = {
        @Index(name = "Id", columnList = "id")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false, length = 32)
    private String nickname;

    @Column( length = 32)
    private String mobile;

    @Column( length = 280)
    private String email;

    @Column(columnDefinition = "datetime")
    private Date createData;

    private int enabled;
}
