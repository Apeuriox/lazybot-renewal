package me.aloic.lazybot.entity.dto.geometry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GD关卡作者信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Creator implements Serializable
{
    private String creatorId;
    private String creatorName;
}
