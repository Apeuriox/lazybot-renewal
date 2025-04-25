package me.aloic.lazybot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebResult implements Serializable {
    private Integer code;

    private Object data;

    private String msg;
}
