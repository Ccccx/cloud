package com.example.factorydemo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-06-16 16:45
 */
@Data
@NoArgsConstructor
public class Org {
    private String id;
    private String org;
    private String pId;
    private Integer rank;
    private String keys;
    private List<Org> children;

    public Org(String id, String org, String pId, Integer rank, String keys) {
        this.id = id;
        this.org = org;
        this.pId = pId;
        this.rank = rank;
        this.keys = keys;
    }
}
