package com.example.factory.project.model;

import com.example.factory.project.ProjectDescription;
import lombok.Data;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 13:45
 */
@Data
public class MutableProjectDescription implements ProjectDescription {
    private String msg;
    private Long id;
}
